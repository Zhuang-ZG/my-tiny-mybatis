package practice.zhuang.mybatis.transaction.jdbc;

import practice.zhuang.mybatis.datasource.DataSourceFactory;
import practice.zhuang.mybatis.transaction.AutoCommit;
import practice.zhuang.mybatis.transaction.Transaction;
import practice.zhuang.mybatis.transaction.TransactionFactory;
import practice.zhuang.mybatis.transaction.TransactionIsolationEnum;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author: ZhuangZG
 * @date 2023/1/11 17:28
 * @Description:
 */
public class JdbcTransactionFactory implements TransactionFactory {

    private Connection connection;
    private DataSource dataSource;
    private AutoCommit autoCommit;
    private TransactionIsolationEnum transactionIsolation;

    @Override
    public Transaction newTransaction(Connection connection) {
        return new JdbcTransaction(connection);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationEnum transactionIsolationEnum, AutoCommit autoCommit) {
        return new JdbcTransaction(dataSource, autoCommit, transactionIsolationEnum);
    }
}
