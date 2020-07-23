package com.mgl.sdk.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @Description TODO
 * @Author fengwei
 * @Date 2020/7/22 16:37
 * @Version 1.0
 */
public class DateFormatUtil {

    /**
     * yyyyMMdd格式的日期
     * @param date 待格式化的日期
     * @return 格式化后的日期
     */
    public static String firstDateFormat(LocalDate date){
        return date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}
