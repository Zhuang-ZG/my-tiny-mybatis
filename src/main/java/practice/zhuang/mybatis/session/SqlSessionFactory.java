package practice.zhuang.mybatis.session;

/**
 * @author: ZhuangZG
 * @date 2023/1/10 13:06
 * @Description:
 */
public interface SqlSessionFactory {

    SqlSession openSession();
}
