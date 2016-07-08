package com.loyo.oa.v2.activityui.home.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.home.MainHomeActivity;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;

/**
 * 【主界面】fragment
 */
public class HomeFragment extends BaseFragment implements OnPageChangeListener {

    private HomeApplicationFragment mHomeApplicationFragment;//tab1又实现2个fragment 我自己的项目有这个需求 点击侧滑直接切换tab的fragment
    private HomeStatisticsFragment mHomeStatisticsFragment;
    private ArrayList<RadioButton> title = new ArrayList<RadioButton>();// 4个标题
    private ViewPager pager;
    private RoundImageView heading;
    private Bundle mBundle;
    private LinearLayout ll_network;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_home, null, false);
        initView(view);
        registerBaseReceiver();
        return view;
    }


    private void initView(View view) {
        heading = (RoundImageView) view.findViewById(R.id.newhome_heading_img);
        ll_network = (LinearLayout) view.findViewById(R.id.ll_network);
        pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setAdapter(new MyPagerAdapter(getActivity()
                .getSupportFragmentManager()));
        pager.setOnPageChangeListener(this);
        title.add((RadioButton) view.findViewById(R.id.title1));// 4个title标签
        title.add((RadioButton) view.findViewById(R.id.title2));
        title.get(0).setOnClickListener(new MyOnClickListener(0));// 设置响应
        title.get(1).setOnClickListener(new MyOnClickListener(1));

//        heading.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainApp.getMainApp().startActivity(getActivity(), SettingActivity.class, MainApp.ENTER_TYPE_RIGHT, false, null);
//            }
//        });
    }

    /**
     * 注册网络变化广播
     */
    protected void registerBaseReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(baseReceiver, filter);
    }

    protected BroadcastReceiver baseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) {
                return;
            }
            String action = intent.getAction();
            if (TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
                NetworkInfo info = manager.getActiveNetworkInfo();
                if (null != info && info.isAvailable() && info.isConnected()) {
                    onNetworkChanged(true);
                } else {
                    onNetworkChanged(false);
                }
            }
        }
    };

    /**
     * 网络状态变化回调方法
     *
     * @param available
     */
    protected void onNetworkChanged(boolean available) {
        ll_network.setVisibility(available ? View.GONE : View.VISIBLE);
    }

    public void setHeadVisibility(boolean hide) {
        heading.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(baseReceiver);
    }

    private class MyOnClickListener implements OnClickListener {
        private int index;

        public MyOnClickListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            title.get(index).setChecked(true);// 设置被选中，否则布局里面的背景不会切换
            pager.setCurrentItem(index);// 把viewpager的视图切过去，实现捏造title跟pager的联动
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (mHomeApplicationFragment == null) {
                        mHomeApplicationFragment = new HomeApplicationFragment();
                        mBundle = new Bundle();
                        mBundle.putParcelable("view", heading);
                        mHomeApplicationFragment.setArguments(mBundle);

                    }
                    return mHomeApplicationFragment;
                case 1:
                    if (mHomeStatisticsFragment == null) {
                        mHomeStatisticsFragment = new HomeStatisticsFragment();
                    }
                    return mHomeStatisticsFragment;

                default:
                    return null;
            }
        }

        //这个方法主要判断是否保存过fragment如果没保存过直接调用getItem的方法创建一个,看父的构造方法就知道了
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            Object a = super.instantiateItem(container, position);
            MainHomeActivity.index = container.getId();
            return a;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            // super.destroyItem(container, position, object);
            //这个方法是一直在重复创建新的fragment,直接注视掉父类的构造方法就可以了,有兴趣的可以打开注释看下打印的生命周期的值
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int index) {
        // TODO Auto-generated method stub
        //记下第每个fragment的索引 实现只允许tab1可以侧滑的效果
        if (index > 0) {
            ((MainHomeActivity) getActivity()).gotoStop();
        } else {
            ((MainHomeActivity) getActivity()).gotoStart();
        }
        title.get(index).setChecked(true);// 设置被选中，否则布局里面的背景不会切换
        if (1 == index) {
            mHomeStatisticsFragment.onInIt();
        }
        LogUtil.d("选择值：" + index);
    }

}