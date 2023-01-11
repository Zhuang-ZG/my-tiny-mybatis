package practice.zhuang.mybatis.session;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import practice.zhuang.entity.User;
import practice.zhuang.mapper.UserMapper;
import practice.zhuang.mybatis.io.Resources;

import java.io.IOException;
import java.io.Reader;

/**
 * @author: ZhuangZG
 * @date 2023/1/11 11:30
 * @Description:
 */
@Slf4j
class SqlSessionFactoryTest {

    @Test
    void testSqlSessionFactory() throws IOException {
        Reader reader = Resources.readAsReader("mybatis-config.xml");

        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(reader);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        String user = userMapper.find();

        log.info(user);
    }

}