/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.common.base;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import com.alibaba.fastjson.JSON;

/**所有返回Object的都转为json
 * @author xiaoyu
 *2016年3月30日
 */
//@Configuration
//@EnableAutoConfiguration
//@Aspect
@Deprecated
public class ReturnJsonAop {

	/**对返回类型为Object类型的进行aop转化
	 *@author xiaoyu
	 *@time 2016年3月30日上午11:41:06
	 */
	@Pointcut("execution(Object com.xiaoyu.modules.biz..*.*(..))")
	public void pointcut() {
		
	}
	/*
	 * 网络释义:
	 * 例如定义切入点表达式  execution (* com.sample.service.impl..*.*(..))
	 * execution()是最常用的切点函数，其语法如下所示：
	 * 整个表达式可以分为五个部分：
	 * 1、execution(): 表达式主体。
	 * 2、第一个*号：表示返回类型，*号表示所有的类型。
	 * 3、包名：表示需要拦截的包名，后面的两个句点表示当前包和当前包的所有子包，com.sample.service.impl包、子孙包下所有类的方法。
	 * 4、第二个*号：表示类名，*号表示所有的类。
	 * 5、*(..):最后这个星号表示方法名，*号表示所有的方法，后面括弧里面表示方法的参数，两个句点表示任何参数。
 	 */
	
//	@Before("pointcut()")
//	public void before() {
//		System.out.println("进入方法,开始执行AOP:");
//	}
	
//	@AfterReturning(value="pointcut()",returning="returnValue")
//	public void afterReturning(JoinPoint joinPoint, Object returnValue) {
//		System.out.println("后置返回通知:"+returnValue+"你好啊");
//	}
	
	/**将所有返回类型为Object的转为json输出
	 *@author xiaoyu
	 *@param point
	 *@return
	 *@time 2016年3月30日上午11:47:42
	 */
	@Around("pointcut()")
	public Object around(ProceedingJoinPoint point) {
		Object result = null;
		//String methodName = point.getSignature().getName();
		try {
			//System.out.println("前置通知:"+methodName);
			result  = point.proceed();
			//System.out.println("返回通知:"+methodName);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("后置通知:"+methodName);
		return JSON.toJSONString(result);
	}
}
