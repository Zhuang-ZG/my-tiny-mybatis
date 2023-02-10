package practice.zhuang.mybatis.executor.statement;

import practice.zhuang.mybatis.mapping.BoundSql;
import practice.zhuang.mybatis.mapping.MappedStatement;
import practice.zhuang.mybatis.session.Configuration;

import javax.sql.rowset.RowSetWarning;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author: ZhuangZG
 * @date 2023/2/9 16:12
 * @Description:
 */
public abstract class BaseStatementHandler implements StatementHandler {

    protected MappedStatement mappedStatement;
    protected Object parameter;
    protected Configuration configuration;

    public BaseStatementHandler(MappedStatement mappedStatement, Object parameter, Configuration configuration) {
        this.mappedStatement = mappedStatement;
        this.parameter = parameter;
        this.configuration = configuration;
    }

    @Override
    public Statement prepareStatement(Connection connection) throws SQLException {
        Statement statement = null;
        try {
            statement = initializeStatement(connection);
            statement.setQueryTimeout(350);
            statement.setFetchSize(10000);
            return statement;
        } catch (Exception e) {
            throw new RuntimeException("prepare statement failed");
        }
    }

    protected abstract Statement initializeStatement(Connection connection) throws SQLException;
}
