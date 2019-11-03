/**
 * 
 */
package com.xiaoyu.simplenote.common.utils;

import java.util.ResourceBundle;

/**
 * @author xiaoyu
 * @date 2019-11
 * @description
 */
public class PropUtil {

    private final static ResourceBundle bundle = ResourceBundle.getBundle("application");

    public static String getProp(String key) {
        return bundle.getString(key);
    }
}
