package practice.zhuang.mybatis.executor.resultset;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import practice.zhuang.mybatis.mapping.BoundSql;
import practice.zhuang.mybatis.mapping.MappedStatement;

import javax.xml.transform.Result;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author: ZhuangZG
 * @date 2023/2/9 16:36
 * @Description:
 */
@Slf4j
public class DefaultResultSetHandler implements ResultSetHandler {

    protected ResultSet resultSet;
    protected BoundSql boundSql;

    public DefaultResultSetHandler(ResultSet resultSet, BoundSql boundSql) {
        this.resultSet = resultSet;
        this.boundSql = boundSql;
    }

    @Override
    public <E> List<E> handleResultSet() throws SQLException {
        String resultType = boundSql.getResultType();
        try {
            Class<?> clazz = Class.forName(resultType);
            return result2Obj(resultSet, clazz);
        } catch (ClassNotFoundException e) {
            log.warn("could not found class: " + resultType);
            return null;
        }
    }

    public <E> List<E> result2Obj(ResultSet resultSet, Class<?> clazz) {
        List<E> result = new ArrayList<>();
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                E instance = (E) clazz.getDeclaredConstructor().newInstance();
                for (int i = 0; i < columnCount; i++) {
                    int columnIndex = i + 1;
                    Object value = resultSet.getObject(columnIndex);
                    String columnName = StrUtil.toCamelCase(metaData.getColumnName(columnIndex));
                    String setterName = "set" + columnName.substring(0,1).toUpperCase(Locale.ENGLISH) + columnName.substring(1);
                    Method setter;
                    if (value instanceof Date) {
                        setter = clazz.getMethod(setterName, Timestamp.class);
                    } else {
                        setter = clazz.getMethod(setterName, value.getClass());
                    }
                    setter.invoke(instance, value);
                }
                result.add(instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
