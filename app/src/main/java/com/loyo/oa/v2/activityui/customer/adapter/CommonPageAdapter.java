package com.loyo.oa.v2.activityui.customer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by xeq on 16/11/10.
 */

public class CommonPageAdapter extends FragmentPagerAdapter {
    private String[] titles;
    private List<Fragment> fm;

    public void setTitles(String[] titles, List<Fragment> fm) {
        this.titles = titles;
        this.fm = fm;
    }

    public CommonPageAdapter(final FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Fragment getItem(final int position) {
        return fm.get(position);
    }
}
