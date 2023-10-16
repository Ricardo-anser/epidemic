package com.gougou.utils;

import com.gougou.entity.Menu;
import com.gougou.entity.User;
import com.gougou.service.IMenuService;
import com.gougou.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author 作者:gougou
 * @version 创建时间：2023/8/3 16:56
 * <p>
 * 类说明
 */
@Component
public class PermsDynamicUtils {
    @Resource
    IMenuService menuService;
    @Resource
    IUserService userService;

    public Map<String,Object> returnPackageTreeandPerms(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username=null;
        if(authentication.isAuthenticated()){
            username= authentication.getName();
        }
        System.out.println(username+"---------------------------------");
        User currentuser=userService.getUserByName(username);
        Set<Menu> menuSet=menuService.getCurrentMenu(currentuser);
        List<Menu> permList=new ArrayList<>(menuSet);
        List<Menu> menuList=menuService.buildTreeMenu(permList);
        Map<String,Object> data=new HashMap<>();
        data.put("permList",permList);
        data.put("menuList",menuList);
        return data;
    }
}
