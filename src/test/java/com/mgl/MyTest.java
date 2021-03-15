package com.mgl;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.lang.Console;
import com.mgl.common.Gloables;
import com.mgl.sdk.utils.GroupUtil;
import com.mgl.utils.compress.CompressUtils;
import com.mgl.utils.file.FileUtil;
import com.mgl.utils.ftp.ftpClientUtil.FtpTool;
import com.mgl.utils.httpclient.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Description:
 * @Author: zhangqi
 * @CreateTime: 2020/6/2311:39
 * @Company: MGL
 */
@Slf4j
public class MyTest {
    public static void main(String[] args) throws Exception {
//        CountDownLatch countDownLatch = new CountDownLatch(2);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < 100000; i++) {
//                    System.out.println(Thread.currentThread().getName() +"==========>" +i);
//                }
//                countDownLatch.countDown();
//            }
//        }).start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 100000; i < 200000; i++) {
//                    System.out.println(Thread.currentThread().getName() +"==========>" +i);
//                }
//                countDownLatch.countDown();
//            }
//        }).start();
//        countDownLatch.await();
//        System.out.println("结束了");

 /*       Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100000; i++) {
                    System.out.println(Thread.currentThread().getName() + "==========>" + i);
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 100000; i < 200000; i++) {
                    System.out.println(Thread.currentThread().getName() + "==========>" + i);
                }
            }
        });
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println("结束了");
    }*/

        List<Integer> intList = new ArrayList<>();
        for (int i = 1; i < 1000; i++) {
            intList.add(i);
        }
        List<List<Integer>> lists = GroupUtil.groupListByQuantity(intList, 99);
        for (List<Integer> list : lists) {
            System.out.println("----------------------------------------------------");
            for (Integer integer : list) {
                System.out.println(integer);
            }
        }
        System.out.println(lists.size());
    }
}
