package com.gougou.controller;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gougou.common.result.Result;
import com.gougou.common.security.LoginFailureHandler;
import com.gougou.entity.Role;
import com.gougou.entity.User;
import com.gougou.entity.UserRole;
import com.gougou.mapper.UserRoleMapper;
import com.gougou.service.IRoleService;
import com.gougou.service.IUserRoleService;
import com.gougou.service.IUserService;
import com.gougou.utils.PermsDynamicUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * <p>
 *  控制器
 *  1.分页查询
 *  2.条件查询
 *  3.角色设置
 *  4.有效无效化
 *  5.删除
 *  6.批量删除
 * </p>
 *
 * @author gougou
 * @since 2023-07-10
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    IUserService userService;
    @Autowired
    IRoleService roleService;
    @Autowired
    IUserRoleService userRoleService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    PermsDynamicUtils permsDynamicUtils;
    /**
     * 分页展示用户信息实现
     * 分页基于MyBatis-plus的分页插件实现
     * @return 包装的页面list信息，用户条数
     */
    @GetMapping("/userlist")
    public Result getUserList(@RequestParam Integer pageNum,
                              @RequestParam Integer pageSize,
                              @RequestParam String username,
                              @RequestParam String name,
                              @RequestParam String phonenumber
    ){
        IPage<User> userPage=new Page<User>(pageNum,pageSize);
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        if(StringUtils.isNotBlank(username) || StringUtils.isNotBlank(name) || StringUtils.isNotBlank(phonenumber)){
            queryWrapper.and(wrapper->wrapper.like(StringUtils.isNotBlank(username),"username",username)
                    .like(StringUtils.isNotBlank(name),"name",name)
                    .like(StringUtils.isNotBlank(phonenumber),"phonenumber",phonenumber));
        }
        IPage<User> newUserPage=userService.page(userPage,queryWrapper);
        Long total=userService.count();
        List<User> userList=newUserPage.getRecords();
        Map<String,Object> data=new HashMap<>();
        data.put("total",total);
        data.put("userList",userList);
        userService.getUserByName("ajaja");
        return Result.success(data);
    }

    /**
     * 添加
     * 1.用户名
     * 2.姓名
     * 3.密码
     * 4.确认密码
     * 5.性别
     * 6.联系电话
     * 7.邮箱
     * 8.状态
     * 9.备注
     * 接受一个json格式字符串
     * 对用户两次输入密码在前端校验是否一致
     * 后端使用security框架进行加密处理
     * CompletableFuture
     * @return Result.success()
     */
    @PostMapping("/insertUser")
    public  Result insertUser(@RequestBody User user){
        System.out.println(user.toString());
        String password=bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(password);
        return Result.success(userService.saveOrUpdate(user));
    }

    /**
     * 修改用户信息
     * @return Result.success()
     */
    @GetMapping("/updateUser")
    public Result updateUser(){
        return null;
    }

    /**
     * 设置无效状态
     * 修改status字段即可
     * @return Result.success()
     */
    @GetMapping("/setStatus")
    public Result setStatus(@RequestParam String status,@RequestParam Integer userId){

        UpdateWrapper updateWrapper=new UpdateWrapper();
        updateWrapper.eq("user_id",userId);
        updateWrapper.set("status",status);
        userService.update(null,updateWrapper);
        return Result.success();
    }

    /**
     * 删除
     * 1.接受id参数，删除对应id的用户信息(采用物理删除)
     * 1.1考量:小项目，引入逻辑删除会产生包括索引，id冲突，新增冲突，大量逻辑删除后的无用条目等
     * 1.2若项目对于用户信息有存储需求，平衡性能的情况下，可以使用逻辑删除
     * 2.连表删除userrole中的分配信息
     * @return 返回删除后字段，删除返回成功失败
     */
    @DeleteMapping ("/deleteUser/{userId}")
    public Result deleteUser(@PathVariable Long userId){
        System.out.println(userId);
        userService.removeById(userId);
        QueryWrapper<UserRole> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        userRoleService.remove(queryWrapper);
        return Result.success();
    }
    @PostMapping("/deleteBatchUser")
    public Result deleteBatchUser(@RequestBody List<Long> userIds){
        userService.removeByIds(userIds);
        QueryWrapper<UserRole> queryWrapper=new QueryWrapper<>();
        userIds.forEach(userId->{
            userRoleService.remove(queryWrapper.eq("user_id",userId));
        });
        return Result.success();
    }
    /**
     * 设置角色
     * 调用roleList，查询所有角色信息，分配角色到用户
     * 用户新增字段存储角色信息
     * 分配角色使用userRole表实现
     * @return dialog弹窗显示的角色信息，分配角色的ids信息
     */
    @PostMapping("/setRole")
    public Result setRole(@RequestParam("userId") Long userId,@RequestParam("roleIds") List<Long> roleIds){
        List<UserRole> userRoleList =new ArrayList<>();
        roleIds.forEach(r->{
            UserRole userRole=new UserRole();
            userRole.setRoleId(r);
            userRole.setUserId(userId);
            userRoleList.add(userRole);
        });
        userRoleService.remove(new QueryWrapper<UserRole>().eq("user_id",userId));
        userRoleService.saveBatch(userRoleList);
//        todo: 角色回显触发全局权限菜单更新
        return Result.success();
    }
    /**
     * 角色回显
     */
    @GetMapping("/reShowRole")
    public Result reShowRole(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam Long userId){
        IPage<Role> rolePage=new Page<Role>(pageNum,pageSize);
        IPage<Role> newRolePage=roleService.page(rolePage);
        Long total=roleService.count();
        List<Role> roleList=newRolePage.getRecords();
        QueryWrapper<UserRole> queryWrapper=new QueryWrapper<>();
        List<UserRole> userRoleList=userRoleService.list(queryWrapper.eq("user_id",userId));
        List<Long> roleIds=userRoleList
                .stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        Map<String,Object> data=new HashMap<>();
        data.put("roleList",roleList);
        data.put("total",total);
        data.put("roleIds",roleIds);
        return Result.success(data);
    }

    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/import")
    public Result importExcel(@RequestParam("file") MultipartFile file) throws IOException {
//        读取文件
        List<User> userList = EasyExcel.read(file.getInputStream()).head(User.class).sheet().doReadSync();
//        去重
        List<User>distinctUserList= userList.stream().distinct().collect(Collectors.toList());
        distinctUserList.forEach(user->{
            String password=user.getPassword();

            user.setPassword(bCryptPasswordEncoder.encode(password));
        });
//        导入数据库
        userService.saveBatch(distinctUserList);
        return Result.success();
    }
    @PostMapping("/export")
    public Result exportExcel(@RequestBody List<Long> userIds){
        List<User> exportUserList=userService.listByIds(userIds);
        Map<String,Object> data=new HashMap<>();
        data.put("exportUserList",exportUserList);
        return Result.success(data);
    }
}
