package com.gougou.service.impl;

import com.gougou.entity.Role;
import com.gougou.mapper.RoleMapper;
import com.gougou.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author gougou
 * @since 2023-07-10
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}
