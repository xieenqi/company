package com.loyo.oa.v2.activityui.signinnew.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;

import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.DynamicFilterTimeModel;
import com.loyo.oa.dropdownmenu.filtermenu.OrganizationFilterModel;
import com.loyo.oa.dropdownmenu.filtermenu.SigninFilterKindModel;
import com.loyo.oa.dropdownmenu.filtermenu.SigninFilterSortModel;
import com.loyo.oa.dropdownmenu.filtermenu.TagMenuModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.model.Tag;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * 【团队拜访】列表
 * Created by yyy on 16/11/10.
 */
public class TeamSigninNewFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2 {

    private View mView;
    private Button btn_add;
    private ViewStub emptyView;
    private PullToRefreshListView listView;
    private PaginationX<Customer> mPagination = new PaginationX<>(20);
    private ArrayList<Tag> mTags;
    private DropDownMenu filterMenu;

    private String field = "";
    private String order = "";
    private String tagsParams = "";

    private Permission permission;


    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_new_signin, null);
            initView(mView);
            loadFilterOptions();
        }
        return mView;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPagination.setPageIndex(1);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPagination.setPageIndex(mPagination.getPageIndex() + 1);
    }

    public void initView(View view) {
        mTags = (ArrayList<Tag>) getArguments().getSerializable("tag");
        permission = (Permission) getArguments().getSerializable("permission");

        btn_add = (Button) view.findViewById(R.id.btn_add);
        emptyView = (ViewStub) mView.findViewById(R.id.vs_nodata);
        filterMenu = (DropDownMenu) view.findViewById(R.id.drop_down_menu);

        listView = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        listView.setEmptyView(emptyView);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);

        btn_add.setOnClickListener(click);
        btn_add.setOnTouchListener(Global.GetTouch());

        Utils.btnHideForListView(listView.getRefreshableView(), btn_add);
    }

    /**
     * 加载顶部菜单
     * */
    private void loadFilterOptions() {

        List<DBDepartment> depts = new ArrayList<>();
        String title = "人员";
        //为超管或权限为全公司 展示全公司成员
        if (permission != null && permission.dataRange == Permission.COMPANY) {
            depts.addAll(OrganizationManager.shareManager().allDepartments());
            //title = "全公司";
            title = "人员";
        }
        //权限为部门 展示我的部门
        else if (permission != null && permission.dataRange == Permission.TEAM) {
            depts.addAll(OrganizationManager.shareManager().currentUserDepartments());
            //title = "本部门";
            title = "人员";
        }
        else {
            //title = "我";
            title = "人员";
            depts.add(OrganizationFilterModel.selfDepartment());
        }

        List<FilterModel> options = new ArrayList<>();
        options.add(DynamicFilterTimeModel.getFilterModel());     //时间
        options.add(SigninFilterSortModel.getFilterModel());      //排序
        options.add(new OrganizationFilterModel(depts, title));   //人员
        DefaultMenuAdapter adapter = new DefaultMenuAdapter(getContext(), options);
        filterMenu.setMenuAdapter(adapter);
        adapter.setCallback(new OnMenuModelsSelected() {
            @Override
            public void onMenuModelsSelected(int menuIndex, List<MenuModel> selectedModels, Object userInfo) {
                filterMenu.close();
                if (menuIndex == 0) {
                    MenuModel model = selectedModels.get(0);
                    String key = model.getKey();
                    String value = model.getValue();
                    filterMenu.headerTabBar.setTitleAtPosition(value, menuIndex);
                    Toast("key:"+key+" value"+value);
                }
                else if (menuIndex == 1) {
                    tagsParams = userInfo.toString();
                    Toast("tagsParams:"+tagsParams);
                }
                /*isPullUp = false;
                page = 1;
                getData();*/
            }
        });
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                //新建跟进
                case R.id.btn_add:

                    break;
            }
        }
    };
}
