package practice.zhuang.mybatis.session;

/**
 * @author: ZhuangZG
 * @date 2023/1/10 13:02
 * @Description:
 */
public interface SqlSession {

    <T> T selectOne(String statementId);

    <T> T selectOne(String statement, Object parameter);

    <T> T getMapper(Class<T> mapperInterface);
}
