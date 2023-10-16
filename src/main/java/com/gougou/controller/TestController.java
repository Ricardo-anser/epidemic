package com.gougou.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.gougou.common.result.Result;
import com.gougou.entity.User;
import com.gougou.service.IUserService;
import com.gougou.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author 作者:gougou
 * @version 创建时间：2023/7/11 20:37
 * <p>
 * 类说明
 */
@RestController
@RequestMapping("/test")
public class TestController {
//    @Autowired
//    IUserService userService;
//    @GetMapping("/list")
//    public Result userlist(@RequestHeader(required = false) String token){
//        if (StrUtil.isNotEmpty(token)){
//            List<User> userList=userService.list();
//            return Result.success(userList);
//        }
//        else {
//            return Result.error("401","无权限访问");
//        }
//    }
//    @GetMapping("/login")
//    public Result login(){
//        String token= JwtUtils.genJwtToken("gougou");
//        return Result.success(token);
//    }
    @PostMapping("/import")
    public Result importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        // 读取 Excel 文件中的数据
        System.out.println("文件上传与读取");
        System.out.println(file);
        List<User> userList = EasyExcel.read(file.getInputStream()).head(User.class).sheet().doReadSync();
        System.out.println("读取完成"+userList.size());
        userList.forEach(System.out::println);
        // TODO: 将读取到的数据保存到数据库或进行其他业务处理
        return Result.success();
    }
}
