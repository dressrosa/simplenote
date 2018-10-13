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
 *              缺点:不可序列化
 */
public class ResponseMapper1 {

    private final static Logger LOG = LoggerFactory.getLogger(ResponseMapper1.class);
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
            dataMap.put(ResponseMapper1.CODE, ResponseCode.SUCCESS.statusCode());
            dataMap.put(ResponseMapper1.MESSAGE, "");
            dataMap.put(ResponseMapper1.COUNT, "");
            dataMap.put(ResponseMapper1.DATA, "");
            return dataMap;
        }

    };

    private ResponseMapper1() {
    };

    /**
     * 内部类
     */
    private static final class MapperInstance {
        public static final ResponseMapper1 MAPPER = new ResponseMapper1();
    }

    /**
     * 返回单例
     */
    public static final ResponseMapper1 createMapper() {
        return MapperInstance.MAPPER;
    }

    /**
     * 返回json数据
     */
    public String resultJson() {
        final Map<String, Object> localMap = this.getLocalMap();
        final String result = JSON.toJSONString(localMap, ResponseMapper1.FILTER,
                SerializerFeature.WriteNullStringAsEmpty);
        LOG.info("code:{},message:{}", localMap.get("code"), localMap.get("message"));
        // getLocalMap().clear();
        localMap.put(ResponseMapper1.CODE, ResponseCode.SUCCESS.statusCode());
        localMap.put(ResponseMapper1.MESSAGE, "");
        localMap.put(ResponseMapper1.COUNT, "");
        localMap.put(ResponseMapper1.DATA, "");
        return result;
    }

    private final Map<String, Object> getLocalMap() {
        return ResponseMapper1.LOCAL.get();
    }

    public ResponseMapper1 code(int code) {
        final int tcode = code;
        final Map<String, Object> dataMap = this.getLocalMap();
        dataMap.put(ResponseMapper1.CODE, tcode);
        // 通用返回信息
        if (ResponseCode.SUCCESS.statusCode() == tcode) {
            dataMap.put(ResponseMapper1.MESSAGE, ResponseCode.SUCCESS.statusMsg());
        } else if (ResponseCode.ARGS_ERROR.statusCode() == tcode) {
            dataMap.put(ResponseMapper1.MESSAGE, ResponseCode.ARGS_ERROR.statusMsg());
        } else if (ResponseCode.FAILED.statusCode() == tcode) {
            dataMap.put(ResponseMapper1.MESSAGE, ResponseCode.FAILED.statusMsg());
        } else if (ResponseCode.EXIST.statusCode() == tcode) {
            dataMap.put(ResponseMapper1.MESSAGE, ResponseCode.EXIST.statusMsg());
        } else if (ResponseCode.NO_DATA.statusCode() == tcode) {
            dataMap.put(ResponseMapper1.MESSAGE, ResponseCode.NO_DATA.statusMsg());
        }
        return this;
    }

    public ResponseMapper1 message(String message) {
        if (message == null) {
            return this;
        }
        this.getLocalMap().put(ResponseMapper1.MESSAGE, message);
        return this;
    }

    public ResponseMapper1 count(Long count) {
        if (count == null) {
            return this;
        }
        this.getLocalMap().put(ResponseMapper1.COUNT, count);
        return this;
    }

    public ResponseMapper1 data(Object data) {
        if (data == null) {
            return this;
        }
        this.getLocalMap().put(ResponseMapper1.DATA, data);
        return this;
    }
}
