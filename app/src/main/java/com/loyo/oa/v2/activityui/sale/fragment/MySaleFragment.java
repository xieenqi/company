package com.loyo.oa.v2.activityui.sale.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.CommonSortTypeMenuModel;
import com.loyo.oa.dropdownmenu.filtermenu.SaleStageMenuModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.model.SaleStage;
import com.loyo.oa.v2.activityui.sale.AddMySaleActivity;
import com.loyo.oa.v2.activityui.sale.SaleDetailsActivity;
import com.loyo.oa.v2.activityui.sale.adapter.AdapterMySaleList;
import com.loyo.oa.v2.activityui.sale.bean.SaleList;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.point.ISale;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【销售机会】我的列表
 * Created by yyy on 16/5/17.
 */
public class MySaleFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2 {

    private View mView;
    private Button btn_add;
    private Intent mIntent;
    private RelativeLayout re_nodata;
    private ViewStub emptyView;
    private PullToRefreshListView listView;
    private AdapterMySaleList adapter;
    private DropDownMenu filterMenu;


    private ArrayList<SaleStage> mSaleStages;
    private ArrayList<SaleRecord> recordData = new ArrayList<>();
    private String stageId = "";
    private String sortType = "";
    private int requestPage = 1;
    private boolean isPull = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_my_sale, null);
            initView(mView);
            loadFilterOptions();
        }
        getData();
        return mView;
    }

    private void initView(View view) {

        mSaleStages = (ArrayList<SaleStage>) getArguments().get("stage");
        listView = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        btn_add = (Button) view.findViewById(R.id.btn_add);
        re_nodata = (RelativeLayout) view.findViewById(R.id.re_nodata);
        emptyView = (ViewStub) view.findViewById(R.id.vs_nodata);

        btn_add.setOnTouchListener(Global.GetTouch());
        btn_add.setOnClickListener(click);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        listView.setEmptyView(emptyView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mIntent = new Intent();
                mIntent.putExtra(ExtraAndResult.IS_TEAM, false);
                mIntent.putExtra("id", adapter.getData().get(position - 1).getId());
                mIntent.setClass(getActivity(), SaleDetailsActivity.class);
                startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
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

                if (menuIndex == 0) { // SaleStage
                    stageId = key;
                }
                else if (menuIndex == 1) { // 排序
                    sortType = key;
                }
                requestPage = 1;
                isPull = false;
                getData();
            }
        });
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        requestPage = 1;
        isPull = false;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isPull = true;
        requestPage++;
        getData();
    }

    /**
     * 获取 我的机会列表
     */
    public void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", requestPage);
        map.put("pageSize", 15);
        map.put("stageId", stageId);
        map.put("sortType", sortType);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISale.class).getSaleMyList(map, new RCallback<SaleList>() {
            @Override
            public void success(SaleList saleMyLists, Response response) {
                HttpErrorCheck.checkResponse("销售机会 客户列表:", response);
                if (null == saleMyLists.records || saleMyLists.records.size() == 0) {
                    if (isPull) {
                        Toast("没有更多数据了!");
                    } else {
                        recordData.clear();
                        re_nodata.setVisibility(View.VISIBLE);
                    }
                    listView.onRefreshComplete();
                } else {
                    if (isPull) {
                        recordData.addAll(saleMyLists.records);
                    } else {
                        recordData.clear();
                        recordData = saleMyLists.records;
                    }
                }
                bindData();
                listView.onRefreshComplete();
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                listView.onRefreshComplete();
            }
        });
    }

    /**
     * 绑定数据
     */
    public void bindData() {
        if (null == adapter) {
            adapter = new AdapterMySaleList(getActivity());
            adapter.setData(recordData);
            listView.setAdapter(adapter);
        } else {
            adapter.setData(recordData);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            //删除后 刷新列表
            case ExtraAndResult.REQUEST_CODE_SOURCE:
                getData();
                break;
            //新增后 刷新列表
            case ExtraAndResult.REQUEST_CODE_STAGE:
                getData();
                break;

            default:
                break;
        }
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                //新建机会
                case R.id.btn_add:
                    mIntent = new Intent();
                    mIntent.setClass(getActivity(), AddMySaleActivity.class);
                    startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);
                    getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                    break;

                default:
                    break;

            }
        }
    };
}
