package com.xiaoyu.common.base;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * 默认为成功
     */
    private static final String COUNT = "count";
    private static final String CODE = "code";
    private static final String MESSAGE = "message";
    private static final String DATA = "data";

    private static final ValueFilter filter = new ValueFilter() {
        @Override
        public Object process(Object object, String name, Object value) {
            return value == null ? "" : value;
        }
    };

    /**
     * 封装响应的数据,避免单例导致的多线程问题
     */
    private static final ThreadLocal<Map<String, Object>> local = new ThreadLocal<Map<String, Object>>() {
        @Override
        protected Map<String, Object> initialValue() {
            final Map<String, Object> dataMap = new HashMap<>(8);
            dataMap.put(ResponseMapper.CODE, ResultConstant.SUCCESS);
            dataMap.put(ResponseMapper.MESSAGE, null);
            dataMap.put(ResponseMapper.COUNT, null);
            dataMap.put(ResponseMapper.DATA, null);
            return dataMap;
        }
    };

    private ResponseMapper() {
    };

    /**
     * 内部类
     */
    // private static final class MapperInstance {
    // public static final ResponseMapper mapper = new ResponseMapper();
    // }
    //
    // // 返回单例
    // public static final ResponseMapper createMapper() {
    // return MapperInstance.mapper;
    // }

    public static ResponseMapper createMapper() {
        return new ResponseMapper();
    }

    // 返回json数据
    public String getResultJson() {
        return JSON.toJSONString(this.getLocalMap(), ResponseMapper.filter, SerializerFeature.WriteNullStringAsEmpty);
    }

    private final Map<String, Object> getLocalMap() {
        return ResponseMapper.local.get();
    }

    public ResponseMapper setCode(String code) {
        final String code1 = code;
        final Map<String, Object> dataMap = this.getLocalMap();
        dataMap.put(ResponseMapper.CODE, code1);
        switch (code1) {// 通用返回信息
        case ResultConstant.SUCCESS:
            dataMap.put(ResponseMapper.MESSAGE, ResultConstant.SUCCESS_MESSAGE);
            break;
        case ResultConstant.ARGS_ERROR:
            dataMap.put(ResponseMapper.MESSAGE, ResultConstant.ARGS_ERROR_MESSAGE);
            break;
        case ResultConstant.EXCEPTION:
            dataMap.put(ResponseMapper.MESSAGE, ResultConstant.EXCEPTION_MESSAGE);
            break;
        case ResultConstant.EXISTS:
            dataMap.put(ResponseMapper.MESSAGE, ResultConstant.EXISTS_MESSAGE);
            break;
        case ResultConstant.NOT_DATA:
            dataMap.put(ResponseMapper.MESSAGE, ResultConstant.NOT_DATA_MESSAGE);
            break;
        }
        return this;

    }

    public ResponseMapper setMessage(String message) {
        if (message == null) {
            return this;
        }
        final Map<String, Object> dataMap = this.getLocalMap();
        dataMap.put(ResponseMapper.MESSAGE, message);
        return this;
    }

    public ResponseMapper setCount(Long count) {
        if (count == null) {
            return this;
        }
        final Map<String, Object> dataMap = this.getLocalMap();
        dataMap.put(ResponseMapper.COUNT, count);
        return this;
    }

    public ResponseMapper setData(Object data) {
        if (data == null) {
            return this;
        }
        final Map<String, Object> dataMap = this.getLocalMap();
        dataMap.put(ResponseMapper.DATA, data);
        return this;
    }
}
