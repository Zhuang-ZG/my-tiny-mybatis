package practice.zhuang.mybatis.transaction.jdbc;

import practice.zhuang.mybatis.transaction.AutoCommit;
import practice.zhuang.mybatis.transaction.Transaction;
import practice.zhuang.mybatis.transaction.TransactionIsolationEnum;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author: ZhuangZG
 * @date 2023/1/11 17:28
 * @Description:
 */
public class JdbcTransaction implements Transaction {

    private Connection connection;
    private DataSource dataSource;
    private AutoCommit autoCommit;
    private TransactionIsolationEnum transactionIsolation;

    public JdbcTransaction(Connection connection) {
        this.connection = connection;
    }

    public JdbcTransaction(DataSource dataSource, AutoCommit autoCommit, TransactionIsolationEnum transactionIsolation) {
        this.dataSource = dataSource;
        this.autoCommit = autoCommit;
        this.transactionIsolation = transactionIsolation;
        initConnection();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public void commit() throws SQLException {
        if (!connection.getAutoCommit()) {
            connection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (!connection.getAutoCommit()) {
            connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if (!connection.getAutoCommit()) {
            connection.close();
        }
    }

    private void initConnection() {
        if (Objects.isNull(this.connection)) {
            try {
                this.connection = dataSource.getConnection();
                this.connection.setAutoCommit(autoCommit.isAutoCommit());
                this.connection.setTransactionIsolation(transactionIsolation.getIsolationLevel());
            } catch (Exception e) {
                throw new RuntimeException("get connection from datasource failed");
            }
        }
    }
}
