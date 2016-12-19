package com.loyo.oa.v2.activityui.worksheet;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.adapter.ResponsableWorksheetsAdapter;
import com.loyo.oa.v2.activityui.worksheet.adapter.WorksheetListAdapter;
import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetEvent;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetEventListWrapper;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetListWrapper;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetListType;
import com.loyo.oa.v2.activityui.worksheet.event.WorksheetEventChangeEvent;
import com.loyo.oa.v2.activityui.worksheet.event.WorksheetEventFinishAction;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.GroupsData;
import com.loyo.oa.v2.common.adapter.BaseGroupsDataAdapter;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshExpandableListView;
import com.loyo.oa.v2.point.IWorksheet;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【工单搜索】
 */

public class WorksheetSearchActivity extends BaseLoadingActivity implements PullToRefreshBase.OnRefreshListener2 {

    private EditText edt_search;
    private ImageView iv_clean;
    private PullToRefreshExpandableListView expandableListView;

    private WorksheetListType searchType;
    private int page = 1;
    private boolean isPullDown = true;
    private Bundle mBundle;
    private String strSearch;
    protected GroupsData groupsData;

    private BaseGroupsDataAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupsData = new GroupsData();
        initView();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_worksheet_search);
    }

    @Override
    public void getPageData() {
        doSearch();
    }

    /**
     * 初始化
     */
    void initView() {
        mBundle = getIntent().getExtras();
        searchType = (WorksheetListType) mBundle.getSerializable(ExtraAndResult.EXTRA_TYPE);

        findViewById(R.id.img_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        iv_clean = (ImageView) findViewById(R.id.iv_clean);
        iv_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_search.setText("");
            }
        });

        edt_search = (EditText) findViewById(R.id.edt_search);
        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    doSearch();
                }
                return false;
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                doSearch();
            }
        });
        edt_search.requestFocus();

        expandableListView = (PullToRefreshExpandableListView) findViewById(R.id.expandableListView);
        expandableListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        expandableListView.setOnRefreshListener(this);

        ExpandableListView innerListView = expandableListView.getRefreshableView();
        if (searchType != WorksheetListType.RESPONSABLE) {
            adapter = new WorksheetListAdapter(this, groupsData, false, false);
        } else {
            adapter = new ResponsableWorksheetsAdapter(this, groupsData, WorksheetEventFinishAction.FROM_SEARCH_LIST);
        }

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
                if (searchType == WorksheetListType.RESPONSABLE) {
                    WorksheetEvent wse = (WorksheetEvent) groupsData.get(groupPosition, childPosition);
                    wsId = wse.wsId != null ? wse.wsId : wse.workSheetId;
                } else {
                    Worksheet ws = (Worksheet) groupsData.get(groupPosition, childPosition);
                    wsId = ws.id;
                }
                if (wsId == null) {
                    wsId = "";
                }

                mIntent.putExtra(ExtraAndResult.EXTRA_ID, wsId);
                startActivity(mIntent);
                hideInputKeyboard(edt_search);

                return true;
            }
        });
        //首次进来不加载数据 默认成功
        ll_loading.setStatus(LoadingLayout.Success);
    }

    public void finishEvent(WorksheetEvent event) {
        Bundle bd = new Bundle();
        bd.putString(ExtraAndResult.CC_USER_ID, event.id /*事件id*/);
        bd.putInt(ExtraAndResult.EXTRA_DATA, 0x02 /*提交完成:0x02,打回重做0x01*/);
        app.startActivity(this, WorksheetSubmitActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bd);
    }

    /* 工单事件信息变更 */
    @Subscribe
    public void onWorksheetEventUpdated(WorksheetEventChangeEvent event) {
        isPullDown = true;
        page = 1;
        getData();
    }

    @Subscribe
    public void onWorkSheetEventFinishAction(WorksheetEventFinishAction action) {
        if (action.data != null && action.eventCode == WorksheetEventFinishAction.FROM_SEARCH_LIST) {
            finishEvent(action.data);
        }
    }

    /**
     * 搜索操作
     */
    public void doSearch() {
        strSearch = edt_search.getText().toString().trim();
        getData();
    }

    protected void getData() {

        if (searchType == WorksheetListType.SELF_CREATED || searchType == WorksheetListType.ASSIGNABLE) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("pageIndex", page);
            map.put("pageSize", 15);
            map.put("type", searchType == WorksheetListType.SELF_CREATED
                    ? 1/* 我创建的 */ : 2/* 我分派的 */);
            map.put("keyword", strSearch);

            RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                    create(IWorksheet.class).getMyWorksheetList(map, new Callback<WorksheetListWrapper>() {
                @Override
                public void success(WorksheetListWrapper listWrapper, Response response) {
                    expandableListView.onRefreshComplete();
                    HttpErrorCheck.checkResponse("我的工单列表：", response, ll_loading);
                    if (isPullDown) {
                        groupsData.clear();
                        if (listWrapper != null && listWrapper.isEmpty())
                            ll_loading.setStatus(LoadingLayout.Empty);
                    }
                    loadData(listWrapper.data.records);
                }

                @Override
                public void failure(RetrofitError error) {
                    expandableListView.onRefreshComplete();
                    HttpErrorCheck.checkError(error, ll_loading, page == 1 ? true : false);
                }
            });
        } else if (searchType == WorksheetListType.RESPONSABLE) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("pageIndex", page);
            map.put("pageSize", 15);
            map.put("keyword", strSearch);

            RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                    create(IWorksheet.class).getResponsableWorksheetList(map, new Callback<WorksheetEventListWrapper>() {
                @Override
                public void success(WorksheetEventListWrapper listWrapper, Response response) {
                    expandableListView.onRefreshComplete();
                    HttpErrorCheck.checkResponse("我负责的工单列表：", response, ll_loading);
                    if (isPullDown) {
                        groupsData.clear();
                        if (listWrapper != null && listWrapper.isEmpty())
                            ll_loading.setStatus(LoadingLayout.Empty);
                    }
                    loadWorksheetEvents(listWrapper.data.records);
                }

                @Override
                public void failure(RetrofitError error) {
                    expandableListView.onRefreshComplete();
                    HttpErrorCheck.checkError(error, ll_loading);
                }
            });

        } else if (searchType == WorksheetListType.TEAM) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("pageIndex", page);
            map.put("pageSize", 15);
            map.put("keyword", strSearch);

            RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                    create(IWorksheet.class).getTeamWorksheetList(map, new Callback<WorksheetListWrapper>() {
                @Override
                public void success(WorksheetListWrapper listWrapper, Response response) {
                    expandableListView.onRefreshComplete();
                    HttpErrorCheck.checkResponse("团队工单列表：", response, ll_loading);
                    if (isPullDown) {
                        groupsData.clear();
                        if (listWrapper != null && listWrapper.isEmpty())
                            ll_loading.setStatus(LoadingLayout.Empty);
                    }
                    loadData(listWrapper.data.records);
                }

                @Override
                public void failure(RetrofitError error) {
                    expandableListView.onRefreshComplete();
                    HttpErrorCheck.checkError(error, ll_loading);
                }
            });
        }


    }

    private void loadData(List<Worksheet> list) {
        Iterator<Worksheet> iterator = list.iterator();
        while (iterator.hasNext()) {
            groupsData.addItem(iterator.next());
        }
        adapter.notifyDataSetChanged();
        expand();
    }

    private void loadWorksheetEvents(List<WorksheetEvent> list) {
        Iterator<WorksheetEvent> iterator = list.iterator();
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
    protected void onDestroy() {
        super.onDestroy();
        hideInputKeyboard(edt_search);
    }
}
