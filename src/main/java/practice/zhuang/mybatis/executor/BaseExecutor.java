package practice.zhuang.mybatis.executor;

import lombok.extern.slf4j.Slf4j;
import practice.zhuang.mybatis.session.Configuration;
import practice.zhuang.mybatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * @author: ZhuangZG
 * @date 2023/2/9 15:52
 * @Description:
 */
@Slf4j
public abstract class BaseExecutor implements Executor {

    protected Configuration configuration;
    protected Transaction transaction;

    public BaseExecutor(Configuration configuration, Transaction transaction) {
        this.configuration = configuration;
        this.transaction = transaction;
    }

    protected boolean closed;

    @Override
    public Transaction getTransaction() {
        verifyExecutorStatus();
        return this.transaction;
    }

    @Override
    public <E> List<E> query(String statementId, Object parameter) {
        verifyExecutorStatus();
        return doQuery(statementId, parameter);
    }

    @Override
    public void commit(boolean required) throws SQLException {
        verifyExecutorStatus();
        if (required) {
            this.transaction.commit();
        }
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        verifyExecutorStatus();
        if (required) {
            this.transaction.rollback();
        }
    }

    @Override
    public void close(boolean forceRollback) {
        if (!isClosed()) {
            if (forceRollback) {
                try {
                    this.transaction.close();
                } catch (Exception e){
                    log.warn("close connection failed..");
                } finally {
                    transaction = null;
                    closed = true;
                }
            }
        }
    }

    protected abstract <E> List<E> doQuery(String statementId, Object parameter);

    protected void verifyExecutorStatus() {
        if (isClosed()) {
            throw new RuntimeException("Executor is already closed");
        }
    }

    protected boolean isClosed() {
        return closed;
    }
}
