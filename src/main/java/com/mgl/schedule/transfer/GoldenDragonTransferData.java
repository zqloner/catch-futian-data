package com.mgl.schedule.transfer;

import com.mgl.sdk.http.MglRestTemplate;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description TODO
 * @Author fengwei
 * @Date 2020/7/23 11:24
 * @Version 1.0
 */
@Component
@Log4j2
public class GoldenDragonTransferData {

    @Autowired
    private MglRestTemplate restTemplate;

    @Value("${brightease.ftpZipPath}")
    private String ftpZipPath;

    @Value("${brightease.goldenDragonCsvPath}")
    private String goldenDragonCsvPath;
}
