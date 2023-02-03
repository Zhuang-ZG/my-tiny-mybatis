package practice.zhuang.mybatis.datasource.pooled;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import practice.zhuang.mybatis.datasource.unpooled.UnpooledDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * @author: ZhuangZG
 * @date 2023/2/2 10:39
 * @Description:
 */
@Slf4j
public class PooledDataSource implements DataSource {

    private final PoolState poolState = new PoolState();
    private UnpooledDataSource dataSource;

    // 最大空闲连接数
    protected Integer poolMaximumIdleConnections = 10;
    // 最大活跃连接数
    protected Integer poolMaximumActiveConnections = 10;
    // 最大检查时间
    protected Integer poolMaximumCheckoutTime = 20000;
    protected Integer poolTimeToWait = 20000;
    // 数据库连接的健康检查时发送到数据库的指令
    protected String poolPingQuery = "NO PING QUERY SET";
    protected boolean poolPingEnabled = false;
    protected int poolPingConnectionsNotUsedFor = 0;
    private int expectedConnectionTypeCode;

    public PooledDataSource() {
        this.dataSource = new UnpooledDataSource();
        this.poolMaximumActiveConnections--;
        this.poolMaximumIdleConnections++;
    }

    /**
     * 将连接归还至数据源连接池
     *
     * @param connection
     */
    protected void pushConnection(PooledConnection connection) throws SQLException {
        synchronized (poolState) {
            if (connection.isValid()) {
                List<PooledConnection> idleConnections = poolState.getIdleConnections();
                if (idleConnections.size() < poolMaximumActiveConnections &&
                        connection.getConnectionTypeCode() == expectedConnectionTypeCode) {
                    poolState.accumulatedCheckoutTime += connection.getCheckoutTime();
                    if (!connection.getRealConnection().getAutoCommit()) {
                        connection.getRealConnection().rollback();
                    }
                    PooledConnection newConnection = new PooledConnection(connection.getRealConnection(), this);
                    poolState.getIdleConnections().add(newConnection);
                    newConnection.setCreatedTimeStamp(System.currentTimeMillis());
                    newConnection.setLastUsedTimeStamp(System.currentTimeMillis());
                    connection.invalidate();
                    log.info("Returned connection " + newConnection.getRealHashCode() + " to pool.");
                    poolState.notifyAll();
                } else {
                    poolState.accumulatedCheckoutTime += connection.getCheckoutTime();
                    if (!connection.getRealConnection().getAutoCommit()) {
                        connection.getRealConnection().rollback();
                    }
                    connection.getRealConnection().close();
                    log.info("Closed connection " + connection.getRealHashCode() + ".");
                    connection.invalidate();
                }
            } else {
                log.info("A bad connection (" + connection.getRealHashCode() + ") attempted to return to the pool, discarding connection.");
                poolState.badConnectionCount++;
            }
        }
    }

    protected PooledConnection popConnection(String username, String password) throws SQLException {
        PooledConnection pooledConnection = null;
        long beginTime = System.currentTimeMillis();
        int localBadConnectionCount = 0;
        boolean countedWait = false;

        while (Objects.isNull(pooledConnection)) {
            synchronized (poolState) {
                List<PooledConnection> idleConnections = poolState.getIdleConnections();
                // 有空闲连接，直接使用
                if (!idleConnections.isEmpty()) {
                    pooledConnection = idleConnections.get(0);
                    log.info("Checked out connection " + pooledConnection.getRealHashCode() + " from pool.");
                    // 无空闲连接
                } else {
                    List<PooledConnection> activeConnections = poolState.getActiveConnections();
                    // 检查活跃连接数未达到最大值，创建新的连接
                    if (activeConnections.size() < poolMaximumActiveConnections) {
                        pooledConnection = new PooledConnection(dataSource.getConnection(), this);
                        log.info("Created connection " + pooledConnection.getRealHashCode() + ".");
                        // 活跃连接数达到最大值，检查最老连接的连接时间是否超过checkoutTime最大值
                    } else {
                        PooledConnection oldestActiveConnection = poolState.getActiveConnections().get(0);
                        long checkoutTimeStamp = oldestActiveConnection.getCheckoutTime();
                        // 已超过checkoutTime, 将最老连接失效，创建新连接；
                        if (checkoutTimeStamp > poolMaximumCheckoutTime) {
                            poolState.claimedOverdueConnectionCount++;
                            poolState.accumulatedCheckoutTime += checkoutTimeStamp;
                            poolState.accumulatedCheckoutTimeOfOverdueConnections += checkoutTimeStamp;
                            poolState.getActiveConnections().remove(oldestActiveConnection);
                            if (!oldestActiveConnection.getRealConnection().getAutoCommit()) {
                                oldestActiveConnection.getRealConnection().rollback();
                            }
                            /*
                             这里失效的仅仅是oldestActiveConnection，而oldestActiveConnection的
                             realConnection是依然有效的，所以我们可以直接使用；
                             */
                            pooledConnection = new PooledConnection(oldestActiveConnection.getRealConnection(), this);
                            oldestActiveConnection.invalidate();
                            log.info("Claimed overdue connection " + pooledConnection.getRealHashCode() + ".");
                            // 未达到checkoutTime()最大值
                        } else {
                            try {
                                if (!countedWait) {
                                    poolState.hadToWaitCount++;
                                    countedWait = true;
                                }
                                log.info("Waiting as long as " + poolTimeToWait + " milliseconds for connection.");
                                long wt = System.currentTimeMillis();
                                poolState.wait(poolTimeToWait);
                                poolState.accumulatedWaitTime += System.currentTimeMillis() - wt;
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }
                }

                if (Objects.nonNull(pooledConnection)) {
                    if (pooledConnection.isValid()) {
                        if (!pooledConnection.getRealConnection().getAutoCommit()) {
                            pooledConnection.getRealConnection().rollback();
                        }
                        pooledConnection.setConnectionTypeCode(assembleConnectionTypeCode(dataSource.getUrl(), username, password));
                        pooledConnection.setCheckoutTimeStamp(System.currentTimeMillis());
                        pooledConnection.setLastUsedTimeStamp(System.currentTimeMillis());
                        poolState.activeConnections.add(pooledConnection);
                        poolState.requestCount++;
                        poolState.accumulatedRequestTime += System.currentTimeMillis() - beginTime;
                    } else {
                        log.info("A bad connection (" + pooledConnection.getRealHashCode() + ") was returned from the pool, getting another connection.");
                        poolState.badConnectionCount++;
                        localBadConnectionCount++;
                        pooledConnection = null;
                        // 失败次数较多，抛异常
                        if (localBadConnectionCount > (poolMaximumIdleConnections + 3)) {
                            log.debug("PooledDataSource: Could not get a good connection to the database.");
                            throw new SQLException("PooledDataSource: Could not get a good connection to the database.");
                        }
                    }
                }
            }
        }
        if (pooledConnection == null) {
            log.debug("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
            throw new SQLException("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
        }

        return pooledConnection;
    }

    private int assembleConnectionTypeCode(String url, String username, String password) {
        return StrUtil.join("", url, username, username).hashCode();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return popConnection(dataSource.getUsername(), dataSource.getPassword()).getProxyConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return popConnection(username, password).getProxyConnection();
    }

    @Override
    protected void finalize() throws Throwable {
        forceCloseAll();
        super.finalize();
    }

    private void forceCloseAll() {
        synchronized (poolState) {
            expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
            for (int i = poolState.getActiveConnections().size(); i > 0; i--) {
                PooledConnection connection = poolState.getActiveConnections().get(i);
                connection.invalidate();
                Connection realConnection = connection.getRealConnection();
                try {
                    if (!realConnection.getAutoCommit()) {
                        realConnection.rollback();
                    }
                    realConnection.close();
                } catch (Exception e) {
                }
            }

            List<PooledConnection> idleConnections = poolState.getIdleConnections();
            for (int i = idleConnections.size(); i > 0; i--) {
                PooledConnection idleConnection = idleConnections.get(i);
                idleConnection.invalidate();

                Connection realConnection = idleConnection.getRealConnection();
                try {
                    if (!realConnection.getAutoCommit()) {
                        realConnection.rollback();
                    }
                } catch (Exception e) {
                }
            }
            log.info("PooledDataSource forcefully closed/removed all connections.");
        }
    }

    protected void pingConnection(PooledConnection connection) {
        boolean result = true;
        Connection realConnection = connection.getRealConnection();
        try {
            result = !realConnection.isClosed();
        } catch (Exception e) {
            log.info("Connection " + connection.getRealHashCode() + " is BAD: " + e.getMessage());
            result = false;
        }

        if (result) {
            if (poolPingEnabled) {
                if (poolPingConnectionsNotUsedFor >= 0 &&
                        connection.getTimeElapsedSinceLastUse() > poolPingConnectionsNotUsedFor) {
                    try {
                        log.info("Testing connection " + connection.getRealHashCode() + " ...");
                        Statement statement = realConnection.createStatement();
                        ResultSet resultSet = statement.executeQuery(poolPingQuery);
                        resultSet.close();
                        if (realConnection.getAutoCommit()) {
                            realConnection.rollback();
                        }
                        result = true;
                        log.info("Connection " + connection.getRealHashCode() + " is GOOD!");
                    } catch (Exception e) {

                    }
                }
            }
        }
    }

    protected void setDriver(String driver) {
        this.dataSource.setDriver(driver);
    }

    protected void setUrl(String url) {
        this.dataSource.setUrl(url);
    }

    protected void setUsername(String username) {
        this.dataSource.setUsername(username);
    }

    protected void setPassword(String password) {
        this.dataSource.setPassword(password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException(getClass().getName() + " is not a wrapper.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }
}
