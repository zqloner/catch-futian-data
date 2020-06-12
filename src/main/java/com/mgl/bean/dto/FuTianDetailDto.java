package com.mgl.bean.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * @Title: FuTianDetailDto
 * @Description:
 * @Company: 盟固利
 * @author: 张奇
 * @date: ceate in 2020/6/9 8:39
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FuTianDetailDto {
    private String time;
    private Map<String,String> codes;
    private List<Object> datas;
}
