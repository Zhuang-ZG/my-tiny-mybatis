package practice.zhuang.mybatis.session.defaults;

import practice.zhuang.mybatis.binding.MapperRegistry;
import practice.zhuang.mybatis.session.SqlSession;
import practice.zhuang.mybatis.session.SqlSessionFactory;

/**
 * @author: ZhuangZG
 * @date 2023/1/10 13:28
 * @Description:
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private MapperRegistry mapperRegistry;

    public DefaultSqlSessionFactory(MapperRegistry mapperRegistry) {
        this.mapperRegistry = mapperRegistry;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(mapperRegistry);
    }
}
