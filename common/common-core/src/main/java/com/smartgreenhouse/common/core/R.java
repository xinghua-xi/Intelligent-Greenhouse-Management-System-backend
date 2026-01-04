package com.smartgreenhouse.common.core;
import lombok.Data;
import java.io.Serializable;

/**
 * 统一 API 响应格式
 * 对应接口文档中的 JSON 结构 [cite: 791]
 */
@Data
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;      // 状态码：200 成功
    private String msg;    // 消息
    private T data;        // 数据负载

    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMsg("Success");
        r.setData(data);
        return r;
    }

    public static <T> R<T> fail(String msg) {
        R<T> r = new R<>();
        r.setCode(500);
        r.setMsg(msg);
        return r;
    }

    public static <T> R<T> fail(int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }
}
