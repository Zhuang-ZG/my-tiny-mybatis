package practice.zhuang.mybatis.session;

import practice.zhuang.mybatis.builder.xml.XmlConfigurationBuilder;
import practice.zhuang.mybatis.session.defaults.DefaultSqlSessionFactory;

import java.io.Reader;

/**
 * @author: ZhuangZG
 * @date 2023/1/10 15:03
 * @Description:
 */
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(Reader reader) {
        XmlConfigurationBuilder xmlConfigurationBuilder = new XmlConfigurationBuilder(reader);
        return this.build(xmlConfigurationBuilder.parse());
    }

    public SqlSessionFactory build(Configuration configuration) {
        return new DefaultSqlSessionFactory(configuration);
    }
}
