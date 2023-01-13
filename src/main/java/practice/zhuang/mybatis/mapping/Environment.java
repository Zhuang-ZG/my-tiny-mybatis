package practice.zhuang.mybatis.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import practice.zhuang.mybatis.datasource.DataSourceFactory;
import practice.zhuang.mybatis.transaction.TransactionFactory;

import javax.sql.DataSource;

/**
 * @author: ZhuangZG
 * @date 2023/1/11 16:55
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class Environment {

    private String id;

    private DataSource dataSource;

    private TransactionFactory transactionManager;
}
