package com.loyo.oa.v2.activityui.worksheet.fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.sale.SaleOpportunitiesManagerActivity;
import com.loyo.oa.v2.activityui.sale.bean.SaleTeamScreen;
import com.loyo.oa.v2.activityui.sale.fragment.TeamSaleFragment;
import com.loyo.oa.v2.activityui.worksheet.WorksheetAddActivity;
import com.loyo.oa.v2.activityui.worksheet.WorksheetDetailActivity;
import com.loyo.oa.v2.activityui.worksheet.adapter.WorksheetListAdapter;
import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetListWrapper;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetTemplate;
import com.loyo.oa.v2.activityui.worksheet.common.GroupsData;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetConfig;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetStatus;
import com.loyo.oa.v2.activityui.worksheet.event.WorksheetChangeEvent;
import com.loyo.oa.v2.common.Event.AppBus;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.SaleCommPopupView;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshExpandableListView;
import com.loyo.oa.v2.point.IWorksheet;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * 【团队线索】
 * Created by yyy on 16/8/19.
 */
public class TeamWorksheetFragment extends BaseGroupsDataFragment implements View.OnClickListener {

    private int statusIndex;  /* 工单状态Index */
    private int typeIndex;    /* 工单类型Index */

    private ArrayList<SaleTeamScreen> statusData = new ArrayList<>();
    private ArrayList<SaleTeamScreen> typeData = new ArrayList<>();
    private ArrayList<WorksheetStatus> statusFilters;
    private ArrayList<WorksheetTemplate> typeFilters;

    private LinearLayout salemy_screen1, salemy_screen2;
    private ImageView salemy_screen1_iv1, salemy_screen1_iv2;
    private TextView tv_tab1, tv_tab2;
    private WindowManager.LayoutParams windowParams;
    private Button btn_add;
    private ViewStub emptyView;

    private Intent mIntent;
    private View mView;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                 /*  状态 */
                case TeamSaleFragment.SALETEAM_SCREEN_TAG2:
                {

                    int newIndex =  (int) msg.getData().get("index");
                    if (statusIndex != newIndex) {
                        statusIndex = newIndex;
                        isPullDown = true;
                        page = 1;
                        tv_tab1.setText(statusFilters.get(statusIndex).getName());
                        showLoading("加载中...");
                        getData();
                    }
                }
                break;

                /* 类型 */
                case TeamSaleFragment.SALETEAM_SCREEN_TAG3:
                {

                    int newIndex =  (int) msg.getData().get("index");
                    if (typeIndex != newIndex) {
                        typeIndex = newIndex;
                        isPullDown = true;
                        page = 1;
                        tv_tab2.setText(typeFilters.get(typeIndex).name);
                        showLoading("加载中...");
                        getData();
                    }
                }

                break;
            }

        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupsData = new GroupsData();
        initFilters();
        AppBus.getInstance().register(this);
    }

    public void onDestroy() {
        super.onDestroy();
        AppBus.getInstance().unregister(this);
    }

    @Subscribe
    public void onWorksheetCreated(WorksheetChangeEvent event) {
        isPullDown = true;
        page = 1;
        getData();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_self_created_worksheet, null);
            initView(mView);
        }
        return mView;
    }

    private void initView(View view) {

        btn_add = (Button) view.findViewById(R.id.btn_add);
        btn_add.setOnTouchListener(Global.GetTouch());
        btn_add.setOnClickListener(this);
        btn_add.setVisibility(View.GONE);
        salemy_screen1 = (LinearLayout) view.findViewById(R.id.salemy_screen1);
        salemy_screen2 = (LinearLayout) view.findViewById(R.id.salemy_screen2);
        salemy_screen1.setOnClickListener(this);
        salemy_screen2.setOnClickListener(this);
        salemy_screen1_iv1 = (ImageView) view.findViewById(R.id.salemy_screen1_iv1);
        salemy_screen1_iv2 = (ImageView) view.findViewById(R.id.salemy_screen1_iv2);
        tv_tab1 = (TextView) view.findViewById(R.id.tv_tab1);
        tv_tab2 = (TextView) view.findViewById(R.id.tv_tab2);

        tv_tab1.setText(statusFilters.get(statusIndex).getName());
        tv_tab2.setText(typeFilters.get(typeIndex).name);

        emptyView = (ViewStub) view.findViewById(R.id.vs_nodata);

        mExpandableListView = (PullToRefreshExpandableListView) mView.findViewById(R.id.expandableListView);
        mExpandableListView.setOnRefreshListener(this);
        //mExpandableListView.setEmptyView(emptyView);

        setupExpandableListView(
                new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        return true;
                    }
                },
                new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                        Worksheet ws = (Worksheet) adapter.getChild(groupPosition, childPosition);
                        String wsId = ws.id != null ? ws.id:"";

                        mIntent = new Intent();
                        mIntent.putExtra(ExtraAndResult.EXTRA_ID, wsId);
                        mIntent.setClass(getActivity(), WorksheetDetailActivity.class);
                        startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);
                        getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);

                        return true;
                    }
                });
        initAdapter();
        expand();

        Utils.btnHideForListView(expandableListView,btn_add);

        showLoading("加载中...");
        getData();
    }

    private void initFilters() {
        statusFilters = new ArrayList<WorksheetStatus>();
        statusFilters.add(WorksheetStatus.Null);
        statusFilters.add(WorksheetStatus.WAITASSIGN);
        statusFilters.add(WorksheetStatus.INPROGRESS);
        statusFilters.add(WorksheetStatus.WAITAPPROVE);
        statusFilters.add(WorksheetStatus.FINISHED);
        statusFilters.add(WorksheetStatus.TEMINATED);

        typeFilters = new ArrayList<WorksheetTemplate>();
        typeFilters.add(WorksheetTemplate.Null);

        ArrayList<WorksheetTemplate> types = WorksheetConfig.getWorksheetTypes(true);
        if (types != null) {
            typeFilters.addAll(types);
        }
        setFilterData();

        statusIndex = 0;
        typeIndex = 0;
    }

    private void setFilterData() {
        for (int i = 0; i < statusFilters.size(); i++) {
            SaleTeamScreen saleTeamScreen = new SaleTeamScreen();
            saleTeamScreen.setName(statusFilters.get(i).getName());
            statusData.add(saleTeamScreen);
        }

        for (int i = 0; i < typeFilters.size(); i++) {
            SaleTeamScreen saleTeamScreen = new SaleTeamScreen();
            saleTeamScreen.setName(typeFilters.get(i).name);
            typeData.add(saleTeamScreen);
        }
    }

    @Override
    public void initAdapter() {
        if (null == adapter) {
            adapter = new WorksheetListAdapter(mActivity, groupsData);
            mExpandableListView.getRefreshableView().setAdapter(adapter);
        }
    }


    @Override
    protected  void getData() {

//        * templateId  工单类型id
//        * status      1:待分派 2:处理中 3:待审核 4:已完成 5:意外中止
//        * keyword     关键字查询
//        * type tab    1:我创建的 2:我分派的
//        * pageIndex
//        * pageSize
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 15);
        if (statusIndex > 0 && statusIndex < statusFilters.size()) {
            map.put("status", statusFilters.get(statusIndex).code);
        }

        if (typeIndex > 0 && typeIndex < typeFilters.size()) {
            map.put("templateId", typeFilters.get(typeIndex).id);
        }

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(IWorksheet.class).getTeamWorksheetList(map, new Callback<WorksheetListWrapper>() {
            @Override
            public void success(WorksheetListWrapper listWrapper, Response response) {
                mExpandableListView.onRefreshComplete();
                HttpErrorCheck.checkResponse("团队工单列表：", response);
                if (isPullDown) {
                    groupsData.clear();
                }
                loadData(listWrapper.data.records);
                mExpandableListView.setEmptyView(emptyView);
            }

            @Override
            public void failure(RetrofitError error) {
                mExpandableListView.onRefreshComplete();
                HttpErrorCheck.checkError(error);
            }
        });

    }

    private void loadData(List<Worksheet> list) {
        Iterator<Worksheet> iterator = list.iterator();
        while (iterator.hasNext()) {
            groupsData.addItem(iterator.next());
        }
        groupsData.sort();
        adapter.notifyDataSetChanged();
        expand();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //新建
            case R.id.btn_add:

                mIntent = new Intent();
                mIntent.setClass(getActivity(), WorksheetAddActivity.class);
                startActivityForResult(mIntent, ExtraAndResult.REQUEST_CODE);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);

                break;

            //时间选择
            case R.id.salemy_screen1: {
                SaleCommPopupView saleCommPopupView = new SaleCommPopupView(getActivity(), mHandler, statusData,
                        SaleOpportunitiesManagerActivity.SCREEN_STAGE, true, statusIndex);
                saleCommPopupView.showAsDropDown(salemy_screen1);
                openPopWindow(salemy_screen1_iv1);
                saleCommPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        closePopupWindow(salemy_screen1_iv1);
                    }
                });

            }
            break;

            //状态
            case R.id.salemy_screen2: {
                SaleCommPopupView saleCommPopupView = new SaleCommPopupView(getActivity(), mHandler, typeData,
                        SaleOpportunitiesManagerActivity.SCREEN_SORT, false, typeIndex);
                saleCommPopupView.showAsDropDown(salemy_screen2);
                openPopWindow(salemy_screen1_iv2);
                saleCommPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        closePopupWindow(salemy_screen1_iv2);
                    }
                });
            }
            break;
        }
    }

    /**
     * PopupWindow关闭 恢复背景正常颜色
     */
    private void closePopupWindow(ImageView view) {
        windowParams = getActivity().getWindow().getAttributes();
        windowParams.alpha = 1f;
        getActivity().getWindow().setAttributes(windowParams);
        view.setBackgroundResource(R.drawable.arrow_down);
    }

    /**
     * PopupWindow打开，背景变暗
     */
    private void openPopWindow(ImageView view) {
        windowParams = getActivity().getWindow().getAttributes();
        windowParams.alpha = 0.9f;
        getActivity().getWindow().setAttributes(windowParams);
        view.setBackgroundResource(R.drawable.arrow_up);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            //新建 删除 编辑 转移客户,回调函数
            case ExtraAndResult.REQUEST_CODE:
                isPullDown = true;
                page = 1;
                break;
        }
    }
}