package com.mgl.service.warns;

import com.mgl.bean.warns.MglCarshopStaticWarning;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 算法一 服务类
 * </p>
 *
 * @author zhangqi
 * @since 2020-06-11
 */
public interface MglCarshopStaticWarningService extends IService<MglCarshopStaticWarning> {

    List<MglCarshopStaticWarning> findListByCarVinAndDate(String vin, String start, String end,String cellNumber);
    List<MglCarshopStaticWarning> findListOrder();
}
