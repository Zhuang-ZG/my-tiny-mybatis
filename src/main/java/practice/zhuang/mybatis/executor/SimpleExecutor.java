package practice.zhuang.mybatis.executor;

import lombok.extern.slf4j.Slf4j;
import practice.zhuang.mybatis.executor.resultset.DefaultResultSetHandler;
import practice.zhuang.mybatis.executor.resultset.ResultSetHandler;
import practice.zhuang.mybatis.executor.statement.PreparedStatementHandler;
import practice.zhuang.mybatis.mapping.MappedStatement;
import practice.zhuang.mybatis.session.Configuration;
import practice.zhuang.mybatis.transaction.Transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author: ZhuangZG
 * @date 2023/2/9 16:00
 * @Description:
 */
@Slf4j
public class SimpleExecutor extends BaseExecutor {

    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    protected <E> List<E> doQuery(String statementId, Object parameter) {
        try {
            MappedStatement mappedStatement = configuration.getMappedStatement(statementId);
            PreparedStatementHandler preparedStatementHandler = new PreparedStatementHandler(mappedStatement, parameter, configuration);
            Statement statement = preparedStatementHandler.prepareStatement(getConnection());
            preparedStatementHandler.parameterize(statement);
            ResultSet resultSet = preparedStatementHandler.query(statement);

            ResultSetHandler resultSetHandler = new DefaultResultSetHandler(resultSet, mappedStatement.getBoundSql());
            return resultSetHandler.<E>handleResultSet();
        } catch (SQLException e) {
            log.warn("execute sql statement failed...caused by " + e);
            return null;
        }
    }

    protected Connection getConnection() {
         try {
             return transaction.getConnection();
         } catch (SQLException e) {
             throw new RuntimeException("could not establish database connection...");
         }
    }
}
