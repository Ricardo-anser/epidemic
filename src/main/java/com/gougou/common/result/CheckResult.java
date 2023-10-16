package com.gougou.common.result;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 作者:gougou
 * @version 创建时间：2023/7/11 20:58
 * <p>
 * jwt认证返回包装类
 */
@Data
@NoArgsConstructor//提供一个无参构造方法
@AllArgsConstructor//提供一个全参构造方法,默认不提供无参构造
public class CheckResult {
    private int errCode;

    private boolean success;

    private Claims claims;
}
