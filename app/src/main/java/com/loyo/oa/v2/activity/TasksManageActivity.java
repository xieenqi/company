package com.loyo.oa.v2.activity;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.fragment.TaskManagerFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_tasks_manage)
public class TasksManageActivity extends BaseFragmentActivity {

    TaskManagerFragment taskManagerFragment=new TaskManagerFragment();

    @AfterViews
    void init() {
        setTouchView(-1);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.main_content, taskManagerFragment);
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
