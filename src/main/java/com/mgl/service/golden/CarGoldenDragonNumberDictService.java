package com.mgl.service.golden;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mgl.bean.golden.CarGoldenDragonNumberDict;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fengwei
 * @since 2020-07-22
 */
public interface CarGoldenDragonNumberDictService extends IService<CarGoldenDragonNumberDict> {

    CarGoldenDragonNumberDict findByCarVin(String carVin);
}
