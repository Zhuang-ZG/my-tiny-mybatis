package practice.zhuang.mybatis.session.defaults;

import cn.hutool.core.util.StrUtil;
import practice.zhuang.mybatis.mapping.BoundSql;
import practice.zhuang.mybatis.mapping.Environment;
import practice.zhuang.mybatis.mapping.MappedStatement;
import practice.zhuang.mybatis.session.Configuration;
import practice.zhuang.mybatis.session.SqlSession;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author: ZhuangZG
 * @date 2023/1/10 13:25
 * @Description:
 */
public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> T selectOne(String statementId) {
        MappedStatement statement = configuration.getMappedStatement(statementId);
        return (T) ("你被代理了！" + "\n方法：" + statement);
    }

    @Override
    public <T> T selectOne(String statementId, Object parameter) {
        try {
            MappedStatement statement = configuration.getMappedStatement(statementId);
            Environment environment = configuration.getEnvironment();

            Connection connection = environment.getDataSource().getConnection();

            BoundSql boundSql = statement.getBoundSql();
            PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());
            preparedStatement.setString(1, ((Object[]) parameter)[0].toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            List<T> objects = result2Object(resultSet, Class.forName(boundSql.getResultType()));

            return objects.get(0);

        } catch (Exception e) {
            throw new RuntimeException("execute sql failed.", e);
        }
    }

    private <T> List<T> result2Object(ResultSet resultSet, Class<?> clazz) {
        ArrayList<T> objects = new ArrayList<>();
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                T newInstance = (T) clazz.getDeclaredConstructor().newInstance();
                for (int i = 0; i < columnCount; i++) {
                    int columnIndex = i + 1;
                    Object columnValue = resultSet.getObject(columnIndex);
                    String columnName = StrUtil.toCamelCase(metaData.getColumnName(columnIndex));
                    String setMethodName = "set" + columnName.substring(0, 1).toUpperCase(Locale.ENGLISH) + columnName.substring(1);
                    Method method;
                    if (columnValue instanceof Timestamp) {
                        method = clazz.getMethod(setMethodName, Date.class);
                    } else {
                        method = clazz.getMethod(setMethodName, columnValue.getClass());
                    }
                    method.invoke(newInstance, columnValue);
                }
                objects.add(newInstance);
            }
        } catch (Exception e) {
            throw new RuntimeException("convert resultSet to Object failed.");
        }
        return objects;
    }

    @Override
    public <T> T getMapper(Class<T> mapperInterface) {
        return configuration.getMapper(mapperInterface, this);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }
}
