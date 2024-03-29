package com.mgl.schedule;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mgl.bean.dto.GoldenDragonDto;
import com.mgl.bean.golden.CarGoldenDragonNumberDict;
import com.mgl.bean.golden.GoldenDragon;
import com.mgl.utils.constants.Gloables;
import com.mgl.utils.sdk.exception.custom.MglRuntimeException;
import com.mgl.utils.sdk.http.MglRestTemplate;
import com.mgl.utils.sdk.thread.MglThreadPoolExecutor;
import com.mgl.utils.sdk.utils.GroupUtil;
import com.mgl.utils.sdk.utils.NumberFormatUtil;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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

    private static final Map<String, String> GLOBAL_MAP = new ConcurrentHashMap<>();

    private List<CarGoldenDragonNumberDict> numberDictList = null;

    private static final List<String> GOLDEN_DRAGON_CSV_LIST = new CopyOnWriteArrayList<>();

    /**
     * 定时任务抓取数据
     */
    @Scheduled(cron = "* * 5-23 * * ?")
    public void getGoldenDragonData(){
        // 保证车辆数据词典不为空
         String updateQueryTime = "00:00";
         if (updateQueryTime.equals(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
                 || CollectionUtils.isEmpty(numberDictList)) {
             numberDictList = carGoldenDragonNumberDictService.list(new QueryWrapper<>(new CarGoldenDragonNumberDict().setCarFlag(0)));
             log.info("【{}】查询金龙数据词典",LocalDateTime.now());
         }
         if (CollectionUtils.isEmpty(numberDictList)) {
             return;
         }
        // 实时生成金龙数据,get请求url有限制，所以采用多线程方式
        MglThreadPoolExecutor executor = null;
        try {
            executor = new MglThreadPoolExecutor(4,32,30,"金龙实时数据请求");
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
    @Scheduled(cron = "0 1 0 * * ?")
    public void uploadGoldenDragonData(){
        log.info("【{}】开始生成金龙数据csv",LocalDateTime.now());
        // 生成昨天的csv
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.plusDays(-1);
        // 创建目录
        String goldenDragonDir = goldenDragonCsvPath + yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        File file = new File(goldenDragonDir);
        if (!file.exists() && !file.isDirectory()) {
            FileUtil.forceDirectory(goldenDragonDir);
        }
        // 由于数据量太大，容易造成OOM,所以每辆汽车单独查询
        List<String> carVinList = carGoldenDragonNumberDictService.queryCarVinList();
        // 将汽车数量放入全局list中
        GOLDEN_DRAGON_CSV_LIST.addAll(carVinList);


        // 多线程来压缩速度
        buildCsv(carVinList,goldenDragonDir,yesterday);


        // 如果csv数量与汽车数量不同，则将没有生成的汽车在重新生成一遍
        log.warn("汽车还有：{}没生成csv", GOLDEN_DRAGON_CSV_LIST.size());
        while (GOLDEN_DRAGON_CSV_LIST.size() != 0) {
            buildCsv(GOLDEN_DRAGON_CSV_LIST,goldenDragonDir,yesterday);
        }
        log.info("csv完成，开始上传ftp,时间：【{}】", LocalDateTime.now());


        try {
            // 压缩
            String[] extention = new String[]{Gloables.CSV_EXTENT};
            List<File> files = FileUtil.listFile(new File(goldenDragonDir), extention, true);
            log.warn("csv文件数目为：{}",files.size());
            if (carVinList.size() == files.size()) {
                CompressUtils.zip(goldenDragonDir, goldenDragonDir + ".zip");
            }
            // FTP
            FtpTool tool = new FtpTool(host, port, username, password);
            boolean isConnection = tool.initFtpClient();
            // 保证ftp服务器能连接上
            while (!isConnection) {
                isConnection = tool.initFtpClient();
                if (isConnection) {
                    break;
                }
            }
            tool.CreateDirecroty(Gloables.GOLDENDRAGON_ZIP_PATH);
            boolean uploadFile = tool.uploadFile(Gloables.GOLDENDRAGON_ZIP_PATH,
                    yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".zip", goldenDragonDir + ".zip");
            // 删除文件夹
            if (uploadFile && file.exists()) {
                CompressUtils.deleteDirectory(new File(goldenDragonDir));
                CompressUtils.doDeleteEmptyDir(goldenDragonDir + ".zip");
            }
            log.info(yesterday + "日=============》数据抓取完毕");
        } catch (Exception e) {
            log.error("上传csv失败",e);
        }


    }

    /**多线程来压缩速度**/
    private void buildCsv(List<String> carVinList, String goldenDragonDir, LocalDate yesterday) {
        // 线程池处理
        MglThreadPoolExecutor poolExecutor = null;
        try {
            poolExecutor = new MglThreadPoolExecutor(16,32,60,"金龙数据生成csv");
            Set<Callable<Map>> callableSet = new HashSet<>();
            for (int i = 0; i < carVinList.size(); i++) {
                int finalI = i;
                callableSet.add(() -> {
                    Map map = new HashMap();
                    map.put("group-" + finalI,generateGoldenDragonCsv(carVinList.get(finalI), goldenDragonDir,yesterday));
                    return map;
                });
            }
            poolExecutor.invokeAll(callableSet);
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
     * @param carVin 金龙数据
     * @param goldenDragonDir 文件夹路径
     * @param yesterday 时间
     * @return csv
     */
    private Object generateGoldenDragonCsv(String carVin, String goldenDragonDir,
                                           LocalDate yesterday) {

            // 根据汽车Vin查询数据
            List<GoldenDragon> goldenDragonList = goldenDragonService.queryDataByCarVin(carVin,yesterday);
            // 为了不影响整体流程，对空集合处理一下
            if (CollectionUtils.isEmpty(goldenDragonList)) {
                log.warn("汽车数据为空，vin:{},时间：{}",carVin,yesterday);
                GoldenDragon goldenDragon = new GoldenDragon();
                goldenDragon.setVin(carVin);
                goldenDragonList.add(goldenDragon);
            }
            // 生成csv
            List<Map<String, Object>> maps = new ArrayList<>();
            goldenDragonList.forEach(entity -> {
                maps.add(BeanAndMap.beanToMap(entity));
            });
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(goldenDragonDir + "/" + goldenDragonList.get(0).getVin() + Gloables.CSV_EXTENT);
            CsvExportUtil.doExport(maps, Gloables.GOLDEN_TITLE, Gloables.GOLDEN_KEYS, os);
            log.info("csv生成成功，vin:{}",carVin);
            GOLDEN_DRAGON_CSV_LIST.remove(carVin);
            log.info("还剩：{}辆汽车没生成csv",GOLDEN_DRAGON_CSV_LIST.size());
        } catch (Exception e) {
            log.error("csv生成出错！,vin:{}",carVin,e);
        }
        return "csv generation successful";
    }

    /**
     * 组装数据
     * @param cars 车辆数据
     * @return
     */
    private synchronized Object buildData(List<CarGoldenDragonNumberDict> cars) {
        GoldenDragonDto dto = null;
        try {
            String token = restTemplate.getForObject(Gloables.GOLD_TOKEN_URL,String.class);
            log.info("[token]:{}",token);
            List<String> carVin = new ArrayList<>();
            cars.forEach(car -> {
                carVin.add(car.getCarVin());
            });
            String carNum = StringUtils.join(carVin,",");
            String requestUrl = Gloables.GOLD_DATA_BASE_URL + carNum + Gloables.GOLD_PARAMS_TYPES + token;
            JSONObject resultData = restTemplate.getForObject(requestUrl,JSONObject.class);
            dto = JSONObject.toJavaObject(resultData, GoldenDragonDto.class);
            log.info("[resultData]:从对方服务器获取数据：{}条",dto.getData().size());
        } catch (RestClientException e) {
            log.error("请求金龙实时数据出错！",e);
        }
        return assembleData(dto.getData());
    }

    /**
     * 组装数据
     * @param data 金龙数据
     */
    private  List<GoldenDragon> assembleData(List<Map<String, String>> data) {
        List<GoldenDragon> goldenDragons = new ArrayList<>();
        data.forEach(entity -> {
            String time = "";
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
            if (StringUtils.isNotBlank(time)) {
                goldenDragon.setDataCurrentTime(time);
                goldenDragon.setCeateTime(LocalDateTime.now());
                String vehicleId = GLOBAL_MAP.get(entity.get("DeviceNo"));
                if (StringUtils.isBlank(vehicleId)) {
                    GLOBAL_MAP.put(entity.get("DeviceNo"),time);
                    goldenDragonService.save(goldenDragon);
                    goldenDragons.add(goldenDragon);
                }else if (!vehicleId.equals(time)) {
                    GLOBAL_MAP.put(entity.get("DeviceNo"),time);
                    goldenDragonService.save(goldenDragon);
                    goldenDragons.add(goldenDragon);
                }

            }

        });
        log.info("厦门金龙数据入库：{}条", goldenDragons.size());
        return goldenDragons;
    }
}
