//package org.bsm.utils;
//
//import com.googlecode.sardine.Sardine;
//import com.googlecode.sardine.SardineFactory;
//import org.apache.commons.io.FileUtils;
//
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLConnection;
//
///**
// * @author GZC
// * @create 2022-02-10 13:53
// * @desc 下载文件工具类
// */
//public class DownloadFileUtil {
//
//    /**
//     * FileUtils下载网络文件
//     *
//     * @param serverUrl       ：网络文件地址
//     * @param savePath：本地保存路径
//     * @param fileSavePath    ：压缩文件保存路径
//     * @return
//     */
//    public static boolean downloadFile(String serverUrl, String savePath, String fileSavePath) throws Exception {
//        boolean result = false;
//        File f = new File(savePath);
//        if (!f.exists()) {
//            if (!f.mkdirs()) {
//                throw new Exception("makdirs: '" + savePath + "'fail");
//            }
//        }
//        URL url = new URL(serverUrl);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setConnectTimeout(3 * 1000);
//        //防止屏蔽程序抓取而放回403错误
//        conn.setRequestProperty("User-Agent", "Mozilla/4.0(compatible;MSIE 5.0;Windows NT;DigExt)");
//        long totalSize = Long.parseLong(conn.getHeaderField("Content-Length"));
//        if (totalSize > 0) {
//            FileUtils.copyURLToFile(url, new File(fileSavePath));
//            result = true;
//        }
//        return result;
//    }
//
//    /**
//     * 字节流下载压缩文件
//     *
//     * @param serverUrl   :网络地址
//     * @param savePath    ：保持路径
//     * @param zipSavePath ：压缩文件保持路径
//     * @return ：下载结果
//     * @throws Exception ：异常
//     */
//    public static boolean downloadZip(String serverUrl, String savePath, String zipSavePath) throws Exception {
//        boolean result = false;
//        File f = new File(savePath);
//        if (!f.exists()) {
//            if (!f.mkdirs()) {
//                throw new Exception("makdirs: '" + savePath + "'fail");
//            }
//        }
//        //Sardine是WebDAV的工具包
//        Sardine sardine = SardineFactory.begin("test", "test");
//        if (sardine.exists(serverUrl)) {
//            URL url = new URL(serverUrl);
//            URLConnection conn = url.openConnection();
//            int length = conn.getContentLength();
//            conn.setConnectTimeout(3 * 1000);
//            // 防止屏蔽程序抓取而返回403错误
//            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//            InputStream is = sardine.getInputStream(serverUrl);
//            BufferedInputStream bis = new BufferedInputStream(is);
//            FileOutputStream fos = new FileOutputStream(zipSavePath);
//            BufferedOutputStream bos = new BufferedOutputStream(fos);
//            int len;
//            byte[] bytes = new byte[length / 5];
//            while ((len = bis.read(bytes)) != -1) {
//                bos.write(bytes, 0, len);
//            }
//            //清除缓存
//            bos.flush();
//            //关闭流
//            fos.close();
//            is.close();
//            bis.close();
//            bos.close();
//            result = true;
//        }
//        return result;
//    }
//
//}
