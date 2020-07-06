package com.mgl.service.golden;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mgl.bean.golden.GoldenDragon;
import com.baomidou.mybatisplus.extension.service.IService;

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
    List<GoldenDragon> getOrderDate();
}
