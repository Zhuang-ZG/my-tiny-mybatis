package practice.zhuang.mybatis.datasource.pooled;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author: ZhuangZG
 * @date 2023/2/2 10:43
 * @Description:
 */
public class PooledConnection implements InvocationHandler {

    private PooledDataSource dataSource;

    @Setter
    @Getter
    private long checkoutTimeStamp;
    @Getter
    @Setter
    private long createdTimeStamp;
    @Getter
    @Setter
    private long lastUsedTimeStamp;
    @Getter
    private boolean valid;
    @Getter
    @Setter
    private int connectionTypeCode;


    private final static String CLOSE = "close";
    private final static Class<?>[] INTERFACE = new Class<?>[]{Connection.class};
    @Setter
    @Getter
    private Connection realConnection;
    @Getter
    @Setter
    private Connection proxyConnection;

    private int hashCode = 0;

    public PooledConnection(Connection connection, PooledDataSource dataSource) {
        this.hashCode = connection.hashCode();
        this.realConnection = connection;
        this.dataSource = dataSource;
        this.createdTimeStamp = System.currentTimeMillis();
        this.lastUsedTimeStamp = System.currentTimeMillis();
        this.valid = true;
        this.proxyConnection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), INTERFACE, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (CLOSE.hashCode() == methodName.hashCode() && CLOSE.equals(methodName)) {
            dataSource.pushConnection(this);
            return null;
        } else {
            if (!Object.class.equals(method.getDeclaringClass())) {
                checkConnection();
            }
            return method.invoke(realConnection, args);
        }
    }

    private void checkConnection() throws SQLException {
        if (!valid) {
            throw new SQLException("Error accessing PooledConnection. Connection is invalid.");
        }
    }

    public int getRealHashCode() {
        return Objects.isNull(realConnection) ? 0 : realConnection.hashCode();
    }

    public long getCheckoutTime() {
        return System.currentTimeMillis() - checkoutTimeStamp;
    }

    public void invalidate() {
        valid = false;
    }

    public long getTimeElapsedSinceLastUse() {
        return System.currentTimeMillis() - lastUsedTimeStamp;
    }
}
