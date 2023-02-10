package practice.zhuang.mybatis.executor.statement;

import practice.zhuang.mybatis.executor.resultset.DefaultResultSetHandler;
import practice.zhuang.mybatis.executor.resultset.ResultSetHandler;
import practice.zhuang.mybatis.mapping.BoundSql;
import practice.zhuang.mybatis.mapping.MappedStatement;
import practice.zhuang.mybatis.session.Configuration;

import java.sql.*;
import java.util.List;

/**
 * @author: ZhuangZG
 * @date 2023/2/9 16:17
 * @Description:
 */
public class PreparedStatementHandler extends BaseStatementHandler {

    public PreparedStatementHandler(MappedStatement mappedStatement, Object parameter, Configuration configuration) {
        super(mappedStatement, parameter, configuration);
    }

    @Override
    protected Statement initializeStatement(Connection connection) throws SQLException {
        BoundSql boundSql = mappedStatement.getBoundSql();
        return connection.prepareStatement(boundSql.getSql());
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.setString(1, ((Object[])parameter)[0].toString());
    }

    @Override
    public ResultSet query(Statement statement) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        return ps.executeQuery();
    }
}
