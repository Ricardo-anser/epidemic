package com.gougou;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gougou.common.result.Result;
import com.gougou.entity.User;
import com.gougou.service.IUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 作者:gougou
 * @version 创建时间：2023/7/19 11:12
 * <p>
 * 类说明
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class PageTest {
    @Autowired
    IUserService userService;
    @Test
    public void getUserListTest(){
        Integer pageSize=10;
        Integer pageNum=1;
        IPage<User> userPage=new Page<User>();
        userPage.setCurrent(pageNum);
        userPage.setSize(pageSize);
        IPage<User> newUserPage=userService.page(userPage);
        List<User> userList=newUserPage.getRecords();
        System.out.println(newUserPage.getSize());
        userList.forEach(System.out::println);
    }
}
