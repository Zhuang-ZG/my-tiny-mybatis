package practice.zhuang.mybatis.datasource.pooled;

import practice.zhuang.mybatis.datasource.unpooled.UnpooledDataSourceFactory;

import javax.sql.DataSource;

/**
 * @author: ZhuangZG
 * @date 2023/2/2 17:15
 * @Description:
 */
public class PooledDataSourceFactory extends UnpooledDataSourceFactory {

    public PooledDataSourceFactory() {
        this.dataSource = new PooledDataSource();
    }
}
