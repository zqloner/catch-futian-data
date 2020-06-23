package com.mgl.bean;

/**
 * @Description:
 * @Author: zhangqi
 * @CreateTime: 2020/6/2311:38
 * @Company: MGL
 */

import lombok.Data;

/**
 * 车辆实体类
 */
@Data
public class Car {

    private String id;
    private String model;//型号
    private String color;//颜色
    private String volume;//排量
    private int year;//年份
}