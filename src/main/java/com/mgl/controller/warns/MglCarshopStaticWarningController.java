package com.mgl.controller.warns;


import com.mgl.bean.carshop.CarNumberDict;
import com.mgl.bean.warns.MglCarshopStaticWarning;
import com.mgl.service.warns.MglCarshopStaticWarningService;
import com.mgl.utils.excel.ExcelUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 算法一 前端控制器
 * </p>
 *
 * @author zhangqi
 * @since 2020-06-11
 */
@Controller
@RequestMapping("/mglCarshopStaticWarning")
public class MglCarshopStaticWarningController {

    @Resource
    private MglCarshopStaticWarningService mglCarshopStaticWarningService;
    /**
     * 导出
     * @param response
     */
    @RequestMapping("/exportExcel")
    public void exportExcel(HttpServletResponse response) {
        List<MglCarshopStaticWarning> list = mglCarshopStaticWarningService.findListOrder();
        ExcelUtils.createExcel(response, list, MglCarshopStaticWarning.class, "cars-warn.xlsx");
    }
}
