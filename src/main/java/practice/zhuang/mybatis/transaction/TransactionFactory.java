package practice.zhuang.mybatis.transaction;

import practice.zhuang.mybatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author: ZhuangZG
 * @date 2023/1/11 17:18
 * @Description:
 */
public interface TransactionFactory {

    /**
     * generate transaction
     * @param connection
     * @return
     */
    Transaction newTransaction(Connection connection);

    /**
     * generate transaction
     * @param dataSource
     * @param transactionIsolationEnum
     * @param autoCommit
     * @return
     */
    Transaction newTransaction(DataSource dataSource,
                               TransactionIsolationEnum transactionIsolationEnum,
                               AutoCommit autoCommit);
}
