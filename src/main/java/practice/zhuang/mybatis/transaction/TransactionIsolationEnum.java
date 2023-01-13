package practice.zhuang.mybatis.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Connection;

/**
 * @author: ZhuangZG
 * @date 2023/1/11 17:21
 * @Description:
 */
@AllArgsConstructor
@Getter
public enum TransactionIsolationEnum {

    /**
     * no transaction
     */
    NONE(Connection.TRANSACTION_NONE),
    READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
    READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
    REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
    SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE),
    ;

    private Integer isolationLevel;
}
