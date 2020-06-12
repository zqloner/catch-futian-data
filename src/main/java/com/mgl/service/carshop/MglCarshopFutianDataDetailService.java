package com.mgl.service.carshop;

import com.mgl.bean.carshop.MglCarshopFutianDataDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhangqi
 * @since 2020-06-09
 */
public interface MglCarshopFutianDataDetailService extends IService<MglCarshopFutianDataDetail> {

    public List<Object> findDetailAll();

    List<MglCarshopFutianDataDetail> findDetailsByVin(String vin,String start,String end);
}
