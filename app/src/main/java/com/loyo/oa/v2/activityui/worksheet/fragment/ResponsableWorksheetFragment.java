package com.loyo.oa.v2.activityui.worksheet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.UmengAnalytics;
import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.WSEventStatusMenuModel;
import com.loyo.oa.dropdownmenu.filtermenu.WorksheetTemplateMenuModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshExpandableListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.WorksheetAddActivity;
import com.loyo.oa.v2.activityui.worksheet.WorksheetDetailActivity;
import com.loyo.oa.v2.activityui.worksheet.WorksheetSubmitActivity;
import com.loyo.oa.v2.activityui.worksheet.adapter.ResponsableWorksheetsAdapter;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetEvent;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetTemplate;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetConfig;
import com.loyo.oa.v2.activityui.worksheet.event.WorksheetChangeEvent;
import com.loyo.oa.v2.activityui.worksheet.event.WorksheetEventChangeEvent;
import com.loyo.oa.v2.activityui.worksheet.event.WorksheetEventFinishAction;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.GroupsData;
import com.loyo.oa.v2.common.fragment.BaseGroupsDataFragment;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.worksheet.api.WorksheetService;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 【我负责的工单】
 */
public class ResponsableWorksheetFragment extends BaseGroupsDataFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private Button btn_add;
    private DropDownMenu filterMenu;

    private String statusParam = "";  /* 工单状态Param */
    private String typeParam = "";    /* 工单类型Param */

    private Intent mIntent;
    private View mView;
    private LoadingLayout ll_loading;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void finishEvent(WorksheetEvent event) {
        Bundle bd = new Bundle();
        bd.putString(ExtraAndResult.CC_USER_ID, event.id /*事件id*/);
        bd.putInt(ExtraAndResult.EXTRA_DATA, 0x02 /*提交完成:0x02,打回重做0x01*/);
        app.startActivity(getActivity(), WorksheetSubmitActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bd);
    }

    @Subscribe
    public void onWorkSheetEventFinishAction(WorksheetEventFinishAction action) {
        if (action.data != null && action.eventCode == WorksheetEventFinishAction.FROM_RESPONSABLE_LIST) {
            finishEvent(action.data);
        }
    }

    /* 工单信息变更 */
    @Subscribe
    public void onWorksheetCreated(WorksheetChangeEvent event) {
        isPullDown = true;
        page = 1;
        getData();
    }

    /* 工单事件信息变更 */
    @Subscribe
    public void onWorksheetEventUpdated(WorksheetEventChangeEvent event) {
        isPullDown = true;
        page = 1;
        getData();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_self_created_worksheet, null);
            groupsData = new GroupsData();
            initView(mView);
            loadFilterOptions();
        }
        return mView;
    }

    private void initView(View view) {
        btn_add = (Button) view.findViewById(R.id.btn_add);
        btn_add.setOnTouchListener(Global.GetTouch());
        btn_add.setOnClickListener(this);
        ll_loading = (LoadingLayout) view.findViewById(R.id.ll_loading);
        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                getData();
            }
        });
        mExpandableListView = (PullToRefreshExpandableListView) mView.findViewById(R.id.expandableListView);
        mExpandableListView.setOnRefreshListener(this);
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
                        mIntent = new Intent();

                        WorksheetEvent wse = (WorksheetEvent) groupsData.get(groupPosition, childPosition);
                        String wsId = wse.wsId != null ? wse.wsId : wse.workSheetId;

                        mIntent.putExtra(ExtraAndResult.EXTRA_ID, wsId);
                        mIntent.setClass(getActivity(), WorksheetDetailActivity.class);
                        startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);
                        getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);

                        return true;
                    }
                });
        initAdapter();
        expand();

        Utils.btnHideForListView(expandableListView, btn_add);

        filterMenu = (DropDownMenu) view.findViewById(R.id.drop_down_menu);
        getData();
    }

    private void loadFilterOptions() {
        final ArrayList<WorksheetTemplate> types = WorksheetConfig.getWorksheetTypes(true);
        List<FilterModel> options = new ArrayList<>();
        options.add(WSEventStatusMenuModel.getFilterModel());
        options.add(WorksheetTemplateMenuModel.getFilterModel(types));
        DefaultMenuAdapter adapter = new DefaultMenuAdapter(getContext(), options);
        filterMenu.setMenuAdapter(adapter);
        adapter.setCallback(new OnMenuModelsSelected() {
            @Override
            public void onMenuModelsSelected(int menuIndex, List<MenuModel> selectedModels, Object userInfo) {
                filterMenu.close();
                MenuModel model = selectedModels.get(0);
                String key = model.getKey();
                String value = model.getValue();
                filterMenu.headerTabBar.setTitleAtPosition(value, menuIndex);

                if (menuIndex == 0) {
                    statusParam = key;
                } else if (menuIndex == 1) {
                    typeParam = key;
                }
                refresh();
            }
        });
    }

    @Override
    public void initAdapter() {
        if (null == adapter) {
            adapter = new ResponsableWorksheetsAdapter(mActivity, groupsData, WorksheetEventFinishAction.FROM_RESPONSABLE_LIST);
            mExpandableListView.getRefreshableView().setAdapter(adapter);
        }
    }

    public void refresh() {
        isPullDown = true;
        page = 1;
        ll_loading.setStatus(LoadingLayout.Loading);
        getData();
    }


    @Override
    protected void getData() {

//        * templateId  工单类型id
//        * status      1:待分派 2:处理中 3:待审核 4:已完成 5:意外中止
//        * keyword     关键字查询
//        * type tab    1:我创建的 2:我分派的
//        * pageIndex
//        * pageSize
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 15);
        map.put("status", statusParam);
        map.put("templateId", typeParam);
        WorksheetService.getResponsableWorksheetList(map)
                .subscribe(new DefaultLoyoSubscriber<PaginationX<WorksheetEvent>>() {
                    @Override
                    public void onError(Throwable e) {
                        @LoyoErrorChecker.CheckType int type =
                                groupsData.size() > 0 ? LoyoErrorChecker.TOAST : LoyoErrorChecker.LOADING_LAYOUT;
                        LoyoErrorChecker.checkLoyoError(e, type, ll_loading);
                        mExpandableListView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(PaginationX<WorksheetEvent> x) {
                        mExpandableListView.onRefreshComplete();

                        if (isPullDown) {
                            groupsData.clear();
                        }
                        if (isPullDown && PaginationX.isEmpty(x) && groupsData.size() == 0) {
                            ll_loading.setStatus(LoadingLayout.Empty);
                        } else if (PaginationX.isEmpty(x)) {
                            Toast("没有更多数据了");
                            ll_loading.setStatus(LoadingLayout.Success);
                        } else {
                            ll_loading.setStatus(LoadingLayout.Success);
                        }

                        loadData(x != null ? x.records : new ArrayList<WorksheetEvent>());
                    }
                });

    }

    private void loadData(List<WorksheetEvent> list) {
        Iterator<WorksheetEvent> iterator = list.iterator();
        while (iterator.hasNext()) {
            groupsData.addItem(iterator.next());
        }
        groupsData.sort();
        try {
            adapter.notifyDataSetChanged();
            expand();
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //新建
            case R.id.btn_add:
                mIntent = new Intent();
                mIntent.setClass(getActivity(), WorksheetAddActivity.class);
                startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                UmengAnalytics.umengSend(mActivity, UmengAnalytics.createWorkSheet);
                break;
        }
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
