package practice.zhuang.mybatis.executor.statement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author: ZhuangZG
 * @date 2023/2/9 15:39
 * @Description:
 */
public interface StatementHandler {

    Statement prepareStatement(Connection connection) throws SQLException;

    void parameterize(Statement statement) throws SQLException;

    ResultSet query(Statement statement) throws SQLException;

}
