package com.mgl.service.golden;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mgl.bean.golden.GoldenDragon;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 金龙数据实时抓取 服务类
 * </p>
 *
 * @author zhangqi
 * @since 2020-07-03
 */
public interface GoldenDragonService extends IService<GoldenDragon> {
    List<GoldenDragon> getOrderDate(String vin);

    /**
     * 查询前一天的数据
     * @param yesterday 前一天
     * @return 集合
     */
    List<GoldenDragon> queryDataTheDayBrfore(LocalDate yesterday);

    /**
     * 查询汽车
     * @param carVin
     * @param yesterday
     * @return
     */
    List<GoldenDragon> queryDataByCarVin(String carVin, LocalDate yesterday);
}
