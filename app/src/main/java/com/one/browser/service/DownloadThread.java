package com.one.browser.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 18517
 */
public class DownloadThread extends Thread {
    /**
     * 定义TAG,在打印log时进行标记
     */
    private static final String TAG = "下载线程类";

    public ConcurrentHashMap<String, Object> data;

    private boolean finish;

    private int renownLength;

    private String FileName;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void run() {
        /**
         * Content
         * */
        Context context = (Context) data.get("content");
        /**
         * 文件下载器
         */
        FileDownloaded downloader = (FileDownloaded) data.get("this");
        /**
         * 下载的URL
         */
        URL downUrl = (URL) data.get("url");
        Log.i(TAG, "下载的URL: " + downUrl);
        /**
         * 下载的数据保存到的文件
         */
        String saveFile = (String) data.get("saveFile");
        Log.i(TAG, "下载的数据保存到的文件: " + saveFile);
        /**
         * 文件名称
         * */
        String fileName = (String) data.get("fileName");
        Log.i(TAG, "文件名称: " + fileName);
        /**
         * 总下载的大小
         */
        int block = (int) data.get("block");
        Log.i(TAG, "总下载的大小: " + block);
        /**
         * 该线程已下载的数据长度
         */
        int downLength = (int) data.get("downLength");
        Log.i(TAG, "该线程已下载的数据长度: " + downLength);
        /**
         * 初始化线程id设置
         */
        int threadId = (int) data.get("threadId");
        Log.i(TAG, "初始化线程id设置: " + threadId);
        /**
         * 文件类型
         * */
        String mime = (String) data.get("mime");
        Log.i(TAG, "文件类型: " + mime);


        //未下载完成
        if (downLength < block || block == -1) {
            Log.i(TAG, "已下载文件大小 >>> " + downLength);
            Log.i(TAG, "文件总大小 >>> " + block);
            try {
                HttpURLConnection http = (HttpURLConnection) downUrl.openConnection();
                http.setConnectTimeout(5 * 1000);
                http.setRequestMethod("GET");
                http.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
                http.setRequestProperty("Accept-Language", "zh-CN");
                http.setRequestProperty("Referer", downUrl.toString());
                http.setRequestProperty("Charset", "UTF-8");
                //开始位置
                int startPos = block * (threadId - 1) + downLength;
                Log.i(TAG, "开始位置 >>> " + startPos);
                //结束位置
                int endPos = block * threadId - 1;
                Log.i(TAG, "结束位置 >>> " + endPos);
                //设置获取实体数据的范围
                http.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
                http.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
                http.setRequestProperty("Connection", "Keep-Alive");
                //获得远程连接的输入流
                InputStream inStream = new BufferedInputStream(http.getInputStream(), 8192);
                //设置本地数据的缓存大小为100MB
                byte[] buffer = new byte[1024 * 100];
                //每次读取的数据量
                int offset = 0;
                //打印该线程开始下载的位置
                print("Thread " + threadId + " start download from position " + startPos);
                // 创建一个新的文件记录
                ContentValues contentValues = new ContentValues();
                // 文件名称
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                // 文件类型
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mime);
                // 保存目录
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, saveFile);
                ContentResolver contentResolver = context.getContentResolver();
                Uri downloadsUri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                // 输出流
                OutputStream outputStream = contentResolver.openOutputStream(downloadsUri);
                Log.i(TAG, "返回真假 >>>: " + downloader.getExited());
                // 用户没有要求停止下载,同时没有达到请求数据的末尾时会一直循环读取数据
                while (!downloader.getExited() && (offset = inStream.read(buffer)) > 0) {
                    Log.i(TAG, "保存的位置  >>>" + saveFile);
                    outputStream.write(buffer, 0, offset);
                    //把新线程已经写到文件中的数据加入到下载长度中
                    downLength += offset;
                    //把该线程已经下载的数据长度更新到数据库和内存哈希表中
                    downloader.update(downLength);
                    //把新下载的数据长度加入到已经下载的数据总长度中
                    downloader.append(offset);
                    renownLength = downLength;
                    Log.i(TAG, "结束下载 >>> ");
                }
                // 刷新缓冲区
                outputStream.flush();
                Log.i(TAG, "刷新缓冲区");
                // 关闭输出流
                outputStream.close();
                Log.i(TAG, "关闭输出流");
                // 关闭输入流
                inStream.close();
                Log.i(TAG, "关闭输入流");
                Log.i(TAG, "下载完成");
                Log.i(TAG, "更新下载完成数据");
                // 设置完成标记为true,无论下载完成还是用户主动中断下载
                this.finish = true;
                // 文件创建成功，返回文件名
                String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
                Cursor cursor = contentResolver.query(downloadsUri, projection, null, null, null);
                int columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                cursor.moveToFirst();
                String FileName = cursor.getString(columnIndex);
                cursor.close();
                Log.i(TAG, "返回文件名称 >>>" + FileName);
                print("Thread " + threadId + " download finish");
                this.FileName = FileName;
            } catch (Exception e) {
                //设置该线程已经下载的长度为-1
                downLength = -1;
                print("Thread " + threadId + ":" + e);
            }
        }
    }

    /**
     * 文件路径
     *
     * @return 返回文件保存的路径
     */
    public String getFileName() {
        return this.FileName;
    }

    /**
     * 下载线程
     *
     * @param data 数据
     */
    public DownloadThread(ConcurrentHashMap<String, Object> data) {
        this.data = data;
    }

    /**
     * 打印输出
     *
     * @param msg 消息
     */
    private static void print(String msg) {
        Log.i(TAG, msg);
    }

    /**
     * 下载是否完成
     *
     * @return 返回真假
     */
    public boolean isFinish() {
        return finish;
    }

    /**
     * 已经下载的内容大小
     *
     * @return 如果返回值为-1,代表下载失败
     */
    public long getDownLength() {
        return renownLength;
    }
}
