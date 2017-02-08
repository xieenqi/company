package com.loyo.oa.v2.contacts;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 选择联系人角色
 * Created by jie on 17/2/8.
 */

public class ContactsRoleSelectActivity extends BaseActivity{
    private TextView tvTitle;
    private ListView listView;
    private LoadingLayout loadingLayout;
    private LinearLayout llBack;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }
    private void initView(){
        tvTitle = (TextView) findViewById(R.id.tv_title);
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        listView= (ListView) findViewById(R.id.lv_list);
    }

    private void getData(){

    }
}
