package com.mgl.utils.sdk.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * @description 数字处理工具
 * @author fengwei
 */
public class NumberUtils {

    private NumberUtils() {}

    private static final String NUM_ERROR_MSG = "数字格式转换失败";

    /**
     * 将字符串转换成Double,转换失败跑出异常
     * @param num
     * @param title 字段描述
     * @return  当字符串为null或""时，返回null
     */
    public static final Double createDouble(String num, String title) {
        if(StringUtils.isEmpty(num)) {
            return null;
        }
        try {
            return Double.valueOf(num);
        } catch (Exception e) {
            throw new NumberFormatException(title + NUM_ERROR_MSG);
        }
    }
    /**
     * 将字符串转换成BigDecimal,转换失败跑出异常
     * @param num
     * @param title 字段描述
     * @return  当字符串为null或""时，返回null
     */
    public static final BigDecimal createBigDecimal(String num, String title) {
        if(StringUtils.isEmpty(num)) {
            return null;
        }
        try {
            return new BigDecimal(num);
        } catch (Exception e) {
            throw new NumberFormatException(title + NUM_ERROR_MSG);
        }
    }
    /**
     * 将字符串转换成Integer,转换失败跑出异常
     * @param num
     * @param title 字段描述
     * @return  当字符串为null或""时，返回null
     */
    public static final Integer createInteger(String num, String title) {
        if(StringUtils.isEmpty(num)) {
            return null;
        }
        try {
            return Integer.valueOf(num);
        } catch (Exception e) {
            throw new NumberFormatException(title + NUM_ERROR_MSG);
        }
    }

    public static final Double sumOfDouble(String num1, String num2) {
        return createDouble(num1, null) + createDouble(num2, null);
    }

    /**
     * 是否是空值
     * 针对BigDecimal、Integer、Double、String类型的数据
     * @param val
     * @return 0、null或空串及其他数据类型返回true
     */
    @SuppressWarnings("rawtypes")
    public static boolean isNoneValue(Object val) {
        return val == null
                || val instanceof Number && ((Number) val).doubleValue() == 0
                || val instanceof String && (StringUtils.isBlank((String) val) || "0".equals(val))
                || val instanceof List && ((List) val).isEmpty()
                || val instanceof Map && ((Map) val).isEmpty()
                || val instanceof JSONObject && ((JSONObject) val).isEmpty()
                || val instanceof JSONArray && ((JSONArray) val).isEmpty();
    }
    /**
     * 是否是空值
     * 针对BigDecimal、Integer、Double、String类型的数据
     * @param val
     * @return 0、null或空串及其他数据类型返回true
     */
    @SuppressWarnings("rawtypes")
    public static boolean isEmptyValue(Object val) {
        return val == null
                || val instanceof List && ((List) val).isEmpty()
                || val instanceof Map && ((Map) val).isEmpty()
                || val instanceof JSONObject && ((JSONObject) val).isEmpty()
                || val instanceof JSONArray && ((JSONArray) val).isEmpty();
    }
    /**
     * 将字符串Y和N转换成1(是)和0(否)
     * @param v
     * @return
     */
    public static Integer toIntegerFlag(String v) {
        if(v == null){
            return null;
        }
        return "Y".equalsIgnoreCase(v) ? 1 : 0;
    }

    /**
     * 四舍五入保留
     * @param var
     * @param scale 保留几位小数
     * @return
     */
    public static BigDecimal roundUp(BigDecimal var, int scale) {
        return var.setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 四舍五入保留
     * @param value
     * @param precision
     * @return
     */
    public static double roundUp(double value, String precision) {
        return Double.parseDouble(new DecimalFormat(precision).format(value));
    }

    /**
     * 判断一个BigDecimal是不是整数
     */
    public static boolean isInteger(BigDecimal bigDecimal) {
        return new BigDecimal(bigDecimal.intValue()).compareTo(bigDecimal) == 0;
    }

}
