package practice.zhuang.mybatis.session;

import practice.zhuang.mybatis.binding.MapperRegistry;
import practice.zhuang.mybatis.mapping.MappedStatement;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: ZhuangZG
 * @date 2023/1/10 14:25
 * @Description:
 */
public class Configuration {

    private MapperRegistry mapperRegistry = new MapperRegistry();
    private Map<String, MappedStatement> mappedStatements = new HashMap<>();

    public void addMapper(Class<?> mapperInterface) {
        mapperRegistry.addMapper(mapperInterface);
    }

    public <T> T getMapper(Class<T> mapperInterface, SqlSession sqlSession) {
        return mapperRegistry.getMapper(mapperInterface, sqlSession);
    }

    public void addMappedStatement(MappedStatement mappedStatement) {
        mappedStatements.put(mappedStatement.getId(), mappedStatement);
    }

    public MappedStatement getMappedStatement(String statementId) {
        return mappedStatements.get(statementId);
    }


}
