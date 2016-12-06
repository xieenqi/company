package com.loyo.oa.v2.activityui.worksheet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.OrganizationFilterModel;
import com.loyo.oa.dropdownmenu.filtermenu.WorksheetStatusMenuModel;
import com.loyo.oa.dropdownmenu.filtermenu.WorksheetTemplateMenuModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.pulltorefresh.PullToRefreshExpandableListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.WorksheetAddActivity;
import com.loyo.oa.v2.activityui.worksheet.WorksheetDetailActivity;
import com.loyo.oa.v2.activityui.worksheet.adapter.TeamWorksheetsAdapter;
import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetListWrapper;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetTemplate;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetConfig;
import com.loyo.oa.v2.activityui.worksheet.event.WorksheetChangeEvent;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.GroupsData;
import com.loyo.oa.v2.common.fragment.BaseGroupsDataFragment;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.Permission;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.point.IWorksheet;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;

import org.greenrobot.eventbus.Subscribe;

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

    private String xpath = "";     /* 查询部门xpath */
    private String userId = "";    /* 查询用户id */
    private String statusParam = "";  /* 工单状态Param */
    private String typeParam = "";    /* 工单类型Param */

    private Button btn_add;
    private ViewStub emptyView;
    private View mView;
    private DropDownMenu filterMenu;

    private Intent mIntent;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.dee("onCreate:");

    }


    @Subscribe
    public void onWorksheetCreated(WorksheetChangeEvent event) {
        isPullDown = true;
        page = 1;
        getData();
    }

    public void refresh() {
        isPullDown = true;
        page = 1;
        showLoading("");
        getData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_team_worksheet, null);
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
        btn_add.setVisibility(View.GONE);
        emptyView = (ViewStub) view.findViewById(R.id.vs_nodata);

        mExpandableListView = (PullToRefreshExpandableListView) mView.findViewById(R.id.expandableListView);
        mExpandableListView.setOnRefreshListener(this);
        mExpandableListView.setEmptyView(emptyView);

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
                        String wsId = ws.id != null ? ws.id : "";

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

        Utils.btnHideForListView(expandableListView, btn_add);
        filterMenu = (DropDownMenu) view.findViewById(R.id.drop_down_menu);

        showLoading("加载中...");
        getData();
    }

    private void loadFilterOptions() {

        List<DBDepartment> depts = new ArrayList<>();
        String title = "部门";
        //为超管或权限为全公司 展示全公司成员
        if (PermissionManager.getInstance().dataRange(BusinessOperation.WORKSHEET_MANAGEMENT)
                == Permission.COMPANY) {
            depts.addAll(OrganizationManager.shareManager().allDepartments());
            title = "全公司";
        }
        //权限为部门 展示我的部门
        else if (PermissionManager.getInstance().dataRange(BusinessOperation.WORKSHEET_MANAGEMENT)
                == Permission.TEAM) {
            depts.addAll(OrganizationManager.shareManager().currentUserDepartments());
            title = "本部门";
        }
        else {
            title = "我";
            depts.add(OrganizationFilterModel.selfDepartment());
        }

        final ArrayList<WorksheetTemplate> types = WorksheetConfig.getWorksheetTypes(true);
        List<FilterModel> options = new ArrayList<>();
        options.add(new OrganizationFilterModel(depts, title));
        options.add(WorksheetStatusMenuModel.getFilterModel());
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
                    // TODO:
                    if (model.getClass().equals(OrganizationFilterModel.DepartmentMenuModel.class)) {
                        xpath = model.getKey();
                        userId = "";
                    }
                    else if (model.getClass().equals(OrganizationFilterModel.UserMenuModel.class)) {
                        xpath = "";
                        userId = model.getKey();
                    }
                }
                else if (menuIndex == 1) {
                    statusParam = key;
                }
                else if (menuIndex == 2) {
                    typeParam = key;
                }
                refresh();
            }
        });
    }

    @Override
    public void initAdapter() {
        if (null == adapter) {
            adapter = new TeamWorksheetsAdapter(mActivity, groupsData);
            mExpandableListView.getRefreshableView().setAdapter(adapter);
        }
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

        if (xpath != null && xpath.length() > 0) {
            map.put("xpath", xpath);
        }
        if (userId != null && userId.length() > 0) {
            map.put("userId", userId);
        }

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(IWorksheet.class).getTeamWorksheetList(map, new Callback<WorksheetListWrapper>() {
            @Override
            public void success(WorksheetListWrapper listWrapper, Response response) {
                mExpandableListView.onRefreshComplete();

                if (isPullDown) {
                    groupsData.clear();
                }
                loadData(listWrapper.data.records);
                HttpErrorCheck.checkResponse("团队工单列表：", response);
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
        LogUtil.dee("size:" + groupsData.size());
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