package com.gougou.service;

import com.gougou.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gougou.entity.User;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gougou
 * @since 2023-07-10
 */
public interface IMenuService extends IService<Menu> {
    public List<Menu> buildTreeMenu(List<Menu> menuList);
    public Set<Menu> getCurrentMenu(User currentuser);
}
