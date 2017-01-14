package com.loyo.oa.v2.activityui.sale.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.UmengAnalytics;
import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.CommonSortTypeMenuModel;
import com.loyo.oa.dropdownmenu.filtermenu.SaleStageMenuModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.hud.toast.LoyoToast;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.ClueManagerActivity;
import com.loyo.oa.v2.activityui.sale.AddMySaleActivity;
import com.loyo.oa.v2.activityui.sale.SaleDetailsActivity;
import com.loyo.oa.v2.activityui.sale.adapter.AdapterMySaleList;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.activityui.sale.bean.SaleStage;
import com.loyo.oa.v2.activityui.sale.contract.MySaleFrgmentContract;
import com.loyo.oa.v2.activityui.sale.model.SaleStageConfig;
import com.loyo.oa.v2.activityui.sale.presenter.MySaleFrgmentPresenterImpl;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 【销售机会】我的列表
 * Created by yyy on 16/5/17.
 */
public class MySaleFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2, MySaleFrgmentContract.View {

    private View mView;
    private Button btn_add;
    private PullToRefreshListView listView;
    private AdapterMySaleList adapter;
    private DropDownMenu filterMenu;
    private ArrayList<SaleStage> mSaleStages;
    private MySaleFrgmentPresenterImpl mPersenter;
    private LoadingLayout ll_loading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_my_sale, null);
            initView(mView);
            loadFilterOptions();
            mPersenter = new MySaleFrgmentPresenterImpl(this);
        }
        mPersenter.getData();
        return mView;
    }

    private void initView(View view) {
//        mSaleStages = (ArrayList<SaleStage>) getArguments().get("stage");
        mSaleStages = SaleStageConfig.getSaleStage(true);
        listView = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        btn_add = (Button) view.findViewById(R.id.btn_add);
        btn_add.setOnTouchListener(Global.GetTouch());
        btn_add.setOnClickListener(click);
        ll_loading = (LoadingLayout) view.findViewById(R.id.ll_loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                mPersenter.getData();
            }
        });
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mIntent = new Intent();
                mIntent.putExtra(ExtraAndResult.IS_TEAM, false);
                mIntent.putExtra("id", adapter.getData().get(position - 1).getId());
                mIntent.setClass(getActivity(), SaleDetailsActivity.class);
                startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);
                mActivity.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
            }
        });
        filterMenu = (DropDownMenu) view.findViewById(R.id.drop_down_menu);
        Utils.btnHideForListView(listView.getRefreshableView(), btn_add);
    }

    private void loadFilterOptions() {
        List<FilterModel> options = new ArrayList<>();
        options.add(SaleStageMenuModel.getStageFilterModel(mSaleStages));
        options.add(CommonSortTypeMenuModel.getFilterModel());
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
                String stageId = "", sortType = "";
                if (menuIndex == 0) { // SaleStage
                    stageId = key;
                    UmengAnalytics.umengSend(mActivity, UmengAnalytics.stageChanceMy);
                } else if (menuIndex == 1) { // 排序
                    sortType = key;
                    UmengAnalytics.umengSend(mActivity, UmengAnalytics.rankChanceMy);
                }
                mPersenter.getScreenData(stageId, sortType);
            }
        });
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPersenter.pullDown();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPersenter.pullUp();
    }


    /**
     * 绑定数据
     */
    public void bindData(ArrayList<SaleRecord> recordData) {
        if (null == adapter) {
            adapter = new AdapterMySaleList(getActivity());
            listView.setAdapter(adapter);
            adapter.setData(recordData);
        } else {
            adapter.setData(recordData);
        }
    }

    @Override
    public void backToTop() {
        if(null!=listView){
            listView.getRefreshableView().setSelection(0);
        }
    }

    @Override
    public LoadingLayout getLoadingUI() {
        return ll_loading;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            //删除后 刷新列表
            case ExtraAndResult.REQUEST_CODE_SOURCE:
                mPersenter.getData();
                break;
            //新增后 刷新列表
            case ExtraAndResult.REQUEST_CODE_STAGE:
                mPersenter.getData();
                break;
        }
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                //新建机会
                case R.id.btn_add:
                    Intent mIntent = new Intent();
                    mIntent.setClass(getActivity(), AddMySaleActivity.class);
                    startActivityForResult(mIntent, mActivity.RESULT_FIRST_USER);
                    mActivity.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                    UmengAnalytics.umengSend(mActivity, UmengAnalytics.createChanceMy);
                    break;
            }
        }
    };

    @Override
    public LoyoProgressHUD showStatusProgress() {
        showCommitLoading();
        return hud;
    }

    @Override
    public LoyoProgressHUD showProgress(String message) {
        showLoading2(message);
        return hud;
    }

    @Override
    public void hideProgress() {
        cancelLoading2();
    }

    @Override
    public void showMsg(String message) {
        LoyoToast.info(getActivity(), message);
    }

    @Override
    public void refreshComplete() {
        listView.onRefreshComplete();
    }
}
