package com.mgl.bean.carshop;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author fengwei
 * @since 2020-07-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MglCarshopTianfuDataBak implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 汽车vin
     */
    private String carVin;

    private String jsonContent;

    /**
     * 内容
     */
    private String textContent;

    /**
     * 创建日期
     */
    private LocalDate createDate;

    /**
     * 数据真实日期
     */
    private LocalDate realDate;


}
