package com.mgl.utils.compress;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

/**
 * @Description:
 * @Author: zhangqi
 * @CreateTime: 2020/7/79:07
 * @Company: MGL
 */

/**
 * 压缩文件夹工具类
 *
 */
public class CompressUtils {

    /**
     * 压缩文件夹到指定zip文件
     *
     * @param srcDir 源文件夹
     * @param targetFile 目标知道zip文件
     * @throws IOException IO异常，抛出给调用者处理
     */
    public static void zip(String srcDir, String targetFile) throws IOException {

        try (
                OutputStream outputStream = new FileOutputStream(targetFile);
        ) {
            zip(srcDir, outputStream);
        }
    }

    /**
     * 压缩文件夹到指定输出流中，可以是本地文件输出流，也可以是web响应下载流
     *
     * @param srcDir 源文件夹
     * @param outputStream 压缩后文件的输出流
     * @throws IOException IO异常，抛出给调用者处理
     */
    public static void zip(String srcDir, OutputStream outputStream) throws IOException {
        try (
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                ArchiveOutputStream out = new ZipArchiveOutputStream(bufferedOutputStream);
        ) {
            Path start = Paths.get(srcDir);
            Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    ArchiveEntry entry = new ZipArchiveEntry(dir.toFile(), start.relativize(dir).toString());
                    out.putArchiveEntry(entry);
                    out.closeArchiveEntry();
                    return super.preVisitDirectory(dir, attrs);
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try (
                            InputStream input = new FileInputStream(file.toFile())
                    ) {
                        ArchiveEntry entry = new ZipArchiveEntry(file.toFile(), start.relativize(file).toString());
                        out.putArchiveEntry(entry);
                        IOUtils.copy(input, out);
                        out.closeArchiveEntry();
                    }
                    return super.visitFile(file, attrs);
                }

            });

        }
    }

    /**
     * 解压zip文件到指定文件夹
     *
     * @param zipFileName 源zip文件路径
     * @param destDir 解压后输出路径
     * @throws IOException IO异常，抛出给调用者处理
     */
    public static void unzip(String zipFileName, String destDir) throws IOException {
        try (
                InputStream inputStream = new FileInputStream(zipFileName);
        ) {
            unzip(inputStream, destDir);
        }

    }

    /**
     * 从输入流中获取zip文件，并解压到指定文件夹
     *
     * @param inputStream zip文件输入流，可以是本地文件输入流，也可以是web请求上传流
     * @param destDir 解压后输出路径
     * @throws IOException IO异常，抛出给调用者处理
     */
    public static void unzip(InputStream inputStream, String destDir) throws IOException {
        try (
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                ArchiveInputStream in = new ZipArchiveInputStream(bufferedInputStream);
        ) {
            ArchiveEntry entry = null;
            while (Objects.nonNull(entry = in.getNextEntry())) {
                if (in.canReadEntryData(entry)) {
                    File file = Paths.get(destDir, entry.getName()).toFile();
                    if (entry.isDirectory()) {
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                    } else {
                        try (
                                OutputStream out = new FileOutputStream(file);
                        ) {
                            IOUtils.copy(in, out);
                        }
                    }
                } else {
                    System.out.println(entry.getName());
                }
            }
        }

    }

    //删除指定文件
    private static void doDeleteEmptyDir(String dir) {
        boolean success = (new File(dir)).delete();
        if (success) {
            System.out.println("Successfully deleted empty directory: " + dir);
        } else {
            System.out.println("Failed to delete empty directory: " + dir);
        }
    }

    // 删除目录下的所有
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }


    /**
     *
     * 删除指定目录及其包含的所有内容
     *
     *
     * */

    public static void deleteDirectory(File dir)

            throws IOException {

        if (!dir.exists()) {

            throw new IllegalArgumentException("目录：" + dir +

                    "不存在");

        }

        if (!dir.isDirectory()) {

            throw new IllegalArgumentException(dir + "不是目录");

        }

        File[] subs = dir.listFiles();

        if (subs != null && subs.length > 0) {

            for (File sub : subs) {

                if (sub.isDirectory()) {

                    deleteDirectory(sub);

                } else {

                    System.out.println(sub);

                    if (!sub.delete()) {

                        throw new IOException("无法删除文件：" + sub);

                    }

                }

            }

        }

        System.out.println(dir);

        if (!dir.delete()) {

            throw new IOException("无法删除目录：" + dir);

        }

    }
}