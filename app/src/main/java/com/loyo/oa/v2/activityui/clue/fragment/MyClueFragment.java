package com.loyo.oa.v2.activityui.clue.fragment;


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

import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.ClueAddActivity;
import com.loyo.oa.v2.activityui.clue.ClueDetailActivity;
import com.loyo.oa.v2.activityui.clue.adapter.MyClueAdapter;
import com.loyo.oa.v2.activityui.clue.bean.ClueList;
import com.loyo.oa.v2.activityui.clue.bean.ClueListItem;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.filtermenu.ClueStatusFilterModel;
import com.loyo.oa.v2.filtermenu.TimeFilterModel;
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
    private ViewStub emptyView;
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
        List<FilterModel> options = new ArrayList<>();
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

                if (menuIndex == 0) { // TimeFilterModel
                    String[] keys = key.split(" ");
                    field = keys[0];
                    order = keys[1];
                }
                else if (menuIndex == 1) { // ClueStatusFilterModel
                    statusKey = key;
                }
                getData();
            }
        });
    }

    private void initView(View view) {
        btn_add = (Button) view.findViewById(R.id.btn_add);
        btn_add.setOnTouchListener(Global.GetTouch());
        btn_add.setOnClickListener(this);
        emptyView = (ViewStub) view.findViewById(R.id.vs_nodata);
        lv_list = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        lv_list.setMode(PullToRefreshBase.Mode.BOTH);
        lv_list.setOnRefreshListener(this);
        lv_list.setEmptyView(emptyView);
        /*列表监听*/
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mIntent = new Intent();
                mIntent.putExtra(ExtraAndResult.IS_TEAM, false);
                mIntent.putExtra(ExtraAndResult.EXTRA_ID, /* 线索id */listData.get(position - 1).id);
                mIntent.setClass(getActivity(), ClueDetailActivity.class);
                startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);

            }
        });
        adapter = new MyClueAdapter(getActivity());
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
                mIntent.setClass(getActivity(), ClueAddActivity.class);
                startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);

                break;
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
     * 请求列表数据
     */
    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 15);
        map.put("field", field);
        map.put("order", order);
        map.put("status", statusKey);
        LogUtil.dee("发送数据:" + MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(IClue.class).getMyCluelist(map, new Callback<ClueList>() {
            @Override
            public void success(ClueList clueList, Response response) {
                lv_list.onRefreshComplete();
                HttpErrorCheck.checkResponse("我的线索列表：", response);
                try {
                    if (!isPullDown) {
                        listData.addAll(clueList.data.records);
                    } else {
                        listData = clueList.data.records;
                    }
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
