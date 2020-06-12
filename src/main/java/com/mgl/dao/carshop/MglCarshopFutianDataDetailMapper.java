package com.mgl.dao.carshop;

import com.mgl.bean.carshop.MglCarshopFutianDataDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhangqi
 * @since 2020-06-09
 */
@Mapper
public interface MglCarshopFutianDataDetailMapper extends BaseMapper<MglCarshopFutianDataDetail> {

    public List<Object> findAll();

    List<MglCarshopFutianDataDetail> findDetailsByVin(@Param("vin") String vin, @Param("start")String start, @Param("end")String end);
}
