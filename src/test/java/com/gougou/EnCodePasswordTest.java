package com.gougou;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.gougou.entity.User;
import com.gougou.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 作者:gougou
 * @version 创建时间：2023/7/12 8:17
 * <p>
 * 类说明
 */
//@Commit提交事务
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class EnCodePasswordTest {
    @Autowired
    UserMapper userMapper;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Test
    public void createEnCodePassword(){
        String encodePassword=bCryptPasswordEncoder.encode("261616gsy");
        System.out.println(encodePassword.length());
        UpdateWrapper<User> userUpdateWrapper=new UpdateWrapper<>();
        userUpdateWrapper.eq("user_id",1L).set("password",encodePassword);
        userMapper.update(null,userUpdateWrapper);
        System.out.println(userMapper.selectList(null));
    }
}
