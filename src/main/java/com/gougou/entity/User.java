package com.gougou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author gougou
 * @since 2023-07-10
 */
@Getter
@Setter
@TableName("sys_user")
@Data
@ApiModel(value = "User对象", description = "")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @ApiModelProperty("用户名")
    private String username;

    private String name;

    private int sex;
    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("用户邮箱")
    private String email;

    @ApiModelProperty("手机号码")
    private String phonenumber;
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("最后登录时间")
    private LocalDateTime loginDate;

    @ApiModelProperty("帐号状态（0有效 1无效）")
    private Integer status;
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm")
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm")
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("备注")
    private String remark;


}
