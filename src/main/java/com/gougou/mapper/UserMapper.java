package com.gougou.mapper;

import com.gougou.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gougou
 * @since 2023-07-10
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
