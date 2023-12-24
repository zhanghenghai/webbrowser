package com.one.browser.http;

import android.util.Log;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.one.browser.data.OkhttpData;

import java.io.IOException;
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
public class HumanFaceHttpServlet {

    private static final String YES = "YES";

    public static void getBase64(String base64, OkhttpData okhttpData) {
        new Thread(() -> {
            OkHttpClient okHttpClient = new OkHttpClient.Builder().connectionPool(new ConnectionPool(5, 10, TimeUnit.MINUTES)).readTimeout(30, TimeUnit.SECONDS).build();
            MediaType mediaType = MediaType.parse("application/json");
            String jsonBody = "{\"images\": [\"" + base64 + "\"]}";
            Log.i("TAG", "getBase64: " + jsonBody);
            RequestBody requestBody = RequestBody.create(mediaType, jsonBody);
            Request request = new Request.Builder().url(HttpUtil.FACEDETECT).post(requestBody).header("Content-Encoding", "gzip").removeHeader("User-Agent").build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.i("TAG", "onFailure: 异步请求失败 >>> " + e.getMessage());
                    okhttpData.onSuccess(null);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    Log.i("TAG", "onResponse: 异步请求成功");
                    try {
                        String jsonData = response.body().string();
                        if (jsonData == null) {
                            Log.i("TAG", "onResponse: 数据为null >>>>>>>>>>>>>> ");
                        }
                        Log.i("TAG", "获取数据 >>> : " + jsonData);
                        // 解析数据
                        JSONObject jsonObject = JSONObject.parseObject(jsonData);
                        // 解析具体数据
                        String results = jsonObject.getString("results");
                        JSONObject json = JSONObject.parseObject(results);
                        String data = json.getString("data");
                        Log.i("TAG", "是否检测到人脸 >>>>: " + data);

                        if (YES.equals(data)) {
                            okhttpData.onSuccess("YES");
                        } else {
                            okhttpData.onSuccess("NO");
                        }
                    } catch (Exception e) {
                        Log.i("TAG", "人脸检测报错 onResponse: >>>>>>>>>>> " + e.getMessage());
                    }
                }
            });
        }).start();
    }
}
