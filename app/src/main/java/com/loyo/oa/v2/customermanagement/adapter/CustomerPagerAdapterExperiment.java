package com.loyo.oa.v2.customermanagement.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.loyo.oa.v2.customermanagement.fragment.TestFragmentExperiment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2017/2/8.
 */

public class CustomerPagerAdapterExperiment extends FragmentPagerAdapter {

    public final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();
    private boolean pullToRefreshEnabled;

    public CustomerPagerAdapterExperiment(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }


    public void setPullToRefreshEnabled(boolean pullToRefreshEnabled) {
        this.pullToRefreshEnabled = pullToRefreshEnabled;
        for (Fragment fragment : mFragments) {
            if (fragment instanceof TestFragmentExperiment) {
                ((TestFragmentExperiment) fragment).setPullToRefreshEnabled(pullToRefreshEnabled);
            }
        }
    }
}
