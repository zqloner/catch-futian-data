package com.mgl.utils.ftp.ftpClientUtil;


import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.SocketException;

public class FtpTool {

    private final static Logger logger = LoggerFactory.getLogger(FtpTool.class);

    private String hostname;
    private Integer port;
    private String username;
    private String password;

    private FTPClient ftpClient = null;

    public FtpTool(String hostname, Integer port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public FtpTool() {
    }

    /**
     * 初始化ftp服务器
     */
    public boolean initFtpClient() {
        ftpClient = new FTPClient();
        ftpClient.setControlEncoding("GBK");
        try {
            logger.info("connecting...ftp服务器:" + this.hostname + ":" + this.port);
            ftpClient.connect(hostname, port);
            ftpClient.login(username, password);
            // 是否成功登录服务器
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                logger.info("连接ftp服务器【失败】:" + this.hostname + ":" + this.port);
            }
            logger.info("连接ftp服务器【成功】:" + this.hostname + ":" + this.port);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 上传文件
     *
     * @param pathname       ftp服务保存地址
     * @param fileName       上传到ftp的文件名
     * @param originfilename 待上传文件的名称（绝对地址） *
     * @return
     */
    public boolean uploadFile(String pathname, String fileName, String originfilename) {
        InputStream inputStream = null;
        try {
            logger.info("开始上传文件");
            inputStream = new FileInputStream(new File(originfilename));
            initFtpClient();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            CreateDirecroty(pathname);
            ftpClient.makeDirectory(pathname);
            ftpClient.changeWorkingDirectory(pathname);
            // 每次数据连接之前，ftp client告诉ftp server开通一个端口来传输数据
            ftpClient.enterLocalPassiveMode();
            // 观察是否真的上传成功
            boolean storeFlag = ftpClient.storeFile(fileName, inputStream);
            System.err.println("storeFlag==" + storeFlag);
            inputStream.close();
            ftpClient.logout();
            logger.info("上传文件【成功】");
        } catch (Exception e) {
            logger.info("上传文件【失败】");
            return false;
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * 上传文件
     *
     * @param pathname    ftp服务保存地址
     * @param fileName    上传到ftp的文件名
     * @param inputStream 输入文件流
     * @return
     */
    public boolean uploadFile(String pathname, String fileName, InputStream inputStream) {
        try {
            logger.info("开始上传文件");
            initFtpClient();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//            ftpClient.setControlEncoding("GBK");
            ftpClient.setControlEncoding("GBK");
            //告知服务器打开客户端将连接到的数据端口以进行数据传输
            ftpClient.enterLocalPassiveMode();
            CreateDirecroty(pathname);
            ftpClient.makeDirectory(pathname);
            ftpClient.changeWorkingDirectory(pathname);


            // 分批多少字节上传
//            OutputStream output;
//            output = ftpClient.storeFileStream(fileName);
//            Util.copyStream(inputStream, output);
//            output.close();
//            if(!ftpClient.completePendingCommand()) {
//                ftpClient.logout();
//                ftpClient.disconnect();
//                System.err.println("File transfer failed.");
//                System.exit(1);
//            }
            ftpClient.storeFile(fileName, inputStream);
            inputStream.close();
            logger.info("上传文件【成功】");
        } catch (Exception e) {
            logger.info("上传文件【失败】");
            e.printStackTrace();
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public void closeLogin() {
        try {
            ftpClient.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @Description: 改变边目录路径
     **/
    public boolean changeWorkingDirectory(String directory) {
        boolean flag = true;
        try {
            flag = ftpClient.changeWorkingDirectory(directory);
            if (flag) {
                logger.info("进入文件夹" + directory + " 成功！");

            } else {
                logger.info("进入文件夹" + directory + " 失败！开始创建文件夹");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return flag;
    }

    /**
     * @Description: 创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建
     **/
    public boolean CreateDirecroty(String remote) throws IOException {
        boolean success = true;
        String directory = remote + "/";
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(new String(directory))) {
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            String path = "";
            String paths = "";
            while (true) {
                String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "GBK");
                path = path + "/" + subDirectory;
                if (!existFile(path)) {
                    if (makeDirectory(subDirectory)) {
                        changeWorkingDirectory(subDirectory);
                    } else {
                        System.out.println("创建目录[" + subDirectory + "]失败");
                        changeWorkingDirectory(subDirectory);
                    }
                } else {
                    changeWorkingDirectory(subDirectory);
                }

                paths = paths + "/" + subDirectory;
                start = end + 1;
                end = directory.indexOf("/", start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
        return success;
    }

    /**
     * @Description: 判断ftp服务器文件是否存在
     **/
    public boolean existFile(String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr.length > 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * @Description: 创建目录
     **/
    public boolean makeDirectory(String dir) {
        boolean flag = true;
        try {
            flag = ftpClient.makeDirectory(dir);
            if (flag) {
                logger.info("创建文件夹" + dir + " 成功！");

            } else {
                logger.info("创建文件夹" + dir + " 失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * * 下载文件 *
     *
     * @param pathname  FTP服务器文件目录 *
     * @param filename  文件名称 *
     * @param localpath 下载后的文件路径 *
     * @return
     */
    public boolean downloadFile(String pathname, String filename, String localpath) {
        boolean flag = false;
        OutputStream os = null;
        try {
            logger.info("开始下载文件");
            initFtpClient();
            // 切换FTP目录
            boolean changeFlag = ftpClient.changeWorkingDirectory(pathname);
            System.err.println("changeFlag==" + changeFlag);

            ftpClient.enterLocalPassiveMode();
            ftpClient.setRemoteVerificationEnabled(false);
            // 查看有哪些文件夹 以确定切换的ftp路径正确
            String[] a = ftpClient.listNames();
            System.err.println(a[0]);

            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile file : ftpFiles) {
                if (filename.equalsIgnoreCase(file.getName())) {
                    File localFile = new File(localpath + "/" + file.getName());
                    os = new FileOutputStream(localFile);
                    ftpClient.retrieveFile(file.getName(), os);
                    os.close();
                }
            }
            ftpClient.logout();
            flag = true;
            logger.info("下载文件成功");
        } catch (Exception e) {
            logger.info("下载文件失败");
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    /**
     * * 删除文件 *
     *
     * @param pathname FTP服务器保存目录 *
     * @param filename 要删除的文件名称 *
     * @return
     */
    public boolean deleteFile(String pathname, String filename) {
        boolean flag = false;
        try {
            System.out.println("开始删除文件");
            initFtpClient();
            // 切换FTP目录
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.dele(filename);
            ftpClient.logout();
            flag = true;
            System.out.println("删除文件【成功】");
        } catch (Exception e) {
            System.out.println("删除文件【失败】");
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    public static void main(String[] args) throws IOException {
//        FtpUtils ftp = new FtpUtils();
//        ftp.initFtpClient();
        // 文件路径写为用户建立时 指定的目录
//        ftp.uploadFile("/home/uftp", "send.txt", "C:\\Users\\11411\\Desktop\\send.txt");
//        boolean b = ftp.existFile("123.png");
        // ftp.downloadFile("/home/ftpFile", "123.png", "E://");
//        ftp.deleteFile("/home/ftpFile", "123.png");
//        System.out.println("ok");
//        testUpLoadFromDisk("/work/data");

    }


    public static String testUpLoadFromDisk(String file) {
        String ftpHost = "ftp.mgldl.com.cn";
        String ftpUserName = "mgl";
        String ftpPassword = "MglAa110";
        int ftpPort = 21;
        String ftpPath = "/福田";
        String fileName2 = null;
        String fileNameEncoded = null;

        String str = file;
        String[] strs = str.split("\\\\");
        for (int i = 0; i < strs.length; i++) {
            System.out.println(strs);
            if (i == strs.length - 1) {
                fileName2 = strs[strs.length - 1];
            }
        }
        try {
            //设置编码
            file = new String(file.getBytes(), "iso-8859-1");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        String str2 = file;
        String[] strs2 = str2.split("\\\\");
        for (int i = 0; i < strs2.length; i++) {
            System.out.println(strs2);
            if (i == strs2.length - 1) {
                fileNameEncoded = strs2[strs2.length - 1];
            }
        }

        String fileName = fileName2;
        //在FTP服务器上生成一个文件，并将一个字符串写入到该文件中
        try {
            InputStream input = new ByteArrayInputStream("test ftp jyf".getBytes("GBK"));
            boolean flag = uploadFile(ftpHost, ftpUserName, ftpPassword, ftpPort, ftpPath, fileNameEncoded, input);
            System.out.println(flag);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "ftp://" + ftpHost + ":" + ftpPort + "/" + fileName;
    }


    public static boolean uploadFile(String ftpHost, String ftpUserName, String ftpPassword, int ftpPort, String ftpPath,
                                     String fileName, InputStream input) {
        boolean success = false;
        FTPClient ftpClient = null;
        try {
            int reply;
            ftpClient = getFTPClient(ftpHost, ftpUserName, ftpPassword, ftpPort);
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                return success;
            }
            // 中文支持
            ftpClient.setControlEncoding("GBK");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            //告知服务器打开客户端将连接到的数据端口以进行数据传输
            ftpClient.enterLocalPassiveMode();
            //更改FTP会话的当前工作目录。
            ftpClient.changeWorkingDirectory(ftpPath);
            //使用给定名称在服务器上存储文件，并从给定的InputStream获取输入。
            ftpClient.storeFile(fileName, input);

            input.close();
            ftpClient.logout();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }

    public static FTPClient getFTPClient(String ftpHost, String ftpUserName,
                                         String ftpPassword, int ftpPort) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(ftpHost, ftpPort);// 连接FTP服务器
            ftpClient.login(ftpUserName, ftpPassword);// 登陆FTP服务器
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                System.out.println("未连接到FTP，用户名或密码错误。");
                ftpClient.disconnect();
            } else {
                System.out.println("FTP连接成功。");
            }
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("FTP的IP地址可能错误，请正确配置。");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("FTP的端口错误,请正确配置。");
        }
        return ftpClient;
    }
}