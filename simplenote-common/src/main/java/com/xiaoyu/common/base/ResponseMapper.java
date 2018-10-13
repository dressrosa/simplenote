package com.xiaoyu.common.base;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;

/**
 * @author hongyu
 * @date 2018-10
 * @description
 */
public class ResponseMapper implements Serializable {

    private static final long serialVersionUID = 1L;
    private final static Logger LOG = LoggerFactory.getLogger(ResponseMapper.class);
    /**
     * 默认为成功
     */
    private long count;
    private int code;
    private String message;
    private Object data;

    private static final ValueFilter FILTER = new ValueFilter() {
        @Override
        public Object process(Object object, String name, Object value) {
            return value == null ? "" : value;
        }
    };

    private ResponseMapper() {
    };

    public static final ResponseMapper createMapper() {
        return new ResponseMapper();
    }

    /**
     * 返回json数据
     */
    public String resultJson() {
        final String result = JSON.toJSONString(this, ResponseMapper.FILTER,
                SerializerFeature.WriteNullStringAsEmpty);
        LOG.info("code:{},message:{}", code, message);
        return result;
    }

    public ResponseMapper code(int code) {
        final int tcode = code;
        this.code = code;
        // 通用返回信息
        if (ResponseCode.SUCCESS.statusCode() == tcode) {
            this.message = ResponseCode.SUCCESS.statusMsg();
        } else if (ResponseCode.ARGS_ERROR.statusCode() == tcode) {
            this.message = ResponseCode.ARGS_ERROR.statusMsg();
        } else if (ResponseCode.FAILED.statusCode() == tcode) {
            this.message = ResponseCode.FAILED.statusMsg();
        } else if (ResponseCode.EXIST.statusCode() == tcode) {
            this.message = ResponseCode.EXIST.statusMsg();
        } else if (ResponseCode.NO_DATA.statusCode() == tcode) {
            this.message = ResponseCode.NO_DATA.statusMsg();
        }
        return this;
    }

    public ResponseMapper message(String message) {
        if (message == null) {
            return this;
        }
        this.message = message;
        return this;
    }

    public ResponseMapper count(Long count) {
        if (count == null) {
            return this;
        }
        this.count = count;
        return this;
    }

    public ResponseMapper data(Object data) {
        if (data == null) {
            return this;
        }
        this.data = data;
        return this;
    }

    public Long getCount() {
        return count;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

}
