package com.mgl;

import com.mgl.utils.MyHttpClientUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * @Title: HttpClientTest
 * @Description:
 * @Company: 盟固利
 * @author: 张奇
 * @date: ceate in 2020/6/5 18:37
 */
public class HttpClientTest {
    public static void main(String[] args) throws InterruptedException {
//        TimeInterval timer = DateUtil.timer();
//        Long start = System.currentTimeMillis();
//        System.out.println(DateUtil.now());
//        System.out.println(DateUtil.date());
//        System.out.println(DateUtil.yesterday());
//        System.out.println(DateUtil.tomorrow());
//        LocalDate now = LocalDate.now();
//        Long end = System.currentTimeMillis();
//        System.out.println(timer.intervalRestart()/1000+"================>");
//        System.out.println("end -start:====>" +(end - start));
//        Thread.sleep(3000);
//        System.out.println(now);
//        System.out.println(now.plusDays(-1));
//        Long end2 = System.currentTimeMillis();
//        System.out.println("end2 - end: ====>"+(end2-end));
//        System.out.println(timer.intervalSecond()+"==================>");


        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.plusDays(-1);
        String url = "http://api.itink.com.cn/api/vehicle/getCanBusByCarId.json";

        Map<String, Object> params = new HashMap();
        params.put("token", "2b37d26a9d4446d48a0a87a0f6852355");
//        params.put("queryDate", yesterday);
        params.put("queryDate", "2020-06-05");
        params.put("carId", "LVCB3L4D6JM002360");

        String content = MyHttpClientUtils.doGetParam(url, params);

    }
}
