package com.loyo.oa.v2.activityui.setting;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.setting.adapter.AdapterSystemMessage;
import com.loyo.oa.v2.activityui.setting.bean.SystemMessageItem;
import com.loyo.oa.v2.activityui.setting.persenter.SystemMessagePControl;
import com.loyo.oa.v2.activityui.setting.viewcontrol.SystemMessageVControl;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.point.IMain;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【系统消息】
 * Created by xeq on 16/11/7.
 */

public class SystemMessageActivity extends BaseLoadingActivity implements PullToRefreshBase.OnRefreshListener2, SystemMessageVControl {

    private LinearLayout ll_back;
    private TextView tv_title, tv_add;
    private PullToRefreshListView lv_list;
    private SystemMessagePControl pControl;
    private AdapterSystemMessage adapterSystemMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_system_message);
    }

    @Override
    public void getPageData() {
        pControl.getPageData(1);
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_add = (TextView) findViewById(R.id.tv_add);
        lv_list = (PullToRefreshListView) findViewById(R.id.lv_list);
        tv_title.setText("系统消息");
        tv_add.setText("全部已读");
        ll_back.setOnClickListener(click);
        tv_add.setOnClickListener(click);
        lv_list.setMode(PullToRefreshBase.Mode.BOTH);
        lv_list.setOnRefreshListener(this);
        adapterSystemMessage = new AdapterSystemMessage(this);
        lv_list.setAdapter(adapterSystemMessage);
        pControl = new SystemMessagePControl(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pControl.getPageData(1);
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_back:
                    onBackPressed();
                    break;
                case R.id.tv_add:
                    RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS()).create(IMain.class).
                            readSystemMessageAll(new Callback<Object>() {
                                @Override
                                public void success(Object o, Response response) {
                                    pControl.getPageData(1);
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Toast("设置不成功");
                                }
                            });
                    break;
            }
        }
    };

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        pControl.pullDown();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        pControl.pullUp();
    }

    @Override
    public void showStatusProgress() {

    }

    @Override
    public void showProgress(String message) {
        showLoading("");
    }

    @Override
    public void hideProgress() {
        cancelLoading();
    }

    @Override
    public void showMsg(String message) {
        Toast(message);
    }

    @Override
    public void setEmptyView() {
        ll_loading.setStatus(LoadingLayout.Empty);
    }

    @Override
    public void getDataComplete() {
        lv_list.onRefreshComplete();
    }

    @Override
    public void bindingView(List<SystemMessageItem> data) {
        adapterSystemMessage.setData(data);
    }

    @Override
    public LoadingLayout getLoadingLayout() {
        return ll_loading;
    }
}
