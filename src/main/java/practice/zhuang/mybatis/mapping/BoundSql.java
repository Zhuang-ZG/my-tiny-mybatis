package practice.zhuang.mybatis.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author: ZhuangZG
 * @date 2023/1/12 13:16
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoundSql {

    private String resultType;
    private String parameterType;
    private Map<Integer, String> parameterMap;
    private String sql;
}
