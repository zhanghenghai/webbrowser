package com.one.browser.service;


import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.IntegerRes;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;


import com.one.browser.sqlite.Download;
import com.one.browser.sqlite.DownloadDao;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 18517
 */
public class DownloadService extends Service {
    /**
     * 通知管理器
     */
    private NotificationManager notificationManager;
    /**
     * 通知构造器
     */
    private NotificationCompat.Builder builder;
    /**
     * 日志
     */
    private static final String TAG = "DownloadService";
    /**
     * 时间单位
     */
    private final TimeUnit unit = TimeUnit.SECONDS;
    /**
     * 下载任务执行器
     */
    private ExecutorService executorService;
    /**
     * 自定义线程工厂类
     */
    static class CustomThreadFactory implements ThreadFactory {
        private final String threadNamePrefix;
        public CustomThreadFactory(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(threadNamePrefix + "-" + thread.getId());
            return thread;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        ThreadFactory threadFactory = new CustomThreadFactory("DownloadThread");
        long keepAliveTime = 60L;
        int maximumPoolSize = 6;
        int corePoolSize = 3;
        executorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        Log.i(TAG, "onCreate: onCreate 已执行 >>>");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int notificationId = (int) System.currentTimeMillis(); // 生成唯一的通知 id
        if (intent != null) {
            // 获取文件下载地址
            String fileUrl = intent.getStringExtra("fileUrl");
            // 获取文件名称
            String fileName = intent.getStringExtra("fileName");
            // 获取文件大小
            String fileSize = intent.getStringExtra("fileSize");
            // 获取文件类型
            String fileType = intent.getStringExtra("fileType");

            Log.i("TAG", "onStartCommand: >>>>>>> 文件地址 " + fileUrl);
            Log.i("TAG", "onStartCommand: >>>>>>> 文件名称 " + fileName);
            Log.i("TAG", "onStartCommand: >>>>>>> 文件大小 " + fileSize);
            Log.i("TAG", "onStartCommand: >>>>>>> 文件类型 " + fileType);
            // 发送通知
            notifyNow(notificationId);
            // 提交下载任务
            executorService.submit(new DownloadTask(fileName, fileUrl, fileType, fileSize, notificationId));
            Log.i(TAG, "onStartCommand: >>>>>>>> onStartCommand 已执行");
        }
        return START_NOT_STICKY;
    }

    /**
     * 在 Android 8.0 及以上版本中，系统强制要求应用程序必须使用通知渠道（NotificationChannel）来管理和配置通知。如果你的应用程序在 Android 8.0 或更高版本上没有指定通知渠道，
     * 那么系统将会默认使用一个名为 "miscellaneous" 的通知渠道，并显示一条警告消息提示用户。
     * 创建通知渠道
     */
    private void createNotificationChannel() {
        // 创建一个名为 "download" 的通知渠道，用于文件下载通知
        NotificationChannel channel = new NotificationChannel("download", "下载", NotificationManager.IMPORTANCE_DEFAULT);
        // 设置通知渠道的描述信息
        channel.setDescription("文件下载通知");
        // 获取系统的通知服务（NotificationManager）实例
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // 将通知渠道注册到系统的通知管理器中
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * 通知构造器发送通知
     */
    private void notifyNow(int notificationId) {
        builder = new NotificationCompat.Builder(DownloadService.this, "download")
                // 设置通知的标题
                .setContentTitle("软件更新中...")
                // 设置通知的图标
                .setSmallIcon(android.R.drawable.stat_sys_download)
                // 设置通知的优先级
                // 通知的优先级只在 Android 7.1（API 级别 25）及更低版本中起作用。在 Android 8.0（API 级别 26）及更高版本中，
                // 通知的优先级由通知渠道的重要性属性决定。因此，在 Android 8.0 及以上版本中，设置通知渠道的重要性属性（使用 setImportance() 方法）是更有效和推荐的做法。
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // 设置通知在用户点击后自动取消
                .setAutoCancel(false)
                // 设置下载进度条
                .setProgress(100, 0, false)
                // 设置为自动取消
                .setAutoCancel(true);
        // 1、发送通知（一定要按照顺序）
        notificationManager.notify(notificationId, builder.build());
        // 2、将服务设置为前台服务（一定要按照顺序）
        startForeground(notificationId, builder.build());
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class DownloadTask implements Runnable {
        private final String fileName;
        private final String fileUrl;
        private final String fileType;
        private final String fileSize;
        private final int notificationId;

        public DownloadTask(String fileName, String fileUrl, String fileType, String fileSize, int notificationId) {
            this.fileName = fileName;
            this.fileUrl = fileUrl;
            this.fileType = fileType;
            this.fileSize = fileSize;
            this.notificationId = notificationId;
        }

        // 任务开始前执行
        @Override
        public void run() {
            Uri uri;
            Log.i("TAG", "文件路径 >>>>>>>: " + fileUrl);
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int fileLength = connection.getContentLength();
                InputStream input = connection.getInputStream();
                OutputStream output;
                // 判断Android版本
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
                    values.put(MediaStore.Files.FileColumns.MIME_TYPE, fileType);
                    values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                    ContentResolver contentResolver = getContentResolver();
                    uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                    Log.i("TAG", "文件保存位置: >>>> " + uri);
                    if (uri != null) {
                        output = contentResolver.openOutputStream(uri);
                    } else {
                        throw new Exception("uri is null");
                    }
                } else {
                    File externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File outputFile = new File(externalDir, fileName);
                    uri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", outputFile);
                    output = getContentResolver().openOutputStream(uri);
                }
                Log.i(TAG, "run: 文件大小 >>>>" + fileLength);
                byte[] data = new byte[1024];
                int total = 0;
                int count;
                boolean isCancelled = false;
                while (!isCancelled && (count = input.read(data)) != -1) {
                    total += count;
                    Log.i(TAG, fileName + "已下载： " + total);
                    Log.i(TAG, fileName + "读取字节： " + count);
                    Log.i(TAG, fileName + "总大小： " + fileLength);
                    if (fileLength > 0) {
                        int progress = ((int) ((float) total * 100 / (float) fileLength));
                        // 发送下载进度通知
                        Log.i(TAG, "run: >>>>>>> 下载进度 " + progress + "%");
                        builder.setProgress(100, progress, false);
                        notificationManager.notify(notificationId, builder.build());
                    }
                    if (output != null) {
                        output.write(data, 0, count);
                    } else {
                        throw new Exception("output is null");
                    }
                }
                if (output != null) {
                    output.flush();
                    output.close();
                } else {
                    throw new Exception("output is null");
                }
                input.close();
                connection.disconnect();
                Log.i(TAG, "run: >>>>>>> 下载完成");
                builder.setContentTitle("下载完成");
                // 发送下载完成通知
                notificationManager.notify(notificationId, builder.build());
                // 存到数据库中
                DownloadDao downloadDao = new DownloadDao(getApplicationContext());
                // 获取当前时间
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = sDateFormat.format(new Date());
                // 根据uri获取文件路径
                String[] filePathColumn = {MediaStore.Files.FileColumns.DATA};
                String path = null;
                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    path = cursor.getString(columnIndex);
                    cursor.close();
                    Log.i(TAG, "run: 文件路径 >>> " + path);
                }
                // 插入数据
                downloadDao.insertDate(new Download(fileName, path, fileType, fileSize, time));
                Log.i(TAG, "run: 插入数据库成功");
                Log.i(TAG, "run: 保存的文件名称 >>> " + fileName);
                Log.i(TAG, "run: 保存的文件路径 >>> " + fileUrl);
                Log.i(TAG, "run: 保存的文件类型 >>> " + fileType);
                Log.i(TAG, "run: 保存的文件大小 >>> " + fileSize);
                Log.i(TAG, "run: 保存的文件时间 >>> " + time);
                // 关闭通知
                notificationManager.cancel(notificationId);
                // 关闭服务
                stopSelf();


                // 将下载的
//                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "艾跳跳.apk");
//                // 检查文件是否存在
//                if (file.exists()) {
//                    // 创建一个 Uri 对象
//                    Uri fileUri;
//                    // 判断 Android 版本
//                    // Android 7.0 及以上需要使用 FileProvider 来共享文件
//                    String authority = getPackageName() + ".provider";
//                    fileUri = FileProvider.getUriForFile(getApplicationContext(), authority, file);
//                    // 创建 Intent 来安装应用程序
//                    Intent installIntent = new Intent(Intent.ACTION_VIEW);
//                    installIntent.setDataAndType(fileUri, "application/vnd.android.package-archive");
//                    installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    // 启动安装器
//                    startActivity(installIntent);
//                } else {
//                    // 文件不存在，做适当的错误处理
//                    // 创建一个Intent对象，指定动作为ACTION_VIEW
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    // 设置数据为文件的URL
//                    intent.setData(Uri.parse(fileUrl));
//                    // 启动系统浏览器
//                    getApplicationContext().startActivity(intent);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.e("TAG", "其他异常: " + e.getMessage());
//                // 文件不存在，做适当的错误处理
//                // 创建一个Intent对象，指定动作为ACTION_VIEW
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                // 设置数据为文件的URL
//                intent.setData(Uri.parse(fileUrl));
//                // 启动系统浏览器
//                getApplicationContext().startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG", "其他异常: " + e.getMessage());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 关闭下载任务执行器
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }
}
