package practice.zhuang.mybatis.binding;

import cn.hutool.core.util.StrUtil;
import practice.zhuang.mybatis.mapping.MappedStatement;
import practice.zhuang.mybatis.mapping.SqlCommandType;
import practice.zhuang.mybatis.session.Configuration;
import practice.zhuang.mybatis.session.SqlSession;

import java.lang.reflect.Method;

/**
 * @author: ZhuangZG
 * @date 2023/1/12 15:22
 * @Description:
 */
public class MapperMethod {

    private SqlCommand sqlCommand;

    public MapperMethod(Method method, Configuration configuration, Class<?> mapperInterface) {
        sqlCommand = new SqlCommand(method, configuration, mapperInterface);
    }

    public Object execute(SqlSession sqlSession, Object[] args) {
        Object result = null;
        switch (sqlCommand.getSqlCommandType()) {
            case INSERT:
                break;
            case DELETE:
                break;
            case UPDATE:
                break;
            case SELECT:
                result = sqlSession.selectOne(sqlCommand.getCommandName(), args);
                break;
            default:
                throw new RuntimeException("unsupported SQL command");
        }
        return result;
    }

    private static class SqlCommand {
        private String commandName;
        private SqlCommandType sqlCommandType;

        public SqlCommand(Method method, Configuration configuration, Class<?> mapperInterface) {
            String methodName = StrUtil.join(StrUtil.DOT, mapperInterface.getName(), method.getName());
            MappedStatement mappedStatement = configuration.getMappedStatement(methodName);
            commandName = mappedStatement.getId();
            sqlCommandType = mappedStatement.getSqlCommandType();
        }

        public String getCommandName() {
            return commandName;
        }

        public SqlCommandType getSqlCommandType() {
            return sqlCommandType;
        }
    }
}
