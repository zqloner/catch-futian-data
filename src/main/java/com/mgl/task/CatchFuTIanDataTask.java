package com.mgl.task;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.lang.Console;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mgl.bean.carshop.CarNumberDict;
import com.mgl.bean.carshop.MglCarshopTianfuData;
import com.mgl.bean.carshop.MglCarshopTianfuDataBak;
import com.mgl.bean.dto.FuTiamDetailDtoList;
import com.mgl.bean.dto.FuTianDetailDto;
import com.mgl.common.Gloables;
import com.mgl.sdk.http.MglRestTemplate;
import com.mgl.sdk.utils.GroupUtil;
import com.mgl.service.carshop.CarNumberDictService;
import com.mgl.service.carshop.MglCarshopTianfuDataBakService;
import com.mgl.service.carshop.MglCarshopTianfuDataService;
import com.mgl.utils.compress.CompressUtils;
import com.mgl.utils.csv.CsvExportUtil;
import com.mgl.utils.file.FileUtil;
import com.mgl.utils.ftp.ftpClientUtil.FtpTool;
import com.mgl.utils.httpclient.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @Title: CatchFuTIanDataTask
 * @Description:
 * @Company: 盟固利
 * @author: 张奇
 * @date: ceate in 2020/6/2 17:36
 */
@Component
public class CatchFuTIanDataTask {

    private static final Logger logger = LoggerFactory.getLogger(CatchFuTIanDataTask.class);

    private int dayNum = -2;

    @Resource
    private CarNumberDictService carNumberDictService;

    @Autowired
    private MglRestTemplate restTemplate;


    @Value("${brightease.ftpZipPath}")
    private String ftpZipPath;

    @Value("${ftp.host}")
    private String host;

    @Value("${ftp.port}")
    private Integer port;

    @Value("${ftp.username}")
    private String username;

    @Value("${ftp.password}")
    private String password;

    /**
     * 抓取数据   导出到csv和不存详情以及上传至FTP
     *
     * @throws Exception
     */
    @Scheduled(cron = "0 15 9 * * ? ")
    public void produceTopicNoDetail() throws Exception {
        List<LocalDate> localDates = new ArrayList<>();
        localDates.add(LocalDate.now());
        for (LocalDate localDate : localDates) {
            try {
                getFuTianData(localDate);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getFuTianData(LocalDate today) throws IOException {
        LocalDate yesterday = today.plusDays(-1);
        List<CarNumberDict> cars = carNumberDictService.list(new QueryWrapper<>(new CarNumberDict().setDelFlag(0)));
//        强制创建目录
        String dir = ftpZipPath + yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        File file = new File(dir);
        if (!file.exists()) {
            FileUtil.forceDirectory(dir);
        }
        for (CarNumberDict car : cars) {
            try {
                String path = Gloables.API_URL + "?" + Gloables.API_PARAM_TOKEN + "=" + Gloables.API_TOKEN + "&" + Gloables.API_PARAM_DATE + "=" + yesterday.toString()
                        + "&" + Gloables.API_PARAM_CARID + "=" + car.getCarVin();
                logger.info("访问路径:" + path);
                FuTiamDetailDtoList fuTiamDetailDtoList = restTemplate.getForObject(path, FuTiamDetailDtoList.class);
                logger.info("成功访问路径:" + path);
                List<FuTianDetailDto> data = fuTiamDetailDtoList.getData();
                String fileName = Gloables.SORT_INIT_NUMBER + car.getId() + Gloables.SPECIAL_SHORT_LINE + car.getCarVin() + Gloables.CSV_EXTENT;
                List<Map<String, Object>> datas = new ArrayList<>();
                FileOutputStream newOs = new FileOutputStream(dir + Gloables.SPECIAL_SLASH + fileName);
                data.forEach(x -> {
                    //                    每条数据的代码
                    Map<String, String> codes = x.getCodes();
                    //                    导出到csv
                    Map<String, Object> map = new HashMap<>();
                    //                    创建数据
                    map.put("vin", car.getCarVin());
                    map.put("car_current_time", x.getTime());
                    if (!StringUtils.isBlank(codes.get("1030002"))) {
                        try {
                            map.put("mileages", (Double.parseDouble(codes.get("1030002")) / 1000) + "");
                        } catch (NumberFormatException e) {
                            map.put("mileages", codes.get("1030002"));
                        }
                    }
                    map.put("speed", codes.get("1010027"));
                    //                    SOC
                    map.put("soc", codes.get("1110045"));
                    //                    总电流总电压最高温度最低温度
                    map.put("total_current", codes.get("1110044"));
                    map.put("total_voltage", codes.get("1110043"));
                    map.put("max_temperature", codes.get("1110050"));
                    map.put("min_temperature", codes.get("1110049"));
                    //                    最高最低电压以及编号
                    map.put("max_voltage", codes.get("1110048"));
                    map.put("min_voltage", codes.get("1110047"));
                    map.put("min_voltage_cell_code", codes.get("1110070"));
                    map.put("max_voltage_cell_code", codes.get("1110068"));
                    map.put("max_temperature_needle", codes.get("1110074"));
                    map.put("min_temperature_needle", codes.get("1110072"));
                    //                    获取codes键
                    List<String> collect = codes.keySet().stream().filter(y -> y.contains("1110107-1")).sorted((a, b) -> Integer.parseInt(a.substring(10)) - Integer.parseInt(b.substring(10))).collect(Collectors.toList());
                    List<String> collect1 = codes.keySet().stream().filter(y -> y.contains("1110108-1")).sorted((a, b) -> Integer.parseInt(a.substring(10)) - Integer.parseInt(b.substring(10))).collect(Collectors.toList());
                    List<Double> list3 = new ArrayList<>();
                    List<Double> list4 = new ArrayList<>();
                    for (int i = 0; i < collect.size(); i++) {
                        try {
                            list3.add(Double.valueOf(codes.get(collect.get(i))));
                        } catch (NumberFormatException e) {
                            list3.add(-99999d);
                        }
                    }
                    //                    单体电压
                    map.put("single_voltage", StringUtils.join(list3, "|"));
                    //                    绝缘电阻
                    map.put("insulation_resistance", codes.get("1110085"));
                    //                    单体温度
                    for (int i = 0; i < collect1.size(); i++) {
                        try {
                            list4.add(Double.valueOf(codes.get(collect1.get(i))));
                        } catch (NumberFormatException e) {
                            list4.add(-99999d);
                        }
                    }
                    map.put("singel_temperature", StringUtils.join(list4, "|"));
                    map.put("soc_low_alarm", codes.get("1110065"));
                    map.put("battery_high_temperature_alarm", codes.get("1110064"));
                    map.put("temperature_difference_alarm", codes.get("1110061"));
                    map.put("equip_overvoltage_alarm", codes.get("1110054"));
                    map.put("equipment_undervoltage_alarm", codes.get("1110053"));
                    map.put("system_mismatch_alarm", codes.get("1110052"));
                    map.put("maximum_alarm_level", codes.get("1110046"));
                    map.put("type_overcharge_alarm", codes.get("1140017"));
                    map.put("soc_jump_alarm", codes.get("1140019"));
                    map.put("insulation_alarm", codes.get("1110087"));
                    map.put("dc_status_alarm", codes.get("1130237"));
                    map.put("high_pressure_interlock_alarm", codes.get("1110157"));
                    map.put("poor_battery_consistency_alarm", codes.get("1110132"));
                    map.put("single_battery_overvoltage_alarm", codes.get("1130180"));
                    map.put("low_voltage_alarm_for_single_battery", codes.get("1130181"));
                    map.put("soc_high_alarm", codes.get("1130183"));
                    //                    这是导出csv
                    datas.add(map);
                });
                //                导出到csv
                CsvExportUtil.doExport(datas, Gloables.CSV_TITLES, Gloables.CSV_KEYS, newOs);
            } catch (Exception e) {
                e.printStackTrace();
                logger.info(car.getCarVin() + "生成csv失败");
                continue;
            }
        }
//        压缩
        CompressUtils.zip(dir, dir + Gloables.ZIP_EXTENT);
//        FTP
        FtpTool tool = new FtpTool(host, port, username, password);
        tool.initFtpClient();
        tool.CreateDirecroty(Gloables.FUTIAN_ZIP_PATH);
        boolean uploadFile = tool.uploadFile(Gloables.FUTIAN_ZIP_PATH, yesterday.format(DateTimeFormatter.ofPattern(Gloables.DATE_FORMAT_YYYYMMDD)) + Gloables.ZIP_EXTENT, dir + Gloables.ZIP_EXTENT);
//      删除文件夹
        if (uploadFile) {
            CompressUtils.deleteDirectory(new File(dir));
            CompressUtils.doDeleteEmptyDir(dir + Gloables.ZIP_EXTENT);
        }
        System.out.println(yesterday + "日=============》数据抓取完毕");
    }


}
