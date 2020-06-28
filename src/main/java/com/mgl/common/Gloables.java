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
    String CSV_TITLES = "vin,车速,数据时间,里程,电池单体电压最高值,最高电压电池单体代号,电池单体电压最低值,最低电压电池单体代号,总电压,总电流,最高温度值,最低温度值,最高温度探针序号,最低温度探针序号,SOC,单体电池温度,单体电池电压,绝缘电阻,SOC低报警,电池高温报警,温度差异报警,车载储能装置类型过压报警,车载储能装置类型欠压报警,充电储能系统不匹配报警,最高报警等级,车载储能装置类型过充报警,SOC跳变报警,绝缘报警,DC状态报警,高压互锁报警,电池单体一致性差报警,单体蓄电池过压报警,单体蓄电池欠压报警,SOC过高报警,加热系统故障,放电超出限制报警,充电超出限制报警,低温报警";  // 设置表头
    String CSV_KEYS = "vin,speed,car_current_time,mileages,max_voltage,max_voltage_cell_code,min_voltage,min_voltage_cell_code,total_voltage,total_current,max_temperature,min_temperature,max_temperature_needle,min_temperature_needle,soc,singel_temperature,single_voltage,insulation_resistance,soc_low_alarm,battery_high_temperature_alarm,temperature_difference_alarm,equip_overvoltage_alarm,equipment_undervoltage_alarm,system_mismatch_alarm,maximum_alarm_level,type_overcharge_alarm,soc_jump_alarm,insulation_alarm,dc_status_alarm,high_pressure_interlock_alarm,poor_battery_consistency_alarm,single_battery_overvoltage_alarm,low_voltage_alarm_for_single_battery,soc_high_alarm,heating_system_failure,discharge_beyond_limit_alarm,dischargeIn_beyond_limit_alarm,low_temperature_warning";  // 设置每列字段

//    报警临界值
    Double WORINING_VALUE = 40D;
}
