package com.loyo.oa.v2.activity.wfinstance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.fragment.WfInstanceManageFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.ViewUtil;

/**
 * 【审批流程】 页面 xnq
 */
public class WfInstanceManageActivity extends BaseFragmentActivity {

    public WfInstanceManageFragment fragment = new WfInstanceManageFragment();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wfinstance_manage);
        initUI();
    }

    void initUI() {
        MainApp.permissionPage = 4;
        getWindow().getDecorView().setOnTouchListener(new ViewUtil.OnTouchListener_softInput_hide());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.main_content, fragment);
        transaction.commit();
    }

    @Override
    protected void onActivityResult(final int requestCode,final int resultCode,final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
