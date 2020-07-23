package com.mgl.controller.changeview;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Title: ChangeViewController
 * @Description:
 * @Company: 盟固利
 * @author: 张奇
 * @date: ceate in 2020/6/10 9:28
 */
@Controller
@RequestMapping("/changeView")
public class ChangeViewController {

    @RequestMapping("/ftp")
    public String mvc1(){
        return "ftp";
    }

    @RequestMapping("/toGoldenDragonImport")
    public String mvc2(){
        return "golden-dragon-import";
    }

    @RequestMapping("/carInfo")
    public String carInfo(){
        return "futian-car-Info";
    }

    @RequestMapping("/totalTable")
    public String totalTable(){
        return "futian-tatal-table";
    }
}
