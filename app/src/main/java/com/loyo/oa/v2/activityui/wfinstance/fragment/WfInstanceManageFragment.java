package com.loyo.oa.v2.activityui.wfinstance.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.adapter.CommonExpandableListAdapter;
import com.loyo.oa.v2.activityui.wfinstance.WfInTypeSelectActivity;
import com.loyo.oa.v2.activityui.wfinstance.WfinstanceInfoActivity_;
import com.loyo.oa.v2.activityui.wfinstance.WfinstanceSearchActivity;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizForm;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.WfInstanceRecord;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.customview.filterview.OnMenuSelectedListener;
import com.loyo.oa.v2.point.IWfInstance;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseCommonMainListFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.client.Response;

/**
 * 【审批流程】界面的 fragment
 */
public class WfInstanceManageFragment extends BaseCommonMainListFragment<WfInstanceRecord> {

    //    private static final String FILTER_CATEGORY[] = new String[]{"全部类型", "我提交的", "我审批的", "我经办的"};
    private static final String FILTER_STATUS[] = new String[]{"全部状态", "待审批", "审批中", "未通过", "已通过"};

    private int category = 0;
    private int status = 0;
    private String bizFormId;

    private ArrayList<BizForm> mBizForms = new ArrayList<>();
    private CommonExpandableListAdapter mAdapter;
    private int page = 1;

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_wfinstance_manager, null);
//        initView(view);
//        return view;
//    }
//
//    private void initView(View view) {
//    }

    public void initAdapter() {
        mAdapter = new CommonExpandableListAdapter(mActivity, pagingGroupDatas);
        mExpandableListView.getRefreshableView().setAdapter(mAdapter);
    }

    public void changeAdapter() {
        mAdapter.setData(pagingGroupDatas);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 到 审批流程 add 添加页面
     */
    public void addNewItem() {
        Intent intent = new Intent();
        intent.setClass(mActivity, WfInTypeSelectActivity.class);
        startActivityForResult(intent, REQUEST_CREATE);
        getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
    }

    /**
     * 到审批流程  的搜索页面
     */
    public void openSearch() {

        Bundle mBundle = new Bundle();
        mBundle.putInt("from", BaseActivity.WFIN_MANAGE);
        app.startActivity(mActivity, WfinstanceSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, false, mBundle);
    }

    @Override
    public void GetData() {
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 20);
        map.put("type", category);
        map.put("status", status);
        map.put("bizformId", bizFormId); //自定义筛选字段

        RestAdapterFactory.getInstance().build(Config_project.API_URL() +
                FinalVariables.wfinstance).create(IWfInstance.class).
                getWfInstancesData(map, WfInstanceManageFragment.this);
    }

    @Override
    public void openItem(int groupPosition, int childPosition) {
        Intent intent = new Intent();
        intent.putExtra(ExtraAndResult.EXTRA_ID, ((WfInstanceRecord) mAdapter.getChild(groupPosition, childPosition)).getId());
        intent.setClass(mActivity, WfinstanceInfoActivity_.class);
        startActivityForResult(intent, REQUEST_REVIEW);
        getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);

    }

    @Override
    public String GetTitle() {
        return "审批流程";
    }

    @Override
    public void initTab() {
        initDropMenu();
    }

    /**
     * 初始化下拉菜单
     */
    private void initDropMenu() {
        String[] defaultTitle = new String[]{"全部状态", "全部类别"};
        mMenu.setVisibility(View.VISIBLE);
        mMenu.setmMenuCount(defaultTitle.length);//Menu的个数
        mMenu.setmShowCount(6);//Menu展开list数量最多只显示的个数
        mMenu.setShowCheck(true);//是否显示展开list的选中项
        mMenu.setmMenuTitleTextSize(14);//Menu的文字大小
        mMenu.setmMenuTitleTextColor(getResources().getColor(R.color.default_menu_press_text));//Menu的文字颜色
        mMenu.setmMenuListTextSize(14);//Menu展开list的文字大小
        mMenu.setmMenuListTextColor(Color.BLACK);//Menu展开list的文字颜色
        mMenu.setmMenuBackColor(Color.WHITE);//Menu的背景颜色
        mMenu.setmMenuPressedBackColor(getResources().getColor(R.color.white));//Menu按下的背景颜色
        mMenu.setmCheckIcon(R.drawable.img_check1);//Menu展开list的勾选图片
        mMenu.setmUpArrow(R.drawable.arrow_up);//Menu默认状态的箭头
        mMenu.setmDownArrow(R.drawable.arrow_down);//Menu按下状态的箭头
        mMenu.setDefaultMenuTitle(defaultTitle);//默认未选择任何过滤的Menu title

        final List<String[]> items = new ArrayList<>();
//        items.add(FILTER_CATEGORY);
        items.add(FILTER_STATUS);

        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", 1);
        params.put("pageSize", 500);
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).getWfBizForms(params, new RCallback<PaginationX<BizForm>>() {
            @Override
            public void success(PaginationX<BizForm> bizFormPaginationX, Response response) {

                if (null != bizFormPaginationX) {
                    mBizForms = bizFormPaginationX.getRecords();
                    if (null != mBizForms && !mBizForms.isEmpty()) {
                        String[] FILTER_TYPE = new String[mBizForms.size() + 1];
                        FILTER_TYPE[0] = "全部类别";
                        for (int i = 0; i < mBizForms.size(); i++) {
                            FILTER_TYPE[i + 1] = mBizForms.get(i).getName();
                        }
                        items.add(FILTER_TYPE);
                        mMenu.setmMenuItems(items);
                    }
                }
            }
        });

        /**
         * 顶部删选Menu
         * */
        mMenu.setMenuSelectedListener(new OnMenuSelectedListener() {
            @Override
            //Menu展开的list点击事件  RowIndex：list的索引  ColumnIndex：menu的索引
            public void onSelected(View listview, int RowIndex, int ColumnIndex) {
                app.logUtil.e(" 行 : " + RowIndex + " 列 : " + ColumnIndex);
                switch (ColumnIndex) {
                    case 0:
                        category = RowIndex;
                        break;
                    case 1:
                        status = RowIndex;
                        break;
                    case 2:
                        if (RowIndex == 0) {
                            bizFormId = "";
                        } else {
                            bizFormId = mBizForms.get(RowIndex - 1).getId();
                        }
                        break;
                }
                onPullDownToRefresh(mExpandableListView);
            }
        });
    }
}
