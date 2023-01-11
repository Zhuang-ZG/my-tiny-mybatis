package practice.zhuang.mybatis.session.defaults;

import practice.zhuang.mybatis.binding.MapperRegistry;
import practice.zhuang.mybatis.mapping.MappedStatement;
import practice.zhuang.mybatis.session.Configuration;
import practice.zhuang.mybatis.session.SqlSession;

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
        MappedStatement statement = configuration.getMappedStatement(statementId);
        return (T) ("你被代理了！" + "\n方法：" + statement);
    }

    @Override
    public <T> T getMapper(Class<T> mapperInterface) {
        return configuration.getMapper(mapperInterface, this);
    }
}
