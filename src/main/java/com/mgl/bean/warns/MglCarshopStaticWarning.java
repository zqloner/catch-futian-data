package com.mgl.bean.warns;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.mgl.utils.excel.annotation.ExcelColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 算法一
 * </p>
 *
 * @author zhangqi
 * @since 2020-06-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MglCarshopStaticWarning implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ExcelColumn(value = "预警类型",col = 2,readConverterExp = "1=一级预警,2=二级预警")
    private Integer type;

    @ExcelColumn(value = "电池编号",col = 4)
    private String cellNumber;

    private Integer delFlag;

    @ExcelColumn(value = "预警值",col = 3)
    private Double value;

    @ExcelColumn(value = "汽车编号",col = 1)
    private String vin;

    private Long curretsTimeSeconds;

    @ExcelColumn(value = "日期",col = 5)
    private String curretsDateTime;

}
