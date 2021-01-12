//package com.mgl;
//
//import cn.hutool.core.codec.Base64Encoder;
//import cn.hutool.core.lang.Console;
//import com.mgl.common.Gloables;
//import com.mgl.utils.compress.CompressUtils;
//import com.mgl.utils.file.FileUtil;
//import com.mgl.utils.ftp.ftpClientUtil.FtpTool;
//import com.mgl.utils.httpclient.HttpClientUtil;
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.File;
//import java.security.MessageDigest;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
///**
// * @Description:
// * @Author: zhangqi
// * @CreateTime: 2020/6/2311:39
// * @Company: MGL
// */
//@Slf4j
//public class MyTest {
//    public static void main(String[] args) throws Exception{
////        List<File> files = FileUtil.listFile(new File(dir), extention, true);
//
//
//        // This represents the BASE64 encoded timestamp sent by the POP server
//        String dataString = Base64Decoder.decode("PDAwMDAuMDAwMDAwMDAwMEBteDEuc2VydmVyLmNvbT4=");
//        byte[] data = dataString.getBytes();
//
//        // The password to access the account
//        byte[] key  = new String("password").getBytes();
//
//        // The address of the e-mail account
//        String user = "client@server.com";
//
//        MessageDigest md5 = MessageDigest.getInstance("MD5");
//        md5.reset();
//
//        if (key.length > 64)
//            key = md5.digest(key);
//
//        byte[] k_ipad = new byte[64];
//        byte[] k_opad = new byte[64];
//
//        System.arraycopy(key, 0, k_ipad, 0, key.length);
//        System.arraycopy(key, 0, k_opad, 0, key.length);
//
//        for (int i=0; i<64; i++)
//        {
//            k_ipad[i] ^= 0x36;
//            k_opad[i] ^= 0x5c;
//        }
//
//        byte[] i_temp = new byte[k_ipad.length + data.length];
//
//        System.arraycopy(k_ipad, 0, i_temp, 0, k_ipad.length);
//        System.arraycopy(data, 0, i_temp, k_ipad.length, data.length);
//
//        i_temp = md5.digest(i_temp);
//
//        byte[] o_temp = new byte[k_opad.length + i_temp.length];
//
//        System.arraycopy(k_opad, 0, o_temp, 0, k_opad.length);
//        System.arraycopy(i_temp, 0, o_temp, k_opad.length, i_temp.length);
//
//        byte[] result = md5.digest(o_temp);
//        StringBuffer hexString = new StringBuffer();
//
//        for (int i=0;i < result.length; i++) {
//            hexString.append(Integer.toHexString((result[i] >>> 4) & 0x0F));
//            hexString.append(Integer.toHexString(0x0F & result[i]));
//        }
//
//
//        System.out.println(Base64Encoder.encode(user + " " + hexString.toString()));
//    }
//}
