package practice.zhuang.mybatis.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.JDBCType;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: ZhuangZG
 * @date 2023/1/12 10:50
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum JdbcType {

    INTEGER(Types.INTEGER),
    FLOAT(Types.FLOAT),
    DOUBLE(Types.DOUBLE),
    DECIMAL(Types.DECIMAL),
    VARCHAR(Types.VARCHAR),
    TIMESTAMP(Types.TIMESTAMP)
    ;

    private int type;

    private static Map<Integer, JdbcType> codeMap = new HashMap<>();
    static {
        Arrays.stream(JdbcType.values()).forEach(c ->{
            codeMap.put(c.getType(), c);
        });
    }

    public JdbcType lookup(Integer code) {
        return codeMap.get(code);
    }
}
