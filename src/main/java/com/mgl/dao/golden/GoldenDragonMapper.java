package com.mgl.dao.golden;

import com.mgl.bean.golden.GoldenDragon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 金龙数据实时抓取 Mapper 接口
 * </p>
 *
 * @author zhangqi
 * @since 2020-07-03
 */
@Mapper
public interface GoldenDragonMapper extends BaseMapper<GoldenDragon> {

    List<GoldenDragon> getOrderDate(String vin);

    /**
     * 查询前一天的数据
     * @param yesterday 前一天
     * @return 集合
     */
    List<GoldenDragon> queryDataTheDayBefore(@Param("yesterday") LocalDate yesterday);

    /**
     * 根据汽车vin查询查询数据
     * @param carVin 汽车vin
     * @param yesterday 时间
     * @return 汽车数据
     */
    List<GoldenDragon> findDataByCarVin(@Param("carVin") String carVin, @Param("yesterday") LocalDate yesterday);
}
