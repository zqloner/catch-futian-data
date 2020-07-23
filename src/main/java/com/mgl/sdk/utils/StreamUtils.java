package com.mgl.sdk.utils;

import java.io.*;

/**
 * 流处理工具类
 */
public class StreamUtils {

    private StreamUtils() {
    }

    /**
     * Read an input stream into a string
     *
     * @param in
     * @return
     * @throws IOException
     */
     public static String streamToString(InputStream in) throws IOException {
        StringBuilder out = new StringBuilder();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }

    /**
     * @方法功能 InputStream 转为 byte
     * @param is
     * @return 字节数组
     * @throws Exception
     */
    public static byte[] stream2Byte(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len = 0;
        byte[] b = new byte[1024];
        while ((len = is.read(b, 0, b.length)) != -1) {
            baos.write(b, 0, len);
        }
        return baos.toByteArray();
    }

    /**
     * @方法功能 InputStream 转为 byte
     * @param inStream
     * @return 字节数组
     * @throws Exception
     */
    public static byte[] inputStream2Byte(InputStream inStream) throws IOException {
        int count = 0;
        while (count == 0) {
            count = inStream.available();
        }
        byte[] b = new byte[count];
        int rc = inStream.read(b);

        if (count != rc) {
            return new byte[0];
        }
        return b;
    }

    /**
     * @方法功能 byte 转为 InputStream
     * @param b 字节数组
     * @return InputStream
     */
    public static InputStream byte2InputStream(byte[] b) {
        return new ByteArrayInputStream(b);
    }

    /**
     * 将异常栈信息转换成String
     * @param e
     * @return
     */
    public static String throwableToString(Throwable e) {
        try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw);) {
            e.printStackTrace(pw);
            return sw.toString();
        } catch (IOException e1) {
            return "throwableToString has error:" + e1.getMessage();
        }
    }

    /**
     * 我们约定，用{}把中文描述括起来，这样可以只截取中文描述
     * 如果没有{}，则取第一行异常信息
     * @param e
     * @return
     */
    public static String throwableFirstLine(Throwable e) {
        String msg = throwableToString(e);
        int cnLeft = msg.indexOf('{') + 1;
        int cnRight = msg.indexOf('}');
        if (cnLeft >= 1 && cnRight > 1) {
            return msg.substring(cnLeft, cnRight);
        }
        int idx = msg.indexOf('\n');
        return msg.substring(0, idx > 0 ? idx : Math.min(64, msg.length()));
    }
}
