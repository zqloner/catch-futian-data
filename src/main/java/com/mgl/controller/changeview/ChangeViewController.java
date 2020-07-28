package com.mgl.controller.changeview;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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


    /**
     * 页面跳转
     * @return
     */
    @GetMapping("/route")
    public String pageChange(HttpServletRequest request, ModelMap map){
        String path = request.getQueryString();
        String[] paths = path.split("&");
        String name = "";
        if(paths !=null && paths.length>0){
            if(paths[0].split("=")!=null && paths[0].split("=").length>=2){
                name = paths[0].split("=")[1];for(int i=1;i<paths.length;i++){
                    if(paths[i].split("=").length == 2) {
                        map.put(paths[i].split("=")[0], paths[i].split("=")[1]);
                    }
                }
            }
        }
        return "pages/"+name;
    }

    @RequestMapping("/ftp")
    public String mvc1(){
        return "ftp";
    }
}
