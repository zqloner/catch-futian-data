package com.mgl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @Description:
 * @Author: zhangqi
 * @CreateTime: 2020/6/2311:39
 * @Company: MGL
 */
public class MyTest {
    public static void main(String[] args) throws Exception{
        String text = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        System.out.println(text);
    }
}
