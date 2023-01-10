package practice.zhuang.mybatis.binding;

import cn.hutool.core.util.ClassUtil;
import practice.zhuang.mybatis.session.SqlSession;

import javax.sound.midi.MidiFileFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author: ZhuangZG
 * @date 2023/1/10 11:12
 * @Description:
 */
public class MapperRegistry {

    private Map<Class<?>, MapperProxyFactory> knownMappers = new ConcurrentHashMap<>();

    public <T> T getMapper(Class<T> mapperInterface, SqlSession sqlSession) {
        MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>)knownMappers.get(mapperInterface);
        if (Objects.isNull(mapperProxyFactory)) {
            throw new RuntimeException("proxy of " + mapperInterface.getName() + " is not exists");
        }
        return mapperProxyFactory.newInstance(sqlSession);
    }

    public void addMapper(Class<?> mapperInterface){
        if (Objects.isNull(mapperInterface)) {
            throw new RuntimeException("mapper class could not be null");
        }
        MapperProxyFactory mapperProxyFactory = knownMappers.get(mapperInterface);
        if (Objects.isNull(mapperProxyFactory)) {
            if (mapperInterface.isInterface()) {
                mapperProxyFactory = new MapperProxyFactory<>(mapperInterface);
                knownMappers.put(mapperInterface, mapperProxyFactory);
            }
        } else {
            throw new RuntimeException("mapper proxy of " + mapperInterface.getName() + " is already exists");
        }
    }

    public void addMappers(String packageName) {
        Set<Class<?>> classes = ClassUtil.scanPackage(packageName);
        classes.forEach(c -> {
            if (c.isInterface()) {
                addMapper(c);
            }
        });
    }
}
