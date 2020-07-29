//package com.mgl.task;
//
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.mgl.bean.carshop.CarNumberDict;
//import com.mgl.bean.carshop.MglCarshopTianfuData;
//import com.mgl.bean.carshop.MglCarshopTianfuDataBak;
//import com.mgl.bean.dto.FuTiamDetailDtoList;
//import com.mgl.bean.dto.FuTianDetailDto;
//import com.mgl.bean.dto.GoldenDragonDto;
//import com.mgl.bean.golden.CarGoldenDragonNumberDict;
//import com.mgl.bean.golden.GoldenDragon;
//import com.mgl.common.Gloables;
//import com.mgl.sdk.utils.NumberFormatUtil;
//import com.mgl.service.carshop.CarNumberDictService;
//import com.mgl.service.carshop.MglCarshopFutianDataDetailService;
//import com.mgl.service.carshop.MglCarshopTianfuDataBakService;
//import com.mgl.service.carshop.MglCarshopTianfuDataService;
//import com.mgl.service.golden.CarGoldenDragonNumberDictService;
//import com.mgl.service.golden.GoldenDragonService;
//import com.mgl.service.warns.MglCarshopStaticWarningService;
//import com.mgl.utils.compress.CompressUtils;
//import com.mgl.utils.csv.CsvExportUtil;
//import com.mgl.utils.file.FileUtil;
//import com.mgl.utils.ftp.ftpClientUtil.FtpTool;
//import com.mgl.utils.httpclient.HttpClientUtil;
//import com.mgl.utils.selfutil.MySelfUtil;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.text.DecimalFormat;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
///**
// * @Title: CatchFuTIanDataTask
// * @Description:
// * @Company: 盟固利
// * @author: 张奇
// * @date: ceate in 2020/6/2 17:36
// */
//@Component
//public class GoldenDragonTask {
//
//    private static final Logger logger = LoggerFactory.getLogger(GoldenDragonTask.class);
//    @Resource
//    private CarGoldenDragonNumberDictService carGoldenDragonNumberDictService;
//    @Resource
//    private GoldenDragonService goldenDragonService;
//
//    private final static Map<String, String> map = new HashMap<>();
//    private List<String> firstGroup = new ArrayList<>();
//    private List<String> secondGroup = new ArrayList<>();
//    private List<String> thirdGroup = new ArrayList<>();
//    private List<String> fourthGroup = new ArrayList<>();
//    private List<String> fifthGroup = new ArrayList<>();
//
//
//    /**
//     * 抓取汽车数据
//     *
//     * @throws Exception
//     */
////    @Async
////    @Scheduled(cron = "0 30 21,22,23 23 * ? ")
//    public void getGoldenDragonCars() throws Exception {
//        List<CarGoldenDragonNumberDict> list = carGoldenDragonNumberDictService.list(new QueryWrapper<>(new CarGoldenDragonNumberDict().setCarFlag(0)));
//        list.forEach(x -> {
//            if (x.getCarId() < 300) {
//                firstGroup.add(x.getCarVin());
//            } else if (x.getCarId() < 600) {
//                secondGroup.add(x.getCarVin());
//            } else if (x.getCarId() < 900) {
//                thirdGroup.add(x.getCarVin());
//            } else if (x.getCarId() < 1200) {
//                fourthGroup.add(x.getCarVin());
//            } else {
//                fifthGroup.add(x.getCarVin());
//            }
//        });
////        FtpTool tool = new FtpTool("ftp.mgldl.com.cn", 21, "mgl", "MglAa110");
////        tool.initFtpClient();
////        tool.CreateDirecroty("/福田/ddcfs/FFF/FDSLFJ/FDSFS");
////        tool.uploadFile("/福田/ddcfs/FFF/FDSLFJ/FDSFS", "test.zip", "D:\\server110\\test.zip");
//
//    }
//
//    /**
//     * 抓取金龙数据  上传至FTP
//     *
//     * @throws Exception
//     */
//    @Async
////    @Scheduled(cron = "* 32-42 21,22,23 23 * ? ")
//    public void firstGroupTask() throws Exception {
//        totalGetDataByUrlByFtp(firstGroup);
//
//    }
//
//    /**
//     * 抓取金龙数据  上传至FTP
//     *
//     * @throws Exception
//     */
////    @Async
////    @Scheduled(cron = "* 32-42 21,22,23 23 * ? ")
//    public void secondGroupTask() throws Exception {
//        totalGetDataByUrlByFtp(secondGroup);
//
//    }
//
//    /**
//     * 抓取金龙数据  上传至FTP
//     *
//     * @throws Exception
//     */
////    @Async
////    @Scheduled(cron = "* 32-42 21,22,23 23 * ? ")
//    public void thirdGroupTask() throws Exception {
//        totalGetDataByUrlByFtp(thirdGroup);
//
//    }
//
//    /**
//     * 抓取金龙数据  上传至FTP
//     *
//     * @throws Exception
//     */
////    @Async
////    @Scheduled(cron = "* 32-42 21,22,23 23 * ? ")
//    public void fourthGroupTask() throws Exception {
//        totalGetDataByUrlByFtp(fourthGroup);
//
//    }
//
//    /**
//     * 抓取金龙数据  上传至FTP
//     *
//     * @throws Exception
//     */
////    @Async
////    @Scheduled(cron = "* 32-42 21,22,23 23 * ? ")
//    public void fifthGroupTask() throws Exception {
//        totalGetDataByUrlByFtp(fifthGroup);
//
//    }
//
//    private void totalGetDataByUrlByFtp(List<String> cars) {
//        String vehicles = StringUtils.join(cars, ",");
//        String finalUrl = Gloables.GOLD_DATA_BASE_URL + vehicles + Gloables.GOLD_PARAMS_TYPES + map.get("token");
//        String datas = HttpClientUtil.doGet(finalUrl, null);
//        if (datas.length() < 30 && datas.contains("unauthorized")) {
//            String token = HttpClientUtil.doGet(Gloables.GOLD_TOKEN_URL, null);
//            map.put("token", token);
//            finalUrl = Gloables.GOLD_DATA_BASE_URL + vehicles + Gloables.GOLD_PARAMS_TYPES + map.get("token");
//            datas = HttpClientUtil.doGet(finalUrl, null);
//        }
//        GoldenDragonDto goldenDragonDto = JSONObject.parseObject(datas, GoldenDragonDto.class);
//        List<Map<String, String>> mapDatas = goldenDragonDto.getData();
//        DecimalFormat df = new DecimalFormat("######0.00");
//        mapDatas.forEach(entity -> {
//            String time = "";
//            GoldenDragon goldenDragon = new GoldenDragon();
//            // 车辆VIN
//            goldenDragon.setVin(entity.get("VehicleID"));
//            // 终端编号
//            goldenDragon.setTerminalNumber(entity.get("DeviceNo"));
//            // 在线状态
//            goldenDragon.setOnline(entity.get("Online"));
//            // 应答结果
//            goldenDragon.setResult(entity.get("Result"));
//            // 总电压
//            Map<String, String> voltageMap = MySelfUtil.getHandleStr(entity.get("329609"));
//            if (voltageMap != null) {
//                String voltage = voltageMap.get("value");
//                Double totalVoltage = Double.parseDouble(voltage) * 0.1;
//                goldenDragon.setTotalVoltage(NumberFormatUtil.doubleFormat(totalVoltage));
//                time = voltageMap.get("time");
//            }
//            // 总电流
//            Map<String, String> electricityMap = MySelfUtil.getHandleStr(entity.get("329610"));
//            if (electricityMap != null) {
//                String electricity = electricityMap.get("value");
//                Double totalElectricity = Double.parseDouble(electricity) * 0.1 - 1000;
//                goldenDragon.setTotalCurrent(NumberFormatUtil.doubleFormat(totalElectricity));
//                time = electricityMap.get("time");
//            }
//            // SOC
//            Map<String, String> socMap = MySelfUtil.getHandleStr(entity.get("329611"));
//            if (socMap != null) {
//                String soc = socMap.get("value");
//                goldenDragon.setSoc(soc);
//                time = socMap.get("time");
//            }
//            // 最高电压电池子系统号
//            Map<String, String> cellSystemNoMaxMap = MySelfUtil.getHandleStr(entity.get("329622"));
//            if (cellSystemNoMaxMap != null) {
//                String cellSystemNoMax = cellSystemNoMaxMap.get("value");
//                goldenDragon.setParamsFirst(cellSystemNoMax);
//                time = cellSystemNoMaxMap.get("time");
//            }
//            // 最高电压电池单体代号
//            Map<String, String> cellSingleNoMaxMap = MySelfUtil.getHandleStr(entity.get("329623"));
//            if (cellSingleNoMaxMap != null) {
//                String cellSingleNoMax = cellSingleNoMaxMap.get("value");
//                goldenDragon.setParamsSecond(cellSingleNoMax);
//                time = cellSingleNoMaxMap.get("time");
//            }
//            // 电池单体电压最高值
//            Map<String, String> cellSinglePeakValueMap = MySelfUtil.getHandleStr(entity.get("329624"));
//            if (cellSinglePeakValueMap != null) {
//                String cellSinglePeakValue = cellSinglePeakValueMap.get("value");
//                Double cellPeakValueDouble = Double.parseDouble(cellSinglePeakValue) * 0.001;
//                goldenDragon.setParamsThird(NumberFormatUtil.doubleFormat(cellPeakValueDouble));
//                time = cellSinglePeakValueMap.get("time");
//            }
//            // 最低电压电池子系统号
//            Map<String, String> cellSystemNoMinMap = MySelfUtil.getHandleStr(entity.get("329625"));
//            if (cellSystemNoMinMap != null) {
//                String cellSystemNoMin = cellSystemNoMinMap.get("value");
//                goldenDragon.setParamsFouth(cellSystemNoMin);
//                time = cellSystemNoMinMap.get("time");
//            }
//            // 最低电压电池单体代号
//            Map<String, String> cellSingleNoMinMap = MySelfUtil.getHandleStr(entity.get("329626"));
//            if (cellSingleNoMinMap != null) {
//                String cellSingleNoMin = cellSingleNoMinMap.get("value");
//                goldenDragon.setParamsFiveth(cellSingleNoMin);
//                time = cellSingleNoMinMap.get("time");
//            }
//            // 电池单体电压最低值
//            Map<String, String> cellSingleMinimumMap = MySelfUtil.getHandleStr(entity.get("329627"));
//            if (cellSingleMinimumMap != null) {
//                String cellSingleMinimum = cellSingleMinimumMap.get("value");
//                Double cellSingleMinimumDouble = Double.parseDouble(cellSingleMinimum) * 0.001;
//                goldenDragon.setParamsSix(NumberFormatUtil.doubleFormat(cellSingleMinimumDouble));
//                time = cellSingleMinimumMap.get("time");
//            }
//            // 最高温度子系统号
//            Map<String, String> temperatureSystemNoMaxMap = MySelfUtil.getHandleStr(entity.get("329628"));
//            if (temperatureSystemNoMaxMap != null) {
//                String temperatureSystemNoMax = temperatureSystemNoMaxMap.get("value");
//                goldenDragon.setParamsSeven(temperatureSystemNoMax);
//            }
//            // 最高温度探针序号
//            Map<String, String> temperatureProbeNoMaxMap = MySelfUtil.getHandleStr(entity.get("329629"));
//            if (temperatureProbeNoMaxMap != null) {
//                String temperatureProbeNoMax = temperatureProbeNoMaxMap.get("value");
//                goldenDragon.setParamsEight(temperatureProbeNoMax);
//            }
//            // 最高温度值
//            Map<String, String> temperatureMaxMap = MySelfUtil.getHandleStr(entity.get("329630"));
//            if (temperatureMaxMap != null) {
//                String temperatureMax = temperatureMaxMap.get("value");
//                Double temperatureMaxDouble = Double.parseDouble(temperatureMax) - 40;
//                goldenDragon.setParamsTen(NumberFormatUtil.doubleFormat(temperatureMaxDouble));
//                time = temperatureMaxMap.get("time");
//            }
//            // 最低温度子系统号
//            Map<String, String> temperatureSystemNoMinMap = MySelfUtil.getHandleStr(entity.get("329631"));
//            if (temperatureSystemNoMinMap != null) {
//                String temperatureSystemNoMin = temperatureSystemNoMinMap.get("value");
//                goldenDragon.setParamsEleven(temperatureSystemNoMin);
//                time = temperatureSystemNoMinMap.get("time");
//            }
//            // 最低温度探针序号
//            Map<String, String> temperatureProbeNoMinMap = MySelfUtil.getHandleStr(entity.get("329632"));
//            if (temperatureProbeNoMinMap != null) {
//                String temperatureProbeNoMin = temperatureProbeNoMinMap.get("value");
//                goldenDragon.setParamsTewlve(temperatureProbeNoMin);
//                time = temperatureProbeNoMinMap.get("time");
//            }
//            // 最低温度值
//            Map<String, String> temperatureMinMap = MySelfUtil.getHandleStr(entity.get("329633"));
//            if (temperatureMinMap != null) {
//                String temperatureMin = temperatureMinMap.get("value");
//                Double temperatureMinDouble = Double.parseDouble(temperatureMin) - 40;
//                goldenDragon.setParamsThirteen(NumberFormatUtil.doubleFormat(temperatureMinDouble));
//                time = temperatureMinMap.get("time");
//            }
//            // 通用报警标志
//            Map<String, String> commonAlarmMarkMap = MySelfUtil.getHandleStr(entity.get("329635"));
//            if (commonAlarmMarkMap != null) {
//                String commonAlarmMark = commonAlarmMarkMap.get("value");
//                goldenDragon.setParamsFourteen(commonAlarmMark);
//                time = commonAlarmMarkMap.get("time");
//            }
//            if (StringUtils.isNotBlank(time)) {
//                goldenDragon.setDataCurrentTime(time);
//                goldenDragon.setCeateTime(LocalDateTime.now());
//                String vehicleId = map.get(entity.get("VehicleID"));
//                if (StringUtils.isBlank(vehicleId)) {
//                    map.put(entity.get("VehicleID"), time);
//                    goldenDragonService.save(goldenDragon);
//                } else if (!vehicleId.equals(time)) {
//                    map.put(entity.get("VehicleID"), time);
//                    goldenDragonService.save(goldenDragon);
//                }
//            }
//        });
//    }
//}
