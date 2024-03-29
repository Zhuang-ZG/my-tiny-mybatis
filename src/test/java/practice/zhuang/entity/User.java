package practice.zhuang.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhuangZG
 * @date 2023/1/11 13:52
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    private String userId;

    private String userHead;

    private String userName;
}
