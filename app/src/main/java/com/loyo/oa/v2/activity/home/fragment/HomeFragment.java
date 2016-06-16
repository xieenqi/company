package com.loyo.oa.v2.activity.home.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.home.ActivityMainHome;

import java.util.ArrayList;


public class HomeFragment extends Fragment implements OnPageChangeListener {
        private EventFragment mEventFragment;//tab1又实现2个fragment 我自己的项目有这个需求 点击侧滑直接切换tab的fragment
    private MailListFragment mMailListFragment;
//    private FoundFragment mFoundFragment;
//    private PersionFragment mPersionFragment;
    private ArrayList<RadioButton> title = new ArrayList<RadioButton>();// 4个标题
    ViewPager pager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_main_home, container, false);
        pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setAdapter(new MyPagerAdapter(getActivity()
                .getSupportFragmentManager()));
        pager.setOnPageChangeListener(this);
        title.add((RadioButton) view.findViewById(R.id.title1));// 4个title标签
        title.add((RadioButton) view.findViewById(R.id.title2));
        title.get(0).setOnClickListener(new MyOnClickListener(0));// 设置响应
        title.get(1).setOnClickListener(new MyOnClickListener(1));
        return view;
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
                    if (mEventFragment == null) {
                        mEventFragment = new EventFragment();
                    }
                    return mEventFragment;
                case 1:
                    if (mMailListFragment == null) {
                        mMailListFragment = new MailListFragment();
                    }
                    return mMailListFragment;
//                case 2:
//                    if (mFoundFragment == null) {
//                        mFoundFragment = new FoundFragment();
//                    }
//                    return mFoundFragment;
//
//                case 3:
//                    if (mPersionFragment == null) {
//                        mPersionFragment = new PersionFragment();
//                    }
//                    return mPersionFragment;

                default:
                    return null;
            }
        }

        //这个方法主要判断是否保存过fragment如果没保存过直接调用getItem的方法创建一个,看父的构造方法就知道了
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            Object a = super.instantiateItem(container, position);
            ActivityMainHome.index = container.getId();
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
            ((ActivityMainHome) getActivity()).gotoStop();
        } else {
            ((ActivityMainHome) getActivity()).gotoStart();
        }
        title.get(index).setChecked(true);// 设置被选中，否则布局里面的背景不会切换
    }

}