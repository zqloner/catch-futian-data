package com.mgl.controller.upload;

import com.mgl.utils.ftp.util.MyFTPUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * @Title: UploadsDownloadsController
 * @Description:
 * @Company: 盟固利
 * @author: 张奇
 * @date: ceate in 2020/6/3 11:17
 */
@Controller
@RequestMapping("/myFtp")
public class UploadsDownloadsController {

    @RequestMapping("/upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest req)
            throws IllegalStateException, IOException {
        MyFTPUtils ftp =new MyFTPUtils();
        ftp.uploadFile("static/image", file.getOriginalFilename(), file.getInputStream());
        return "http://static.image.com/ftp/images/"+"static/image/"+file.getOriginalFilename();
    }
}
