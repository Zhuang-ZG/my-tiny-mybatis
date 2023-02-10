package practice.zhuang.mybatis.executor;

import practice.zhuang.mybatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * @author: ZhuangZG
 * @date 2023/2/9 15:39
 * @Description:
 */
public interface Executor {

    Transaction getTransaction();

    <E> List<E> query(String statementId, Object parameter);

    void commit(boolean required) throws SQLException;

    void rollback(boolean required) throws SQLException;

    void close(boolean forceRollback);
}
