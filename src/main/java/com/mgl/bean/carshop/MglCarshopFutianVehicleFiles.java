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
 * 车辆档案总表
 * </p>
 *
 * @author zhangqi
 * @since 2020-06-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MglCarshopFutianVehicleFiles implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 省份
     */
    @ExcelColumn(value = "省份",col = 1)
    private String province;

    /**
     * 市级
     */
    @ExcelColumn(value = "市级",col = 2)
    private String city;

    /**
     * 区/县
     */
    @ExcelColumn(value = "区/县",col = 3)
    private String countyInfo;

    /**
     * 整车厂
     */
    @ExcelColumn(value = "整车厂",col = 4)
    private String carFactory;

    /**
     * 电池类型
     */
    @ExcelColumn(value = "电池类型",col = 5)
    private String batteryType;

    /**
     * 容量
     */
    @ExcelColumn(value = "容量",col = 6)
    private String capacity;

    /**
     * 电池系统
     */
    @ExcelColumn(value = "电池系统",col = 7)
    private String batterySystem;

    /**
     * 并联
     */
    @ExcelColumn(value = "并联",col = 8)
    private String parallelConnection;

    /**
     * 串联
     */
    @ExcelColumn(value = "串联",col = 9)
    private String seriesConnection;

    /**
     * 整车系统
     */
    @ExcelColumn(value = "整车系统",col = 10)
    private String vehicleSystem;

    /**
     * 结构（本项内容为开口填充项）
     */
    @ExcelColumn(value = "结构（本项内容为开口填充项）",col = 11)
    private String construction;

    /**
     * 整车配置，尽量补充
     */
    @ExcelColumn(value = "整车配置，尽量补充",col = 12)
    private String vehicleConfiguration;

    /**
     * 订单号
     */
    @ExcelColumn(value = "订单号",col = 13)
    private String orderNumber;

    /**
     * 数量
     */
    @ExcelColumn(value = "数量",col = 14)
    private Integer number;

    /**
     * 运行日期(严格按照格式:1970-01-01)
     */
    @ExcelColumn(value = "运行日期(严格按照格式:1970-01-01)",col = 15)
    private String runDate;

    /**
     * 质保期限（年/公里）
     */
    @ExcelColumn(value = "质保期限（年/公里）",col = 16)
    private String warrantyPeriod;

    /**
     * 是否
过保/报废
     */
    @ExcelColumn(value = "是否（过保/报废）",col = 17)
    private String status;

    /**
     * 责任工程师
     */
    @ExcelColumn(value = "责任工程师",col = 18)
    private String dutyEngineer;

    /**
     * 覆盖授权服务站
     */
    @ExcelColumn(value = "覆盖授权服务站",col = 19)
    private String serviceStation;

    /**
     * 备注
     */
    @ExcelColumn(value = "备注",col = 20)
    private String note;

    /**
     * 万公里
2020年3月
     */
    @ExcelColumn(value = "万公里2020年3月",col = 21)
    private String threeMayKilometers;

    /**
     * 万公里2020年2月
     */
    @ExcelColumn(value = "万公里2020年2月",col = 22)
    private String twoSecondKilometers;

    /**
     * 问题
     */
    @ExcelColumn(value = "问题",col = 23)
    private String question;

    /**
     * 公里差
     */
    @ExcelColumn(value = "公里差",col = 24)
    private String kePoor;

    /**
     * 删除标识符
     */
    private Integer delflag;


}
