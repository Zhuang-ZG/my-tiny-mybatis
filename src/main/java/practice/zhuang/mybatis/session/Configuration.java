package practice.zhuang.mybatis.session;

import practice.zhuang.mybatis.binding.MapperRegistry;
import practice.zhuang.mybatis.datasource.DataSourceFactory;
import practice.zhuang.mybatis.datasource.druid.DruidDataSourceFactory;
import practice.zhuang.mybatis.mapping.Environment;
import practice.zhuang.mybatis.mapping.MappedStatement;
import practice.zhuang.mybatis.transaction.Transaction;
import practice.zhuang.mybatis.transaction.TransactionFactory;
import practice.zhuang.mybatis.transaction.jdbc.JdbcTransactionFactory;
import practice.zhuang.mybatis.type.TypeAliasesRegistry;

import javax.sql.DataSource;
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

    private Environment environment = new Environment();

    private TypeAliasesRegistry typeAliasesRegistry = new TypeAliasesRegistry();

    public Configuration() {
        typeAliasesRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasesRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);
    }

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

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public TypeAliasesRegistry getTypeAliasesRegistry() {
        return typeAliasesRegistry;
    }
}
