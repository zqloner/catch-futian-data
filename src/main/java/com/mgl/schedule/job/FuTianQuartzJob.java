package com.mgl.schedule.job;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mgl.bean.carshop.CarNumberDict;
import com.mgl.bean.carshop.MglCarshopTianfuData;
import com.mgl.bean.dto.FuTiamDetailDtoList;
import com.mgl.bean.dto.FuTianDetailDto;
import com.mgl.common.Gloables;
import com.mgl.sdk.http.MglRestTemplate;
import com.mgl.sdk.utils.DateFormatUtil;
import com.mgl.service.carshop.CarNumberDictService;
import com.mgl.service.carshop.MglCarshopTianfuDataService;
import com.mgl.utils.file.FileUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author fengwei
 * @Date 2020/7/22 16:25
 * @Version 1.0
 */
@Component
@Log4j2
public class FuTianQuartzJob {

    @Autowired
    private MglCarshopTianfuDataService mglCarshopTianfuDataService;

    @Autowired
    private CarNumberDictService carNumberDictService;

    @Autowired
    private MglRestTemplate restTemplate;

    @Value("${brightease.ftpZipPath}")
    private String ftpZipPath;

    @Value("${brightease.csvBeiyouPath}")
    private String csvBeiyouPath;

//    @Scheduled(cron = "* * * * * ? ")
    public void getFuTianData(){
        Map<String,String> params = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.plusDays(-1);
        // 获取汽车数据词典
        List<CarNumberDict> cars = carNumberDictService.list(new QueryWrapper<>(new CarNumberDict().setDelFlag(0)));
        // 创建文件夹
        String dir = ftpZipPath + "/" + DateFormatUtil.firstDateFormat(yesterday);
        FileUtil.forceDirectory(dir);
        // 循环获取车辆实时数据
        // 请求地址 http://api.itink.com.cn/api/vehicle/getCanBusByCarId.json?token=2b37d26a9d4446d48a0a87a0f6852355&queryDate=2020-07-12&carId=LVBV3J0BXJE900173
        String requestUrl = Gloables.API_URL;
        for (CarNumberDict car : cars) {
            try {
                String resultData = restTemplate.getForObject(requestUrl + "?token={token}&queryDate={queryDate}&carId={carId}",
                        String.class, Gloables.API_TOKEN, yesterday.toString(), car.getCarVin());
                // 存原始数据
                mglCarshopTianfuDataService.save(new MglCarshopTianfuData().setCarVin((String) params.get(Gloables.API_PARAM_CARID)).
                        setJsonContent(resultData).setRealDate(yesterday).setCreateDate(today));
                // 存详情
                FuTiamDetailDtoList fuTiamDetailDtoList = JSONObject.parseObject(resultData,FuTiamDetailDtoList.class);
                List<FuTianDetailDto> fuTiamDetailDtoListData = fuTiamDetailDtoList.getData();
                String csvFileName =
                        1000000 + car.getId() + "-" + params.get(Gloables.API_PARAM_CARID) + "-" + yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + Gloables.CSV_EXTENT;
                String fileName = 1000000 + car.getId() + "-" + params.get(Gloables.API_PARAM_CARID) + Gloables.CSV_EXTENT;
                // 导出为CSV
                List<Map<String,Object>> dataList = new ArrayList<>();
                // FileOutputStream os = new FileOutputStream(csvBeiyouPath + csvFileName);
                FileOutputStream fileOutputStream = new FileOutputStream(dir + "/" + fileName);
            } catch (Exception e) {
                log.error("汽车Vin:{}获取数据发生异常",car.getCarVin());

            }
        }
    }
}
