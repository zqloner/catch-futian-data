package com.mgl.schedule.job;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mgl.bean.dto.GoldenDragonDto;
import com.mgl.bean.golden.CarGoldenDragonNumberDict;
import com.mgl.bean.golden.GoldenDragon;
import com.mgl.common.Gloables;
import com.mgl.sdk.exception.custom.MglRuntimeException;
import com.mgl.sdk.http.MglRestTemplate;
import com.mgl.sdk.thread.MglThreadPoolExecutor;
import com.mgl.sdk.utils.GroupUtil;
import com.mgl.sdk.utils.NumberFormatUtil;
import com.mgl.service.golden.CarGoldenDragonNumberDictService;
import com.mgl.service.golden.GoldenDragonService;
import com.mgl.utils.compress.CompressUtils;
import com.mgl.utils.csv.CsvExportUtil;
import com.mgl.utils.file.FileUtil;
import com.mgl.utils.ftp.ftpClientUtil.FtpTool;
import com.mgl.utils.props.BeanAndMap;
import com.mgl.utils.selfutil.MySelfUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * @Description TODO
 * @Author fengwei
 * @Date 2020/7/22 9:27
 * @Version 1.0
 */
@Component
@Log4j2
public class GoldenDragonQuartzJob {

    @Autowired
    private MglRestTemplate restTemplate;

    @Autowired
    private GoldenDragonService goldenDragonService;

    @Autowired
    private CarGoldenDragonNumberDictService carGoldenDragonNumberDictService;

    @Value("${brightease.ftpZipPath}")
    private String ftpZipPath;

    @Value("${brightease.goldenDragonCsvPath}")
    private String goldenDragonCsvPath;

    @Value("${ftp.host}")
    private String host;

    @Value("${ftp.port}")
    private Integer port;

    @Value("${ftp.username}")
    private String username;

    @Value("${ftp.password}")
    private String password;

    private static final Map<String, String> map = new HashMap<>();
    private String time = "";

    /**
     * 定时任务抓取数据
     */
    @Scheduled(cron = "* * 5-23 * * ? ")
    @Async
    public void getGoldenDragonData(){
        List<CarGoldenDragonNumberDict> numberDictList =
                carGoldenDragonNumberDictService.list(new QueryWrapper<>(new CarGoldenDragonNumberDict().setCarFlag(0)));
        if (CollectionUtils.isEmpty(numberDictList)) {
            return;
        }
        // 创建线程池
        MglThreadPoolExecutor executor = null;
        try {
            executor = new MglThreadPoolExecutor(5,5,30,"金龙实时数据请求");
            Set<Callable<Map>> callables = new HashSet<>();
            // 将汽车分组（每300辆分一组）
            List<List<CarGoldenDragonNumberDict>> lists = GroupUtil.divideGroup(numberDictList, 300);
            for (int i = 0; i < lists.size(); i++) {
                int finalI = i;
                callables.add(() -> {
                    Map map = new HashMap();
                    map.put("group-" + finalI,buildData(lists.get(finalI)));
                    return map;
                });
            }
            executor.invokeAll(callables);
        } catch (Exception e) {
            throw new MglRuntimeException("分组请求金龙数据出错！", e);
        } finally {
            // 检查线程池是否关闭，如果没有关闭就再次关闭，防止异常发生
            if (executor != null && !executor.isShutdown()) {
                executor.shutdown();
            }
        }
    }

    /**
     * 定时任务生成csv
     */
    @Scheduled(cron = "0 0 0 * * ? ")
    @Async
    public void uploadGoldenDragonData(){
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.plusDays(-1);
        // 查询出金龙数据(昨天生成的)
        List<GoldenDragon> goldenDragonList = goldenDragonService.queryDataTheDayBrfore(yesterday);
        // 创建目录
        File file = new File(goldenDragonCsvPath);
        if (!file.exists()) {
            FileUtil.forceDirectory(goldenDragonCsvPath);
        }
        String dir = ftpZipPath + "/" + yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        FileUtil.forceDirectory(dir);
        // 创建线程池
        MglThreadPoolExecutor poolExecutor = null;
        try {
            poolExecutor = new MglThreadPoolExecutor(5,16,30,"金龙数据生成csv");
            Set<Callable<Map>> callables = new HashSet<>();
            for (int i = 0;i < goldenDragonList.size(); i++) {
                int finalI = i;
                callables.add(() -> {
                    Map map = new HashMap();
                    map.put("group-" + finalI,generateGoldenDragonCsv(goldenDragonList.get(finalI)));
                    return map;
                });
            }
            poolExecutor.invokeAll(callables);
            // 压缩
            String[] extention = new String[]{Gloables.CSV_EXTENT};
            List<File> files = FileUtil.listFile(new File(goldenDragonCsvPath), extention, true);
            if (goldenDragonList.size() == files.size()) {
                CompressUtils.zip(dir, dir + ".zip");
            }
            // FTP
            FtpTool tool = new FtpTool(host, port, username, password);
            tool.initFtpClient();
            tool.CreateDirecroty(Gloables.GOLDENDRAGON_ZIP_PATH);
            boolean uploadFile = tool.uploadFile(Gloables.GOLDENDRAGON_ZIP_PATH,
                    yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".zip", dir + ".zip");
            // 删除文件夹
            if (uploadFile) {
                CompressUtils.deleteDirectory(new File(dir));
                CompressUtils.doDeleteEmptyDir(dir + ".zip");
            }
            log.info(yesterday + "日=============》数据抓取完毕");
        } catch (Exception e) {
            throw new MglRuntimeException("生成csv过程出错！", e);
        } finally {
            // 检查线程池是否关闭，如果没有关闭就再次关闭，防止异常发生
            if (poolExecutor != null && !poolExecutor.isShutdown()) {
                poolExecutor.shutdown();
            }
        }
    }

    /**
     * 生成金龙的csv
     * @param goldenDragon 金龙数据
     * @return csv
     */
    private Object generateGoldenDragonCsv(GoldenDragon goldenDragon) {
        // 生成csv
        List<Map<String, Object>> maps = new ArrayList<>();
        maps.add(BeanAndMap.beanToMap(goldenDragon));
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(goldenDragonCsvPath + goldenDragon.getVin() + Gloables.CSV_EXTENT);
            CsvExportUtil.doExport(maps, Gloables.GOLDEN_TITLE, Gloables.GOLDEN_KEYS, os);
        } catch (Exception e) {
            throw new MglRuntimeException("金龙生成csv出错！", e);
        }
        return null;
    }

    /**
     * 组装数据
     * @param cars 车辆数据
     * @return
     */
    private Object buildData(List<CarGoldenDragonNumberDict> cars) {
        String token = restTemplate.getForObject(Gloables.GOLD_TOKEN_URL,String.class);
        log.info("[token]:{}",token);
        List<String> carVin = new ArrayList<>();
        cars.forEach(car -> {
            carVin.add(car.getCarVin());
        });
        String carNum = StringUtils.join(carVin,",");
        String requestUrl = Gloables.GOLD_DATA_BASE_URL + carNum + Gloables.GOLD_PARAMS_TYPES + token;
        JSONObject resultData = restTemplate.getForObject(requestUrl,JSONObject.class);
        log.info("[resultData]:{}",resultData);
        GoldenDragonDto dto = JSONObject.toJavaObject(resultData, GoldenDragonDto.class);
        return assembleData(dto.getData());
    }

    /**
     * 组装数据
     * @param data 金龙数据
     */
    private List<GoldenDragon> assembleData(List<Map<String, String>> data) {
        List<GoldenDragon> goldenDragons = new ArrayList<>();
        data.forEach(entity -> {
            GoldenDragon goldenDragon = new GoldenDragon();
            // 车辆VIN
            goldenDragon.setVin(entity.get("VehicleID"));
            // 终端编号
            goldenDragon.setTerminalNumber(entity.get("DeviceNo"));
            // 在线状态
            goldenDragon.setOnline(entity.get("Online"));
            // 应答结果
            goldenDragon.setResult(entity.get("Result"));
            // 总电压
            Map<String, String> voltageMap = MySelfUtil.getHandleStr(entity.get("329609"));
            if (voltageMap != null) {
                String voltage = voltageMap.get("value");
                Double totalVoltage = Double.parseDouble(voltage) * 0.1;
                goldenDragon.setTotalVoltage(NumberFormatUtil.doubleFormat(totalVoltage));
                time = voltageMap.get("time");
            }
            // 总电流
            Map<String, String> electricityMap = MySelfUtil.getHandleStr(entity.get("329610"));
            if (electricityMap != null) {
                String electricity = electricityMap.get("value");
                Double totalElectricity = Double.parseDouble(electricity) * 0.1 - 1000;
                goldenDragon.setTotalCurrent(NumberFormatUtil.doubleFormat(totalElectricity));
                time = electricityMap.get("time");
            }
            // SOC
            Map<String,String> socMap = MySelfUtil.getHandleStr(entity.get("329611"));
            if (socMap != null) {
                String soc = socMap.get("value");
                goldenDragon.setSoc(soc);
                time = socMap.get("time");
            }
            // 最高电压电池子系统号
            Map<String,String> cellSystemNoMaxMap = MySelfUtil.getHandleStr(entity.get("329622"));
            if (cellSystemNoMaxMap != null) {
                String cellSystemNoMax = cellSystemNoMaxMap.get("value");
                goldenDragon.setParamsFirst(cellSystemNoMax);
                time = cellSystemNoMaxMap.get("time");
            }
            // 最高电压电池单体代号
            Map<String,String> cellSingleNoMaxMap = MySelfUtil.getHandleStr(entity.get("329623"));
            if (cellSingleNoMaxMap != null) {
                String cellSingleNoMax = cellSingleNoMaxMap.get("value");
                goldenDragon.setParamsSecond(cellSingleNoMax);
                time = cellSingleNoMaxMap.get("time");
            }
            // 电池单体电压最高值
            Map<String,String> cellSinglePeakValueMap = MySelfUtil.getHandleStr(entity.get("329624"));
            if (cellSinglePeakValueMap != null) {
                String cellSinglePeakValue = cellSinglePeakValueMap.get("value");
                Double cellPeakValueDouble = Double.parseDouble(cellSinglePeakValue) * 0.001;
                goldenDragon.setParamsThird(NumberFormatUtil.doubleFormat(cellPeakValueDouble));
                time = cellSinglePeakValueMap.get("time");
            }
            // 最低电压电池子系统号
            Map<String,String> cellSystemNoMinMap = MySelfUtil.getHandleStr(entity.get("329625"));
            if (cellSystemNoMinMap != null) {
                String cellSystemNoMin = cellSystemNoMinMap.get("value");
                goldenDragon.setParamsFouth(cellSystemNoMin);
                time = cellSystemNoMinMap.get("time");
            }
            // 最低电压电池单体代号
            Map<String,String> cellSingleNoMinMap = MySelfUtil.getHandleStr(entity.get("329626"));
            if (cellSingleNoMinMap != null) {
                String cellSingleNoMin = cellSingleNoMinMap.get("value");
                goldenDragon.setParamsFiveth(cellSingleNoMin);
                time = cellSingleNoMinMap.get("time");
            }
            // 电池单体电压最低值
            Map<String,String> cellSingleMinimumMap = MySelfUtil.getHandleStr(entity.get("329627"));
            if (cellSingleMinimumMap != null) {
                String cellSingleMinimum = cellSingleMinimumMap.get("value");
                Double cellSingleMinimumDouble = Double.parseDouble(cellSingleMinimum) * 0.001;
                goldenDragon.setParamsSix(NumberFormatUtil.doubleFormat(cellSingleMinimumDouble));
                time = cellSingleMinimumMap.get("time");
            }
            // 最高温度子系统号
            Map<String,String> temperatureSystemNoMaxMap = MySelfUtil.getHandleStr(entity.get("329628"));
            if (temperatureSystemNoMaxMap != null) {
                String temperatureSystemNoMax = temperatureSystemNoMaxMap.get("value");
                goldenDragon.setParamsSeven(temperatureSystemNoMax);
            }
            // 最高温度探针序号
            Map<String,String> temperatureProbeNoMaxMap = MySelfUtil.getHandleStr(entity.get("329629"));
            if (temperatureProbeNoMaxMap != null) {
                String temperatureProbeNoMax = temperatureProbeNoMaxMap.get("value");
                goldenDragon.setParamsEight(temperatureProbeNoMax);
            }
            // 最高温度值
            Map<String,String> temperatureMaxMap = MySelfUtil.getHandleStr(entity.get("329630"));
            if (temperatureMaxMap != null) {
                String temperatureMax = temperatureMaxMap.get("value");
                Double temperatureMaxDouble = Double.parseDouble(temperatureMax) - 40;
                goldenDragon.setParamsTen(NumberFormatUtil.doubleFormat(temperatureMaxDouble));
                time = temperatureMaxMap.get("time");
            }
            // 最低温度子系统号
            Map<String,String> temperatureSystemNoMinMap = MySelfUtil.getHandleStr(entity.get("329631"));
            if (temperatureSystemNoMinMap != null) {
                String temperatureSystemNoMin = temperatureSystemNoMinMap.get("value");
                goldenDragon.setParamsEleven(temperatureSystemNoMin);
                time = temperatureSystemNoMinMap.get("time");
            }
            // 最低温度探针序号
            Map<String,String> temperatureProbeNoMinMap = MySelfUtil.getHandleStr(entity.get("329632"));
            if (temperatureProbeNoMinMap != null) {
                String temperatureProbeNoMin = temperatureProbeNoMinMap.get("value");
                goldenDragon.setParamsTewlve(temperatureProbeNoMin);
                time = temperatureProbeNoMinMap.get("time");
            }
            // 最低温度值
            Map<String,String> temperatureMinMap = MySelfUtil.getHandleStr(entity.get("329633"));
            if (temperatureMinMap != null) {
                String temperatureMin = temperatureMinMap.get("value");
                Double temperatureMinDouble = Double.parseDouble(temperatureMin) - 40;
                goldenDragon.setParamsThirteen(NumberFormatUtil.doubleFormat(temperatureMinDouble));
                time = temperatureMinMap.get("time");
            }
            // 通用报警标志
            Map<String,String> commonAlarmMarkMap = MySelfUtil.getHandleStr(entity.get("329635"));
            if (commonAlarmMarkMap != null) {
                String commonAlarmMark = commonAlarmMarkMap.get("value");
                goldenDragon.setParamsFourteen(commonAlarmMark);
                time = commonAlarmMarkMap.get("time");
            }
            System.out.println("++++++++++++++"+time);
            if (StringUtils.isNotBlank(time)) {
                goldenDragon.setDataCurrentTime(time);
                goldenDragon.setCeateTime(LocalDateTime.now());
                String vehicleId = map.get(entity.get("VehicleID"));
                if (StringUtils.isBlank(vehicleId)) {
                    map.put(entity.get("VehicleID"),time);
                    goldenDragonService.save(goldenDragon);
                    goldenDragons.add(goldenDragon);
                }else if (!vehicleId.equals(time)) {
                    map.put(entity.get("VehicleID"),time);
                    goldenDragonService.save(goldenDragon);
                    goldenDragons.add(goldenDragon);
                }

            }

        });
        return goldenDragons;
    }
}
