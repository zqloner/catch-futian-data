package com.mgl.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mgl.bean.carshop.CarNumberDict;
import com.mgl.bean.carshop.MglCarshopFutianDataDetail;
import com.mgl.bean.carshop.MglCarshopTianfuData;
import com.mgl.bean.dto.FuTiamDetailDtoList;
import com.mgl.bean.dto.FuTianDetailDto;
import com.mgl.bean.dto.VoltageVo;
import com.mgl.bean.warns.MglCarshopStaticWarning;
import com.mgl.service.carshop.CarNumberDictService;
import com.mgl.service.carshop.MglCarshopFutianDataDetailService;
import com.mgl.service.carshop.MglCarshopTianfuDataService;
import com.mgl.service.warns.MglCarshopStaticWarningService;
import com.mgl.utils.MyHttpClientUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    @Resource
    private MglCarshopTianfuDataService mglCarshopTianfuDataService;
    @Resource
    private CarNumberDictService carNumberDictService;
    @Resource
    private MglCarshopFutianDataDetailService mglCarshopFutianDataDetailService;
    @Resource
    private MglCarshopStaticWarningService mglCarshopStaticWarningService;

    /**
     * 抓取数据
     *
     * @throws Exception
     */
    @Scheduled(cron = "0 0 12 * * ? ")
    public void produceTopic() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.plusDays(-1);
        String url = "http://api.itink.com.cn/api/vehicle/getCanBusByCarId.json";
        List<CarNumberDict> cars = carNumberDictService.list(new QueryWrapper<>(new CarNumberDict().setDelFlag(0)));
        Map<String, Object> params = new HashMap();
        params.put("token", "2b37d26a9d4446d48a0a87a0f6852355");
        params.put("queryDate", yesterday);
        TimeInterval timer = DateUtil.timer();
        for (CarNumberDict car : cars) {
            try {
                params.put("carId", car.getCarVin());
                timer.intervalRestart();
                String content = MyHttpClientUtils.doGetParam(url, params);
                System.out.println(car.getCarVin() + "====" + yesterday + "日====>抓取花费时间: " + timer.intervalSecond() + "s");
                timer.intervalRestart();
                mglCarshopTianfuDataService.save(new MglCarshopTianfuData().setCarVin((String) params.get("carId")).setTextContent(content).setRealDate(yesterday).setCreateDate(today));
                System.out.println(car.getCarVin() + "====" + yesterday + "日====>存库花费时间: " + timer.intervalSecond() + "s");
//                存详情
                FuTiamDetailDtoList fuTiamDetailDtoList = JSONObject.parseObject(content, FuTiamDetailDtoList.class);
                List<FuTianDetailDto> data = fuTiamDetailDtoList.getData();
//        转换时间
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                timer.intervalRestart();
                List<MglCarshopFutianDataDetail> mglCarshopFutianDataDetails = new ArrayList<>();
                data.forEach(x -> {
//                    每条数据的代码
                    Map<String, String> codes = x.getCodes();
//                    创建数据
                    MglCarshopFutianDataDetail mglCarshopFutianDataDetail = new MglCarshopFutianDataDetail();
                    mglCarshopFutianDataDetail.setVin((String) params.get("carId"));
                    mglCarshopFutianDataDetail.setOrderNumber(car.getOrderNumber());
                    mglCarshopFutianDataDetail.setCarCurrentTime(x.getTime());
                    if (!StringUtils.isBlank(codes.get("1030002"))) {
                        try {
                            mglCarshopFutianDataDetail.setMileages((Double.parseDouble(codes.get("1030002")) / 1000) + "");
                        } catch (NumberFormatException e) {
                            mglCarshopFutianDataDetail.setMileages(codes.get("1030002"));
                            e.printStackTrace();
                        }
                    }
                    mglCarshopFutianDataDetail.setSpeed(codes.get("1010027"));
                    mglCarshopFutianDataDetail.setRunModel(codes.get("1140013"));
                    mglCarshopFutianDataDetail.setChargeState(codes.get("1110004"));
                    mglCarshopFutianDataDetail.setSoc(codes.get("1110045"));
                    mglCarshopFutianDataDetail.setTotalCurrent(codes.get("1110044"));
                    mglCarshopFutianDataDetail.setTotalVoltage(codes.get("1110043"));
                    mglCarshopFutianDataDetail.setMaxTemperature(codes.get("1110050"));
                    mglCarshopFutianDataDetail.setMinTemperature(codes.get("1110049"));
                    mglCarshopFutianDataDetail.setMaxVoltage(codes.get("1110048"));
                    mglCarshopFutianDataDetail.setMinVoltage(codes.get("1110047"));
                    mglCarshopFutianDataDetail.setMinVoltageCellCode(codes.get("1110070"));
                    mglCarshopFutianDataDetail.setMaxVoltageCellCode(codes.get("1110068"));
                    mglCarshopFutianDataDetail.setMinVoltageBoxCode(codes.get("1110069"));
                    mglCarshopFutianDataDetail.setMaxVoltageBoxCode(codes.get("1110067"));
                    mglCarshopFutianDataDetail.setMinTemperatureNeedle(codes.get("1110074"));
                    mglCarshopFutianDataDetail.setMaxTemperatureNeedle(codes.get("1110072"));
                    mglCarshopFutianDataDetail.setMinTemperatrureBoxCode(codes.get("1110073"));
                    mglCarshopFutianDataDetail.setMaxTemperatrureBoxCode(codes.get("1110071"));
//                    获取codes键
                    List<String> collect = codes.keySet().stream().filter(y -> y.contains("1110107-1")).sorted((a, b) -> Integer.parseInt(a.substring(10)) - Integer.parseInt(b.substring(10))).collect(Collectors.toList());
                    List<String> collect1 = codes.keySet().stream().filter(y -> y.contains("1110108-1")).sorted((a, b) -> Integer.parseInt(a.substring(10)) - Integer.parseInt(b.substring(10))).collect(Collectors.toList());
                    List<VoltageVo> list = new ArrayList<>();
                    List<VoltageVo> list1 = new ArrayList<>();
                    collect.forEach(m -> {
                        VoltageVo voltageVo = new VoltageVo();
                        voltageVo.setKey(m);
                        voltageVo.setValue(Double.valueOf(codes.get(m)));
                        list.add(voltageVo);
                    });
                    collect1.forEach(m -> {
                        VoltageVo voltageVo = new VoltageVo();
                        voltageVo.setKey(m);
                        voltageVo.setValue(Double.valueOf(codes.get(m)));
                        list1.add(voltageVo);
                    });
                    mglCarshopFutianDataDetail.setSingleCellVoltage(JSONObject.toJSONString(list));
                    mglCarshopFutianDataDetail.setSingleCellTemperature(JSONObject.toJSONString(list1));
                    mglCarshopFutianDataDetail.setBatteryVersionInformation(codes.get("1130195"));
                    mglCarshopFutianDataDetail.setTotalNumberOfSingleBatteries(codes.get("1110076-1"));
                    mglCarshopFutianDataDetail.setSocLowAlarm(codes.get("1110065"));
                    mglCarshopFutianDataDetail.setBatteryHighTemperatureAlarm(codes.get("1110064"));
                    mglCarshopFutianDataDetail.setTemperatureDifferenceAlarm(codes.get("1110061"));
                    mglCarshopFutianDataDetail.setEquipOvervoltageAlarm(codes.get("1110054"));
                    mglCarshopFutianDataDetail.setEquipmentUndervoltageAlarm(codes.get("1110053"));
                    mglCarshopFutianDataDetail.setSystemMismatchAlarm(codes.get("1110052"));
                    mglCarshopFutianDataDetail.setMaximumAlarmLevel(codes.get("1110046"));
                    mglCarshopFutianDataDetail.setTypeOverchargeAlarm(codes.get("1140017"));
                    mglCarshopFutianDataDetail.setSocJumpAlarm(codes.get("1140019"));
                    mglCarshopFutianDataDetail.setInsulationAlarm(codes.get("1110087"));
                    mglCarshopFutianDataDetail.setDcStatusAlarm(codes.get("1130237"));
                    mglCarshopFutianDataDetail.setDcTemperatrureAlarm(codes.get("1130238"));
                    mglCarshopFutianDataDetail.setHighPressureInterlockAlarm(codes.get("1110157"));
                    mglCarshopFutianDataDetail.setPoorBatteryConsistencyAlarm(codes.get("1110132"));
                    mglCarshopFutianDataDetail.setSingleBatteryOvervoltageAlarm(codes.get("1130180"));
                    mglCarshopFutianDataDetail.setLowVoltageAlarmForSingleBattery(codes.get("1130181"));
                    mglCarshopFutianDataDetail.setSocHighAlarm(codes.get("1130183"));
                    mglCarshopFutianDataDetails.add(mglCarshopFutianDataDetail);
                });
                System.out.println(car.getCarVin() + "====" + yesterday + "日====>执行循环花费时间: " + timer.intervalSecond() + "s");
                timer.intervalRestart();
                mglCarshopFutianDataDetailService.saveBatch(mglCarshopFutianDataDetails);
                System.out.println(car.getCarVin() + "====" + yesterday + "日====>存详情列表花费时间: " + timer.intervalSecond() + "s");
            } catch (Exception e) {
                e.printStackTrace();
                mglCarshopTianfuDataService.save(new MglCarshopTianfuData().setCarVin((String) params.get("carId")).setTextContent("数据异常").setRealDate(yesterday).setCreateDate(today));
                continue;
            }
        }
        System.out.println(yesterday + "日=============》数据抓取完毕");
    }


    /**
     * 算法一
     *
     * @throws Exception
     */
    @Scheduled(cron = "0 0 22 * * ? ")
    public void firstArithmetic() throws Exception {
        List<CarNumberDict> cars = carNumberDictService.list(new QueryWrapper<>(new CarNumberDict().setDelFlag(0)));
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.plusDays(-1);
        String start = yesterday + " 00:00:00";
        String end = yesterday + " 23:59:59";
        cars.forEach(x -> {
            List<MglCarshopFutianDataDetail> detailsByVin = mglCarshopFutianDataDetailService.findDetailsByVin(x.getCarVin(),start,end);
            List<List<VoltageVo>> lists = new ArrayList<>();
            for (int i = 0; i < detailsByVin.size(); i++) {
                List<VoltageVo> bills = JSONArray.parseArray(detailsByVin.get(i).getSingleCellVoltage(), VoltageVo.class);
                Double d = bills.stream().collect(Collectors.averagingDouble(VoltageVo::getValue));
                List<VoltageVo> collect = bills.stream().collect(Collectors.toList());
                collect.forEach(n->n.setValue(d-n.getValue()));
                lists.add(collect);
            }
            List<MglCarshopStaticWarning> mglCarshopStaticWarnings = new ArrayList<>();
            if (lists.size() > 0) {
                int length = lists.get(0).size();
                for (int i = 0; i < length; i++) {
                    MglCarshopStaticWarning mglCarshopStaticWarning = new MglCarshopStaticWarning();
                    mglCarshopStaticWarning.setDelFlag(0);
                    mglCarshopStaticWarning.setCellNumber(lists.get(0).get(i).getKey());
                    List<Double> list = new ArrayList<>();
                    for (int j = 0; j < lists.size(); j++) {
                        if (lists.get(j).size() == length) {
                            list.add(lists.get(j).get(i).getValue());
                        }else {
                            continue;
                        }
                    }
                    List<Double> collect = list.stream().filter(m -> m > 0).collect(Collectors.toList());
                    Double asDouble = null;
                    if (collect.size() > 0) {
                        asDouble = collect.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
                    } else {
                        continue;
                    }
//                    mglCarshopStaticWarning.setValue(asDouble * 1000);
                    mglCarshopStaticWarning.setValue((double) Math.round(asDouble * 100000) / 100);

                    mglCarshopStaticWarning.setVin(x.getCarVin());
                    mglCarshopStaticWarning.setCurretsDateTime(yesterday.toString());
                    mglCarshopStaticWarning.setCurretsTimeSeconds(yesterday.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli());
//                if (d > 40) {
                    if (asDouble * 1000 > 40) {
                        mglCarshopStaticWarning.setType(1);
                        mglCarshopStaticWarnings.add(mglCarshopStaticWarning);
                    } else {
                        mglCarshopStaticWarning.setType(0);
                    }
                }
            }
            mglCarshopStaticWarningService.saveBatch(mglCarshopStaticWarnings);
        });
    }



    /**
     * 扫描满足二级预警的一级预警将其修改为二级预警
     *
     * @throws Exception
     */
    @Scheduled(cron = "0 0 23 * * ? ")
    public void changeLevel() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.plusDays(-1);
        LocalDate sevenDaysAgo = yesterday.plusDays(-8);
        List<MglCarshopStaticWarning> list = mglCarshopStaticWarningService.list(new QueryWrapper<>(new MglCarshopStaticWarning().setCurretsDateTime(yesterday.toString()).setType(1).setDelFlag(0)));
        list.forEach(x -> {
            int flag = 0;
//            List<MglCarshopStaticWarning> listByCarVinAndDate = mglCarshopStaticWarningService.findListByCarVinAndDate(x.getVin(),sevenDaysAgo.toString(),yesterday.toString(),x.getCellNumber());
            LocalDate localDate = LocalDate.parse(x.getCurretsDateTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate starDate = localDate.plusDays(-6);
            List<MglCarshopStaticWarning> listByCarVinAndDate = mglCarshopStaticWarningService.findListByCarVinAndDate(x.getVin(),starDate.toString(),x.getCurretsDateTime(),x.getCellNumber());
            if (listByCarVinAndDate.size() >= 7) {
                for (int i = 0; i < listByCarVinAndDate.size() - 1; i++) {
//                    if (listByCarVinAndDate.get(i).getValue() >= listByCarVinAndDate.get(i+1).getValue()) {
                    if (listByCarVinAndDate.get(i).getValue() >= listByCarVinAndDate.get(i+1).getValue()) {
                        break;
                    }
                    flag++;
                }
            }
            if (flag == 6) {
                MglCarshopStaticWarning mglCarshopStaticWarning = mglCarshopStaticWarningService.getById(x.getId()).setType(2);
                mglCarshopStaticWarningService.saveOrUpdate(mglCarshopStaticWarning);
            }
        });
    }
}
