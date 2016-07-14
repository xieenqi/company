package com.loyo.oa.v2.activityui.signin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.bean.User;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.activityui.signin.fragment.SignInOfUserFragment;
import com.loyo.oa.v2.tool.BaseFragment;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/**
 * Created by pj on 15/10/10.
 */
@EActivity(R.layout.activity_legworks_list)
public class LegworksListActivity extends FragmentActivity {

    @ViewById ViewGroup img_title_left;
    @ViewById TextView tv_title_1;
    @Extra("data")
    User mUser;
    @Extra(ExtraAndResult.EXTRA_DATA)
    long endAt;
    @Extra("position")
    int defaultPosition;

    private FragmentManager fragmentManager = getSupportFragmentManager();

    @AfterViews
    void initViews() {
        tv_title_1.setText(mUser.getRealname() + "的拜访记录");
        img_title_left.setOnTouchListener(Global.GetTouch());
        Bundle b = new Bundle();
        b.putSerializable("user", mUser);
        b.putBoolean("disabled", true);
        b.putInt("position", defaultPosition);
        b.putLong(ExtraAndResult.EXTRA_DATA, endAt);
        BaseFragment fragment = (BaseFragment) Fragment.instantiate(this, SignInOfUserFragment.class.getName(), b);
        fragmentManager.beginTransaction().replace(R.id.layout_list_container, fragment).commit();
    }

    @Click({R.id.img_title_left})
    void onClick() {
        finish();
    }
}
