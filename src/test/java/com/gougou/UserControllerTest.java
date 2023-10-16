package com.gougou;

import com.gougou.common.result.Result;
import com.gougou.controller.UserController;
import com.gougou.entity.User;
import com.gougou.service.IUserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
/**
 * @author 作者:gougou
 * @version 创建时间：2023/8/9 20:44
 * <p>
 * 类说明
 */
public class UserControllerTest {

    @Mock
    private IUserService iUserService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserController userController;

    @Before
    public void seyUp(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testAddUser(){
        User user=new User();
        when(bCryptPasswordEncoder.encode(any(String.class))).thenReturn("encodePassword");
        when(iUserService.saveOrUpdate(any(User.class))).thenReturn(true);
        // 调用控制器的方法
        Result responseEntity = userController.insertUser(user);

        // 验证返回结果
        Assert.assertEquals(true, responseEntity.getData());

    }
}
