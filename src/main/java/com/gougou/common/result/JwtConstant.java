package com.gougou.common.result;

/**
 * @author 作者:gougou
 * @version 创建时间：2023/7/11 21:00
 * <p>
 * 类说明
 */
public interface JwtConstant {

    /**
     * token
     */
    public static final int JWT_ERRCODE_NULL = 4000;			//Token不存在
    public static final int JWT_ERRCODE_EXPIRE = 4001;			//Token过期
    public static final int JWT_ERRCODE_FAIL = 4002;			//验证不通过

    /**
     * JWT
     */
    public static final String JWT_SECERT = "8677df7fc3a34e26a61c034d5ec8245d";			//密匙
    public static final long JWT_TTL = 24*60 * 60 * 1000;
}
