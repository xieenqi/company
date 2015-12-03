package com.loyo.oa.v2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.fragment.WfInstanceManageFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.ViewUtil;

/**
 * 【审批流程】 页面 xnq
 */
public class WfInstanceManageActivity extends BaseFragmentActivity {

    WfInstanceManageFragment fragment = new WfInstanceManageFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wfinstance_manage);

        initUI();
    }

    void initUI() {
        getWindow().getDecorView().setOnTouchListener(new ViewUtil.OnTouchListener_softInput_hide());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.main_content, fragment);
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
