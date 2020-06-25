package com.mgl.utils.ftp.bju;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: zhangqi
 * @CreateTime: 2020/6/2417:46
 * @Company: MGL
 *        <!--ftp-->  bjunion
 *         <dependency>
 *             <groupId>com.jcraft</groupId>
 *             <artifactId>jsch</artifactId>
 *             <version>0.1.55</version>
 *         </dependency>
 *
 */
public class FileUploadUtil {

    public static String upload(InputStream inputStream, String towPath, String suffix) {
        String src = "";
        try{
            ChannelSftp chSftp = SFTPChannel.getChannel(60000);
            Date date = new Date();
            long timeMillis = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String dir = (SFTPChannel.serverpath.equals("") ? "" : SFTPChannel.serverpath + "/") + towPath + "/" + sdf.format(date);
            boolean result = mkDir(chSftp, dir);
            // 目标文件名
            String dst = "/opt/nginx-root/" + dir + "/" + timeMillis + "." + suffix;
            System.out.println("dst==="+dst);
            // 图片访问src
            src = "http://" + SFTPChannel.reqUrl + "/" + dir + "/" + timeMillis + "." + suffix;
            System.out.println("src==="+src);
            if(result){
                chSftp.put(inputStream, dst, ChannelSftp.OVERWRITE);
                //chSftp.quit();
                //SFTPChannel.closeChannel();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return src;
    }
    private static boolean mkDir(ChannelSftp chSftp, String dirPath){
        try{
            String[] paths = dirPath.split("/");
            String filePath = "/opt/nginx-root";
            chSftp.cd("/opt/nginx-root");
            List<LsEntry> list = chSftp.ls(filePath);
            for(int i = 0; i < paths.length; i++){
                list = chSftp.ls(filePath);
                String path = paths[i];
                if(!isExist(list, path)){
                    chSftp.mkdir(path);
                }
                chSftp.cd(path);
                filePath += "/" + path;
            }
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    private static boolean isExist(List<LsEntry> list, String name){
        if(list.size() == 0){
            return false;
        }else{
            for(LsEntry ls : list){
                if(ls.getFilename().equals(name)){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 删除文件
     *
     * @param directory
     *            要删除文件所在目录
     * @param deleteFile
     *            要删除的文件
     *
     * @throws Exception
     */
    public void delete(String directory, String deleteFile) throws Exception {
        ChannelSftp chSftp = null;
        try {
            chSftp = SFTPChannel.getChannel(60000);
            chSftp.cd(directory);
            chSftp.rm(deleteFile);
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                chSftp.quit();
                SFTPChannel.closeChannel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
