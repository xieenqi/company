package com.loyo.oa.v2.activityui.followup.fragment;

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
import com.loyo.oa.dropdownmenu.filtermenu.TagMenuModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.followup.adapter.FollowUpListAdapter;
import com.loyo.oa.v2.activityui.followup.model.FollowUpListModel;
import com.loyo.oa.v2.activityui.followup.viewcontrol.FollowUpListView;
import com.loyo.oa.v2.activityui.other.model.Tag;
import com.loyo.oa.v2.activityui.signinnew.adapter.SigninNewListAdapter;
import com.loyo.oa.v2.activityui.signinnew.model.SigninNewListModel;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.point.ISigninNeworFollowUp;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * 【我的跟进】列表
 * Created by yyy on 16/6/1.
 */
public class SelfFollowUpFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2,FollowUpListView {

    private View mView;
    private Button btn_add;
    private ViewStub emptyView;
    private PullToRefreshListView listView;
    private DropDownMenu filterMenu;
    private ArrayList<Tag> mTags;

    private String menuTimeKey = ""; /*时间*/
    private String menuChosKey = ""; /*筛选*/

    private boolean isTopAdd;

    private ArrayList<FollowUpListModel> listModel = new ArrayList<>();
    private PaginationX<FollowUpListModel> mPagination = new PaginationX<>(20);

    private FollowUpListAdapter mAdapter;

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_followup, null);
            initView(mView);
            loadFilterOptions();
        }
        return mView;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = true;
        mPagination.setPageIndex(1);
        getData(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = false;
        mPagination.setPageIndex(mPagination.getPageIndex() + 1);
        getData(true);
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
     * */
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
                switch (menuIndex){

                    /*时间*/
                    case 0:
                        menuTimeKey = selectedModels.get(0).getKey();
                        filterMenu.headerTabBar.setTitleAtPosition(model.getValue(), menuIndex);
                        Toast("key:"+menuTimeKey+" value"+model.getValue());
                        break;

                    /*筛选*/
                    case 1:
                        menuChosKey = model.getKey();
                        Toast("key:"+menuChosKey+" value"+model.getValue());
                        break;

                }
                getData(false);
            }
        });
        getData(false);
    }


    /**
     * 数据绑定
     */
    public void bindData() {
        if (null == mAdapter) {
            mAdapter = new FollowUpListAdapter(getActivity(), listModel, this);
            listView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取Self列表数据
     */
    private void getData(boolean isPullOrDown) {
        if(!isPullOrDown){
            showLoading("");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", "");//我的传id,团队则空着
        map.put("xpath", "");
        map.put("timeType", 5);//时间查询
        map.put("method", 0); //跟进类型0:全部 1:线索 2:客户
        map.put("typeId","");
        map.put("split",true);
        map.put("pageIndex", mPagination.getPageIndex());
        map.put("pageSize", isTopAdd ? listModel.size() >= 20 ? listModel.size() : 20 : 20);
        LogUtil.dee("发送数据:" + MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninNeworFollowUp.class).selfFollowUp(map, new RCallback<BaseBeanT<PaginationX<FollowUpListModel>>>() {
            @Override
            public void success(BaseBeanT<PaginationX<FollowUpListModel>> paginationX, Response response) {
                HttpErrorCheck.checkResponse("我的拜访", response);
                listView.onRefreshComplete();
                if (isTopAdd) {
                    listModel.clear();
                }
                mPagination = paginationX.data;
                listModel.addAll(paginationX.data.getRecords());
                bindData();
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                listView.onRefreshComplete();
                super.failure(error);
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


    @Override
    public void commentEmbl(int position) {

    }

    @Override
    public void deleteCommentEmbl(String id) {

    }
}
