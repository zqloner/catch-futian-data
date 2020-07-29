package com.mgl.service.golden.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mgl.bean.golden.GoldenDragon;
import com.mgl.dao.golden.GoldenDragonMapper;
import com.mgl.service.golden.GoldenDragonService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 金龙数据实时抓取 服务实现类
 * </p>
 *
 * @author zhangqi
 * @since 2020-07-03
 */
@Service("goldenDragonServiceImpl")
public class GoldenDragonServiceImpl extends ServiceImpl<GoldenDragonMapper, GoldenDragon> implements GoldenDragonService {

    @Resource
    private GoldenDragonMapper dragonMapper;

    @Override
    public List<GoldenDragon> getOrderDate(String vin) {
        return dragonMapper.getOrderDate(vin);
    }

    @Override
    public List<GoldenDragon> queryDataTheDayBrfore(LocalDate yesterday) {
        return dragonMapper.queryDataTheDayBefore(yesterday);
    }

    @Override
    public List<GoldenDragon> queryDataByCarVin(String carVin, LocalDate yesterday) {
        return dragonMapper.findDataByCarVin(carVin, yesterday);
    }
}
