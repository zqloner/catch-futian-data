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
 * @since 2020-06-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CarNumberDict implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * VIN
     */
    @ExcelColumn(value = "VIN",col = 1)
    private String carVin;

    /**
     * 车牌号
     */
    @ExcelColumn(value = "车牌号",col = 2)
    private String carNumber;

    /**
     * 省份
     */
    @ExcelColumn(value = "省份",col = 3)
    private String province;

    /**
     * 市级
     */
    @ExcelColumn(value = "市级",col = 4)
    private String city;

    /**
     * 区/县
     */
    @ExcelColumn(value = "区县",col = 5)
    private String countyInfo;

    /**
     * 整车客户
     */
    @ExcelColumn(value = "整车客户",col = 6)
    private String vehicleCustomer;

    /**
     * 电池编号
     */
    @ExcelColumn(value = "电池编号",col = 7)
    private String batteryNumber;

    /**
     * 订单号
     */
    @ExcelColumn(value = "订单号",col = 8)
    private String orderNumber;

    /**
     * 电池类型
     */
    @ExcelColumn(value = "电池类型",col = 9)
    private String batteryType;

    /**
     * 单体容量
     */
    @ExcelColumn(value = "单体容量",col = 10)
    private String singleCapacity;

    /**
     * 现BCU软件版本
     */
    @ExcelColumn(value = "现BCU软件版本",col = 11)
    private String nowBcuSoftwareVersion;

    /**
     * 原BCU软件版本
     */
    @ExcelColumn(value = "原BCU软件版本",col = 12)
    private String originalBcuSoftwareVersion;

    /**
     * 公交线路
     */
    @ExcelColumn(value = "公交线路",col = 13)
    private String busRoute;

    /**
     * 公交公司
     */
    @ExcelColumn(value = "公交公司",col = 14)
    private String busCompany;

    /**
     * 车辆投入使用时间
     */
    @ExcelColumn(value = "车辆投入使用时间",col = 15)
    private String vehicleServiceTime;

    /**
     * 混合模式
     */
    @ExcelColumn(value = "混合模式",col = 16)
    private String mixedModel;

    /**
     * 主机厂车工号
     */
    @ExcelColumn(value = "主机厂车工号",col = 17)
    private String mainTurningNumber;

    /**
     * 原电池编号
     */
    @ExcelColumn(value = "原电池编号",col = 18)
    private String galvanicCellNumber;

    /**
     * 备注
     */
    @ExcelColumn(value = "备注",col = 19)
    private String note;

    /**
     * 删除标识符
     */
    private Integer delFlag;


}
