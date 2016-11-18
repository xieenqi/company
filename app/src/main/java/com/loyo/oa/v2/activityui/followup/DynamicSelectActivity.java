package com.loyo.oa.v2.activityui.followup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.adapter.CommonPageAdapter;
import com.loyo.oa.v2.activityui.followup.fragment.DynamicSelectClueFragment;
import com.loyo.oa.v2.activityui.followup.fragment.DynamicSelectCustomerFragment;
import com.loyo.oa.v2.customview.PagerSlidingTabStrip;
import com.loyo.oa.v2.tool.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 【选择跟进对象】可以选客户 可以选线索
 * Created by xeq on 16/11/10.
 */

public class DynamicSelectActivity extends BaseFragmentActivity implements View.OnClickListener {

    private LinearLayout ll_back;
    private TextView tv_title;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private DynamicSelectCustomerFragment selectCustomer;
    private DynamicSelectClueFragment selectClue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_select);
        selectCustomer = new DynamicSelectCustomerFragment();
        selectClue = new DynamicSelectClueFragment();
        initView();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        tabs.setTextSize(app.spTopx(16));
        CommonPageAdapter adapter = new CommonPageAdapter(getSupportFragmentManager());
        List<Fragment> fm = new ArrayList<>();
        fm.add(selectCustomer);
        fm.add(selectClue);
        adapter.setTitles(new String[]{"客户", "线索"}, fm);
        pager.setAdapter(adapter);
        int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        tabs.setViewPager(pager);
        tv_title.setText("选择跟进对象");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                onBackPressed();
                break;

        }
    }
}
