package com.loyo.oa.v2.activityui.worksheet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.common.ClueCommon;
import com.loyo.oa.v2.activityui.customer.CommonTagSelectActivity;
import com.loyo.oa.v2.activityui.customer.CommonTagSelectActivity_;
import com.loyo.oa.v2.activityui.other.adapter.CommonCategoryAdapter;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetConfig;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetListType;
import com.loyo.oa.v2.activityui.worksheet.fragment.AssignableWorksheetFragment;
import com.loyo.oa.v2.activityui.worksheet.fragment.ResponsableWorksheetFragment;
import com.loyo.oa.v2.activityui.worksheet.fragment.SelfCreatedWorksheetFragment;
import com.loyo.oa.v2.activityui.worksheet.fragment.TeamWorksheetFragment;
import com.loyo.oa.v2.activityui.worksheet.fragment.WorksheetAddStep1Fragment;
import com.loyo.oa.v2.activityui.worksheet.fragment.WorksheetAddStep2Fragment;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 新建跟进动态
 */
public class WorksheetAddActivity extends FragmentActivity implements View.OnClickListener {

    private FragmentManager fragmentManager = getSupportFragmentManager();

    private int mIndex = -1;
    private List<BaseFragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet_add);
        init();
        WorksheetConfig.getWorksheetTypes(true/* 没有数据就从网络获取 */);
    }

    private void init() {
        initChildren();
    }

    /**
     * 初始化子片段
     */
    private void initChildren() {

        BaseFragment fragment = null;

        Bundle b = new Bundle();
        fragment = (BaseFragment) Fragment.instantiate(this, WorksheetAddStep1Fragment.class.getName(), b);
        fragments.add(fragment);

        b = new Bundle();
        fragment = (BaseFragment) Fragment.instantiate(this, WorksheetAddStep2Fragment.class.getName(), b);
        fragments.add(fragment);

        changeChild(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    /**
     * 改变子片段动画
     *
     * @param index
     */
    private void changeChild(int index) {
        if (index != mIndex && fragments.size() > 0) {
            mIndex = index;
            try {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fl_order_container, fragments.get(index));
                fragmentTransaction.commit();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void nextStep() {
        changeChild(1);
    }

    public void previousStep() {
        changeChild(0);
    }
}

