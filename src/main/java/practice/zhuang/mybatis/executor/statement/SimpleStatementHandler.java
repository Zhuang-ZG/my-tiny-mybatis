package practice.zhuang.mybatis.executor.statement;

import practice.zhuang.mybatis.executor.resultset.DefaultResultSetHandler;
import practice.zhuang.mybatis.executor.resultset.ResultSetHandler;
import practice.zhuang.mybatis.mapping.MappedStatement;
import practice.zhuang.mybatis.session.Configuration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author: ZhuangZG
 * @date 2023/2/9 16:25
 * @Description:
 */
public class SimpleStatementHandler extends BaseStatementHandler {

    public SimpleStatementHandler(MappedStatement mappedStatement, Object parameter, Configuration configuration) {
        super(mappedStatement, parameter, configuration);
    }

    @Override
    protected Statement initializeStatement(Connection connection) throws SQLException {
        return connection.createStatement();
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {
        // no need to parameterize
    }

    @Override
    public ResultSet query(Statement statement) throws SQLException {
        statement.execute(mappedStatement.getBoundSql().getSql());
        return statement.getResultSet();
    }
}
