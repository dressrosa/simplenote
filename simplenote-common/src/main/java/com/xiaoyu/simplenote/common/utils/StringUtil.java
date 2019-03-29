/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.common.utils;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author hongyu
 * @date 2018-02
 * @description
 */
public class StringUtil extends StringUtils {

    private static final char SEPARATOR = '_';
    private static final String CHARSET_NAME = "UTF-8";

    /**
     * 转换为字节数组
     * 
     * @param str
     * @return
     */
    public static byte[] getBytes(String str) {
        if (str != null) {
            try {
                return str.getBytes(StringUtil.CHARSET_NAME);
            } catch (final UnsupportedEncodingException e) {
                return new byte[0];
            }
        } else {
            return new byte[0];
        }
    }

    /**
     * 转换为字节数组
     * 
     * @param str
     * @return
     */
    public static String toString(byte[] bytes) {
        try {
            return new String(bytes, StringUtil.CHARSET_NAME);
        } catch (final UnsupportedEncodingException e) {
            return StringUtil.EMPTY;
        }
    }

    /**
     * 是否包含字符串
     * 
     * @param str
     *            验证字符串
     * @param strs
     *            字符串组
     * @return 包含返回true
     */
    public static boolean inString(String str, String... strs) {
        if (str != null) {
            for (final String s : strs) {
                if (str.equals(StringUtil.trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 替换掉HTML标签方法
     */
    public static String replaceHtml(String html) {
        if (StringUtil.isBlank(html)) {
            return "";
        }
        final String regEx = "<.+?>";
        final Pattern p = Pattern.compile(regEx);
        final Matcher m = p.matcher(html);
        final String s = m.replaceAll("");
        return s;
    }

    /**
     * 缩略字符串（不区分中英文字符）
     * 
     * @param str
     *            目标字符串
     * @param length
     *            截取长度
     * @return
     */
    public static String abbr(String str, int length) {
        if (str == null) {
            return "";
        }
        try {
            final StringBuilder sb = new StringBuilder();
            int currentLength = 0;
            for (final char c : StringUtil.replaceHtml(StringEscapeUtils.unescapeHtml4(str)).toCharArray()) {
                currentLength += String.valueOf(c).getBytes("GBK").length;
                if (currentLength <= length - 3) {
                    sb.append(c);
                } else {
                    sb.append("...");
                    break;
                }
            }
            return sb.toString();
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 驼峰命名法工具
     * 
     * @return toCamelCase("hello_world") == "helloWorld"
     *         toCapitalizeCamelCase("hello_world") == "HelloWorld"
     *         toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }

        s = s.toLowerCase();

        final StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);

            if (c == StringUtil.SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * 驼峰命名法工具
     * 
     * @return toCamelCase("hello_world") == "helloWorld"
     *         toCapitalizeCamelCase("hello_world") == "HelloWorld"
     *         toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = StringUtil.toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * 驼峰命名法工具
     * 
     * @return toCamelCase("hello_world") == "helloWorld"
     *         toCapitalizeCamelCase("hello_world") == "HelloWorld"
     *         toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toUnderScoreCase(String s) {
        if (s == null) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);

            boolean nextUpperCase = true;

            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }

            if ((i > 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    sb.append(StringUtil.SEPARATOR);
                }
                upperCase = true;
            } else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    public static boolean isEmail(String str) {
        final String regex = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        return Pattern.matches(regex, str);
    }

    public static boolean isMobile(String str) {
        final String regex = "^1[345789]\\d{9}$";
        return Pattern.matches(regex, str);
    }

}
