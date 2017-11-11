/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.utils;

import java.security.MessageDigest;

/**
 * @author xiaoyu 2016年3月28日
 */
public class Md5Utils {

    private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
            "e", "f" };

    public static String MD5(String s) {
        return Md5Utils.MD5Encode(s);
    }

    /**
     * 转换字节数组为16进制字串
     * 
     * @param b
     *            字节数组
     * @return 16进制字串
     */
    public static String byteArrayToHexString(byte[] b) {
        final StringBuilder resultSb = new StringBuilder();
        for (final byte aB : b) {
            resultSb.append(Md5Utils.byteToHexString(aB));
        }
        return resultSb.toString();
    }

    /**
     * 转换byte到16进制
     * 
     * @param b
     *            要转换的byte
     * @return 16进制格式
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        final int d1 = n / 16;
        final int d2 = n % 16;
        return Md5Utils.hexDigits[d1] + Md5Utils.hexDigits[d2];
    }

    /**
     * MD5编码
     * 
     * @param origin
     *            原始字符串
     * @return 经过MD5加密之后的结果
     */
    public static String MD5Encode(String origin) {
        String resultString = null;
        try {
            resultString = origin;
            final MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = Md5Utils.byteArrayToHexString(md.digest(resultString.getBytes()));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }

}
