package com.mgl;



import com.mgl.bean.carshop.CarNumberDict;
import com.mgl.utils.csv.CsvExportUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Title: HttpClientTest
 * @Description:
 * @Company: 盟固利
 * @author: 张奇
 * @date: ceate in 2020/6/5 18:37
 */
@Slf4j
public class HttpClientTest {
    public static void main(String[] args) throws Exception {
//        LocalDate today = LocalDate.now();
//        LocalDate yesterday = today.plusDays(-1);
//        String url = "http://api.itink.com.cn/api/vehicle/getCanBusByCarId.json";
//
//        Map<String, Object> params = new HashMap();
//        params.put("token", "2b37d26a9d4446d48a0a87a0f6852355");
////        params.put("queryDate", yesterday);
//        params.put("queryDate", "2020-06-22");
//        params.put("carId", "LVCB3L4D2GM001991");
//        String content = MyHttpClientUtils.doGetParam(url, params);
//        System.out.println(content);



        // 查询需要导出的数据
        List<CarNumberDict> carNumberDicts = new ArrayList<>();
        CarNumberDict dict1 = new CarNumberDict();
        CarNumberDict dict2 = new CarNumberDict();
        CarNumberDict dict3 = new CarNumberDict();
        dict1.setCarVin("4234fdsrwer");
        dict2.setCarVin("fadsfdasga");
        dict3.setCarVin("pepjofdlr");
        dict1.setOrderNumber("2356465");
        dict2.setOrderNumber("56465321");
        dict3.setOrderNumber("6846465");
        dict1.setCity("北京");
        dict2.setCity("四川");
        dict3.setCity("山东");

        carNumberDicts.add(dict1);
        carNumberDicts.add(dict2);
        carNumberDicts.add(dict3);


        // 构造导出数据结构
        String titles = "vin,订单号,城市";  // 设置表头
        String keys = "vin,order,city";  // 设置每列字段

        // 构造导出数据
        List<Map<String, Object>> datas = new ArrayList<>();
        Map<String, Object> map = null;
        for (CarNumberDict dict : carNumberDicts) {
            map = new HashMap<>();
            map.put("vin", dict.getCarVin());
            map.put("order", dict.getOrderNumber());
            map.put("city", dict.getCity());
            datas.add(map);
        }

        // 设置导出文件前缀
        String fName = "data_";

        // 文件导出
        try {
//            OutputStream os = response.getOutputStream();
            FileOutputStream os = new FileOutputStream("D:\\aaa\\a.csv");
//            CsvExportUtil.responseSetProperties(fName, os);
            CsvExportUtil.doExport(datas, titles, keys, os);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
