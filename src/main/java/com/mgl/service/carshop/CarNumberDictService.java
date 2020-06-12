package com.mgl.service.carshop;

import com.mgl.bean.carshop.CarNumberDict;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhangqi
 * @since 2020-06-11
 */
public interface CarNumberDictService extends IService<CarNumberDict> {
    CarNumberDict findByCarVin(String carVin);
}
