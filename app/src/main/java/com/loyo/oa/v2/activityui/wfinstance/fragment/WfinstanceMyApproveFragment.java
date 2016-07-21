package com.loyo.oa.v2.activityui.wfinstance.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.wfinstance.WfInstanceManageActivity;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizForm;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.filterview.DropDownMenu;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshExpandableListView;
import com.loyo.oa.v2.point.IWfInstance;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by xeq on 16/7/21.
 * 审批【我审批的】
 */
public class WfinstanceMyApproveFragment extends BaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private PullToRefreshExpandableListView expandableListView;
    private Button btn_add;
    private DropDownMenu mMenu;
    private ViewStub emptyView;
    private static final String FILTER_STATUS[] = new String[]{"全部状态", "待审批", "审批中", "未通过", "已通过"};
    private ArrayList<BizForm> mBizForms = new ArrayList<>();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wflinstance, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        expandableListView = (PullToRefreshExpandableListView) view.findViewById(R.id.expandableListView);
        btn_add = (Button) view.findViewById(R.id.btn_add);
        mMenu = (DropDownMenu) view.findViewById(R.id.drop_menu);
        emptyView = (ViewStub) view.findViewById(R.id.vs_nodata);
        btn_add.setOnTouchListener(Global.GetTouch());
        btn_add.setOnClickListener(this);
        expandableListView.setOnRefreshListener(this);
        expandableListView.setEmptyView(emptyView);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                ((WfInstanceManageActivity) getActivity()).addNewItem();
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }
}
