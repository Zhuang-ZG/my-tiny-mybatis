package practice.zhuang.mybatis.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhuangZG
 * @date 2023/1/11 17:22
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class AutoCommit {

    private boolean autoCommit;

}
