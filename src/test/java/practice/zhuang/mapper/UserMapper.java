package practice.zhuang.mapper;

import practice.zhuang.entity.User;

/**
 * @author: ZhuangZG
 * @date 2023/1/5 19:14
 * @Description:
 */
public interface UserMapper {

    User find(String id);

}
