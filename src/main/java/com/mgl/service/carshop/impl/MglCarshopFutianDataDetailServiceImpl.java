package com.mgl.service.carshop.impl;

import com.mgl.bean.carshop.MglCarshopFutianDataDetail;
import com.mgl.dao.carshop.MglCarshopFutianDataDetailMapper;
import com.mgl.service.carshop.MglCarshopFutianDataDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhangqi
 * @since 2020-06-09
 */
@Service
public class MglCarshopFutianDataDetailServiceImpl extends ServiceImpl<MglCarshopFutianDataDetailMapper, MglCarshopFutianDataDetail> implements MglCarshopFutianDataDetailService {

    @Resource
    private MglCarshopFutianDataDetailMapper mglCarshopFutianDataDetailMapper;

    @Override
    public List<Object> findDetailAll() {
        return mglCarshopFutianDataDetailMapper.findAll();
    }

    @Override
    public List<MglCarshopFutianDataDetail> findDetailsByVin(String vin,String start,String end){
        return  mglCarshopFutianDataDetailMapper.findDetailsByVin(vin,start,end);
    }
}
