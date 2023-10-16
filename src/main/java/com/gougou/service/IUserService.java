package com.gougou.service;

import com.gougou.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gougou
 * @since 2023-07-10
 */
public interface IUserService extends IService<User> {
     User getUserByName(String username);

}
