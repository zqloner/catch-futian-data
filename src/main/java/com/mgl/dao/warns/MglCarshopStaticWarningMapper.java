package com.mgl.dao.warns;

import com.mgl.bean.warns.MglCarshopStaticWarning;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 算法一 Mapper 接口
 * </p>
 *
 * @author zhangqi
 * @since 2020-06-11
 */
@Mapper
public interface MglCarshopStaticWarningMapper extends BaseMapper<MglCarshopStaticWarning> {

    List<MglCarshopStaticWarning> findListByCarVinAndDate(@Param("vin") String vin, @Param("start") String start, @Param("end") String end,@Param("cellNumber") String cellNumber);
    List<MglCarshopStaticWarning> findListOrder();
}
