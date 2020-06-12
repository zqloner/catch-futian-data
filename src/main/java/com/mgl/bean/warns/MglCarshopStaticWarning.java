package com.mgl.bean.warns;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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

    private Double staticPresure;

    private Integer type;

    private String cellNumber;

    private Integer delFlag;

    private Double value;

    private String vin;

    private Long curretsTimeSeconds;

    private String curretsDateTime;

}
