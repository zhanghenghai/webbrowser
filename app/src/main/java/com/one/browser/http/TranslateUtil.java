package com.one.browser.http;


import android.util.Log;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.one.browser.data.OkhttpData;
import com.one.browser.utils.HttpUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author 18517
 */
public class TranslateUtil {
    private static String LAN_AUTO = "auto";

    /**
     * @param text         文本
     * @param old_language 旧语言
     * @param new_language 新语言
     * @param okhttpData
     */
    public String getMessage(String text, String old_language, String new_language, OkhttpData okhttpData) {
        String url = HttpUtil.TRANSLATE;
        Log.i("TAG", "网络请求 >>>>> "+url);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("q", text);
        jsonObject.put("from", old_language);
        jsonObject.put("to", new_language);
        RequestBody requestBody = RequestBody.create(jsonObject.toJSONString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String data = response.body().string();
                Log.i("TAG", "返回成功 >>>: " + data);
                okhttpData.onSuccess(data);
            }
        });


        return null;
    }

}