package com.loyo.oa.v2.activityui.followup.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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
import com.loyo.oa.dropdownmenu.filtermenu.TagMenuModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.DynamicSelectActivity;
import com.loyo.oa.v2.activityui.followup.FollowUpDetailsActivity;
import com.loyo.oa.v2.activityui.other.model.Tag;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * 【我的跟进】列表
 * Created by yyy on 16/6/1.
 */
public class SelfFollowUpFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2 {

    private View mView;
    private Button btn_add;
    private ViewStub emptyView;
    private PullToRefreshListView listView;
    private PaginationX<Customer> mPagination = new PaginationX<>(20);
    private DropDownMenu filterMenu;
    private ArrayList<Tag> mTags;

    private String menuTimeKey = ""; /*时间*/
    private String menuChosKey = ""; /*筛选*/

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_dynamic, null);
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
     */
    private void loadFilterOptions() {
        List<FilterModel> options = new ArrayList<>();
        options.add(DynamicFilterTimeModel.getFilterModel());     //时间
        options.add(TagMenuModel.getTagFilterModel(mTags));       //筛选
        DefaultMenuAdapter adapter = new DefaultMenuAdapter(getContext(), options);
        filterMenu.setMenuAdapter(adapter);
        adapter.setCallback(new OnMenuModelsSelected() {
            @Override
            public void onMenuModelsSelected(int menuIndex, List<MenuModel> selectedModels, Object userInfo) {
                filterMenu.close();
                MenuModel model = selectedModels.get(0);
                switch (menuIndex) {

                    /*时间*/
                    case 0:
                        menuTimeKey = selectedModels.get(0).getKey();
                        filterMenu.headerTabBar.setTitleAtPosition(model.getValue(), menuIndex);
                        Toast("key:" + menuTimeKey + " value" + model.getValue());
                        break;

                    /*筛选*/
                    case 1:
                        menuChosKey = model.getKey();
                        Toast("key:" + menuChosKey + " value" + model.getValue());
                        break;

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
                    startActivityForResult(new Intent(getActivity(), DynamicSelectActivity.class), Activity.RESULT_FIRST_USER);
                    getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                    break;
            }
        }
    };
}
