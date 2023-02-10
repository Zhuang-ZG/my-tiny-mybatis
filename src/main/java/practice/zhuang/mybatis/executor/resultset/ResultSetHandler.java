package practice.zhuang.mybatis.executor.resultset;

import practice.zhuang.mybatis.mapping.MappedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author: ZhuangZG
 * @date 2023/2/9 15:38
 * @Description:
 */
public interface ResultSetHandler {

    <E> List<E> handleResultSet() throws SQLException;
}
