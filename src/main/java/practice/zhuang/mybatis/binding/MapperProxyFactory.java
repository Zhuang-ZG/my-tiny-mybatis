package practice.zhuang.mybatis.binding;

import practice.zhuang.mybatis.session.SqlSession;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author: ZhuangZG
 * @date 2023/1/5 19:04
 * @Description:
 */
public class MapperProxyFactory<T> {

    private Class<T> mapperInterface;

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public T newInstance(SqlSession sqlSession) {
        MapperProxy proxy = new MapperProxy(mapperInterface, sqlSession);
        return  (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, proxy);
    }

}
