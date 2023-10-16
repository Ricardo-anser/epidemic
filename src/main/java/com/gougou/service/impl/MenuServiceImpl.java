package com.gougou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gougou.entity.Menu;
import com.gougou.entity.Role;
import com.gougou.entity.User;
import com.gougou.mapper.MenuMapper;
import com.gougou.service.IMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gougou.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author gougou
 * @since 2023-07-10
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {


    @Autowired
    IRoleService roleService;
    @Autowired
    IMenuService menuService;
    @Override
    public List<Menu> buildTreeMenu(List<Menu> menuList) {
        List<Menu> resultMenuList=new ArrayList<>();
        for (Menu menu:menuList
             ) {
            for (Menu e:menuList
                 ) {
                if (e.getParentId().equals(menu.getMenuId())){
                    menu.getChildren().add(e);
                }
            }
            if (menu.getParentId()==0L){
                resultMenuList.add(menu);
            }
        }
        return resultMenuList;
    }

    /**
     * 获取当前用户的登录菜单，获取角色后判断是否失效
     * 获取菜单后也判断是否失效
     * @param currentuser 当前用户对象
     * @return menu 去重集合
     */
    @Override
    public Set<Menu> getCurrentMenu(User currentuser) {
        LocalDateTime now=LocalDateTime.now();
        List<Role> roleList=roleService.list(new QueryWrapper<Role>().inSql("role_id","select role_id from sys_user_role where user_id="+currentuser.getUserId()));
        roleList.removeIf(role -> role.getStatus() == 0||role.getDisabledTime().compareTo(now)<=0);
        Set<Menu> menuSet=new HashSet<>();
        for (Role role:roleList
        ) {
            List<Menu> menuList = menuService.list(new QueryWrapper<Menu>().inSql("menu_id","select menu_id from sys_role_menu where role_id="+role.getRoleId()));
            for (Menu menu:menuList
            ) {
                if(menu.getStatus()==1) {
                    menuSet.add(menu);
                }
            }
        }
        return menuSet;
    }
}
