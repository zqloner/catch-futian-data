package com.mgl.common;

/**
 * @Description:
 * @Author: zhangqi
 * @CreateTime: 2020/6/2314:36
 * @Company: MGL
 */
public interface Gloables {
    //    token
    String API_PARAM_TOKEN = "token";
    String API_PARAM_DATE = "queryDate";
    String API_PARAM_CARID = "carId";
    String API_URL = "http://api.itink.com.cn/api/vehicle/getCanBusByCarId.json";
    String API_TOKEN = "2b37d26a9d4446d48a0a87a0f6852355";

    //    导出到CSV
    String CSV_EXTENT = ".csv";    //导出的路径,用于拼接
    String CSV_PATH = "D:\\aaa\\";    //导出的路径,用于拼接
    String CSV_TITLES = "vin,车速,时间,里程,单体最高电压,单体坐高电压编号,单体最低电压,单体最低电压编号,总电压,总电流,最高温度,最低温度,SOC";  // 设置表头
    String CSV_KEYS = "vin,speed,car_current_time,mileages,max_voltage,max_voltage_cell_code,min_voltage,min_voltage_cell_code,total_voltage,total_current,max_temperature,min_temperature,soc";  // 设置每列字段

//    报警临界值
    Double WORINING_VALUE = 40D;
}
