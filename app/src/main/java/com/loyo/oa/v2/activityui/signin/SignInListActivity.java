package com.loyo.oa.v2.activityui.signin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.signin.adapter.SignInListAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.LegWork;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 描述 :【拜访签到列表】
 * com.loyo.oa.v2.activity
 * 作者 : ykb
 * 时间 : 15/9/25.
 */
@EActivity(R.layout.activity_signin_list)
public class SignInListActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2 {

    @ViewById ViewGroup layout_back;
    @ViewById TextView tv_title;
    @ViewById(R.id.listView_legworks) PullToRefreshListView lv;
    @ViewById ViewGroup layout_add;
    @Extra Customer mCustomer;
    @Extra boolean isMyUser;

    private PaginationX<LegWork> workPaginationX = new PaginationX<>(20);
    private ArrayList<LegWork> legWorks = new ArrayList<>();
    private SignInListAdapter adapter;
    private Permission permission;
    private boolean isTopAdd;
    private boolean isChanged;

    @AfterViews
    void initViews() {
        setTouchView(NO_SCROLL);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("拜访签到");
        layout_back.setOnTouchListener(Global.GetTouch());
        if (!isMyUser) {
            layout_add.setVisibility(View.GONE);
        }
        layout_add.setOnTouchListener(Global.GetTouch());
        getData();

        //超级管理员\权限判断
        if (!MainApp.user.isSuperUser()) {
            try {
                permission = (Permission) MainApp.rootMap.get("0206");
            } catch (NullPointerException e) {
                e.printStackTrace();
                Toast("发布公告权限,code错误:0402");
            }
        }
    }

    @Override
    public void onBackPressed() {
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, isChanged ? RESULT_OK : 0, new Intent());
    }

    @Click(R.id.layout_back)
    void back() {
        onBackPressed();
    }

    /**
     * 新增拜访记录
     */
    @Click(R.id.layout_add)
    void createNewSignIn() {
        if(null != permission && !permission.isEnable()){
            showGeneralDialog(true, false, "此功能权限已关闭，请联系管理员开启后再试！")
                    .setNoCancelOnclick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            generalPopView.dismiss();
                        }
                    });
        }else{
            Bundle b = new Bundle();
            b.putSerializable("data", mCustomer);
            app.startActivityForResult(this, SignInActivity.class, MainApp.ENTER_TYPE_BUTTOM, FinalVariables.REQUEST_CREATE_LEGWORK, b);
        }
    }

    /**
     * 绑定数据
     */
    private void bindData() {
        if (null == adapter) {
            adapter = new SignInListAdapter(this, SignInListAdapter.TYPE_LIST_OF_CUSTOMER, legWorks);
            lv.setAdapter(adapter);
            lv.setOnRefreshListener(this);
            lv.setMode(PullToRefreshBase.Mode.BOTH);
        } else {
            adapter.setLegWorks(legWorks);
            adapter.notifyDataSetChanged();
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                LegWork legWork = legWorks.get(i - 1);
                previewLegwork(legWork);
            }
        });
    }

    /**
     * 获取列表
     */
    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        //        map.put("userId", 0);
        map.put("startAt", DateTool.getDateToTimestamp("2014-01-01", app.df5) / 1000);
        map.put("endAt", DateTool.getEndAt_ofDay() / 1000);
        map.put("customerId", mCustomer.getId());
        map.put("pageIndex", workPaginationX.getPageIndex());
        map.put("pageSize", isTopAdd ? legWorks.size() >= 20 ? legWorks.size() : 20 : 20);

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getLegworks(map, new RCallback<PaginationX<LegWork>>() {
            @Override
            public void success(final PaginationX<LegWork> paginationX, final Response response) {
                HttpErrorCheck.checkResponse("拜访签到列表数据：", response);
                workPaginationX = paginationX;
                lv.onRefreshComplete();
                if (isTopAdd) {
                    legWorks.clear();
                }
                legWorks.addAll(paginationX.getRecords());
                bindData();
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                lv.onRefreshComplete();
                super.failure(error);
            }
        });
    }

    /**
     * 查看【签到详情】
     *
     * @param legWork
     */
    private void previewLegwork(final LegWork legWork) {
        Intent intent = new Intent(this, SignInfoActivity.class);
        intent.putExtra(LegWork.class.getName(), legWork);
        intent.putExtra("mCustomer", mCustomer);
        intent.putExtra(ExtraAndResult.EXTRA_STATUS, true);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != FinalVariables.REQUEST_CREATE_LEGWORK || resultCode != Activity.RESULT_OK || null == data) {
            return;
        }
        isChanged = true;
        onPullDownToRefresh(lv);
    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        workPaginationX.setPageIndex(1);
        isTopAdd = true;
        getData();
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        isTopAdd = false;
        workPaginationX.setPageIndex(workPaginationX.getPageIndex() + 1);
        getData();
    }
}
