package com.gougou;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gougou.entity.Menu;
import com.gougou.entity.Role;
import com.gougou.entity.User;
import com.gougou.mapper.MenuMapper;
import com.gougou.mapper.RoleMapper;
import com.gougou.mapper.UserMapper;
import com.gougou.service.IMenuService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 作者:gougou
 * @version 创建时间：2023/7/11 16:19
 * <p>
 * 类说明
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MenuListTest {
    @Autowired
    UserMapper userMapper;
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    MenuMapper menuMapper;
    @Autowired
    IMenuService menuService;
    @Test
    public void getUserAuthorityInfo(){
        StringBuffer authority=new StringBuffer();
        List<Role> roleList=roleMapper.selectList(new QueryWrapper<Role>().inSql("role_id","select role_id from sys_user_role where user_id="+1));
        if (roleList.size()>0){
        String roleCodeStrs=roleList.stream().map(r->"ROLE_"+r.getCode()).collect(Collectors.joining(","));
        authority.append(roleCodeStrs);
        }
        Set<String> menuCodeSet=new HashSet<String>();
        for (Role role:roleList) {
            List<Menu> menuList=menuMapper.selectList(new QueryWrapper<Menu>().inSql("menu_id","select menu_id from sys_role_menu where role_id="+role.getRoleId()));
            for (Menu menu:
                 menuList) {
                String perms=menu.getPerms();
                if (perms!=null){
                    menuCodeSet.add(perms);
                }
            }
        }
        if (menuCodeSet.size()>0){
            authority.append(",");
            String menuCodeStrs=menuCodeSet.stream().collect(Collectors.joining(","));
            authority.append(menuCodeStrs);
        }
        System.out.println("authority:"+authority.toString());
    }
    @Test
    public void buildTreemenuTest(){
        Role role = roleMapper.selectOne(new QueryWrapper<Role>().eq("role_id",1));
        List<Menu> menuList=menuMapper.selectList(null);//这里直接查询所有，表中没有其他角色
        List<Menu>resultMenuList=menuService.buildTreeMenu(menuList);
        System.out.println(JSONUtil.toJsonStr(resultMenuList));
    }
}
