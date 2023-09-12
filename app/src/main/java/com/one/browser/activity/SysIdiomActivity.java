package com.one.browser.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.one.browser.R;
import com.one.browser.http.IdiomUtil;
import com.one.browser.onClick.itemOnClick;
import com.one.browser.utils.HttpState;
import com.one.browser.utils.HttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author 18517
 */
public class SysIdiomActivity extends AppCompatActivity {

    private TextInputLayout textInputLayout;
    private TextInputEditText textInputEditText;
    private RecyclerView recyclerView;
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
    private final String CODE = "code";
    private final String DATA = "data";
    private final String NULL = "[null]";
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idiom);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .navigationBarDarkIcon(true)
                .init();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("成语查询");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        ExtendedFloatingActionButton fab = findViewById(R.id.fab);

        textInputEditText = findViewById(R.id.textInputEditText);
        textInputLayout = findViewById(R.id.textInputLayout);
        recyclerView = findViewById(R.id.rv);
        recyclerView.setItemViewCacheSize(9999);



        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fab.setOnClickListener(view -> {
            if (TextUtils.isEmpty(Objects.requireNonNull(textInputEditText.getText()).toString())) {
                textInputLayout.setError("请输入成语");
                textInputLayout.setErrorEnabled(true);
            } else {
                if (!itemOnClick.isVPNConnected(SysIdiomActivity.this)) {
                    itemOnClick.LoadingDialog(SysIdiomActivity.this);
                    String name = textInputEditText.getText().toString();

                    // Observable 被观察者
                    compositeDisposable.add(Observable.fromCallable(() -> {
                                String url = HttpUtil.IDIOM + "name=" + name;
                                OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
                                Request request = new Request.Builder().url(url).get().build();
                                Response response = okHttpClient.newCall(request).execute();
                                if (response.isSuccessful()) {
                                    ResponseBody responseBody = response.body();
                                    if (responseBody != null) {
                                        return responseBody.string();
                                    }
                                }
                                return null;
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(result -> {
                                itemOnClick.LoadingDialogClose();
                                if (result != null) {
                                    try {
                                        JSONObject json = JSON.parseObject(result);
                                        if (json.getInteger(CODE) == (HttpState.SUCCESS.getI())) {
                                            Log.i("TAG", "data: >>>>> " + json.getString("data"));
                                            if (json.getString(DATA) != null && !NULL.equals(json.getString(DATA))) {
                                                listmap = JSON.parseObject(json.getString("data"), new TypeReference<ArrayList<HashMap<String, Object>>>() {
                                                });
                                                recyclerView.setAdapter(new Recyclerview1Adapter(listmap));
                                            } else {
                                                itemOnClick.Toast(SysIdiomActivity.this, "没有查询到相关信息");
                                            }
                                        } else {
                                            Log.i("TAG", "onCreate: >>>>>>> ");
                                            itemOnClick.Toast(SysIdiomActivity.this, json.getString("msg"));
                                        }
                                    } catch (Exception e) {
                                        itemOnClick.Toast(SysIdiomActivity.this, "网络请求失败");
                                        Log.i("TAG", "onCreate: " + e.getMessage());
                                    }
                                } else {
                                    itemOnClick.Toast(SysIdiomActivity.this, "网络请求失败");
                                }
                            },throwable -> {
                                itemOnClick.Toast(SysIdiomActivity.this, "网络请求失败");
                                Log.i("TAG", "onCreate: " + throwable.getMessage());
                            }));
                }
            }
        });
    }


    public static class Recyclerview1Adapter extends RecyclerView.Adapter<Recyclerview1Adapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;

        // 获取到值
        public Recyclerview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) parent.getContext().getSystemService(SysIdiomActivity.LAYOUT_INFLATER_SERVICE);
            View _v = _inflater.inflate(R.layout.item_idiom, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.idiom_word.setText(Objects.requireNonNull(_data.get(position).get("word")).toString());
            holder.idiom_explanation.setText(Objects.requireNonNull(_data.get(position).get("explanation")).toString());

        }

        @Override
        public int getItemCount() {
            return _data.size();
        }

        // 监听器
        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView idiom_word;
            private final TextView idiom_explanation;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                idiom_word = itemView.findViewById(R.id.idiom_word);
                idiom_explanation = itemView.findViewById(R.id.idiom_explanation);
            }
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}