package com.mgl.controller.golden;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mgl.bean.carshop.CarNumberDict;
import com.mgl.bean.golden.GoldenDragon;
import com.mgl.service.golden.GoldenDragonService;
import com.mgl.utils.csv.CsvExportUtil;
import com.mgl.utils.excel.ExcelUtils;
import com.mgl.utils.props.BeanAndMap;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 金龙数据实时抓取 前端控制器
 * </p>
 *
 * @author zhangqi
 * @since 2020-07-03
 */
@Controller
@RequestMapping("/goldenDragon")
public class GoldenDragonController {

    @Resource
    private GoldenDragonService goldenDragonService;

    /**
     * 导出
     *
     * @param response
     */
    @RequestMapping("/exportCsv")
    public void exportExcel(HttpServletResponse response) {
        try {
            FileOutputStream os = new FileOutputStream("D:\\aaa.csv");
            String tiltle = "vin,总电压,总电流,soc,最高电压电池子系统号,最高电压电池单体代号,电池单体电压最高值,最低电压电池子系统号,最低电压电池单体代号,电池单体电压最低值,最高温度子系统号,最高温度探针序号,最高温度值,最低温度子系统号,最低温度探针序号,最低温度值,通用报警标志,时间";
            String keys = "vin,totalVoltage,totalCurrent,soc,paramsFirst,paramsSecond,paramsThird,paramsFouth,paramsFiveth,paramsSix,paramsSeven,paramsEight,paramsTen,paramsEleven,paramsTewlve,paramsThirteen,paramsFourteen,dataCurrentTime";
            List<GoldenDragon> list = goldenDragonService.getOrderDate();
            List<Map<String, Object>> maps = new ArrayList<>();
            list.forEach(x->{
                maps.add(BeanAndMap.beanToMap(x));
            });
            CsvExportUtil.doExport(maps,tiltle,keys,os);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
