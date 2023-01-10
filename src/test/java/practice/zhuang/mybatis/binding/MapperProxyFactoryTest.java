package practice.zhuang.mybatis.binding;

import cn.hutool.core.lang.hash.Hash;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import practice.zhuang.entity.UserMapper;
import practice.zhuang.mybatis.session.SqlSession;
import practice.zhuang.mybatis.session.defaults.DefaultSqlSession;
import practice.zhuang.mybatis.session.defaults.DefaultSqlSessionFactory;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author: ZhuangZG
 * @date 2023/1/5 19:13
 * @Description:
 */
@Slf4j
class MapperProxyFactoryTest {

    @Test
    void testProxy() {
        MapperRegistry mapperRegistry = new MapperRegistry();
        mapperRegistry.addMappers("practice.zhuang.entity");
        DefaultSqlSessionFactory defaultSqlSessionFactory = new DefaultSqlSessionFactory(mapperRegistry);
        SqlSession sqlSession = defaultSqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        log.info(userMapper.find());
    }
}