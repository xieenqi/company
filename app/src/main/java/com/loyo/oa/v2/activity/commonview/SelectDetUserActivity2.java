package com.loyo.oa.v2.activity.commonview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.customview.HorizontalScrollListView;

public class SelectDetUserActivity2 extends BaseActivity implements View.OnClickListener {
    private LinearLayout llBack;
    private Button btnTitleRight;
    private HorizontalScrollListView lvSelectUser;
    private TextView tv_toSearch;
    private RecyclerView rvDepartments;
    private RecyclerView rvUsers;
    private int screenHeight;
    private int screenWidth;
    private LinearLayoutManager mDepartmentLayoutManager;
    private LinearLayoutManager mUserLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_det_user2);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        // 获取屏幕高度\宽度
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
    }

    private void initView() {
        assignViews();

        // 设置部门与用户的layout的宽度比为1：2
        ViewGroup.LayoutParams lp_department = rvDepartments.getLayoutParams();
        lp_department.width = screenWidth / 3;
        rvDepartments.setLayoutParams(lp_department);

        mDepartmentLayoutManager = new LinearLayoutManager(this);
        mDepartmentLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mUserLayoutManager = new LinearLayoutManager(this);
        mUserLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvDepartments.setLayoutManager(mDepartmentLayoutManager);
        rvUsers.setLayoutManager(mUserLayoutManager);
    }

    private void assignViews() {
        tv_toSearch = (TextView) findViewById(R.id.tv_toSearch);
        btnTitleRight = (Button) findViewById(R.id.btn_title_right);
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        lvSelectUser = (HorizontalScrollListView) findViewById(R.id.lv_selectUser);
        rvDepartments = (RecyclerView) findViewById(R.id.rv_departments);
        rvUsers = (RecyclerView) findViewById(R.id.rv_users);
    }

    private void initListener() {
        llBack.setOnClickListener(this);
        btnTitleRight.setOnClickListener(this);
        tv_toSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_title_right:

                break;
            case R.id.tv_toSearch:

                break;
            default:

                break;
        }
    }
}
