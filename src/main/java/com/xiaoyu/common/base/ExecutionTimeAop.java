/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.base;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com.xiaoyu.common.utils.JedisUtils;

/**
 * 计算方法运行时间
 * 
 * @author xiaoyu 2016年4月3日
 */
@Configuration
@Aspect
public class ExecutionTimeAop {

    private final static Logger LOG = LoggerFactory.getLogger(ExecutionTimeAop.class);

    /**
     * 网络释义: 例如定义切入点表达式 execution (* com.sample.service.impl..*.*(..))
     * execution()是最常用的切点函数，其语法如下所示： 整个表达式可以分为五个部分： 1、execution(): 表达式主体。
     * 2、第一个*号：表示返回类型，*号表示所有的类型。
     * 3、包名：表示需要拦截的包名，后面的两个句点表示当前包和当前包的所有子包，com.sample.service.impl包、子孙包下所有类的方法。
     * 4、第二个*号：表示类名，*号表示所有的类。
     * 5、*(..):最后这个星号表示方法名，*号表示所有的方法，后面括弧里面表示方法的参数，两个句点表示任何参数。
     */
    @Pointcut("execution(* com.xiaoyu.modules.controller..*(..))")
    public void pointcut() {

    }

    /**
     * 限制次数
     */
    private static final int URI_LIMIT = 100;

    @SuppressWarnings("null")
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) {
        final Object[] args = point.getArgs();
        HttpServletRequest request = null;
        // 客户端ip
        String ip = null;
        // 接口地址
        String uri = null;
        // 用户id
        // final String userId = null;
        // 登录 token
        // final String token = null;
        // final String redisUserId = null;
        String ipLimit = null;//
        // 调用方法名
        String methodName = null;
        // ip限制访问次数
        int limit = 0;
        for (final Object o : args) {
            if (o instanceof HttpServletRequest) {
                request = (HttpServletRequest) o;
                break;
            }
        }
        ip = request.getRemoteHost();
        uri = request.getRequestURI();
        LOG.info("ip:" + ip + " uri:" + uri);
        methodName = point.getSignature().getName();
        if (uri.endsWith(".jpg") || uri.endsWith(".png") || uri.endsWith(".jpeg")) {
            return null;
        }
        // 无需登录情况下
        // if (uri.startsWith("/public")) {
        ipLimit = JedisUtils.hget(methodName + ":" + ip, uri);
        if (null == ipLimit) {
            JedisUtils.hset(methodName + ":" + ip, uri, 1 + "", 60);
            ipLimit = "1";
        }
        limit = Integer.valueOf(ipLimit);
        if (limit > ExecutionTimeAop.URI_LIMIT) {
            ResponseMapper mapper = ResponseMapper.createMapper();
            mapper.code(ResponseCode.FAILED.statusCode())
                    .message("访问次数异常,一分钟之内无法访问");
            JedisUtils.hset(methodName + ":" + ip, uri, limit + "", 60);
            JedisUtils.hincrby(methodName + ":" + ip, uri, 1);
            return mapper.resultJson();
        } else {
            JedisUtils.hincrby(methodName + ":" + ip, uri, 1);
        }
        return this.getResult(point);
        // }
        // 需登录情况下
        // userId = request.getHeader("userId");
        // token = request.getHeader("token");
        // redisUserId = JedisUtils.get(token);
        // if (null == redisUserId) {
        // ResponseMapper mapper = ResponseMapper.createMapper();
        // mapper.setCode(ResponseCode.ARGS_ERROR).setMessage("token异常,请重新登录");
        // return mapper.getResultJson();
        // }
        // if (redisUserId.equals(userId)) {
        // JedisUtils.set(token, userId, 6 * 60 * 60);
        // } else {
        // ResponseMapper mapper = ResponseMapper.createMapper();
        // mapper.setCode(ResponseCode.ARGS_ERROR).setMessage("请先登录");
        // return mapper.getResultJson();
        // }
        // // 一个用户同一个ip下访问同一个api的次数(1 min之内)
        // ipLimit = JedisUtils.hget(userId + ":" + ip, uri);
        // if (null == ipLimit) {
        // JedisUtils.hset(userId + ":" + ip, uri, 1 + "", 60);
        // ipLimit = "1";
        // }
        // limit = Integer.valueOf(ipLimit);
        // if (limit > URI_LIMIT) {
        // ResponseMapper mapper = ResponseMapper.createMapper();
        // mapper.setCode(ResponseCode.EXCEPTION).setMessage("访问次数异常,一分钟之内无法访问");
        // JedisUtils.hset(userId + ":" + ip, uri, limit + "", 60);
        // JedisUtils.hincrby(userId + ":" + ip, uri, 1);
        // return mapper.getResultJson();
        // } else {
        // JedisUtils.hincrby(userId + ":" + ip, uri, 1);
        // }
        // return getResult(point);
    }

    private Object getResult(ProceedingJoinPoint point) {
        Object result = null;
        final String methodName = point.getSignature().getName();
        long start = System.currentTimeMillis();
        try {
            result = point.proceed();
        } catch (final Throwable e) {
            LOG.error(e.toString());
        }
        LOG.info("方法[{}]执行时间为:[{}milliseconds] ", methodName, (System.currentTimeMillis() - start));
        return result;
    }

}
