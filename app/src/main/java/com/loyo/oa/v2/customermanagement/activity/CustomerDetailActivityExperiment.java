package com.loyo.oa.v2.customermanagement.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customermanagement.adapter.CustomerPagerAdapter;
import com.loyo.oa.v2.customermanagement.fragment.TestFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;

public class CustomerDetailActivityExperiment extends BaseFragmentActivity
        implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener {

    ViewGroup img_title_left, img_title_right;
    TextView tv_title_1;
    AppBarLayout appBarLayout;
    CustomerPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);

        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());

        img_title_left.setOnClickListener(this);
        img_title_right.setOnClickListener(this);

        tv_title_1.setText("客户详情");


        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        appBarLayout.addOnOffsetChangedListener(this);

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Log.v("tag", v.getId() + "");
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new CustomerPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TestFragment(), "跟进 1");
        adapter.addFragment(new TestFragment(), "联系人 2");
        adapter.addFragment(new TestFragment(), "拜访 3");

        adapter.addFragment(new TestFragment(), "机会 4");
        adapter.addFragment(new TestFragment(), "订单 5");
        adapter.addFragment(new TestFragment(), "任务 6");

        adapter.addFragment(new TestFragment(), "审批 7");
        adapter.addFragment(new TestFragment(), "附件 8");
        viewPager.setAdapter(adapter);
    }

    /**
     * Called when the {@link AppBarLayout}'s layout offset has been changed. This allows
     * child views to implement custom behavior based on the offset (for instance pinning a
     * view at a certain y value).
     *
     * @param appBarLayout   the {@link AppBarLayout} which offset has changed
     * @param verticalOffset the vertical offset for the parent {@link AppBarLayout}, in px
     */
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset >= 0) {
            adapter.setPullToRefreshEnabled(true);
        }
        else {
            adapter.setPullToRefreshEnabled(false);
        }
    }
}
