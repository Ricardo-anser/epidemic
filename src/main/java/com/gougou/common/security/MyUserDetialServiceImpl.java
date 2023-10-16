package com.gougou.common.security;

import com.gougou.common.exception.UserCountLockException;
import com.gougou.entity.User;
import com.gougou.service.IMenuService;
import com.gougou.service.IRoleService;
import com.gougou.service.IUserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 作者:gougou
 * @version 创建时间：2023/7/11 22:35
 * <p>
 * 类说明
 */
@Slf4j
@Service
public class MyUserDetialServiceImpl implements UserDetailsService {
    @Autowired
    IUserService userService;
    @Autowired
    IRoleService roleService;
    @Autowired
    IMenuService menuService;

    /**
     * 按用户名载入用户信息
     * @param username 用户名
     * @return security包装的用户信息
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user=userService.getUserByName(username);
        System.out.println(username+"用户名为");

        if (user==null){
            throw new UsernameNotFoundException("不存在此用户");
        }else if(user.getStatus()==0){
            throw new UserCountLockException("账号已经无效");
        }
        log.info(user.getPassword());
        System.out.println(getUserAuthority(user.getUserId()));
//      ？
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),getUserAuthority(user.getUserId()));
    }
    /**
     * 方法需要重写以获得用户角色权限信息
     * @param id 用户id
     * @return 权限列表
     */
    public List<GrantedAuthority> getUserAuthority(Long id) {
        return new ArrayList<>();
    }

}
