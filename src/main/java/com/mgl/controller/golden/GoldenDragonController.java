package com.mgl.controller.golden;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mgl.bean.carshop.CarNumberDict;
import com.mgl.bean.golden.GoldenDragon;
import com.mgl.common.Gloables;
import com.mgl.service.golden.GoldenDragonService;
import com.mgl.utils.compress.CompressUtils;
import com.mgl.utils.csv.CsvExportUtil;
import com.mgl.utils.excel.ExcelUtils;
import com.mgl.utils.file.FileUtil;
import com.mgl.utils.props.BeanAndMap;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
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

    private HttpServletResponse response;

    /**
     * 导出
     *
     * @param response
     */
    @RequestMapping("/exportCsv")
    @ResponseBody
    public void exportExcel(HttpServletResponse response, HttpServletRequest request) {
        this.response = response;
        try {
            File file = new File("D:\\GoldenDragon");
            if (!file.exists()) {
                file.mkdir();
            }
            List<GoldenDragon> list = goldenDragonService.getOrderDate();
            List<Map<String, Object>> maps = new ArrayList<>();
            list.forEach(x -> {
                maps.add(BeanAndMap.beanToMap(x));
            });
            for (int i = 0;i < 2; i++) {
                FileOutputStream os = new FileOutputStream("D:\\GoldenDragon\\aaa"+i+".csv");
                CsvExportUtil.doExport(maps, Gloables.GOLDEN_TITLE, Gloables.GOLDEN_KEYS, os);
            }
            String[] extention = new String[]{".csv"};
            List<File> files = FileUtil.listFile(new File("D:\\GoldenDragon"), extention, true);
            System.out.println(files);
            if (files.size()==2) {
                Thread.sleep(1000);
                CompressUtils.zip("D:\\GoldenDragon",response.getOutputStream());
                CompressUtils.deleteDir(new File("D:\\GoldenDragon"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
