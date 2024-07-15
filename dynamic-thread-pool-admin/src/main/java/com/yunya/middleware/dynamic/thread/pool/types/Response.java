package com.yunya.middleware.dynamic.thread.pool.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * @Description 结果类
 * @Author yunyala
 * @Date 2024/7/14 17:38
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;

    private String info;

    private T data;

    public enum Code {
        SUCCESS("0000", "成功"),
        UN_ERROR("0001", "失败"),
        ILLEGAL_PARAMETER("0001", "非法参数");

        private String code;
        private String info;

        Code() {
        }

        Code(String code, String info) {
            this.code = code;
            this.info = info;
        }

        public String getCode() {
            return code;
        }

        public String getInfo() {
            return info;
        }
    }

}
