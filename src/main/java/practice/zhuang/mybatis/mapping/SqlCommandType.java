package practice.zhuang.mybatis.mapping;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: ZhuangZG
 * @date 2023/1/10 15:02
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum SqlCommandType {

    INSERT("insert"),

    DELETE("delete"),

    UPDATE("update"),

    SELECT("select"),
    ;

    private String command;

}
