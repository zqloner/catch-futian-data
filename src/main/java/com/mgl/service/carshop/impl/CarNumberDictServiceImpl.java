package com.mgl.service.carshop.impl;

import com.mgl.bean.carshop.CarNumberDict;
import com.mgl.dao.carshop.CarNumberDictMapper;
import com.mgl.service.carshop.CarNumberDictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhangqi
 * @since 2020-06-11
 */
@Service
public class CarNumberDictServiceImpl extends ServiceImpl<CarNumberDictMapper, CarNumberDict> implements CarNumberDictService {

    @Resource
    private CarNumberDictMapper carNumberDictMapper;

    @Override
    public CarNumberDict findByCarVin(String carVin) {
        return carNumberDictMapper.findByVinName(carVin);
    }
}
