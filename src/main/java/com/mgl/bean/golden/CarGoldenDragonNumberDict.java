package com.mgl.bean.golden;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.mgl.utils.excel.annotation.ExcelColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author fengwei
 * @since 2020-07-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CarGoldenDragonNumberDict implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增长主键
     */
    @TableId(value = "car_id", type = IdType.AUTO)
    private Long carId;

    /**
     * 车牌号
     */
    @ExcelColumn(value = "车牌号",col = 1)
    private String carNum;

    /**
     * 车号
     */
    @ExcelColumn(value = "车号",col = 2)
    private String carNo;

    /**
     * 终端编号
     */
    @ExcelColumn(value = "终端编号",col = 3)
    private String carTerminalNum;

    /**
     * VIN
     */
    @ExcelColumn(value = "VIN",col = 4)
    private String carVin;

    /**
     * 车型
     */
    @ExcelColumn(value = "车型",col = 5)
    private String carType;

    /**
     * 里程(公里）
     */
    @ExcelColumn(value = "里程(公里）",col = 6)
    private String carMileage;

    /**
     * 订单客户
     */
    @ExcelColumn(value = "订单客户",col = 7)
    private String carCustomerClient;

    /**
     * 上报时间
     */
    @ExcelColumn(value = "上报时间",col = 8)
    private String carReportDate;

    /**
     * 最后位置
     */
    @ExcelColumn(value = "最后位置",col = 9)
    private String carFinalLocation;

    /**
     * 软删除
     */
    private Integer carFlag;


}
