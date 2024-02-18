package com.one.browser.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.gyf.immersionbar.ImmersionBar;
import com.one.browser.R;
import com.one.browser.adapter.ChargeAdapter;
import com.one.browser.entity.GroupedChargeData;
import com.one.browser.sqlite.Charge;
import com.one.browser.sqlite.ChargeDao;
import com.one.browser.utils.DateUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 18517
 */
public class SysChargeActivity extends AppCompatActivity {
    private List<GroupedChargeData> dataList;

    private List<Charge> chargeList;

    private ChargeAdapter chargeAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_charge);


        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .navigationBarDarkIcon(true)
                .init();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("简单记账");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        initData();
        initControl();
    }


    private void initControl() {
        RecyclerView recyclerView = findViewById(R.id.chargeList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        // 获取数据
        getAdapter();
        // 实例化Adapter
        chargeAdapter = new ChargeAdapter(dataList);
        recyclerView.setAdapter(chargeAdapter);

    }


    private Map<String, List<Charge>> groupDataByTime(List<Charge> dataList) {
        Map<String, List<Charge>> groupedData = new LinkedHashMap<>();
        for (Charge item : dataList) {
            String time = DateUtil.getTime(item.getTime());
            if (groupedData.containsKey(time)) {
                groupedData.get(time).add(item);
            } else {
                List<Charge> group = new ArrayList<>();
                group.add(item);
                groupedData.put(time, group);
            }
        }
        return groupedData;
    }

    private void initData() {
        ChargeDao chargeDao = new ChargeDao(this);
        if (chargeDao.isDataExist()) {
            chargeList = chargeDao.getAll();
        } else {
            chargeList = new LinkedList<>();
        }
    }

    private void getAdapter() {
        dataList = new ArrayList<>();
        Map<String, List<Charge>> groupedData = groupDataByTime(chargeList);
        for (Map.Entry<String, List<Charge>> entry : groupedData.entrySet()) {
            String groupTitle = entry.getKey();
            List<Charge> groupData = entry.getValue();
            dataList.add(new GroupedChargeData(groupTitle, groupData));
        }
    }

}