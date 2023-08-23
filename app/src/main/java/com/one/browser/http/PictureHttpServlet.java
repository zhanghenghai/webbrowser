package com.one.browser.http;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;


import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.one.browser.data.OkhttpData;
import com.one.browser.entity.Resource;
import com.one.browser.entity.Size;
import com.one.browser.sqlite.CommonDao;
import com.one.browser.sqlite.ExaminationDao;
import com.one.browser.sqlite.StudentDao;
import com.one.browser.sqlite.VisaDao;
import com.one.browser.utils.HttpUtil;
import com.one.browser.utils.SaveFileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author 18517
 */
public class PictureHttpServlet {


    public static void getBase64(Context context, String base64, OkhttpData okhttpData) {
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
            OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS).build();
            MediaType mediaType = MediaType.parse("application/json");
            String jsonBody = "{\"images\": [\"" + base64 + "\"]}";
            Log.i("TAG", "getBase64: " + jsonBody);
            RequestBody requestBody = RequestBody.create(mediaType, jsonBody);
            Request request = new Request.Builder().url(HttpUtil.MATTING).post(requestBody).build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.i("TAG", "onFailure: 异步请求失败 >>> "+e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    Log.i("TAG", "onResponse: 异步请求成功");
                    String jsonData = response.body().string();
                    Log.i("TAG", "获取数据 >>> : " + jsonData);
                    // 解析数据
                    JSONObject jsonObject = JSONObject.parseObject(jsonData);
                    // 解析状态字段
                    String status = jsonObject.getString("status");
                    Log.i("TAG", "获取状态 >>>> " + status);
                    // 解析具体数据
                    String results = jsonObject.getString("results");
                    JSONObject json = JSONObject.parseObject(results);
                    String data = json.getString("data");
                    okhttpData.onSuccess(data);

//                    JSONArray jsonArray = jsonObject.getJSONArray("results");
//
//                    for (int i = 0; i < jsonArray.size(); i++) {
//                        Log.i("TAG", "继续解析: ");
//                        JSONObject data = jsonArray.getJSONObject(i);
//                        String json = data.getString("data");
//                        Log.i("TAG", "获取data数据 :  >>>" + json);
//                        okhttpData.onSuccess(json);
//                    }
                }
            });
        });
        // 关闭线程池
        threadPool.shutdown();
    }


    /**
     * @param context 调用对象
     */
    public static void getMessage(Context context) {

        // 线程池的核心线程数
        int corePoolSize = 10;
        // 线程池的最大线程数
        int maximumPoolSize = 20;
        // 非核心线程的空闲超时时间 60秒
        long keepAliveTime = 60;
        // TimeUnit：空闲超时时间的单位。
        // LinkedBlockingQueue：任务队列，用于存放待执行的任务。
        ThreadFactory threadFactory = new CustomThreadFactory("MessageThread");
        ExecutorService threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), threadFactory);

        // 提交任务给线程池
        threadPool.execute(() -> {
            // 网络请求
            OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
            Request request = new Request.Builder().url(HttpUtil.HTTP + "/api/papers").get().build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.i("TAG", "onFailure: 异步请求失败 >>> "+e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    Log.i("TAG", "onResponse: 异步请求成功");
                    try {
                        // 异步请求成功
                        assert response.body() != null;
                        String json = response.body().string();
                        Log.i("TAG", "获取数据 >>> : " + json);
                        JSONObject jsonObject = JSON.parseObject(json);
                        String data = jsonObject.getString("data");
                        if (!data.isEmpty()) {
                            JSONArray datas = JSONArray.parseArray(data);
                            for (Object o : datas) {
                                JSONObject js = JSON.parseObject(o.toString());
                                if (js.getString("常用尺寸") != null) {
                                    CommonDao commonDao = new CommonDao(context);
                                    String str = js.getString("常用尺寸");
                                    List<Size> list = JSONObject.parseObject(str, new TypeReference<List<Size>>() {
                                    });
                                    Log.i("TAG", "获取网络数据大小: " + list.size());
                                    Log.i("TAG", "onResponse: " + commonDao.getAll());
                                    if (commonDao.getAll() != null) {
                                        if (list.size() != commonDao.getAll().size()) {
                                            commonDao.deleteData();
                                            for (Size s : list) {
                                                System.out.println("常用尺寸标题 >>> " + s.getClassifyTitle());
                                                commonDao.inserData(new Resource(s.getClassifyTitle(), s.getClassifyPixelX(), s.getClassifyPixelY(), s.getClassifyWashX(), s.getClassifyWashY()));
                                            }
                                        }
                                    } else {
                                        for (Size s : list) {
                                            System.out.println("常用尺寸标题 >>> " + s.getClassifyTitle());
                                            commonDao.inserData(new Resource(s.getClassifyTitle(), s.getClassifyPixelX(), s.getClassifyPixelY(), s.getClassifyWashX(), s.getClassifyWashY()));
                                        }
                                    }
                                }
                                if (js.getString("学生证件") != null) {
                                    StudentDao studentDao = new StudentDao(context);
                                    String str = js.getString("学生证件");
                                    List<Size> list = JSONObject.parseObject(str, new TypeReference<List<Size>>() {
                                    });
                                    if (studentDao.getAll() != null) {
                                        if (list.size() != studentDao.getAll().size()) {
                                            studentDao.deleteData();
                                            for (Size s : list) {
                                                System.out.println("学生证件标题 >>> " + s.getClassifyTitle());
                                                studentDao.inserData(new Resource(s.getClassifyTitle(), s.getClassifyPixelX(), s.getClassifyPixelY(), s.getClassifyWashX(), s.getClassifyWashY()));
                                            }
                                        }
                                    } else {
                                        for (Size s : list) {
                                            System.out.println("学生证件标题 >>> " + s.getClassifyTitle());
                                            studentDao.inserData(new Resource(s.getClassifyTitle(), s.getClassifyPixelX(), s.getClassifyPixelY(), s.getClassifyWashX(), s.getClassifyWashY()));
                                        }
                                    }
                                }
                                if (js.getString("考试证件") != null) {
                                    ExaminationDao examinationDao = new ExaminationDao(context);
                                    String str = js.getString("考试证件");
                                    List<Size> list = JSONObject.parseObject(str, new TypeReference<List<Size>>() {
                                    });
                                    if (examinationDao.getAll() != null) {
                                        if (list.size() != examinationDao.getAll().size()) {
                                            examinationDao.deleteData();
                                            for (Size s : list) {
                                                System.out.println("考试证件标题 >>> " + s.getClassifyTitle());
                                                examinationDao.inserData(new Resource(s.getClassifyTitle(), s.getClassifyPixelX(), s.getClassifyPixelY(), s.getClassifyWashX(), s.getClassifyWashY()));
                                            }
                                        }
                                    } else {
                                        for (Size s : list) {
                                            System.out.println("考试证件标题 >>> " + s.getClassifyTitle());
                                            examinationDao.inserData(new Resource(s.getClassifyTitle(), s.getClassifyPixelX(), s.getClassifyPixelY(), s.getClassifyWashX(), s.getClassifyWashY()));
                                        }
                                    }
                                }
                                if (js.getString("签证证件") != null) {
                                    VisaDao visaDao = new VisaDao(context);
                                    String str = js.getString("签证证件");
                                    List<Size> list = JSONObject.parseObject(str, new TypeReference<List<Size>>() {
                                    });
                                    if (visaDao.getAll() != null) {
                                        if (list.size() != visaDao.getAll().size()) {
                                            visaDao.deleteData();
                                            for (Size s : list) {
                                                System.out.println("签证证件标题 >>> " + s.getClassifyTitle());
                                                visaDao.inserData(new Resource(s.getClassifyTitle(), s.getClassifyPixelX(), s.getClassifyPixelY(), s.getClassifyWashX(), s.getClassifyWashY()));
                                            }
                                        }
                                    } else {
                                        for (Size s : list) {
                                            System.out.println("签证证件标题 >>> " + s.getClassifyTitle());
                                            visaDao.inserData(new Resource(s.getClassifyTitle(), s.getClassifyPixelX(), s.getClassifyPixelY(), s.getClassifyWashX(), s.getClassifyWashY()));
                                        }
                                    }
                                }
                            }
                        }
                    } catch (JSONException | IOException e) {
                        Log.i("TAG", "版本更新 JSON解析失败: ");
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
