package com.mgl.dao.golden;

import com.mgl.bean.golden.CarGoldenDragonNumberDict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fengwei
 * @since 2020-07-22
 */
@Mapper
public interface CarGoldenDragonNumberDictMapper extends BaseMapper<CarGoldenDragonNumberDict> {

    CarGoldenDragonNumberDict findByVinName(String carVin);

    /**
     * 查询汽车vin
     * @return
     */
    List<String> findCarVin();

    List<String> getCarsInfo();
}
