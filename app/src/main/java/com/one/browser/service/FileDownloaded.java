package com.one.browser.service;

import android.annotation.SuppressLint;
import android.content.Context;

import android.os.Environment;
import android.util.Log;

import com.one.browser.config.AppConfig;
import com.one.browser.config.MimeConfig;
import com.one.browser.entity.DownloadMsg;
import com.one.browser.sqlite.Download;
import com.one.browser.sqlite.DownloadDao;
import com.one.browser.sqlite.FileService;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author 18517
 */
public class FileDownloaded {
    /**
     * 设置一个查log时的一个标志
     */
    private static final String TAG = "文件下载类";
    /**
     * 设置响应码为200,代表访问成功
     */
    private static final int RESPONSE = 200;
    /**
     * 获取本地数据库的业务Bean
     */
    private FileService fileService;
    /**
     * 停止下载的标志
     */
    private boolean exited;
    /**
     * 程序的上下文对象
     */
    private Context context;
    /**
     * 已下载的文件长度
     */
    private int downloadedSize = 0;
    /**
     * 开始的文件长度
     */
    private int fileSize = 0;
    /**
     * 线程下载器
     */
    private DownloadThread threads;
    /**
     * 数据保存到本地的文件中
     */
    private String saveFile;
    /**
     * 下载的长度
     */
    private int data;
    /**
     * 每条线程下载的长度
     */
    private int block;
    /**
     * 下载的路径
     */
    private String downloadUrl;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件类型
     */
    private String mime;

    /**
     * 退出下载
     */
    public void exit() {
        //将退出的标志设置为true;
        this.exited = true;
    }

    /**
     * 下载状态返回
     */
    public boolean getExited() {
        Log.i(TAG, "getExited: " + exited);
        return this.exited;
    }

    /**
     * 获取文件的大小
     */
    public int getFileSize() {
        return fileSize;
    }

    /**
     * 累计已下载的大小
     * 使用同步锁来解决并发的访问问题
     */
    protected synchronized void append(int size) {
        //把实时下载的长度加入到总的下载长度中
        downloadedSize += size;
    }

    /**
     * 更新指定线程最后下载的位置
     *
     * @param pos 最后下载的位置
     */
    protected synchronized void update(int pos) {
        Log.i(TAG, "update: 更新下载位置 >>>" + pos);
        //把指定线程id的线程赋予最新的下载长度,以前的值会被覆盖掉
        this.data = pos;
        Log.i(TAG, "update: 更新完毕下载位置 >>>" + this.data);
    }


    /**
     * 构建文件下载器
     *
     * @param downloadUrl 下载路径
     */
    public FileDownloaded(Context context, String downloadUrl) {
        this.context = context;
        this.downloadUrl = downloadUrl;
    }

    /**
     * 初始化
     *
     * @param fileSaveDir 文件的保存目录
     * @param fileName    下载文件名称
     * @param mime        下载文件类型
     * @param fileSize    下载文件大小
     * @return 返回文件名称
     */
    public void init(String fileSaveDir, String fileName, String mime, int fileSize) {
        this.fileName = fileName;
        this.mime = mime;
        this.fileSize = fileSize;
        this.saveFile = fileSaveDir;
        Log.i(TAG, "获取文件名 >>> " + this.fileName);
        Log.i(TAG, "文件类型 >>> " + mime);
        Log.i(TAG, "文件大小 >>> " + fileSize);
        try {
            // 数据库链接服务
            //fileService = new FileService(this.context);
            //根据下载路径实例化URL
            URL url = new URL(this.downloadUrl);
            Log.i(TAG, "FileDownloaded: " + fileSaveDir);
            this.downloadedSize += this.data;
            Log.i(TAG, "init: " + this.downloadedSize);
            this.block = this.fileSize;
        } catch (Exception e) {
            //打印错误
            print(e.toString());
            throw new RuntimeException("无法连接URL");
        }
    }


    /**
     * 开始下载文件
     *
     * @param listener 监听下载数量的变化,如果不需要了解实时下载的数量,可以设置为null
     * @return 已下载文件大小
     */
    public void download(DownloadProgressListener listener) throws Exception {
        Log.i(TAG, "保存位置 >>>" + this.saveFile);
        try {
            URL url = new URL(this.downloadUrl);
            //如果原先未曾下载或者原先的下载线程数与现在的线程数不一致
            this.data = 0;
            //设置已经下载的长度为0
            this.downloadedSize = 0;
            //开启线程进行下载
            int downLength = this.data;
            if (this.downloadedSize < this.fileSize) {
                //初始化特定id的线程
                ConcurrentHashMap<String, Object> concurrentHashMap = new ConcurrentHashMap<>();
                concurrentHashMap.put("content", context);
                concurrentHashMap.put("this", this);
                concurrentHashMap.put("url", url);
                concurrentHashMap.put("saveFile", this.saveFile);
                concurrentHashMap.put("fileName", this.fileName);
                concurrentHashMap.put("block", this.block);
                concurrentHashMap.put("mime", this.mime);
                concurrentHashMap.put("downLength", downLength);
                concurrentHashMap.put("threadId", 1);
                threads = new DownloadThread(concurrentHashMap);
                Log.i(TAG, "线程 >>> " + this.threads);
                threads.setPriority(7);
                //启动线程
                threads.start();
            } else {
                // 表明线程已完成下载任务
                if (this.fileSize == -1) {
                    Log.i(TAG, "download: 文件大小为 -1 ");
                    ConcurrentHashMap<String, Object> concurrentHashMap = new ConcurrentHashMap<>();
                    concurrentHashMap.put("content", context);
                    concurrentHashMap.put("this", this);
                    concurrentHashMap.put("url", url);
                    concurrentHashMap.put("saveFile", this.saveFile);
                    concurrentHashMap.put("fileName", this.fileName);
                    concurrentHashMap.put("block", this.block);
                    concurrentHashMap.put("mime", this.mime);
                    concurrentHashMap.put("downLength", downLength);
                    concurrentHashMap.put("threadId", 1);
                    threads = new DownloadThread(concurrentHashMap);
                    Log.i(TAG, "线程 >>> " + this.threads);
                    threads.setPriority(7);
                    //启动线程
                    threads.start();
                }
            }
            // 设置初始值
            boolean notFinish = true;
            //下载未完成
            while (notFinish) {
                // 线程休眠不要影响主线程运行
                Thread.sleep(900);
                Log.i(TAG, "下载未完成正在下载 >>>>> ");
                // 下载完成
                notFinish = false;
                //判断是否下载完成
                if (!this.threads.isFinish()) {
                    //如果发现线程未完成下载
                    notFinish = true;
                }
                if (listener != null && !this.exited) {
                    DownloadMsg downloadMsg = new DownloadMsg(this.downloadedSize, this.fileSize, this.saveFile, fileName, false);
                    listener.onDownload(downloadMsg);
                }
            }
            //通知目前已经下载完成的数据长度
            if (downloadedSize == this.fileSize) {
                Log.i(TAG, "download: 删除下载成功的");
            }
        } catch (Exception e) {
            print(e.toString());
            throw new Exception("文件下载异常");
        }
        Log.i(TAG, "正在下载文件已下载       >>> " + this.downloadedSize);
        Log.i(TAG, "正在下载文件总大小       >>> " + this.fileSize);
        Log.i(TAG, "正在下文件保存目录       >>> " + this.saveFile);
        Log.i(TAG, "正在下载的文件名称       >>> " + this.fileName);
        Log.i(TAG, "正在下载的文件状态       >>> " + this.exited);
        // 进行判断 如果总文件大小获取不到为-1时
        if (this.fileSize == -1) {
            this.fileSize = this.downloadedSize;
            // 下载完成将最终返回的下载路径返回
            DownloadMsg downloadMsg = new DownloadMsg(this.downloadedSize, this.fileSize, this.saveFile, this.threads.getFileName(), this.exited);
            listener.onDownload(downloadMsg);
        } else {
            // 下载完成将最终返回的下载路径返回
            DownloadMsg downloadMsg = new DownloadMsg(this.downloadedSize, this.fileSize, this.saveFile, this.threads.getFileName(), this.exited);
            listener.onDownload(downloadMsg);
        }

        //获取文件类型
        String type = mime(this.threads.getFileName());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sDateFormat.format(new Date());
        DownloadDao downloadDao = new DownloadDao(context);

        Log.i(TAG, "数据库存储的文件名 >>> " + this.threads.getFileName());
        Log.i(TAG, "数据库存储的文件目录 >>> " + AppConfig.PACKAGE);
        Log.i(TAG, "数据库存储的文件类型 >>> " + type);
        Log.i(TAG, "数据库存储的时间 >>> " + time);
        downloadDao.insertDate(new Download(this.threads.getFileName(), AppConfig.PACKAGE, type, time));
    }


    /**
     * 打印信息
     *
     * @param msg 信息字符串
     */
    private static void print(String msg) {
        Log.i(TAG, msg);
    }

    private long date() {
        long timestamp = System.currentTimeMillis();
        Log.i(TAG, "时间戳 >>>" + timestamp);
        return timestamp;
    }

    /**
     * 获取文件类型
     *
     * @param fileName 文件名称
     * @return 返回文件类型
     */
    private String mime(String fileName) {
        String type = "*/*";
        // 截取文件名
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1) {
            String suffix = fileName.substring(dotIndex).toLowerCase();
            Log.i(TAG, "截取完毕数据: " + suffix);
            if (!suffix.equals("")) {
                for (int i = 0; i < MimeConfig.MIME.length; i++) {
                    if (suffix.equals(MimeConfig.MIME[i][0])) {
                        type = MimeConfig.MIME[i][1];
                        return type;
                    }
                }
            }
        }
        return type;
    }
}