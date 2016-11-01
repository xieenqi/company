package com.loyo.oa.v2.activityui.clue.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.ClueDetailActivity;
import com.loyo.oa.v2.activityui.clue.adapter.TeamClueAdapter;
import com.loyo.oa.v2.activityui.clue.bean.ClueList;
import com.loyo.oa.v2.activityui.clue.bean.ClueListItem;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.filtermenu.ClueStatus;
import com.loyo.oa.v2.filtermenu.ClueStatusFilterModel;
import com.loyo.oa.v2.filtermenu.OrganizationFilterModel;
import com.loyo.oa.v2.filtermenu.TimeFilterModel;
import com.loyo.oa.v2.point.IClue;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * 【团队线索】
 * Created by yyy on 16/8/19.
 */
public class TeamClueFragment extends BaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private int page = 1;
    private boolean isPullDown = true, isKind;
    private String xPath = "";
    private String userId = "";
    private String field = "";
    private String order = "";
    private String statusKey = "";

    private ArrayList<ClueListItem> listData = new ArrayList<>();
    private Permission permission;
    private ViewStub emptyView;
    private PullToRefreshListView lv_list;
    private TeamClueAdapter adapter;
    private View mView;
    private DropDownMenu filterMenu;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_team_clue, null);
            initView(mView);
            loadFilterOptions();
        }
        return mView;
    }

    private void initView(View view) {
        permission = (Permission) getArguments().getSerializable("permission");
        emptyView = (ViewStub) view.findViewById(R.id.vs_nodata);
        lv_list = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        lv_list.setMode(PullToRefreshBase.Mode.BOTH);
        lv_list.setOnRefreshListener(this);
        lv_list.setEmptyView(emptyView);

        /*列表监听*/
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(ExtraAndResult.IS_TEAM, false);
                intent.putExtra(ExtraAndResult.EXTRA_ID, /* 线索id */listData.get(position - 1).id);
                intent.setClass(getActivity(), ClueDetailActivity.class);
                startActivityForResult(intent, getActivity().RESULT_FIRST_USER);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);

            }
        });
        adapter = new TeamClueAdapter(getActivity());
        lv_list.setAdapter(adapter);
        filterMenu = (DropDownMenu) view.findViewById(R.id.drop_down_menu);
        getData();
    }

    private void loadFilterOptions() {

        statusKey = ClueStatus.All.getKey();

        List<DBDepartment> depts = new ArrayList<>();
        String title = "部门";
        //为超管或权限为全公司 展示全公司成员
        if (permission != null && permission.dataRange == Permission.COMPANY) {
            depts.addAll(OrganizationManager.shareManager().allDepartments());
            title = "全公司";
        }
        //权限为部门 展示我的部门
        else if (permission != null && permission.dataRange == Permission.TEAM) {
            depts.addAll(OrganizationManager.shareManager().currentUserDepartments());
            title = "本部门";
        }
        else {
            title = "我";
            depts.add(OrganizationFilterModel.selfDepartment());
        }

        List<FilterModel> options = new ArrayList<>();
        options.add(new OrganizationFilterModel(depts, title));
        options.add(TimeFilterModel.getFilterModel());
        options.add(ClueStatusFilterModel.getFilterModel());
        DefaultMenuAdapter adapter = new DefaultMenuAdapter(getContext(), options);
        filterMenu.setMenuAdapter(adapter);
        adapter.setCallback(new OnMenuModelsSelected() {
            @Override
            public void onMenuModelsSelected(int menuIndex, List<MenuModel> selectedModels) {
                filterMenu.close();
                MenuModel model = selectedModels.get(0);
                String key = model.getKey();
                String value = model.getValue();
                filterMenu.headerTabBar.setTitleAtPosition(value, menuIndex);

                if (menuIndex == 0) {
                    // TODO:
                    if (model.getClass().equals(OrganizationFilterModel.DepartmentMenuModel.class)) {
                        xPath = model.getKey();
                        userId = "";
                    }
                    else if (model.getClass().equals(OrganizationFilterModel.UserMenuModel.class)) {
                        xPath = "";
                        userId = model.getKey();
                    }
                }
                else if (menuIndex == 1) { // TimeFilterModel
                    String[] keys = key.split(" ");
                    field = keys[0];
                    order = keys[1];
                }
                else if (menuIndex == 2) { // ClueStatusFilterModel
                    statusKey = key;
                }
                getData();
            }
        });
    }

    public void wersi() {
        //为超管或权限为全公司 展示全公司成员
        if (permission != null && permission.dataRange == Permission.COMPANY) {
            setUser(OrganizationManager.shareManager().allDepartments());
        }
        //权限为部门 展示我的部门
        else if (permission != null && permission.dataRange == Permission.TEAM) {
            setUser(OrganizationManager.shareManager().currentUserDepartments());
        }
        //权限为个人 展示自己
        else if (permission != null && permission.dataRange == Permission.PERSONAL) {
        }
    }

    /**
     * 组装部门格式
     */
    private void setUser(List<DBDepartment> values) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    /**
     * PopupWindow关闭 恢复背景正常颜色
     */
    private void closePopupWindow(ImageView view) {
        view.setBackgroundResource(R.drawable.arrow_down);
    }

    /**
     * PopupWindow打开，背景变暗
     */
    private void openPopWindow(ImageView view) {
        view.setBackgroundResource(R.drawable.arrow_up);
    }

    /**
     * 获取团队数据列表
     */
    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 15);
        map.put("status", statusKey);
        map.put("field", field);
        map.put("order", order);
        map.put("xpath", xPath);
        map.put("userId", userId);
        LogUtil.dee("发送数据:" + MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(IClue.class).getTeamCluelist(map, new Callback<ClueList>() {
            @Override
            public void success(ClueList clueList, Response response) {
                lv_list.onRefreshComplete();
                HttpErrorCheck.checkResponse("我的线索列表：", response);
                if (null == clueList.data || clueList.data.records == null) {
                    if (isPullDown && listData.size() > 0) {
                        listData.clear();
                    } else {
                        Toast("没有相关数据");
                        return;
                    }
                } else {
                    if (isPullDown) {
                        listData.clear();
                    }
                    listData.addAll(clueList.data.records);
                }
                try {
//                    if (isPullDown) {
//                        listData.clear();
//                    }
//                    if (null == clueList.data.records) {
//                        listData.clear();
//                        Toast("没有相关数据");
//                    } else {
//                        listData.addAll(clueList.data.records);
//                    }
                    adapter.setData(listData);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                lv_list.onRefreshComplete();
                HttpErrorCheck.checkError(error);
            }
        });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            //新建 删除 编辑 转移客户,回调函数
            case ExtraAndResult.REQUEST_CODE:
                isPullDown = true;
                page = 1;
                getData();
                break;
        }
    }
}
