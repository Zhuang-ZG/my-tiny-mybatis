package practice.zhuang.mybatis.binding;

import cn.hutool.core.lang.hash.Hash;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import practice.zhuang.entity.UserMapper;

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
        HashMap<String, String> sqlSession = new HashMap<>() {{
            put("practice.zhuang.entity.UserMapper.find", "find OK");
        }};

        MapperProxyFactory<UserMapper> proxyFactory = new MapperProxyFactory<>(UserMapper.class);
        UserMapper userMapper = proxyFactory.newInstance(sqlSession);
        log.info(userMapper.find());
    }
}