package com.gougou.common.security;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.gougou.common.result.Result;
import com.gougou.entity.Menu;
import com.gougou.entity.Role;
import com.gougou.entity.User;
import com.gougou.service.IMenuService;
import com.gougou.service.IRoleService;
import com.gougou.service.IUserService;
import com.gougou.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author 作者:gougou
 * @version 创建时间：2023/7/11 13:16
 * <p>
 * 类说明
 */
@Slf4j
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    IUserService userService;
    @Autowired
    IRoleService roleService;
    @Autowired
    IMenuService menuService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream =
                httpServletResponse.getOutputStream();
        String username= authentication.getName();
//        更新用户最后登录记录
        userService.update(new UpdateWrapper<User>().set("login_date",new Date()).eq("username",username));
//        创建currentuser对象
        User currentuser= userService.getUserByName(username);
//        颁发token
        log.info(username);
        String token = JwtUtils.genJwtToken(username);
        log.info(token);
//        构建去重菜单列表
        Set<Menu> menuSet=menuService.getCurrentMenu(currentuser);
//        排序
        List<Menu> unSortedMenuList=new ArrayList<>(menuSet);
        unSortedMenuList.sort(Comparator.comparing(Menu::getOrderNum));
//        目录树
        List<Menu> menuList=menuService.buildTreeMenu(unSortedMenuList);
//        权限菜单列表返回
        Map<String,Object> res=new HashMap<>();
        res.put("token",token);
        res.put("currentuser",currentuser);
        res.put("menuList",menuList);
        res.put("permsList",unSortedMenuList);
        outputStream.write(JSONUtil.toJsonStr(Result.success(res)).getBytes());
        outputStream.flush();
        outputStream.close();
    }

}
