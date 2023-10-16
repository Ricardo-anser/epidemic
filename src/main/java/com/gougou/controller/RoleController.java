package com.gougou.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gougou.common.result.Result;
import com.gougou.entity.Menu;
import com.gougou.entity.Role;
import com.gougou.entity.RoleMenu;
import com.gougou.service.IMenuService;
import com.gougou.service.IRoleMenuService;
import com.gougou.service.IRoleService;
import com.gougou.service.IUserService;
import com.gougou.utils.PermsDynamicUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author gougou
 * @since 2023-07-10
 */
@Slf4j
@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    IUserService userService;
    @Autowired
    IRoleService roleService;
    @Autowired
    IMenuService menuService;
    @Autowired
    IRoleMenuService roleMenuService;
    @Autowired
    PermsDynamicUtils permsDynamicUtils;
//    @PreAuthorize("hasAuthority('system:menu:query')")
    /**
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/rolelist")
    public Result getRoleList(@RequestParam Integer pageNum,
                              @RequestParam Integer pageSize,
                              @RequestParam String name,
                              @RequestParam String code){
        IPage<Role> rolePage=new Page<>(pageNum,pageSize);
        QueryWrapper<Role> queryWrapper=new QueryWrapper<>();
        if(StringUtils.isNotBlank(name) || StringUtils.isNotBlank(code)){
            queryWrapper.and(wrapper->wrapper.like(StringUtils.isNotBlank(name),"name",name)
                    .like(StringUtils.isNotBlank(code),"code",code));
        }
        IPage<Role> newRolePage=roleService.page(rolePage,queryWrapper);
        Long total=roleService.count();
        List<Role> roleList=newRolePage.getRecords();
        Map<String,Object> data=new HashMap<>();
        data.put("roleList",roleList);
        data.put("total",total);
        return Result.success(data);
    }

    /**
     * 实现权限菜单的返回操作，以及选中状态的回显
     * @param roleId 使用菜单权限表实现状态回显的id字段
     * @return
     */
    @GetMapping("/reshowMenu")
    public Result reshowMenu(@RequestParam Long roleId){
        List<Menu> menuList=menuService.list();
        List<Menu> treeMenuList=menuService.buildTreeMenu(menuList);
        QueryWrapper<RoleMenu> queryWrapper=new QueryWrapper<>();
        List<RoleMenu> roleMenuList=roleMenuService.list(queryWrapper.eq("role_id",roleId));
        List<Long> childrenMenuIds=new ArrayList<>();
        if(!roleMenuList.isEmpty()){
            List<Long> menuIds=roleMenuList
                    .stream()
                    .map(RoleMenu::getMenuId)
                    .collect(Collectors.toList());
            List<Menu> menus=menuService.listByIds(menuIds);
//      避免魔法值的使用
            menus.forEach(m->{
                if("F".equals(m.getMenuType())){
                    childrenMenuIds.add(m.getMenuId());
                }
            });
        }
        Map<String,Object> data=new HashMap<>();
        data.put("treeMenuList",treeMenuList);
        data.put("menuIds",childrenMenuIds);
        return Result.success(data);
    }
    @PostMapping("/setMenu")
    public Result setMenu(@RequestParam("roleId") Long roleId,@RequestParam("menuIds") List<Long> menuIds){
        List<RoleMenu> roleMenuList=new ArrayList<>();
        menuIds.forEach(m->{
            RoleMenu roleMenu=new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(m);
            roleMenuList.add(roleMenu);
        });
        roleMenuService.remove(new QueryWrapper<RoleMenu>().eq("role_id",roleId));
        roleMenuService.saveBatch(roleMenuList);
//      菜单设置触发全局权限菜单更新
        Map<String,Object> data= permsDynamicUtils.returnPackageTreeandPerms();
        return Result.success(data);
    }

    @GetMapping("/setStatus")
    public Result setStatus(@RequestParam String status,@RequestParam Integer roleId){

        UpdateWrapper<Role> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("role_id",roleId);
        updateWrapper.set("status",status);
        roleService.update(null,updateWrapper);
//      状态设置触发全局权限菜单更新
        Map<String,Object> data= permsDynamicUtils.returnPackageTreeandPerms();
        return Result.success(data);
    }
    @PostMapping("/insertRole")
    public Result insertRole(@RequestBody Role role){
        return Result.success(roleService.saveOrUpdate(role));
    }
    @DeleteMapping("/deleteRole/{roleId}")
    public Result deleteRole(@PathVariable Long roleId){
        roleService.removeById(roleId);
        QueryWrapper<RoleMenu> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("role_id",roleId);
        roleMenuService.remove(queryWrapper);
//      角色删除触发全局权限菜单更新
        Map<String,Object> data= permsDynamicUtils.returnPackageTreeandPerms();
        return Result.success(data);
    }
    @PostMapping("/deleteBatchRole")
    public Result deleteBatchRole(@RequestBody List<Long> roleIds){
        roleService.removeByIds(roleIds);
        QueryWrapper<RoleMenu> queryWrapper=new QueryWrapper<>();
        roleIds.forEach(roleId-> roleMenuService.remove(queryWrapper.eq("role_id",roleId)));
//      批量删除触发全局权限菜单更新
        Map<String,Object> data= permsDynamicUtils.returnPackageTreeandPerms();
        return Result.success(data);
    }

}
