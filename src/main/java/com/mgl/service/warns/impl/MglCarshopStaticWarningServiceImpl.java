package com.mgl.service.warns.impl;

import com.mgl.bean.warns.MglCarshopStaticWarning;
import com.mgl.dao.warns.MglCarshopStaticWarningMapper;
import com.mgl.service.warns.MglCarshopStaticWarningService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 算法一 服务实现类
 * </p>
 *
 * @author zhangqi
 * @since 2020-06-11
 */
@Service
public class MglCarshopStaticWarningServiceImpl extends ServiceImpl<MglCarshopStaticWarningMapper, MglCarshopStaticWarning> implements MglCarshopStaticWarningService {
    @Resource
    private MglCarshopStaticWarningMapper mglCarshopStaticWarningMapper;

    @Override
    public List<MglCarshopStaticWarning> findListByCarVinAndDate(String vin, String start, String end) {
        return mglCarshopStaticWarningMapper.findListByCarVinAndDate(vin, start,end);
    }
}
