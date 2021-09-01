package com.mgl.utils.sdk.utils;

import java.text.DecimalFormat;

/**
 * @Description 数字格式化工具类
 * @Author fengwei
 * @Date 2020/7/22 11:33
 * @Version 1.0
 */
public class NumberFormatUtil {

    /**
     * double类型保留两位小数
     * @param param 待格式化的参数
     * @return 格式化后的参数
     */
    public static String doubleFormat(Double param){
        DecimalFormat df = new DecimalFormat("######0.00");
        return df.format(param);
    }
}
