package com.mgl.controller.golden;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.mgl.bean.golden.GoldenDragon;
import com.mgl.common.Gloables;
import com.mgl.service.golden.GoldenDragonService;
import com.mgl.utils.compress.CompressUtils;
import com.mgl.utils.csv.CsvExportUtil;
import com.mgl.utils.file.FileUtil;
import com.mgl.utils.props.BeanAndMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
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
    private static final Logger logger = LoggerFactory.getLogger(GoldenDragonController.class);

    @Resource
    private GoldenDragonService goldenDragonService;

    @Value("${brightease.goldenDragonCsvPath}")
    private String goldenDragonCsvPath;

    /**
     * 导出
     *
     * @param response
     */
    @RequestMapping("/exportCsv")
    @ResponseBody
    public void exportExcel(HttpServletResponse response, HttpServletRequest request) {
        try {
            List<String> cars = new ArrayList<>();
            cars.add("LA9CB22D3KALA6162");
            cars.add("LA6C7GAB1JC304865");
            cars.add("LA6C7GAB2JB201959");
            cars.add("LA9CB22D0K0LA6058");
            cars.add("LA6C7K1B7JB201894");
//            创建文件夹
            File file = new File(goldenDragonCsvPath);
            if (!file.exists()) {
                file.mkdir();
            }

            Long flag  = 0L;
            for (String car : cars) {
                List<GoldenDragon> list = goldenDragonService.getOrderDate(car);
                List<Map<String, Object>> maps = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(list)) {
                    flag ++;
                }
                list.forEach(x -> {
                    maps.add(BeanAndMap.beanToMap(x));
                    try {
                        FileOutputStream os = new FileOutputStream(goldenDragonCsvPath + car + Gloables.CSV_EXTENT);
                        CsvExportUtil.doExport(maps, Gloables.GOLDEN_TITLE, Gloables.GOLDEN_KEYS, os);
                    } catch (Exception e) {
                        logger.info("抛出异常啦");
                    }
                });
            }
            String[] extention = new String[]{Gloables.CSV_EXTENT};
            List<File> files = FileUtil.listFile(new File(goldenDragonCsvPath), extention, true);
            System.out.println(files);
            if (flag==files.size()) {
                Thread.sleep(2000);
                CompressUtils.zip(goldenDragonCsvPath, response.getOutputStream());
                CompressUtils.deleteDirectory(new File(goldenDragonCsvPath));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
