package practice.zhuang.mybatis.session.defaults;

import practice.zhuang.mybatis.binding.MapperRegistry;
import practice.zhuang.mybatis.session.SqlSession;

/**
 * @author: ZhuangZG
 * @date 2023/1/10 13:25
 * @Description:
 */
public class DefaultSqlSession implements SqlSession {

    private MapperRegistry mapperRegistry;

    public DefaultSqlSession(MapperRegistry mapperRegistry) {
        this.mapperRegistry = mapperRegistry;
    }

    @Override
    public <T> T selectOne(String statementId) {
        return (T)("被代理 " + statementId);
    }

    @Override
    public <T> T selectOne(String statementId, Object parameter) {
        return (T)("被代理 " + statementId);
    }

    @Override
    public <T> T getMapper(Class<T> mapperInterface) {
        return mapperRegistry.getMapper(mapperInterface, this);
    }
}
