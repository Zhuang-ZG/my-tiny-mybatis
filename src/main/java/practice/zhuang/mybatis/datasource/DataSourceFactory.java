package practice.zhuang.mybatis.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author: ZhuangZG
 * @date 2023/1/11 16:55
 * @Description:
 */
public interface DataSourceFactory {

    /**
     * set datasource properties
     * @param properties
     * @return
     */
    void setProperties(Properties properties);

    /**
     * get datasource instance
     * @return
     */
    DataSource getDataSource();
}
