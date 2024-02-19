package com.one.browser.utils;

import android.util.Log;

import com.one.browser.config.MimeConfig;
import com.one.browser.entity.WebMessage;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 18517
 */
public class WebMessageUtil {
    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件大小
     */
    private int fileSize;

    /**
     * 设置响应码为200,代表访问成功
     */
    private static final int RESPONSE = 200;

    private static final String DISPOSITION = "Content-disposition";

    private static final Pattern PATTERN = Pattern.compile("=([^&]+)");


    public WebMessage gitFileName(String downloadUrl) {
        Log.i("TAG", "获取到的链接 >>>> " + downloadUrl);
        try {
            URL url = new URL(downloadUrl);
            //创建远程连接句柄,这里并未真正连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //设置连接超时事件为10秒
            connection.setConnectTimeout(10000);
            //设置禁止压缩
            connection.setRequestProperty("Accept-Encoding", "identity");
            //建立连接
            connection.connect();
            // 打印返回的Http的头字段集合
            printResponseHeader(connection);
            //获取文件名称
            if (getFileName(connection) != null) {
                // 1、从请求中获取
                this.fileName = getFileName(connection);
                Log.i("TAG", "gitFileName:  >>> " + fileName);
            } else if (getUrl(downloadUrl) != null) {
                // 2、从链接中获取
                this.fileName = getUrl(downloadUrl);
                Log.i("TAG", "getUrl:  >>> " + fileName);
            } else {
                // 3、如果都没找到的话,默认取一个文件名
                this.fileName = UUID.randomUUID() + ".tmp";
                Log.i("TAG", "gitFileName:  >>> " + fileName);
            }
            Log.i("TAG", "获取文件名 >>> " + this.fileName);
            // 文件类型
            this.fileType = getMime(connection);
            Log.i("TAG", "文件类型 >>> " + getMime(connection));
            //对返回的状态码进行判断,用于检查是否请求成功,返回200时执行下面的代码
            if (connection.getResponseCode() == RESPONSE) {
                //根据响应获得文件大小
                this.fileSize = connection.getContentLength();
                if (this.fileSize <= 0) {
                    //文件长度小于等于0时抛出运行时异常
                    //throw new RuntimeException("不知道文件大小");
                    Log.i("TAG", "无法判断文件大小 >>>> ");
                }
            } else {
                //打印错误信息
                print("服务器响应错误:" + connection.getResponseCode() + connection.getResponseMessage());
                throw new RuntimeException("服务器反馈出错");
            }
        } catch (Exception e) {
            //打印错误
            print(e.toString());
            throw new RuntimeException("无法连接URL");
        }
        return new WebMessage(this.fileName, this.fileType, this.fileSize);
    }

    /**
     * 打印 Http头字段
     *
     * @param http
     */
    public static void printResponseHeader(HttpURLConnection http) {
        // 获取http响应的头字段
        Map<String, String> header = getHttpResponseHeader(http);
        // 使用增强for循环遍历取得头字段的值,此时遍历的循环顺序与输入树勋相同
        for (Map.Entry<String, String> entry : header.entrySet()) {
            // 当有键的时候则获取值,如果没有则为空字符串
            String key = entry.getKey() != null ? entry.getKey() + ":" : "";
            // 打印键和值得组合
            Log.i("TAG", (key + entry.getValue()));
        }
    }

    /**
     * 获取Http响应头字段
     *
     * @param http 参数
     * @return 返回map
     */
    public static Map<String, String> getHttpResponseHeader(HttpURLConnection http) {
        //使用LinkedHashMap保证写入和便利的时候的顺序相同,而且允许空值
        Map<String, String> header = new LinkedHashMap<String, String>();
        //此处使用无线循环,因为不知道头字段的数量
        for (int i = 0; ; i++) {
            //获取第i个头字段的值
            String mine = http.getHeaderField(i);
            if (mine == null) {
                //没值说明头字段已经循环完毕了,使用break跳出循环
                break;
            }
            //获得第i个头字段的键
            header.put(http.getHeaderFieldKey(i), mine);
        }
        return header;
    }


    /**
     * 获取文件名
     */
    private String getFileName(HttpURLConnection conn) throws UnsupportedEncodingException {
        //获取content-disposition返回字段,里面可能包含文件名
        if (conn.getHeaderField(DISPOSITION) != null) {
            //使用正则表达式查询文件名
            String disposition = conn.getHeaderField(DISPOSITION);
            Log.i("TAG", "getFileName: " + disposition);
            Matcher matcher = PATTERN.matcher(disposition);
            if (matcher.find()) {
                String filename = matcher.group(1);
                String decodedFilename = URLDecoder.decode(filename, "UTF-8");
                System.out.println("Content-Disposition 获取文件名 >>> " + decodedFilename);
                // 进一步筛选
                for (int i = 0; i < MimeConfig.MIME.length; i++) {
                    if (decodedFilename.contains(MimeConfig.MIME[i][0])) {
                        // 如果存在则打印出来
                        Log.i("TAG", "获取到的文件类型: " + MimeConfig.MIME[i][0]);
                        // 此时进行截取
                        String fileName = decodedFilename.substring(0, decodedFilename.indexOf(MimeConfig.MIME[i][0])) + MimeConfig.MIME[i][0];
                        Log.i("TAG", "获取文件: " + fileName);
                        return fileName.trim();
                    }
                }
                return decodedFilename.trim();
            }
        }
        return null;
    }

    private String getUrl(String downloadUrl) {
        Log.i("TAG", "通过URL 获取文件下载名 >>> " + downloadUrl);
        String filename = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1);
        Log.i("TAG", "截取之后文件名 >>> : " + filename);
        for (int i = 0; i < MimeConfig.MIME.length; i++) {
            if (filename.contains(MimeConfig.MIME[i][0])) {
                // 如果存在则打印出来
                Log.i("TAG", "获取到的文件类型: " + MimeConfig.MIME[i][0]);
                // 此时进行截取
                String fileName = filename.substring(0, filename.indexOf(MimeConfig.MIME[i][0])) + MimeConfig.MIME[i][0];
                Log.i("TAG", "获取文件: " + fileName);
                return fileName.trim();
            }
        }
        return filename;
    }

    /**
     * 获取文件类型
     */
    private String getMime(HttpURLConnection connection) {
        // 判断文件类型
        if (connection.getHeaderField(DISPOSITION) != null) {
            String type = connection.getHeaderField("Content-Type");
            Log.i("TAG", "getMime: " + type);
            String[] ty = type.split(";");
            return ty[0];
        } else {
            return "application/octet-stream";
        }
    }

    /**
     * 打印信息
     *
     * @param msg 信息字符串
     */
    private static void print(String msg) {
        Log.i("TAG", "日志输出 >>>" + msg);
    }

}
