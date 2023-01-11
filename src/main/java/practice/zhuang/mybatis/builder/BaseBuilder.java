package practice.zhuang.mybatis.builder;

import practice.zhuang.mybatis.session.Configuration;

/**
 * @author: ZhuangZG
 * @date 2023/1/10 14:24
 * @Description:
 */
public abstract class BaseBuilder {

    protected final Configuration configuration;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    protected Configuration getConfiguration() {
        return configuration;
    }
}
