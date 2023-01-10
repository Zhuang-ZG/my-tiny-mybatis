package practice.zhuang.mybatis.binding;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import practice.zhuang.mybatis.session.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: ZhuangZG
 * @date 2023/1/5 19:04
 * @Description:
 */
@Slf4j
public class MapperProxy implements InvocationHandler {

    private Class<?> mapperInterface;
    private SqlSession sqlSession;

    public MapperProxy(Class<?> mapperInterface, SqlSession sqlSession) {
        this.mapperInterface = mapperInterface;
        this.sqlSession = sqlSession;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class)) {
            method.invoke(this, args);
        }
        String methodName = StrUtil.join(StrUtil.DOT, mapperInterface.getName(), method.getName());
        return sqlSession.selectOne(methodName);
    }
}
