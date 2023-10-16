package com.gougou.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 作者:gougou
 * @version 创建时间：2023/7/10 8:25
 * <p>
 * 统一请求返回体
 */
@Data
@NoArgsConstructor//提供一个无参构造方法
@AllArgsConstructor//提供一个全参构造方法,默认不提供无参构造
public class Result {
    private String code;//报错代码返回,如404,502,等
    private String msg;//错误信息返回
    private Object data;//原先未包装时的数据
    public static Result success() {
        return new Result(Constant.CODE_200, "", null);
    }

    public static Result success(Object data) {
        return new Result(Constant.CODE_200, "", data);
    }

    public static Result error(String code, String msg) {
        return new Result(code, msg, null);
    }

    public static Result error() {
        return new Result(Constant.CODE_500, "系统错误", null);
    }
}
