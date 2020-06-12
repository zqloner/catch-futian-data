package com.mgl.bean.carshop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.mgl.utils.excel.annotation.ExcelColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhangqi
 * @since 2020-06-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MglCarshopFutianDataDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ExcelColumn(value = "汽车vin", col = 1)
    private String vin;

    /**
     * time   时间
     */
    @ExcelColumn(value = "time", col = 2)
    private String carCurrentTime;

    /**
     *   里程   "1030002" 
     */
    @ExcelColumn(value = "里程(km)", col = 3)
    private String mileages;

    /**
     * 车速   1010027
     */
    @ExcelColumn(value = "车速", col = 4)
    private String speed;

    /**
     * 运行模式  1140013
     */
    @ExcelColumn(value = "运行模式", col = 5)
    private String runModel;

    /**
     * 充电状态 1110004
     */
    @ExcelColumn(value = "充电状态", col = 6)
    private String chargeState;

    /**
     * SOC    1110045
     */
    @ExcelColumn(value = "SOC", col = 7)
    private String soc;

    /**
     * 总电流   1110044
     */
    @ExcelColumn(value = "总电流", col = 8)
    private String totalCurrent;

    /**
     * 总电压   1110043
     */
    @ExcelColumn(value = "总电压", col = 9)
    private String totalVoltage;

    /**
     * 最高温度   1110050
     */
    @ExcelColumn(value = "最高温度", col = 10)
    private String maxTemperature;

    /**
     * 最低温度  1110049 
     */
    @ExcelColumn(value = "最低温度", col = 11)
    private String minTemperature;

    /**
     * 电池单体电压最高值    1110048
     */
    @ExcelColumn(value = "电池单体电压最高值", col = 12)
    private String maxVoltage;

    /**
     * 电池单体电压最低值    1110047
     */
    @ExcelColumn(value = "电池单体电压最低值", col = 13)
    private String minVoltage;

    /**
     * 最低电压电池单体代号   1110070
     */
    @ExcelColumn(value = "最低电压电池单体代号", col = 14)
    private String minVoltageCellCode;

    /**
     * 最高电压电池单体代号   1110068
     */
    @ExcelColumn(value = "最高电压电池单体代号", col = 15)
    private String maxVoltageCellCode;

    /**
     * 最低电压电池箱代号   1110069
     */
    @ExcelColumn(value = "最低电压电池箱代号", col = 16)
    private String minVoltageBoxCode;

    /**
     * 最高电压电池箱代号   1110067
     */
    @ExcelColumn(value = "最高电压电池箱代号", col = 17)
    private String maxVoltageBoxCode;

    /**
     * 最低温度探针序号    1110074
     */
    @ExcelColumn(value = "最低温度探针序号", col = 18)
    private String minTemperatureNeedle;

    /**
     * 最高温度探针序号   1110072
     */
    @ExcelColumn(value = "最高温度探针序号", col = 19)
    private String maxTemperatureNeedle;

    /**
     * 单体电池最低温度箱号   1110073
     */
    @ExcelColumn(value = "单体电池最低温度箱号", col = 20)
    private String minTemperatrureBoxCode;

    /**
     * 单体电池最高温度箱号   1110071
     */
    @ExcelColumn(value = "单体电池最高温度箱号", col = 21)
    private String maxTemperatrureBoxCode;

    /**
     * 单体电池电压(这是多个)   1110107-1
     */
    @ExcelColumn(value = "单体电池电压", col = 22)
    private String singleCellVoltage;

    /**
     * 单体电池电压(这是多个）   1110108-1
     */
    @ExcelColumn(value = "单体电池电压", col = 23)
    private String singleCellTemperature;

    /**
     * 电池管理系统软件版本信息时间  1130195
     */
    @ExcelColumn(value = "电池管理系统软件版本信息时间", col = 24)
    private String batteryVersionInformation;

    /**
     * 单体蓄电池总数   1110076-1
     */
    @ExcelColumn(value = "单体蓄电池总数", col = 25)
    private String totalNumberOfSingleBatteries;

    /**
     * SOC低报警   1110065
     */
    @ExcelColumn(value = "SOC低报警", col = 26)
    private String socLowAlarm;

    /**
     * 电池高温报警    1110064
     */
    @ExcelColumn(value = "电池高温报警", col = 27)
    private String batteryHighTemperatureAlarm;

    /**
     * 温度差异报警     1110061
     */
    @ExcelColumn(value = "温度差异报警", col = 28)
    private String temperatureDifferenceAlarm;

    /**
     * 车载储能装置类型过压报警    1110054
     */
    @ExcelColumn(value = "车载储能装置类型过压报警", col = 29)
    private String equipOvervoltageAlarm;

    /**
     * 车载储能装置类型欠压报警    1110053
     */
    @ExcelColumn(value = "车载储能装置类型欠压报警", col = 30)
    private String equipmentUndervoltageAlarm;

    /**
     * 充电储能系统不匹配报警    1110052
     */
    @ExcelColumn(value = "充电储能系统不匹配报警", col = 31)
    private String systemMismatchAlarm;

    /**
     * 最高报警等级   1110046
     */
    @ExcelColumn(value = "最高报警等级", col = 32)
    private String maximumAlarmLevel;

    /**
     * 车载储能装置类型过充报警  1140017
     */
    @ExcelColumn(value = "车载储能装置类型过充报警", col = 33)
    private String typeOverchargeAlarm;

    /**
     * SOC跳变报警   1140019
     */
    @ExcelColumn(value = "SOC跳变报警", col = 34)
    private String socJumpAlarm;

    /**
     * 绝缘报警   1110087
     */
    @ExcelColumn(value = "绝缘报警", col = 35)
    private String insulationAlarm;

    /**
     * DC/DC状态报警   1130237
     */
    @ExcelColumn(value = "DC状态报警", col = 36)
    private String dcStatusAlarm;

    /**
     * DC/DC温度报警   1130238
     */
    @ExcelColumn(value = "DC温度报警", col = 37)
    private String dcTemperatrureAlarm;

    /**
     * 高压互锁报警   1110157
     */
    @ExcelColumn(value = "高压互锁报警", col = 38)
    private String highPressureInterlockAlarm;

    /**
     * 电池单体一致性差报警   1110132
     */
    @ExcelColumn(value = "电池单体一致性差报警", col = 39)
    private String poorBatteryConsistencyAlarm;

    /**
     * 单体蓄电池过压报警    1130180
     */
    @ExcelColumn(value = "单体蓄电池过压报警", col = 40)
    private String singleBatteryOvervoltageAlarm;

    /**
     * 单体蓄电池欠压报警    1130181
     */
    @ExcelColumn(value = "单体蓄电池欠压报警", col = 41)
    private String lowVoltageAlarmForSingleBattery;

    /**
     * SOC过高报警    1130183
     */
    @ExcelColumn(value = "SOC过高报警", col = 42)
    private String socHighAlarm;

    /**
     * SOC过高报警    1130183
     */
    @ExcelColumn(value = "订单号", col = 43)
    private String orderNumber;

}
