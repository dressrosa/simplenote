package com.xiaoyu.common.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;

/**
 * 2017年3月23日上午10:38:51
 * 
 * @author xiaoyu
 * @description 封装返回的数据 这里采用单例+threadlocal经过测试对优化并没有啥卵用(也没坏处,和使用平常的new几乎无差)
 *              只是用来学习threadlocal对单例的多线程问题的解决
 */
public class ResponseMapper {

    private final static Logger logger = LoggerFactory.getLogger(ResponseMapper.class);
    /**
     * 默认为成功
     */
    private static final String COUNT = "count";
    private static final String CODE = "code";
    private static final String MESSAGE = "message";
    private static final String DATA = "data";

    private static final ValueFilter FILTER = new ValueFilter() {
        @Override
        public Object process(Object object, String name, Object value) {
            return value == null ? "" : value;
        }
    };

    /**
     * 封装响应的数据,避免单例导致的多线程问题
     */
    private final static ThreadLocal<ConcurrentHashMap<String, Object>> LOCAL = new ThreadLocal<ConcurrentHashMap<String, Object>>() {
        @Override
        protected ConcurrentHashMap<String, Object> initialValue() {
            final ConcurrentHashMap<String, Object> dataMap = new ConcurrentHashMap<>(8);
            dataMap.put(ResponseMapper.CODE, ResponseCode.SUCCESS.statusCode());
            dataMap.put(ResponseMapper.MESSAGE, "");
            dataMap.put(ResponseMapper.COUNT, "");
            dataMap.put(ResponseMapper.DATA, "");
            return dataMap;
        }

    };

    private ResponseMapper() {
    };

    /**
     * 内部类
     */
    private static final class MapperInstance {
        public static final ResponseMapper MAPPER = new ResponseMapper();
    }

    // 返回单例
    public static final ResponseMapper createMapper() {
        return MapperInstance.MAPPER;
    }

    // public static ResponseMapper createMapper() {
    // return new ResponseMapper();
    // }

    // 返回json数据
    public String resultJson() {
        final String result = JSON.toJSONString(this.getLocalMap(), ResponseMapper.FILTER,
                SerializerFeature.WriteNullStringAsEmpty);
        logger.info("code:" + this.getLocalMap().get("code"));
        // getLocalMap().clear();
        this.getLocalMap().put(ResponseMapper.CODE, ResponseCode.SUCCESS.statusCode());
        this.getLocalMap().put(ResponseMapper.MESSAGE, "");
        this.getLocalMap().put(ResponseMapper.COUNT, "");
        this.getLocalMap().put(ResponseMapper.DATA, "");
        return result;
    }

    private final Map<String, Object> getLocalMap() {
        return ResponseMapper.LOCAL.get();
    }

    public ResponseMapper code(int code) {
        final int code1 = code;
        final Map<String, Object> dataMap = this.getLocalMap();
        dataMap.put(ResponseMapper.CODE, code1);
        // 通用返回信息
        if (ResponseCode.SUCCESS.statusCode() == code1) {
            dataMap.put(ResponseMapper.MESSAGE, ResponseCode.SUCCESS.statusMsg());
        } else if (ResponseCode.ARGS_ERROR.statusCode() == code1) {
            dataMap.put(ResponseMapper.MESSAGE, ResponseCode.ARGS_ERROR.statusMsg());
        } else if (ResponseCode.FAILED.statusCode() == code1) {
            dataMap.put(ResponseMapper.MESSAGE, ResponseCode.FAILED.statusMsg());
        } else if (ResponseCode.EXIST.statusCode() == code1) {
            dataMap.put(ResponseMapper.MESSAGE, ResponseCode.EXIST.statusMsg());
        } else if (ResponseCode.NO_DATA.statusCode() == code1) {
            dataMap.put(ResponseMapper.MESSAGE, ResponseCode.NO_DATA.statusMsg());
        }
        return this;
    }

    public ResponseMapper message(String message) {
        if (message == null) {
            return this;
        }
        final Map<String, Object> dataMap = this.getLocalMap();
        dataMap.put(ResponseMapper.MESSAGE, message);
        return this;
    }

    public ResponseMapper count(Long count) {
        if (count == null) {
            return this;
        }
        final Map<String, Object> dataMap = this.getLocalMap();
        dataMap.put(ResponseMapper.COUNT, count);
        return this;
    }

    public ResponseMapper data(Object data) {
        if (data == null) {
            return this;
        }
        final Map<String, Object> dataMap = this.getLocalMap();
        dataMap.put(ResponseMapper.DATA, data);
        return this;
    }
}
