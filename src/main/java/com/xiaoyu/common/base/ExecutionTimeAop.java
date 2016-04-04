/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.common.base;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**计算方法运行时间
 * @author xiaoyu
 *2016年4月3日
 */
@Configuration
@EnableAutoConfiguration
@Aspect
public class ExecutionTimeAop {

	private static Logger logger = Logger.getLogger(ExecutionTimeAop.class);
	
	@Pointcut("execution(* com.xiaoyu.modules.biz..*.*(..))")
	public void pointcut() {
		
	}

	
	/**计算方法的执行时间
	 *@author xiaoyu
	 *@param point
	 *@return
	 *@time 2016年4月3日下午12:07:58
	 */
	@Around("pointcut()")
	public Object around(ProceedingJoinPoint point) {
		Object result = null;
		long start=0;long end=0;
		String methodName = point.getSignature().getName();
		try {
			start = System.currentTimeMillis();
			result = point.proceed();
			end = System.currentTimeMillis();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		logger.info("方法["+methodName+"]执行时间为:["+(end-start)+" milliseconds] ");
		return result;
	}


}
