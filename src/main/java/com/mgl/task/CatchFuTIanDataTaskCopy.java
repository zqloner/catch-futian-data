package com.mgl.task;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mgl.bean.carshop.CarNumberDict;
import com.mgl.bean.carshop.MglCarshopTianfuData;
import com.mgl.bean.carshop.MglCarshopTianfuDataBak;
import com.mgl.bean.dto.FuTiamDetailDtoList;
import com.mgl.bean.dto.FuTianDetailDto;
import com.mgl.bean.dto.GoldenDragonDto;
import com.mgl.bean.golden.GoldenDragon;
import com.mgl.common.Gloables;
import com.mgl.service.carshop.CarNumberDictService;
import com.mgl.service.carshop.MglCarshopFutianDataDetailService;
import com.mgl.service.carshop.MglCarshopTianfuDataBakService;
import com.mgl.service.carshop.MglCarshopTianfuDataService;
import com.mgl.service.golden.GoldenDragonService;
import com.mgl.service.warns.MglCarshopStaticWarningService;
import com.mgl.utils.compress.CompressUtils;
import com.mgl.utils.csv.CsvExportUtil;
import com.mgl.utils.file.FileUtil;
import com.mgl.utils.ftp.ftpClientUtil.FtpTool;
import com.mgl.utils.httpclient.HttpClientUtil;
import com.mgl.utils.selfutil.MySelfUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Title: CatchFuTIanDataTask
 * @Description:
 * @Company: 盟固利
 * @author: 张奇
 * @date: ceate in 2020/6/2 17:36
 */
@Component
public class CatchFuTIanDataTaskCopy {

    private static final Logger logger = LoggerFactory.getLogger(CatchFuTIanDataTaskCopy.class);

    @Resource
    private MglCarshopTianfuDataService mglCarshopTianfuDataService;
    @Autowired
    private MglCarshopTianfuDataBakService mglCarshopTianfuDataBakService;
    @Resource
    private CarNumberDictService carNumberDictService;
    @Resource
    private MglCarshopFutianDataDetailService mglCarshopFutianDataDetailService;
    @Resource
    private MglCarshopStaticWarningService mglCarshopStaticWarningService;
    @Resource
    private GoldenDragonService goldenDragonService;

//    @Value("${brightease.csvBeiyouPath}:")
//    private String csvBeiyouPath;
//
//    @Value("${brightease.csvNewPath}")
//    private String csvNewPath;

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

    private final static Map<String, String> map = new HashMap<>();
    private String token = "";


    /**
     * 抓取数据   导出到csv和存详情
     *
     * @throws Exception
     */
//    @Scheduled(cron = "0 0 0 * * ? ")
//    @Async
//    public void produceTopic() throws Exception {
//        LocalDate today = LocalDate.now();
//        LocalDate yesterday = today.plusDays(-1);
//        String url = Gloables.API_URL;
//        List<CarNumberDict> cars = carNumberDictService.list(new QueryWrapper<>(new CarNumberDict().setDelFlag(0)));
//        Map<String, String> params = new HashMap();
//        params.put(Gloables.API_PARAM_TOKEN, Gloables.API_TOKEN);
//        params.put(Gloables.API_PARAM_DATE, yesterday.toString());
//        TimeInterval timer = DateUtil.timer();
//        for (CarNumberDict car : cars) {
//            try {
//                params.put(Gloables.API_PARAM_CARID, car.getCarVin());
//                timer.intervalRestart();
//                String content = HttpClientUtil.doGet(url, params);
//                while (content.contains("http")) {
//                    logger.info("重新获取数据中......");
//                    content = HttpClientUtil.doGet(url, params);
//                }
//                System.out.println(car.getCarVin() + "====" + yesterday + "日====>抓取花费时间: " + timer.intervalSecond() + "s");
//                timer.intervalRestart();
//                mglCarshopTianfuDataService.save(new MglCarshopTianfuData().setCarVin((String) params.get(Gloables.API_PARAM_CARID)).setJsonContent(content).setRealDate(yesterday).setCreateDate(today));
//                System.out.println(car.getCarVin() + "====" + yesterday + "日====>存库花费时间: " + timer.intervalSecond() + "s");
////                存详情
//                FuTiamDetailDtoList fuTiamDetailDtoList = JSONObject.parseObject(content, FuTiamDetailDtoList.class);
//                List<FuTianDetailDto> data = fuTiamDetailDtoList.getData();
//                timer.intervalRestart();
//                List<MglCarshopFutianDataDetail> mglCarshopFutianDataDetails = new ArrayList<>();
//                String fileName = params.get(Gloables.API_PARAM_CARID) + "-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + Gloables.CSV_EXTENT;
////                导出到csv
//                List<Map<String, Object>> datas = new ArrayList<>();
//                if (data == null || data.size() == 0) {
//                    continue;
//                }
//                FileOutputStream os = new FileOutputStream(csvPath + fileName);
//                data.forEach(x -> {
////                    每条数据的代码
//                    Map<String, String> codes = x.getCodes();
////                    导出到csv
//                    Map<String, Object> map = new HashMap<>();
//                    Map<String, Object> voltage = new HashMap<>();
//                    Map<String, Object> temperature = new HashMap<>();
////                    创建数据
//                    MglCarshopFutianDataDetail mglCarshopFutianDataDetail = new MglCarshopFutianDataDetail();
//                    mglCarshopFutianDataDetail.setVin((String) params.get(Gloables.API_PARAM_CARID));
//                    map.put("vin", (String) params.get(Gloables.API_PARAM_CARID));
//                    voltage.put("vin", (String) params.get(Gloables.API_PARAM_CARID));
//                    temperature.put("vin", (String) params.get(Gloables.API_PARAM_CARID));
//                    mglCarshopFutianDataDetail.setOrderNumber(car.getOrderNumber());
//                    mglCarshopFutianDataDetail.setCarCurrentTime(x.getTime());
//                    map.put("car_current_time", x.getTime());
//                    voltage.put("time", x.getTime());
//                    temperature.put("time", x.getTime());
//                    if (!StringUtils.isBlank(codes.get("1030002"))) {
//                        try {
//                            mglCarshopFutianDataDetail.setMileages((Double.parseDouble(codes.get("1030002")) / 1000) + "");
//                            map.put("mileages", (Double.parseDouble(codes.get("1030002")) / 1000) + "");
//                        } catch (NumberFormatException e) {
//                            mglCarshopFutianDataDetail.setMileages(codes.get("1030002"));
//                            map.put("mileages", codes.get("1030002"));
//                        }
//                    }
//                    mglCarshopFutianDataDetail.setSpeed(codes.get("1010027"));
//                    map.put("speed", codes.get("1010027"));
//                    mglCarshopFutianDataDetail.setRunModel(codes.get("1140013"));
//                    mglCarshopFutianDataDetail.setChargeState(codes.get("1110004"));
////                    SOC
//                    mglCarshopFutianDataDetail.setSoc(codes.get("1110045"));
//                    map.put("soc", codes.get("1110045"));
////                    总电流总电压最高温度最低温度
//                    mglCarshopFutianDataDetail.setTotalCurrent(codes.get("1110044"));
//                    map.put("total_current", codes.get("1110044"));
//                    mglCarshopFutianDataDetail.setTotalVoltage(codes.get("1110043"));
//                    map.put("total_voltage", codes.get("1110043"));
//                    mglCarshopFutianDataDetail.setMaxTemperature(codes.get("1110050"));
//                    map.put("max_temperature", codes.get("1110050"));
//                    mglCarshopFutianDataDetail.setMinTemperature(codes.get("1110049"));
//                    map.put("min_temperature", codes.get("1110049"));
////                    最高最低电压以及编号
//                    mglCarshopFutianDataDetail.setMaxVoltage(codes.get("1110048"));
//                    map.put("max_voltage", codes.get("1110048"));
//                    mglCarshopFutianDataDetail.setMinVoltage(codes.get("1110047"));
//                    map.put("min_voltage", codes.get("1110047"));
//                    mglCarshopFutianDataDetail.setMinVoltageCellCode(codes.get("1110070"));
//                    map.put("min_voltage_cell_code", codes.get("1110070"));
//                    mglCarshopFutianDataDetail.setMaxVoltageCellCode(codes.get("1110068"));
//                    map.put("max_voltage_cell_code", codes.get("1110068"));
//                    mglCarshopFutianDataDetail.setMinVoltageBoxCode(codes.get("1110069"));
//                    mglCarshopFutianDataDetail.setMaxVoltageBoxCode(codes.get("1110067"));
//                    mglCarshopFutianDataDetail.setMinTemperatureNeedle(codes.get("1110074"));
//                    map.put("max_temperature_needle", codes.get("1110074"));
//                    mglCarshopFutianDataDetail.setMaxTemperatureNeedle(codes.get("1110072"));
//                    map.put("min_temperature_needle", codes.get("1110072"));
//                    mglCarshopFutianDataDetail.setMinTemperatrureBoxCode(codes.get("1110073"));
//                    mglCarshopFutianDataDetail.setMaxTemperatrureBoxCode(codes.get("1110071"));
////                    获取codes键
//                    List<String> collect = codes.keySet().stream().filter(y -> y.contains("1110107-1")).sorted((a, b) -> Integer.parseInt(a.substring(10)) - Integer.parseInt(b.substring(10))).collect(Collectors.toList());
//                    List<String> collect1 = codes.keySet().stream().filter(y -> y.contains("1110108-1")).sorted((a, b) -> Integer.parseInt(a.substring(10)) - Integer.parseInt(b.substring(10))).collect(Collectors.toList());
//                    List<VoltageVo> list = new ArrayList<>();
//                    List<VoltageVo> list1 = new ArrayList<>();
//                    List<Double> list3 = new ArrayList<>();
//                    List<Double> list4 = new ArrayList<>();
//                    for (int i = 0; i < collect.size(); i++) {
//                        VoltageVo voltageVo = new VoltageVo();
//                        voltageVo.setKey(collect.get(i));
//                        try {
//                            voltageVo.setValue(Double.valueOf(codes.get(collect.get(i))));
//                        } catch (NumberFormatException e) {
//                            voltageVo.setValue(-99999d);
//                        }
//                        list.add(voltageVo);
//                        try {
//                            list3.add(Double.valueOf(codes.get(collect.get(i))));
//                        } catch (NumberFormatException e) {
//                            list3.add(-99999d);
//                        }
//                        voltage.put("V" + (i + 1), codes.get(collect.get(i)));
//                    }
////                    单体电压
//                    map.put("single_voltage", StringUtils.join(list3, "|"));
////                    绝缘电阻
//                    map.put("insulation_resistance", codes.get("1110085"));
////                    单体温度
//                    for (int i = 0; i < collect1.size(); i++) {
//                        VoltageVo voltageVo = new VoltageVo();
//                        voltageVo.setKey(collect1.get(i));
//                        try {
//                            voltageVo.setValue(Double.valueOf(codes.get(collect1.get(i))));
//                        } catch (NumberFormatException e) {
//                            voltageVo.setValue(-99999d);
//                        }
//                        list1.add(voltageVo);
//                        try {
//                            list4.add(Double.valueOf(codes.get(collect1.get(i))));
//                        } catch (NumberFormatException e) {
//                            list4.add(-99999d);
//                        }
//                        temperature.put("T" + (i + 1), codes.get(collect1.get(i)));
//                    }
//                    map.put("singel_temperature", StringUtils.join(list4, "|"));
//                    mglCarshopFutianDataDetail.setSingleCellVoltage(JSONObject.toJSONString(list));
//                    mglCarshopFutianDataDetail.setSingleCellTemperature(JSONObject.toJSONString(list1));
//                    mglCarshopFutianDataDetail.setBatteryVersionInformation(codes.get("1130195"));
//                    mglCarshopFutianDataDetail.setTotalNumberOfSingleBatteries(codes.get("1110076-1"));
//                    mglCarshopFutianDataDetail.setSocLowAlarm(codes.get("1110065"));
//                    map.put("soc_low_alarm", codes.get("1110065"));
//                    map.put("battery_high_temperature_alarm", codes.get("1110064"));
//                    map.put("temperature_difference_alarm", codes.get("1110061"));
//                    map.put("equip_overvoltage_alarm", codes.get("1110054"));
//                    map.put("equipment_undervoltage_alarm", codes.get("1110053"));
//                    map.put("system_mismatch_alarm", codes.get("1110052"));
//                    map.put("maximum_alarm_level", codes.get("1110046"));
//                    map.put("type_overcharge_alarm", codes.get("1140017"));
//                    map.put("soc_jump_alarm", codes.get("1140019"));
//                    map.put("insulation_alarm", codes.get("1110087"));
//                    map.put("dc_status_alarm", codes.get("1130237"));
//                    map.put("high_pressure_interlock_alarm", codes.get("1110157"));
//                    map.put("poor_battery_consistency_alarm", codes.get("1110132"));
//                    map.put("single_battery_overvoltage_alarm", codes.get("1130180"));
//                    map.put("low_voltage_alarm_for_single_battery", codes.get("1130181"));
//                    map.put("soc_high_alarm", codes.get("1130183"));
//                    mglCarshopFutianDataDetail.setBatteryHighTemperatureAlarm(codes.get("1110064"));
//                    mglCarshopFutianDataDetail.setTemperatureDifferenceAlarm(codes.get("1110061"));
//                    mglCarshopFutianDataDetail.setEquipOvervoltageAlarm(codes.get("1110054"));
//                    mglCarshopFutianDataDetail.setEquipmentUndervoltageAlarm(codes.get("1110053"));
//                    mglCarshopFutianDataDetail.setSystemMismatchAlarm(codes.get("1110052"));
//                    mglCarshopFutianDataDetail.setMaximumAlarmLevel(codes.get("1110046"));
//                    mglCarshopFutianDataDetail.setTypeOverchargeAlarm(codes.get("1140017"));
//                    mglCarshopFutianDataDetail.setSocJumpAlarm(codes.get("1140019"));
//                    mglCarshopFutianDataDetail.setInsulationAlarm(codes.get("1110087"));
//                    mglCarshopFutianDataDetail.setDcStatusAlarm(codes.get("1130237"));
//                    mglCarshopFutianDataDetail.setDcTemperatrureAlarm(codes.get("1130238"));
//                    mglCarshopFutianDataDetail.setHighPressureInterlockAlarm(codes.get("1110157"));
//                    mglCarshopFutianDataDetail.setPoorBatteryConsistencyAlarm(codes.get("1110132"));
//                    mglCarshopFutianDataDetail.setSingleBatteryOvervoltageAlarm(codes.get("1130180"));
//                    mglCarshopFutianDataDetail.setLowVoltageAlarmForSingleBattery(codes.get("1130181"));
//                    mglCarshopFutianDataDetail.setSocHighAlarm(codes.get("1130183"));
////                    这是导出csv
//                    datas.add(map);
//                    mglCarshopFutianDataDetails.add(mglCarshopFutianDataDetail);
//                });
////                导出到csv
//                CsvExportUtil.doExport(datas, Gloables.CSV_TITLES, Gloables.CSV_KEYS, os);
//                System.out.println(car.getCarVin() + "====" + yesterday + "日====>执行循环花费时间: " + timer.intervalSecond() + "s");
//                timer.intervalRestart();
//                mglCarshopFutianDataDetailService.saveBatch(mglCarshopFutianDataDetails);
//                System.out.println(car.getCarVin() + "====" + yesterday + "日====>存详情列表花费时间: " + timer.intervalSecond() + "s");
//            } catch (Exception e) {
//                e.printStackTrace();
//                mglCarshopTianfuDataService.save(new MglCarshopTianfuData().setCarVin((String) params.get("carId")).setJsonContent("数据异常").setRealDate(yesterday).setCreateDate(today));
//                continue;
//            }
//        }
//        System.out.println(yesterday + "日=============》数据抓取完毕");
//    }


    /**
     * 抓取数据   导出到csv和不存详情以及上传至FTP
     *
     * @throws Exception
     */
//    @Scheduled(cron = "0 0 0 * * ? ")
//    @Async
    public void produceTopicNoDetail() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.plusDays(-1);
        String url = Gloables.API_URL;
        List<CarNumberDict> cars = carNumberDictService.list(new QueryWrapper<>(new CarNumberDict().setDelFlag(0)));
        Map<String, String> params = new HashMap();
        params.put(Gloables.API_PARAM_TOKEN, Gloables.API_TOKEN);
        params.put(Gloables.API_PARAM_DATE, yesterday.toString());
//        强制创建目录
        String dir = ftpZipPath + "/" + yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        FileUtil.forceDirectory(dir);
        for (CarNumberDict car : cars) {
            try {
                params.put(Gloables.API_PARAM_CARID, car.getCarVin());
                String content = HttpClientUtil.doGet(url, params);
                while (content.contains("http")) {
                    logger.info("重新获取数据中......");
                    content = HttpClientUtil.doGet(url, params);
                }
//                存原始数据
                mglCarshopTianfuDataService.save(new MglCarshopTianfuData().setCarVin((String) params.get(Gloables.API_PARAM_CARID)).setJsonContent(content).setRealDate(yesterday).setCreateDate(today));
                mglCarshopTianfuDataBakService.save(new MglCarshopTianfuDataBak().setCarVin((String) params.get(Gloables.API_PARAM_CARID)).setJsonContent(content).setRealDate(yesterday).setCreateDate(today));
//                存详情
                FuTiamDetailDtoList fuTiamDetailDtoList = JSONObject.parseObject(content, FuTiamDetailDtoList.class);
                List<FuTianDetailDto> data = fuTiamDetailDtoList.getData();
                String fileName1 = 1000000 + car.getId() + "-" + params.get(Gloables.API_PARAM_CARID) + "-" + yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + Gloables.CSV_EXTENT;
                String fileName = 1000000 + car.getId() + "-" + params.get(Gloables.API_PARAM_CARID) + Gloables.CSV_EXTENT;
//                导出到csv
                List<Map<String, Object>> datas = new ArrayList<>();
//                if (CollectionUtils.isEmpty(data)) {
//                    continue;
//                }
//                FileOutputStream os = new FileOutputStream(csvBeiyouPath + fileName1);
                FileOutputStream newOs = new FileOutputStream(dir + "/" + fileName);
                data.forEach(x -> {
//                    每条数据的代码
                    Map<String, String> codes = x.getCodes();
//                    导出到csv
                    Map<String, Object> map = new HashMap<>();
//                    创建数据
                    map.put("vin", (String) params.get(Gloables.API_PARAM_CARID));
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
//                CsvExportUtil.doExport(datas, Gloables.CSV_TITLES, Gloables.CSV_KEYS, os);
                CsvExportUtil.doExport(datas, Gloables.CSV_TITLES, Gloables.CSV_KEYS, newOs);
            } catch (Exception e) {
                e.printStackTrace();
                mglCarshopTianfuDataService.save(new MglCarshopTianfuData().setCarVin((String) params.get("carId")).setJsonContent("数据异常").setRealDate(yesterday).setCreateDate(today));
                continue;
            }
        }
//        压缩
        String[] extention = new String[]{Gloables.CSV_EXTENT};
        List<File> files = FileUtil.listFile(new File(dir), extention, true);
        if (cars.size() == files.size()) {
            CompressUtils.zip(dir, dir + ".zip");
        }
//        FTP
        FtpTool tool = new FtpTool(host, port, username, password);
        tool.initFtpClient();
        tool.CreateDirecroty(Gloables.FUTIAN_ZIP_PATH);
        boolean uploadFile = tool.uploadFile(Gloables.FUTIAN_ZIP_PATH, yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".zip", dir + ".zip");
//      删除文件夹
//        if (uploadFile) {
//            CompressUtils.deleteDirectory(new File(dir));
//            CompressUtils.doDeleteEmptyDir(dir + ".zip");
//        }
        System.out.println(yesterday + "日=============》数据抓取完毕");
    }


    /**
     * 抓取7天数据
     *
     * @throws Exception
     */
//    @Scheduled(cron = "0 45 8 14 * ? ")
//    @Async
//    public void produceTopicCopy() {
//        List<CarNumberDict> cars = carNumberDictService.list(new QueryWrapper<>(new CarNumberDict().setDelFlag(0)));
////        记录异常数据
//        List<String> list = new ArrayList<>();
//        List<LocalDate> localDates = new ArrayList<>();
//        localDates.add(LocalDate.now().plusDays(-8));
//        localDates.add(LocalDate.now().plusDays(-7));
//        localDates.add(LocalDate.now().plusDays(-6));
//        localDates.add(LocalDate.now().plusDays(-5));
//        localDates.add(LocalDate.now().plusDays(-4));
//        localDates.add(LocalDate.now().plusDays(-3));
//        localDates.add(LocalDate.now().plusDays(-2));
//        int flag = 100000;
//        for (CarNumberDict car : cars) {
//            flag ++;
//            try {
//                List<Map<String, Object>> datas = new ArrayList<>();
//                String fileName =flag+"-"+ car.getCarVin() + Gloables.CSV_EXTENT;
//                FileOutputStream os = new FileOutputStream(csvPath + fileName);
//                for (LocalDate localDate : localDates) {
//                    Map<String, String> params = new HashMap();
//                    params.put(Gloables.API_PARAM_TOKEN, Gloables.API_TOKEN);
//                    params.put(Gloables.API_PARAM_DATE, localDate.toString());
//                    params.put(Gloables.API_PARAM_CARID, car.getCarVin());
//                    String url = Gloables.API_URL;
////                    String content = MyHttpClientUtils.doGetParam(url, params);
//                    String content = HttpClientUtil.doGet(url, params);
//                    while (content.contains("http")) {
//                        content = HttpClientUtil.doGet(url, params);
//                    }
//                    FuTiamDetailDtoList fuTiamDetailDtoList = null;
////                    fuTiamDetailDtoList = JSONObject.parseObject(content, FuTiamDetailDtoList.class);
//                    fuTiamDetailDtoList = JSONObject.parseObject(content, FuTiamDetailDtoList.class);
//                    if (fuTiamDetailDtoList == null) {
//                        continue;
//                    }
//                    List<FuTianDetailDto> data = fuTiamDetailDtoList.getData();
//                    if (data == null || data.size() == 0) {
//                        continue;
//                    }
//                    data.forEach(x -> {
//                        //                    每条数据的代码
//                        Map<String, String> codes = x.getCodes();
//                        //                    导出到csv
//                        Map<String, Object> map = new HashMap<>();
//                        Map<String, Object> voltage = new HashMap<>();
//                        Map<String, Object> temperature = new HashMap<>();
//                        //                    创建数据
//                        MglCarshopFutianDataDetail mglCarshopFutianDataDetail = new MglCarshopFutianDataDetail();
//                        mglCarshopFutianDataDetail.setVin((String) params.get(Gloables.API_PARAM_CARID));
//                        map.put("vin", (String) params.get(Gloables.API_PARAM_CARID));
//                        voltage.put("vin", (String) params.get(Gloables.API_PARAM_CARID));
//                        temperature.put("vin", (String) params.get(Gloables.API_PARAM_CARID));
//                        mglCarshopFutianDataDetail.setOrderNumber(car.getOrderNumber());
//                        mglCarshopFutianDataDetail.setCarCurrentTime(x.getTime());
//                        map.put("car_current_time", x.getTime());
//                        voltage.put("time", x.getTime());
//                        temperature.put("time", x.getTime());
//                        if (!StringUtils.isBlank(codes.get("1030002"))) {
//                            try {
//                                mglCarshopFutianDataDetail.setMileages((Double.parseDouble(codes.get("1030002")) / 1000) + "");
//                                map.put("mileages", (Double.parseDouble(codes.get("1030002")) / 1000) + "");
//                            } catch (NumberFormatException e) {
//                                mglCarshopFutianDataDetail.setMileages(codes.get("1030002"));
//                                map.put("mileages", codes.get("1030002"));
//                            }
//                        }
//                        mglCarshopFutianDataDetail.setSpeed(codes.get("1010027"));
//                        map.put("speed", codes.get("1010027"));
//                        mglCarshopFutianDataDetail.setRunModel(codes.get("1140013"));
//                        mglCarshopFutianDataDetail.setChargeState(codes.get("1110004"));
//                        //                    SOC
//                        mglCarshopFutianDataDetail.setSoc(codes.get("1110045"));
//                        map.put("soc", codes.get("1110045"));
//                        //                    总电流总电压最高温度最低温度
//                        mglCarshopFutianDataDetail.setTotalCurrent(codes.get("1110044"));
//                        map.put("total_current", codes.get("1110044"));
//                        mglCarshopFutianDataDetail.setTotalVoltage(codes.get("1110043"));
//                        map.put("total_voltage", codes.get("1110043"));
//                        mglCarshopFutianDataDetail.setMaxTemperature(codes.get("1110050"));
//                        map.put("max_temperature", codes.get("1110050"));
//                        mglCarshopFutianDataDetail.setMinTemperature(codes.get("1110049"));
//                        map.put("min_temperature", codes.get("1110049"));
//                        //                    最高最低电压以及编号
//                        mglCarshopFutianDataDetail.setMaxVoltage(codes.get("1110048"));
//                        map.put("max_voltage", codes.get("1110048"));
//                        mglCarshopFutianDataDetail.setMinVoltage(codes.get("1110047"));
//                        map.put("min_voltage", codes.get("1110047"));
//                        mglCarshopFutianDataDetail.setMinVoltageCellCode(codes.get("1110070"));
//                        map.put("min_voltage_cell_code", codes.get("1110070"));
//                        mglCarshopFutianDataDetail.setMaxVoltageCellCode(codes.get("1110068"));
//                        map.put("max_voltage_cell_code", codes.get("1110068"));
//                        mglCarshopFutianDataDetail.setMinVoltageBoxCode(codes.get("1110069"));
//                        mglCarshopFutianDataDetail.setMaxVoltageBoxCode(codes.get("1110067"));
//                        mglCarshopFutianDataDetail.setMinTemperatureNeedle(codes.get("1110074"));
//                        map.put("max_temperature_needle", codes.get("1110074"));
//                        mglCarshopFutianDataDetail.setMaxTemperatureNeedle(codes.get("1110072"));
//                        map.put("min_temperature_needle", codes.get("1110072"));
//                        mglCarshopFutianDataDetail.setMinTemperatrureBoxCode(codes.get("1110073"));
//                        mglCarshopFutianDataDetail.setMaxTemperatrureBoxCode(codes.get("1110071"));
//                        //                    获取codes键
//                        List<String> collect = codes.keySet().stream().filter(y -> y.contains("1110107-1")).sorted((a, b) -> Integer.parseInt(a.substring(10)) - Integer.parseInt(b.substring(10))).collect(Collectors.toList());
//                        List<String> collect1 = codes.keySet().stream().filter(y -> y.contains("1110108-1")).sorted((a, b) -> Integer.parseInt(a.substring(10)) - Integer.parseInt(b.substring(10))).collect(Collectors.toList());
//                        List<Double> list3 = new ArrayList<>();
//                        List<Double> list4 = new ArrayList<>();
//                        for (int i = 0; i < collect.size(); i++) {
//                            try {
//                                list3.add(Double.valueOf(codes.get(collect.get(i))));
//                            } catch (NumberFormatException e) {
//                                list3.clear();
//                            }
//                        }
//                        //                    单体电压
//                        map.put("single_voltage", StringUtils.join(list3, "|"));
//                        //                    绝缘电阻
//                        map.put("insulation_resistance", codes.get("1110085"));
//                        //                    单体温度
//                        for (int i = 0; i < collect1.size(); i++) {
//                            try {
//                                list4.add(Double.valueOf(codes.get(collect1.get(i))));
//                            } catch (NumberFormatException e) {
//                                list4.clear();
//                            }
//                            temperature.put("T" + (i + 1), codes.get(collect1.get(i)));
//                        }
//                        map.put("singel_temperature", StringUtils.join(list4, "|"));
//                        mglCarshopFutianDataDetail.setBatteryVersionInformation(codes.get("1130195"));
//                        mglCarshopFutianDataDetail.setTotalNumberOfSingleBatteries(codes.get("1110076-1"));
//                        mglCarshopFutianDataDetail.setSocLowAlarm(codes.get("1110065"));
//                        map.put("soc_low_alarm", codes.get("1110065"));
//                        map.put("battery_high_temperature_alarm", codes.get("1110064"));
//                        map.put("temperature_difference_alarm", codes.get("1110061"));
//                        map.put("equip_overvoltage_alarm", codes.get("1110054"));
//                        map.put("equipment_undervoltage_alarm", codes.get("1110053"));
//                        map.put("system_mismatch_alarm", codes.get("1110052"));
//                        map.put("maximum_alarm_level", codes.get("1110046"));
//                        map.put("type_overcharge_alarm", codes.get("1140017"));
//                        map.put("soc_jump_alarm", codes.get("1140019"));
//                        map.put("insulation_alarm", codes.get("1110087"));
//                        map.put("dc_status_alarm", codes.get("1130237"));
//                        map.put("high_pressure_interlock_alarm", codes.get("1110157"));
//                        map.put("poor_battery_consistency_alarm", codes.get("1110132"));
//                        map.put("single_battery_overvoltage_alarm", codes.get("1130180"));
//                        map.put("low_voltage_alarm_for_single_battery", codes.get("1130181"));
//                        map.put("soc_high_alarm", codes.get("1130183"));
//                        mglCarshopFutianDataDetail.setBatteryHighTemperatureAlarm(codes.get("1110064"));
//                        mglCarshopFutianDataDetail.setTemperatureDifferenceAlarm(codes.get("1110061"));
//                        mglCarshopFutianDataDetail.setEquipOvervoltageAlarm(codes.get("1110054"));
//                        mglCarshopFutianDataDetail.setEquipmentUndervoltageAlarm(codes.get("1110053"));
//                        mglCarshopFutianDataDetail.setSystemMismatchAlarm(codes.get("1110052"));
//                        mglCarshopFutianDataDetail.setMaximumAlarmLevel(codes.get("1110046"));
//                        mglCarshopFutianDataDetail.setTypeOverchargeAlarm(codes.get("1140017"));
//                        mglCarshopFutianDataDetail.setSocJumpAlarm(codes.get("1140019"));
//                        mglCarshopFutianDataDetail.setInsulationAlarm(codes.get("1110087"));
//                        mglCarshopFutianDataDetail.setDcStatusAlarm(codes.get("1130237"));
//                        mglCarshopFutianDataDetail.setDcTemperatrureAlarm(codes.get("1130238"));
//                        mglCarshopFutianDataDetail.setHighPressureInterlockAlarm(codes.get("1110157"));
//                        mglCarshopFutianDataDetail.setPoorBatteryConsistencyAlarm(codes.get("1110132"));
//                        mglCarshopFutianDataDetail.setSingleBatteryOvervoltageAlarm(codes.get("1130180"));
//                        mglCarshopFutianDataDetail.setLowVoltageAlarmForSingleBattery(codes.get("1130181"));
//                        mglCarshopFutianDataDetail.setSocHighAlarm(codes.get("1130183"));
//                        //                    这是导出csv
//                        datas.add(map);
//                    });
//                }
//                CsvExportUtil.doExport(datas, Gloables.CSV_TITLES, Gloables.CSV_KEYS, os);
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("异常数据"+car.getCarVin());
//            }
//        }
//    }


    /**
     * 算法一
     *
     * @throws Exception
     */
//    @Scheduled(cron = "0 0 20 * * ? ")
//    @Async
//    public void firstArithmetic() throws Exception {
//        List<CarNumberDict> cars = carNumberDictService.list(new QueryWrapper<>(new CarNumberDict().setDelFlag(0)));
//        LocalDate today = LocalDate.now();
//        LocalDate yesterday = today.plusDays(-1);
//        String start = yesterday + " 00:00:00";
//        String end = yesterday + " 23:59:59";
//        cars.forEach(x -> {
//            List<MglCarshopFutianDataDetail> detailsByVin = mglCarshopFutianDataDetailService.findDetailsByVin(x.getCarVin(), start, end);
//            List<List<VoltageVo>> lists = new ArrayList<>();
//            for (int i = 0; i < detailsByVin.size(); i++) {
//                List<VoltageVo> bills = JSONArray.parseArray(detailsByVin.get(i).getSingleCellVoltage(), VoltageVo.class);
//                Double d = bills.stream().collect(Collectors.averagingDouble(VoltageVo::getValue));
//                List<VoltageVo> collect = bills.stream().collect(Collectors.toList());
//                collect.forEach(n -> n.setValue(d - n.getValue()));
//                lists.add(collect);
//            }
//            List<MglCarshopStaticWarning> mglCarshopStaticWarnings = new ArrayList<>();
//            if (lists.size() > 0) {
//                int length = lists.get(0).size();
//                for (int i = 0; i < length; i++) {
//                    MglCarshopStaticWarning mglCarshopStaticWarning = new MglCarshopStaticWarning();
//                    mglCarshopStaticWarning.setDelFlag(0);
//                    mglCarshopStaticWarning.setCellNumber(lists.get(0).get(i).getKey());
//                    List<Double> list = new ArrayList<>();
//                    for (int j = 0; j < lists.size(); j++) {
//                        if (lists.get(j).size() == length) {
//                            list.add(lists.get(j).get(i).getValue());
//                        } else {
//                            continue;
//                        }
//                    }
//                    List<Double> collect = list.stream().filter(m -> m > 0).collect(Collectors.toList());
//                    Double asDouble = null;
//                    if (collect.size() > 0) {
//                        asDouble = collect.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
//                    } else {
//                        continue;
//                    }
////                    mglCarshopStaticWarning.setValue(asDouble * 1000);
//                    mglCarshopStaticWarning.setValue((double) Math.round(asDouble * 100000) / 100);
//
//                    mglCarshopStaticWarning.setVin(x.getCarVin());
//                    mglCarshopStaticWarning.setCurretsDateTime(yesterday.toString());
//                    mglCarshopStaticWarning.setCurretsTimeSeconds(yesterday.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli());
////                if (d > 40) {
//                    if (asDouble * 1000 > Gloables.WORINING_VALUE) {
//                        mglCarshopStaticWarning.setType(1);
//                        mglCarshopStaticWarnings.add(mglCarshopStaticWarning);
//                    } else {
//                        mglCarshopStaticWarning.setType(0);
//                    }
//                }
//            }
//            mglCarshopStaticWarningService.saveBatch(mglCarshopStaticWarnings);
//        });
//    }


    /**
     * 扫描满足二级预警的一级预警将其修改为二级预警
     *
     * @throws Exception
     */
//    @Scheduled(cron = "0 0 23 * * ? ")
//    @Async
//    public void changeLevel() throws Exception {
//        LocalDate today = LocalDate.now();
//        LocalDate yesterday = today.plusDays(-1);
//        LocalDate sevenDaysAgo = yesterday.plusDays(-8);
//        List<MglCarshopStaticWarning> list = mglCarshopStaticWarningService.list(new QueryWrapper<>(new MglCarshopStaticWarning().setCurretsDateTime(yesterday.toString()).setType(1).setDelFlag(0)));
//        list.forEach(x -> {
//            int flag = 0;
//            LocalDate localDate = LocalDate.parse(x.getCurretsDateTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//            LocalDate starDate = localDate.plusDays(-6);
//            List<MglCarshopStaticWarning> listByCarVinAndDate = mglCarshopStaticWarningService.findListByCarVinAndDate(x.getVin(), starDate.toString(), x.getCurretsDateTime(), x.getCellNumber());
//            if (listByCarVinAndDate.size() >= 7) {
//                for (int i = 0; i < listByCarVinAndDate.size() - 1; i++) {
//                    if (listByCarVinAndDate.get(i).getValue() >= listByCarVinAndDate.get(i + 1).getValue()) {
//                        break;
//                    }
//                    flag++;
//                }
//            }
//            if (flag == 6) {
//                MglCarshopStaticWarning mglCarshopStaticWarning = mglCarshopStaticWarningService.getById(x.getId()).setType(2);
//                mglCarshopStaticWarningService.saveOrUpdate(mglCarshopStaticWarning);
//            }
//        });
//    }

    /**
     * 抓取金龙数据
     *
     * @throws Exception
     */
//    @Scheduled(cron = "* 0-10 14 * * ? ")
//    @Scheduled(cron = "* * * * * ? ")
    @Async
    public void getGoldenDragonData() throws Exception {
        List<String> cars = new ArrayList<>();
        cars.add("LA9CB22D3KALA6162");
        cars.add("LA6C7GAB1JC304865");
        cars.add("LA6C7GAB2JB201959");
        cars.add("LA9CB22D0K0LA6058");
        cars.add("LA6C7K1B7JB201894");
        totalGetDataByUrl(cars);
    }

    private void totalGetDataByUrl(List<String> cars) {
        String vehicles = StringUtils.join(cars, ",");
        String finalUrl = Gloables.GOLD_DATA_BASE_URL + vehicles + Gloables.GOLD_PARAMS_TYPES + map.get("token");
        String datas = HttpClientUtil.doGet(finalUrl, null);
        if (datas.length() < 30 && datas.contains("unauthorized")) {
            String token = HttpClientUtil.doGet(Gloables.GOLD_TOKEN_URL, null);
            map.put("token", token);
            finalUrl = Gloables.GOLD_DATA_BASE_URL + vehicles + Gloables.GOLD_PARAMS_TYPES + map.get("token");
            datas = HttpClientUtil.doGet(finalUrl, null);
        }
        GoldenDragonDto goldenDragonDto = JSONObject.parseObject(datas, GoldenDragonDto.class);
        List<Map<String, String>> mapDatas = goldenDragonDto.getData();
        DecimalFormat df = new DecimalFormat("######0.00");
        mapDatas.forEach(x -> {
            String time = "";
            GoldenDragon dragon = new GoldenDragon();
            dragon.setVin(x.get("VehicleID"));
            dragon.setTerminalNumber(x.get("DeviceNo"));
            dragon.setOnline(x.get("Online"));
            dragon.setResult(x.get("Result"));
            if (StringUtils.isNotBlank(MySelfUtil.getHandleStr(x.get("329609")).get("value"))) {
                dragon.setTotalVoltage(df.format(Double.parseDouble(MySelfUtil.getHandleStr(x.get("329609")).get("value")) * 0.1) + "");
            }
            time = MySelfUtil.getHandleStr(x.get("329609")).get("time");
            if (StringUtils.isNotBlank(MySelfUtil.getHandleStr(x.get("329610")).get("value"))) {
                dragon.setTotalCurrent(df.format(Double.parseDouble(MySelfUtil.getHandleStr(x.get("329610")).get("value")) * 0.1 - 1000) + "");
            }
            time = MySelfUtil.getHandleStr(x.get("329610")).get("time");
            dragon.setSoc(MySelfUtil.getHandleStr(x.get("329611")).get("value"));
            time = MySelfUtil.getHandleStr(x.get("329611")).get("time");

            dragon.setParamsFirst(MySelfUtil.getHandleStr(x.get("329622")).get("value"));
            time = MySelfUtil.getHandleStr(x.get("329622")).get("time");

            dragon.setParamsSecond(MySelfUtil.getHandleStr(x.get("329623")).get("value"));
            time = MySelfUtil.getHandleStr(x.get("329623")).get("time");

            if (StringUtils.isNotBlank(MySelfUtil.getHandleStr(x.get("329624")).get("value"))) {
                    dragon.setParamsThird(df.format(Double.parseDouble(MySelfUtil.getHandleStr(x.get("329624")).get("value")) * 0.001) + "");
            }
            time = MySelfUtil.getHandleStr(x.get("329624")).get("time");

            dragon.setParamsFouth(MySelfUtil.getHandleStr(x.get("329625")).get("value"));
            time = MySelfUtil.getHandleStr(x.get("329625")).get("time");

            dragon.setParamsFiveth(MySelfUtil.getHandleStr(x.get("329626")).get("value"));
            time = MySelfUtil.getHandleStr(x.get("329626")).get("time");

            if (StringUtils.isNotBlank(MySelfUtil.getHandleStr(x.get("329627")).get("value"))) {
                dragon.setParamsSix(df.format(Double.parseDouble(MySelfUtil.getHandleStr(x.get("329627")).get("value")) * 0.001) + "");
            }
            time = MySelfUtil.getHandleStr(x.get("329627")).get("time");

            dragon.setParamsSeven(MySelfUtil.getHandleStr(x.get("329628")).get("value"));
            time = MySelfUtil.getHandleStr(x.get("329628")).get("time");

            dragon.setParamsEight(MySelfUtil.getHandleStr(x.get("329629")).get("value"));
            time = MySelfUtil.getHandleStr(x.get("329629")).get("time");

            if (StringUtils.isNotBlank(MySelfUtil.getHandleStr(x.get("329630")).get("value"))) {
                dragon.setParamsTen(df.format(Double.parseDouble(MySelfUtil.getHandleStr(x.get("329630")).get("value")) - 40) + "");
            }
            time = MySelfUtil.getHandleStr(x.get("329630")).get("time");

            dragon.setParamsEleven(MySelfUtil.getHandleStr(x.get("329631")).get("value"));
            time = MySelfUtil.getHandleStr(x.get("329631")).get("time");

            dragon.setParamsTewlve(MySelfUtil.getHandleStr(x.get("329632")).get("value"));
            time = MySelfUtil.getHandleStr(x.get("329632")).get("time");

            if (StringUtils.isNotBlank(MySelfUtil.getHandleStr(x.get("329633")).get("value"))) {
                dragon.setParamsThirteen(df.format(Double.parseDouble(MySelfUtil.getHandleStr(x.get("329633")).get("value")) - 40) + "");
            }
            time = MySelfUtil.getHandleStr(x.get("329633")).get("time");

            dragon.setParamsFourteen(MySelfUtil.getHandleStr(x.get("329635")).get("value"));
            time = MySelfUtil.getHandleStr(x.get("329635")).get("time");
            if (StringUtils.isNotBlank(time)) {
                dragon.setDataCurrentTime(time);
                dragon.setCeateTime(LocalDateTime.now());
                String mapVin = map.get(x.get("VehicleID"));
                if (StringUtils.isBlank(mapVin)) {
                    map.put(x.get("VehicleID"), time);
                } else {
                    if (!mapVin.equals(time)) {
                        map.put(x.get("VehicleID"), time);
                        goldenDragonService.save(dragon);
                    }
                }
            }
        });
    }


    //    @Scheduled(cron = "0 0 23 * * ? ")
//    @Async
//    public void changeLevel() throws Exception {
//        LocalDate today = LocalDate.now();
//        LocalDate yesterday = today.plusDays(-1);
//        LocalDate sevenDaysAgo = yesterday.plusDays(-8);
//        List<MglCarshopStaticWarning> list = mglCarshopStaticWarningService.list(new QueryWrapper<>(new MglCarshopStaticWarning().setCurretsDateTime(yesterday.toString()).setType(1).setDelFlag(0)));
//        list.forEach(x -> {
//            int flag = 0;
//            LocalDate localDate = LocalDate.parse(x.getCurretsDateTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//            LocalDate starDate = localDate.plusDays(-6);
//            List<MglCarshopStaticWarning> listByCarVinAndDate = mglCarshopStaticWarningService.findListByCarVinAndDate(x.getVin(), starDate.toString(), x.getCurretsDateTime(), x.getCellNumber());
//            if (listByCarVinAndDate.size() >= 7) {
//                for (int i = 0; i < listByCarVinAndDate.size() - 1; i++) {
//                    if (listByCarVinAndDate.get(i).getValue() >= listByCarVinAndDate.get(i + 1).getValue()) {
//                        break;
//                    }
//                    flag++;
//                }
//            }
//            if (flag == 6) {
//                MglCarshopStaticWarning mglCarshopStaticWarning = mglCarshopStaticWarningService.getById(x.getId()).setType(2);
//                mglCarshopStaticWarningService.saveOrUpdate(mglCarshopStaticWarning);
//            }
//        });
//    }

    /**
     * 抓取金龙数据  上传至FTP
     *
     * @throws Exception
     */
//    @Scheduled(cron = "* 42-52 18 21 * ? ")
//    @Scheduled(cron = "* * * * * ? ")
    @Async
    public void getGoldenDragonDataByFtp() throws Exception {
        List<String> cars = new ArrayList<>();
        cars.add("LA9CB22D3KALA6162");
        cars.add("LA6C7GAB1JC304865");
        cars.add("LA6C7GAB2JB201959");
        cars.add("LA9CB22D0K0LA6058");
        cars.add("LA6C7K1B7JB201894");
        totalGetDataByUrlByFtp(cars);

//        FtpTool tool = new FtpTool("ftp.mgldl.com.cn", 21, "mgl", "MglAa110");
//        tool.initFtpClient();
//        tool.CreateDirecroty("/福田/ddcfs/FFF/FDSLFJ/FDSFS");
//        tool.uploadFile("/福田/ddcfs/FFF/FDSLFJ/FDSFS", "test.zip", "D:\\server110\\test.zip");

    }

    private void totalGetDataByUrlByFtp(List<String> cars) {
        String vehicles = StringUtils.join(cars, ",");
        String finalUrl = Gloables.GOLD_DATA_BASE_URL + vehicles + Gloables.GOLD_PARAMS_TYPES + map.get("token");
        String datas = HttpClientUtil.doGet(finalUrl, null);
        if (datas.length() < 30 && datas.contains("unauthorized")) {
            String token = HttpClientUtil.doGet(Gloables.GOLD_TOKEN_URL, null);
            map.put("token", token);
            finalUrl = Gloables.GOLD_DATA_BASE_URL + vehicles + Gloables.GOLD_PARAMS_TYPES + map.get("token");
            datas = HttpClientUtil.doGet(finalUrl, null);
        }
        GoldenDragonDto goldenDragonDto = JSONObject.parseObject(datas, GoldenDragonDto.class);
        List<Map<String, String>> mapDatas = goldenDragonDto.getData();
        DecimalFormat df = new DecimalFormat("######0.00");
        mapDatas.forEach(x -> {
            String time = "";
            GoldenDragon dragon = new GoldenDragon();
            dragon.setVin(x.get("VehicleID"));
            dragon.setTerminalNumber(x.get("DeviceNo"));
            dragon.setOnline(x.get("Online"));
            dragon.setResult(x.get("Result"));
            if (StringUtils.isNotBlank(MySelfUtil.getHandleStr(x.get("329609")).get("value"))) {
                dragon.setTotalVoltage(df.format(Double.parseDouble(MySelfUtil.getHandleStr(x.get("329609")).get("value")) * 0.1) + "");
            }
            time = MySelfUtil.getHandleStr(x.get("329609")).get("time");
            if (StringUtils.isNotBlank(MySelfUtil.getHandleStr(x.get("329610")).get("value"))) {
                dragon.setTotalCurrent(df.format(Double.parseDouble(MySelfUtil.getHandleStr(x.get("329610")).get("value")) * 0.1 - 1000) + "");
            }
            time = MySelfUtil.getHandleStr(x.get("329610")).get("time");
            dragon.setSoc(MySelfUtil.getHandleStr(x.get("329611")).get("value"));
            time = MySelfUtil.getHandleStr(x.get("329611")).get("time");

            dragon.setParamsFirst(MySelfUtil.getHandleStr(x.get("329622")).get("value"));
            time = MySelfUtil.getHandleStr(x.get("329622")).get("time");

            dragon.setParamsSecond(MySelfUtil.getHandleStr(x.get("329623")).get("value"));
            time = MySelfUtil.getHandleStr(x.get("329623")).get("time");

            if (StringUtils.isNotBlank(MySelfUtil.getHandleStr(x.get("329624")).get("value"))) {
                dragon.setParamsThird(df.format(Double.parseDouble(MySelfUtil.getHandleStr(x.get("329624")).get("value")) * 0.001) + "");
            }
            time = MySelfUtil.getHandleStr(x.get("329624")).get("time");

            dragon.setParamsFouth(MySelfUtil.getHandleStr(x.get("329625")).get("value"));
            time = MySelfUtil.getHandleStr(x.get("329625")).get("time");

            dragon.setParamsFiveth(MySelfUtil.getHandleStr(x.get("329626")).get("value"));
            time = MySelfUtil.getHandleStr(x.get("329626")).get("time");

            if (StringUtils.isNotBlank(MySelfUtil.getHandleStr(x.get("329627")).get("value"))) {
                dragon.setParamsSix(df.format(Double.parseDouble(MySelfUtil.getHandleStr(x.get("329627")).get("value")) * 0.001) + "");
            }
            time = MySelfUtil.getHandleStr(x.get("329627")).get("time");

            dragon.setParamsSeven(MySelfUtil.getHandleStr(x.get("329628")).get("value"));
            time = MySelfUtil.getHandleStr(x.get("329628")).get("time");

            dragon.setParamsEight(MySelfUtil.getHandleStr(x.get("329629")).get("value"));
            time = MySelfUtil.getHandleStr(x.get("329629")).get("time");

            if (StringUtils.isNotBlank(MySelfUtil.getHandleStr(x.get("329630")).get("value"))) {
                dragon.setParamsTen(df.format(Double.parseDouble(MySelfUtil.getHandleStr(x.get("329630")).get("value")) - 40) + "");
            }
            time = MySelfUtil.getHandleStr(x.get("329630")).get("time");

            dragon.setParamsEleven(MySelfUtil.getHandleStr(x.get("329631")).get("value"));
            time = MySelfUtil.getHandleStr(x.get("329631")).get("time");

            dragon.setParamsTewlve(MySelfUtil.getHandleStr(x.get("329632")).get("value"));
            time = MySelfUtil.getHandleStr(x.get("329632")).get("time");

            if (StringUtils.isNotBlank(MySelfUtil.getHandleStr(x.get("329633")).get("value"))) {
                dragon.setParamsThirteen(df.format(Double.parseDouble(MySelfUtil.getHandleStr(x.get("329633")).get("value")) - 40) + "");
            }
            time = MySelfUtil.getHandleStr(x.get("329633")).get("time");

            dragon.setParamsFourteen(MySelfUtil.getHandleStr(x.get("329635")).get("value"));
            time = MySelfUtil.getHandleStr(x.get("329635")).get("time");
            if (StringUtils.isNotBlank(time)) {
                dragon.setDataCurrentTime(time);
                dragon.setCeateTime(LocalDateTime.now());
                String mapVin = map.get(x.get("VehicleID"));
                if (StringUtils.isBlank(mapVin)) {
                    map.put(x.get("VehicleID"), time);
                }
                if (!mapVin.equals(time)) {
                    map.put(x.get("VehicleID"), time);
                    goldenDragonService.save(dragon);
                }
            }
        });
    }


}