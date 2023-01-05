package practice.zhuang.mybatis.binding;

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

    public T newInstance(Map<String, String> sqlSession) {
        MapperProxy proxy = new MapperProxy(mapperInterface, sqlSession);
        T newProxyInstance = (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{mapperInterface}, proxy);
        return newProxyInstance;
    }

}
