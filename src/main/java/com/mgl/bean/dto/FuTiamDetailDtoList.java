package com.mgl.bean.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Title: FuTiamDetailDtoList
 * @Description:
 * @Company: 盟固利
 * @author: 张奇
 * @date: ceate in 2020/6/9 8:44
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FuTiamDetailDtoList {
    private List<FuTianDetailDto> data;
    private Integer code;
}
