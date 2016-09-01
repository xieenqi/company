package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.ClueDynamicAddActivity;
import com.loyo.oa.v2.activityui.order.bean.OrderDetail;
import com.loyo.oa.v2.activityui.worksheet.WorksheetAddActivity;
import com.loyo.oa.v2.activityui.worksheet.WorksheetDetailActivity;
import com.loyo.oa.v2.activityui.worksheet.adapter.BaseGroupsDataAdapter;
import com.loyo.oa.v2.activityui.worksheet.adapter.ResponsableWorksheetsAdapter;
import com.loyo.oa.v2.activityui.worksheet.adapter.WorksheetListAdapter;
import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetEvent;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetEventListWrapper;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetListWrapper;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetOrder;
import com.loyo.oa.v2.activityui.worksheet.common.GroupsData;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetListType;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Event.AppBus;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshExpandableListView;
import com.loyo.oa.v2.point.IWorksheet;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【工单搜索】
 */

public class OrderWorksheetsActivity extends BaseActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private PullToRefreshExpandableListView expandableListView;
    private ViewStub emptyView;
    private ViewGroup layout_add;

    private OrderDetail detail;
    private int page = 1;
    private boolean isPullDown = true;
    private Bundle mBundle;
    protected GroupsData groupsData;

    private BaseGroupsDataAdapter adapter;

    private LayoutInflater mInflater;

    boolean isMyUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_order_worksheet);
        groupsData = new GroupsData();
        mBundle = getIntent().getExtras();
        detail = ( OrderDetail) mBundle.getSerializable(ExtraAndResult.EXTRA_OBJ);
        if (detail == null || detail.id == null) {
            Toast("参数错误");
            finish();
        }

        initView();
        getData();
    }

    @Subscribe
    public void onWorksheetCreated(Worksheet data) {
        isPullDown = true;
        page = 1;
        getData();
    }

    /**
     * 初始化
     */
    void initView() {
        setTitle("工单");

        mInflater = LayoutInflater.from(this);
        emptyView = (ViewStub) findViewById(R.id.vs_nodata);

        findViewById(R.id.img_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        layout_add = (ViewGroup) findViewById(R.id.layout_add);
        isMyUser = detail.directorId != null
                && detail.directorId.equals(MainApp.getMainApp().user.id);
        if (!isMyUser) {
            layout_add.setVisibility(View.GONE);
        }
        layout_add.setOnClickListener(this);

        expandableListView = (PullToRefreshExpandableListView)findViewById(R.id.expandableListView);
        expandableListView.setMode(PullToRefreshBase.Mode.BOTH);
        expandableListView.setOnRefreshListener(this);

        ExpandableListView innerListView = expandableListView.getRefreshableView();
        adapter = new WorksheetListAdapter(this, groupsData);

        innerListView.setAdapter(adapter);
        innerListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        innerListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                Intent mIntent = new Intent(getApplicationContext(), WorksheetDetailActivity.class);

                String wsId = null;
                Worksheet ws =(Worksheet) groupsData.get(groupPosition, childPosition);
                wsId = ws.id;
                if (wsId == null) {
                    wsId = "";
                }

                mIntent.putExtra(ExtraAndResult.EXTRA_ID, wsId);
                startActivity(mIntent);

                return true;
            }
        });
    }

    protected  void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 15);
        map.put("type", 1);

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(IWorksheet.class).getWorksheetListByOrder(detail.id, map, new Callback<WorksheetListWrapper>() {
            @Override
            public void success(WorksheetListWrapper listWrapper, Response response) {
                expandableListView.onRefreshComplete();
                HttpErrorCheck.checkResponse("我的工单列表：", response);
                if (isPullDown) {
                    groupsData.clear();
                }
                loadData(listWrapper.data.records);
                expandableListView.setEmptyView(emptyView);
            }

            @Override
            public void failure(RetrofitError error) {
                expandableListView.onRefreshComplete();
                HttpErrorCheck.checkError(error);
            }
        });
    }

    private void loadData(List<Worksheet> list) {
        Iterator<Worksheet> iterator = list.iterator();
        while (iterator.hasNext()) {
            groupsData.addItem(iterator.next());
        }
        adapter.notifyDataSetChanged();
        expand();
    }

    protected void expand() {
        for (int i = 0; i < groupsData.size(); i++) {
            expandableListView.getRefreshableView().expandGroup(i, false);//true 自动滑到底部
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        isPullDown = true;
        page = 1;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isPullDown = false;
        page++;
        getData();
    }

    protected void changeAdapter() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /*返回*/
            case R.id.img_title_left:
                onBackPressed();
                break;

            /*新建线索*/
            case R.id.layout_add:
                Bundle bundle = new Bundle();
                bundle.putSerializable(ExtraAndResult.EXTRA_OBJ, WorksheetOrder.converFromDetail(detail));
                app.startActivityForResult(this, WorksheetAddActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE, bundle);
                break;

            default:
                break;
        }
    }
}
