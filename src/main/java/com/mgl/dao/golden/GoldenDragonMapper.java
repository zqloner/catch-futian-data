package com.mgl.dao.golden;

import com.mgl.bean.golden.GoldenDragon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

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
}
