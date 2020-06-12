package com.mgl.controller.carshop;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mgl.bean.carshop.CarNumberDict;
import com.mgl.bean.carshop.MglCarshopFutianDataDetail;
import com.mgl.service.carshop.CarNumberDictService;
import com.mgl.service.carshop.MglCarshopFutianDataDetailService;
import com.mgl.utils.excel.ExcelUtils;
import com.mgl.utils.poi.ExportExcel2007;
import com.mgl.utils.poi.exception.ReportInternalException;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *  福田数据的导出控制器
 * @author zhangqi
 * @since 2020-06-08
 */
@Controller
@RequestMapping("/mglCarshopFutianDataDetail")
public class MglCarshopFutianDataDetailController {

    @Resource
    private MglCarshopFutianDataDetailService mglCarshopFutianDataDetailService;

    @Resource
    private CarNumberDictService carNumberDictService;

    /**
     * 每20万条数据放一个sheet
     * @param response
     */
    @RequestMapping("/exportExcel")
    public void exportFutianDataSXSSF(HttpServletResponse response) {
        List<MglCarshopFutianDataDetail> list = mglCarshopFutianDataDetailService.list(null);
        ExcelUtils.createExcel(response, list, MglCarshopFutianDataDetail.class, "futian_cars_detail.xlsx");
    }

    /**
     * 一个汽车放一个sheet
     * @param response
     */
    @RequestMapping("/exportExcelByVin")
    public void exportExcelByVin(HttpServletResponse response) {
        List<CarNumberDict> cars = carNumberDictService.list(new QueryWrapper<>(new CarNumberDict().setDelFlag(0)));
        List<List<MglCarshopFutianDataDetail>> lists = new ArrayList<>();
        cars.forEach(x->{
            lists.add(mglCarshopFutianDataDetailService.list(new QueryWrapper<>(new MglCarshopFutianDataDetail().setVin(x.getCarVin()))));
        });
        ExcelUtils.createExcelByCarVin(response, lists, MglCarshopFutianDataDetail.class, "futian_cars_detail.xlsx");
    }
}
