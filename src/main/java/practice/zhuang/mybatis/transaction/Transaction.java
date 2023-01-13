package practice.zhuang.mybatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author: ZhuangZG
 * @date 2023/1/11 17:20
 * @Description:
 */
public interface Transaction {

    /**
     * get connection of datasource
     * @return
     * @throws SQLException
     */
    Connection getConnection() throws SQLException;
    /**
     * commit
     */
    void commit() throws SQLException;

    /**
     * rollback
     */
    void rollback() throws SQLException;
    /**
     * close
     */
    void close() throws SQLException;
}
