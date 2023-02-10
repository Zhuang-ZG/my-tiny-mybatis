package practice.zhuang.mybatis.session.defaults;

import practice.zhuang.mybatis.executor.Executor;
import practice.zhuang.mybatis.executor.SimpleExecutor;
import practice.zhuang.mybatis.mapping.Environment;
import practice.zhuang.mybatis.session.Configuration;
import practice.zhuang.mybatis.session.SqlSession;
import practice.zhuang.mybatis.session.SqlSessionFactory;
import practice.zhuang.mybatis.transaction.AutoCommit;
import practice.zhuang.mybatis.transaction.Transaction;
import practice.zhuang.mybatis.transaction.TransactionFactory;
import practice.zhuang.mybatis.transaction.TransactionIsolationEnum;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author: ZhuangZG
 * @date 2023/1/10 13:28
 * @Description:
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        Transaction transaction= null;
        try {
            final Environment environment = configuration.getEnvironment();
            TransactionFactory transactionFactory = environment.getTransactionFactory();
            AutoCommit autoCommit = new AutoCommit(false);
            transaction = transactionFactory.newTransaction(configuration.getEnvironment().getDataSource(), TransactionIsolationEnum.READ_COMMITTED, autoCommit);
            final Executor executor = new SimpleExecutor(configuration, transaction);
            return new DefaultSqlSession(configuration, executor);
        } catch (Exception e) {
            try {
                if (Objects.nonNull(transaction)) {
                    transaction.close();
                }
            } catch (SQLException sqlException) {
            }
            throw new RuntimeException("Error opening session.  Cause: " + e);
        }
    }
}
