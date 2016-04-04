/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.common.utils;
import java.security.SecureRandom;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;


/**生成唯一性id
 * @author xiaoyu
 *2016年3月24日
 */
public class IdGenerator extends RandomStringUtils implements org.springframework.util.IdGenerator {

	private static SecureRandom random = new SecureRandom();
	
	/**
	 * 无分隔符的id
	 */
	public static String uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	/**
	 * 生成long型id
	 */
	public static long randomLong() {
		return Math.abs(random.nextLong());
	}

	/**
	 * 有-分隔符的id
	 */
	@Override
	public UUID generateId() {
		return UUID.randomUUID();
	}
}
