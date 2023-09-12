package com.one.browser.http;

import android.util.Log;

import androidx.annotation.NonNull;

import com.one.browser.data.OkhttpData;
import com.one.browser.utils.HttpUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author 18517
 */
public class IdiomUtil {

    public String getMessage(String name, OkhttpData okhttpData) {
        String url = HttpUtil.IDIOM+"name="+name;
        Log.i("TAG", "网络请求 >>>>> "+url);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
        Request request = new Request.Builder().url(url).get().build();
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
