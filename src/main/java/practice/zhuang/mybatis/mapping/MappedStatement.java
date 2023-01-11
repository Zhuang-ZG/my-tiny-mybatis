package practice.zhuang.mybatis.mapping;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author: ZhuangZG
 * @date 2023/1/10 15:36
 * @Description:
 */
@Data
@Builder
public class MappedStatement {

    private String id;
    private String resultType;
    private String parameterType;
    private Map<Integer, String> parameterMap;
    private String sql;
    private SqlCommandType sqlCommandType;
}
