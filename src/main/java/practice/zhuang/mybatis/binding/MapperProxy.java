package practice.zhuang.mybatis.binding;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import practice.zhuang.mybatis.session.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: ZhuangZG
 * @date 2023/1/5 19:04
 * @Description:
 */
@Slf4j
public class MapperProxy implements InvocationHandler {

    private Class<?> mapperInterface;
    private SqlSession sqlSession;
    private Map<Method, MapperMethod> cacheMethods = new ConcurrentHashMap<>();

    public MapperProxy(Class<?> mapperInterface, SqlSession sqlSession) {
        this.mapperInterface = mapperInterface;
        this.sqlSession = sqlSession;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class)) {
            method.invoke(this, args);
        }
        MapperMethod mapperMethod = cacheMethod(method);
        return mapperMethod.execute(sqlSession, args);
    }

    public MapperMethod cacheMethod(Method method) {

        MapperMethod mapperMethod = cacheMethods.get(method);
        if (Objects.isNull(mapperMethod)) {
            mapperMethod = new MapperMethod(method, sqlSession.getConfiguration(), mapperInterface);
        }
        return mapperMethod;
    }
}
