package com.loyo.oa.v2.activityui.work;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.work.fragment.WorkReportsManageFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.ViewUtil;

/**
 * 工作报告  页面
 */
public class WorkReportsManageActivity extends BaseFragmentActivity {

    WorkReportsManageFragment workReportsManageFragment = new WorkReportsManageFragment();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workreports_manage);
        initUI();
    }

    void initUI() {
        MainApp.permissionPage = 3;
        getWindow().getDecorView().setOnTouchListener(new ViewUtil.OnTouchListener_softInput_hide());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.main_content, workReportsManageFragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(final int requestCode,final int resultCode,final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
