package com.mgl.dao.carshop;

import com.mgl.bean.carshop.CarNumberDict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhangqi
 * @since 2020-06-11
 */
@Mapper
public interface CarNumberDictMapper extends BaseMapper<CarNumberDict> {

    CarNumberDict findByVinName(String carVin);
}
