package com.mgl.controller.upload;

import com.mgl.utils.ftp.bju.FileUploadUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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

        return  FileUploadUtil.upload(file.getInputStream(), "catch-data", file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1));
    }
}
