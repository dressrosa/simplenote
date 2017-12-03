/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.constant;

/**
 * 返回状态常量
 * 
 * @author xiaoyu 2016年4月12日
 */
public enum StateConstant {

    /**
     * 1000
     */
    SUCCESS(1000),
    /**
     * 1001
     */
    FAILURE(1001),
    /**
     * 1002
     */
    EXCECPTION(1002),
    /**
     * 1003
     */
    NOFINDINGS(1003);

    private int state;

    private StateConstant() {

    }

    private StateConstant(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return this.state + "";
    }

}
