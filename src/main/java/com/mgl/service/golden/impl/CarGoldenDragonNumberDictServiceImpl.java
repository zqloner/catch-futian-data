package com.mgl.service.golden.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mgl.bean.golden.CarGoldenDragonNumberDict;
import com.mgl.dao.golden.CarGoldenDragonNumberDictMapper;
import com.mgl.service.golden.CarGoldenDragonNumberDictService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fengwei
 * @since 2020-07-22
 */
@Service
public class CarGoldenDragonNumberDictServiceImpl extends ServiceImpl<CarGoldenDragonNumberDictMapper, CarGoldenDragonNumberDict> implements CarGoldenDragonNumberDictService {

    @Resource
    private CarGoldenDragonNumberDictMapper carGoldenDragonNumberDictMapper;

    @Override
    public CarGoldenDragonNumberDict findByCarVin(String carVin) {
        return carGoldenDragonNumberDictMapper.findByVinName(carVin);
    }

    @Override
    public List<CarGoldenDragonNumberDict> queryCarVinList() {
        return carGoldenDragonNumberDictMapper.findCarVin();
    }

}
