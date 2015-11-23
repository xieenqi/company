package com.loyo.oa.v2.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.fragment.ProjectManageFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

/**
 * com.loyo.oa.v2.activity
 * 描述 :项目列表主界面
 * 作者 : ykb
 * 时间 : 15/9/7.
 */
@EActivity(R.layout.activity_project)
public class ProjectManageActivity extends BaseFragmentActivity{

    @AfterViews
    void initUI()
    {
        setTouchView(-1);
        getSupportFragmentManager().beginTransaction().add(R.id.project_container, Fragment.instantiate(this, ProjectManageFragment.class.getName())).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
