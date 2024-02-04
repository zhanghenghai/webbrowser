package com.one.browser.http;

import android.util.Log;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.one.browser.data.OkhttpData;


import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author 18517
 */
public class CartoonHttpServlet {

    public static void getBase64(String base64,String type, OkhttpData okhttpData) {
        // 线程池的核心线程数
        int corePoolSize = 10;
        // 线程池的最大线程数
        int maximumPoolSize = 20;
        // 非核心线程的空闲超时时间 60秒
        long keepAliveTime = 60;
        // TimeUnit：空闲超时时间的单位。
        // LinkedBlockingQueue：任务队列，用于存放待执行的任务。
        ThreadFactory threadFactory = new CustomThreadFactory("Base64Thread");
        ExecutorService threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), threadFactory);

        // 提交任务给线程池
        threadPool.execute(() -> {
            OkHttpClient okHttpClient = new OkHttpClient.Builder().connectionPool(new ConnectionPool(5, 10, TimeUnit.MINUTES)).readTimeout(30, TimeUnit.SECONDS).build();
            MediaType mediaType = MediaType.parse("application/json");
            String body = "{\"image\": \"" + base64 + "\",\"type\": \"" + type + "\"}";
            Log.i("TAG", "getBase64: " + body);
            RequestBody requestBody = RequestBody.create(mediaType, body);
            Request request = new Request.Builder().url(HttpUtil.CARTOON).post(requestBody).header("Content-Encoding", "gzip").removeHeader("User-Agent").build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.i("TAG", "onFailure: 异步请求失败 >>> " + e.getMessage());
                    okhttpData.onSuccess(null);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    Log.i("TAG", "onResponse: 异步请求成功");
                    try {
                        String jsonData = response.body().string();
                        if (jsonData == null) {
                            Log.i("TAG", "onResponse: 数据为null >>>>>>>>>>>>>> ");
                        }
                        Log.i("TAG", "获取数据 >>> : " + jsonData);
                        // 解析数据
                        JSONObject jsonObject = JSON.parseObject(jsonData);
                        String json = jsonObject.getString("data");
                        Log.i("TAG", "图片返回结果 >>> " + json);
                        JSONObject image = JSON.parseObject(json);
                        String RequestImageURL = image.getString("imageURL");
                        okhttpData.onSuccess(RequestImageURL);
                    } catch (Exception e) {
                        okhttpData.onSuccess(null);

                    }
                }
            });
        });
        // 关闭线程池
        threadPool.shutdown();
    }


    static class CustomThreadFactory implements ThreadFactory {
        private final String namePrefix;

        public CustomThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName(namePrefix + "-" + t.getId());
            // 可以设置线程的其他属性，如优先级、是否守护线程等
            // ...
            return t;
        }
    }
}
