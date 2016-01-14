package com.loyo.oa.v2.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.NearCount;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.fragment.CustomerCommonFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.customview.PagerSlidingTabStrip;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.activity
 * 描述 :【附近客户】页面 个人 和团队
 * 作者 : ykb
 * 时间 : 15/9/17.
 */
@EActivity(R.layout.activity_nearbycustomers)
public class NearByCustomersActivity extends BaseFragmentActivity {

    private String[] TITLES = {"我的客戶(0)", "公司已赢单客户(0)"};

    @ViewById ViewGroup layout_back;
    @ViewById ImageView iv_submit;
    @ViewById TextView tv_title;

    @ViewById PagerSlidingTabStrip tabs;
    @ViewById ViewPager pager;

    @Extra String position;
    @Extra NearCount nearCount;
    @Extra int type;//客户类型

    MyPagerAdapter adapter;
    private ArrayList<CustomerCommonFragment> fragmentXes = new ArrayList<>();

    @AfterViews
    void initViews() {
        setTouchView(-1);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("附近客户");
        iv_submit.setVisibility(View.VISIBLE);
        iv_submit.setImageResource(R.drawable.icon_menu_map);
        iv_submit.setOnTouchListener(Global.GetTouch());
        layout_back.setOnTouchListener(Global.GetTouch());
        if (null != nearCount) {
            if (type == 1) {
                TITLES[0] = type == Customer.CUSTOMER_TYPE_MINE ? "我的客戶(" : "团队客戶(";
                TITLES[0] += nearCount.count + ")";
                TITLES[1] = "公司已赢单客戶(" + nearCount.winCount + ")";
            } else {
                tabs.setVisibility(View.GONE);
            }
        }
        initFragments();
        initTabs();
        LogUtil.d(position + " 附近客户数据： " + type);
    }

    @Click(R.id.layout_back)
    void back() {
        onBackPressed();
    }

    /**
     * 点击进入map
     */
    @Click(R.id.iv_submit)
    void previewOnMap() {
        ArrayList<Customer> customers = new ArrayList<>();
        if (type == 1) {//我的附近可 要加两部分
            customers.addAll(fragmentXes.get(0).getmCustomers());
            customers.addAll(fragmentXes.get(1).getmCustomers());
        } else {//团队客户
            customers = fragmentXes.get(pager.getCurrentItem()).getmCustomers();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("customers", customers);
        bundle.putInt(ExtraAndResult.EXTRA_DATA, fragmentXes.get(0).getmCustomers().size());
        bundle.putInt(ExtraAndResult.EXTRA_TYPE, type);
        app.startActivity(this, NearByCustomersMapActivity_.class, MainApp.ENTER_TYPE_BUTTOM, false, bundle);
    }

    /**
     * 初始化选项卡
     */
    private void initTabs() {
        tabs.setTextSize(app.spTopx(18));
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        tabs.setViewPager(pager);

    }

    /**
     * 初始化片段
     */
    private void initFragments() {
        for (int i = 0; i < TITLES.length; i++) {
            Bundle bundle = new Bundle();
            bundle.putInt("type", i == 0 ? (type == Customer.CUSTOMER_TYPE_MINE ?
                    Customer.CUSTOMER_TYPE_NEAR_MINE : Customer.CUSTOMER_TYPE_NEAR_TEAM) : Customer.CUSTOMER_TYPE_NEAR_COMPANY);
            bundle.putString("position", position);
            bundle.putBoolean("isNear", true);
            CustomerCommonFragment fragment = (CustomerCommonFragment) Fragment.instantiate(this,
                    CustomerCommonFragment.class.getName(), bundle);
            fragmentXes.add(fragment);
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentXes.isEmpty() ? null : fragmentXes.get(position);
        }
    }
}
