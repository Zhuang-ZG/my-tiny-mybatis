package practice.zhuang.mybatis.session.defaults;

import cn.hutool.core.util.StrUtil;
import practice.zhuang.mybatis.executor.Executor;
import practice.zhuang.mybatis.executor.SimpleExecutor;
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
    private Executor executor;

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    @Override
    public <T> T selectOne(String statementId) {
        MappedStatement statement = configuration.getMappedStatement(statementId);
        return (T) ("你被代理了！" + "\n方法：" + statement);
    }

    @Override
    public <T> T selectOne(String statementId, Object parameter) {
        List<T> query = executor.query(statementId, parameter);
        return query.get(0);
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
