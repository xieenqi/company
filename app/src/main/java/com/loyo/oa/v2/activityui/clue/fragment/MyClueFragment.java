package com.loyo.oa.v2.activityui.clue.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.Button;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.ClueStatus;
import com.loyo.oa.dropdownmenu.filtermenu.ClueStatusFilterModel;
import com.loyo.oa.dropdownmenu.filtermenu.TimeFilterModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.ClueAddActivity;
import com.loyo.oa.v2.activityui.clue.ClueDetailActivity;
import com.loyo.oa.v2.activityui.clue.adapter.MyClueAdapter;
import com.loyo.oa.v2.activityui.clue.api.ClueService;
import com.loyo.oa.v2.activityui.clue.model.ClueList;
import com.loyo.oa.v2.activityui.clue.model.ClueListItem;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.point.IClue;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【我的线索】
 * Created by yyy on 16/8/19.
 */
public class MyClueFragment extends BaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    /* Data */
    private int page = 1;     /*翻页页数*/
    private boolean isPullDown = true;
    private String field = "";
    private String order = "";
    private String statusKey = "";
    private ArrayList<ClueListItem> listData = new ArrayList<>();
    private Intent mIntent;

    /* View */
    private Button btn_add;
    private LoadingLayout ll_loading;
    private PullToRefreshListView lv_list;
    private MyClueAdapter adapter;
    private DropDownMenu filterMenu;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_my_clue, null);
            initView(mView);
            loadFilterOptions();
        }
        return mView;
    }

    private void loadFilterOptions() {
        statusKey = ClueStatus.All.getKey();
        List<FilterModel> options = new ArrayList<>();
        options.add(TimeFilterModel.getFilterModel());
        options.add(ClueStatusFilterModel.getFilterModel());
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

                if (menuIndex == 0) { // TimeFilterModel
                    String[] keys = key.split(" ");
                    field = keys[0];
                    order = keys[1];
                } else if (menuIndex == 1) { // ClueStatusFilterModel
                    statusKey = key;
                }
                ll_loading.setStatus(LoadingLayout.Loading);
                isPullDown = true;
                page = 1;
                getData();
            }
        });
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
        lv_list = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        lv_list.setMode(PullToRefreshBase.Mode.BOTH);
        lv_list.setOnRefreshListener(this);
        /*列表监听*/
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mIntent = new Intent();
                mIntent.putExtra(ExtraAndResult.IS_TEAM, false);
                mIntent.putExtra(ExtraAndResult.EXTRA_ID, /* 线索id */listData.get(position - 1).id);
                mIntent.setClass(mActivity, ClueDetailActivity.class);
                startActivityForResult(mIntent, mActivity.RESULT_FIRST_USER);
                mActivity.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);

            }
        });
        adapter = new MyClueAdapter(mActivity);
        lv_list.setAdapter(adapter);
        getData();
        Utils.btnHideForListView(lv_list.getRefreshableView(), btn_add);
        filterMenu = (DropDownMenu) view.findViewById(R.id.drop_down_menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //新建
            case R.id.btn_add:

                mIntent = new Intent();
                mIntent.setClass(mActivity, ClueAddActivity.class);
                startActivityForResult(mIntent, mActivity.RESULT_FIRST_USER);
                mActivity.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);

                break;
            default:
                break;
        }
    }

    /**
     * 请求列表数据
     */
    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 15);
        map.put("field", field);
        map.put("order", order);
        map.put("status", statusKey);
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
//                create(IClue.class).getMyCluelist(map, new Callback<ClueList>() {
//            @Override
//            public void success(ClueList clueList, Response response) {
//                lv_list.onRefreshComplete();
//                HttpErrorCheck.checkResponse("我的线索列表：", response, ll_loading);
//                try {
//                    if (!isPullDown) {
//                        listData.addAll(clueList.data.records);
//                    } else {
//                        if (clueList.data.records == null)
//                            ll_loading.setStatus(LoadingLayout.Empty);
//                        listData = clueList.data.records;
//                    }
//                    adapter.setData(listData);
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                lv_list.onRefreshComplete();
//                HttpErrorCheck.checkError(error, ll_loading, page == 1 ? true : false);
//            }
//        });

        //新网络模块
        ClueService.getMyClueList(map).subscribe(new DefaultLoyoSubscriber<ClueList>() {
            @Override
            public void onError(Throwable e) {
                /* 重写父类方法，不调用super */
                @LoyoErrorChecker.CheckType
                int type =page != 1 ?LoyoErrorChecker.TOAST : LoyoErrorChecker.LOADING_LAYOUT;
                LoyoErrorChecker.checkLoyoError(e, type, ll_loading);
                lv_list.onRefreshComplete();
            }

            @Override
            public void onNext(ClueList clueList) {
                ll_loading.setStatus(LoadingLayout.Success);
                lv_list.onRefreshComplete();
                try {
                    if (!isPullDown) {
                        if(null!=clueList.data.records)listData.addAll(clueList.data.records);
                    } else {
                        if (clueList.data.records == null)
                            ll_loading.setStatus(LoadingLayout.Empty);
                        listData = clueList.data.records;
                    }
                    adapter.setData(listData);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
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
