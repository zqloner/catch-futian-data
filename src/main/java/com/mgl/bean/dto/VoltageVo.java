package com.mgl.bean.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Title: VoltageVo   用于获取电芯编号的实体类。
 * @Description:
 * @Company: 盟固利
 * @author: 张奇
 * @date: ceate in 2020/6/11 16:19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class VoltageVo {
    private String key;
    private Double value;
}
