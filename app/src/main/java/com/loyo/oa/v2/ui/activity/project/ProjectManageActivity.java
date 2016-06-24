package com.loyo.oa.v2.ui.activity.project;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.ui.fragment.ProjectManageFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

/**
 * com.loyo.oa.v2.ui.activity
 * 描述 :项目列表主界面
 * 作者 : ykb
 * 时间 : 15/9/7.
 *
 * 项目业务page的刷新回调在BaseMainListFragmentX_中
 */
@EActivity(R.layout.activity_project)
public class ProjectManageActivity extends BaseFragmentActivity {

    @AfterViews
    void initUI() {
        MainApp.permissionPage = 1;
        setTouchView(-1);
        getSupportFragmentManager().beginTransaction().add(R.id.project_container,
                Fragment.instantiate(this, ProjectManageFragment.class.getName())).commit();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
