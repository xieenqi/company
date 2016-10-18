package com.loyo.oa.v2.activityui.wfinstance.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ExpandableListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.wfinstance.WfInstanceManageActivity;
import com.loyo.oa.v2.activityui.wfinstance.WfinstanceInfoActivity_;
import com.loyo.oa.v2.activityui.wfinstance.adapter.WflnstanceMySubmitAdapter;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizForm;
import com.loyo.oa.v2.activityui.wfinstance.bean.WflnstanceItemData;
import com.loyo.oa.v2.activityui.wfinstance.bean.WflnstanceListItem;
import com.loyo.oa.v2.activityui.wfinstance.presenter.WfinMyApprovePresenter;
import com.loyo.oa.v2.activityui.wfinstance.presenter.impl.WfinMyApprovePresenterImpl;
import com.loyo.oa.v2.activityui.wfinstance.viewcontrol.WfinMyApproveView;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.filterview.DropDownMenu;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshExpandableListView;
import com.loyo.oa.v2.tool.BaseFragment;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;

/**
 * 【我审批的】
 * Restruture by yyy on 16/10/17
 */
public class WfinstanceMyApproveFragment extends BaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2,WfinMyApproveView {

    private Button btn_add;
    private ViewStub emptyView;
    private DropDownMenu mMenu;
    private WfinMyApprovePresenter mPresenter;
    private WflnstanceMySubmitAdapter mAdapter;
    private PullToRefreshExpandableListView expandableListView;

    private static final String FILTER_STATUS[] = new String[]{"全部状态", "待我审批的", "未到我审批的", "我同意的", "我驳回的"};
    private boolean isTopAdd = false;
    private int page = 1;

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
        page = 1;
        isTopAdd = true;
        mPresenter = new WfinMyApprovePresenterImpl(mMenu,this,getActivity());
        mPresenter.initDropMenu(FILTER_STATUS);
        initList();
        initAdapter();
        mPresenter.getApproveWfInstancesList(page,isTopAdd);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*新建审批*/
            case R.id.btn_add:
                ((WfInstanceManageActivity) getActivity()).addNewItem();
                break;
        }
    }

    /**
     * ListView初始化
     */
    private void initList() {
        ExpandableListView mListView = expandableListView.getRefreshableView();
        initAdapter();
        mPresenter.initListView(mListView,btn_add);
    }

    /**
     * 初始化Adapter
     * */
    public void initAdapter() {
        mAdapter = new WflnstanceMySubmitAdapter(mActivity);
        expandableListView.getRefreshableView().setAdapter(mAdapter);
    }


    /**
     * 展开listview
     */
    protected void expand(ArrayList<WflnstanceItemData> datas) {
        for (int i = 0; i < datas.size(); i++) {
            expandableListView.getRefreshableView().expandGroup(i, false);//true 自动滑到底部
        }
    }

    /**
     * 下拉刷新回调
     * */
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = true;
        page = 1;
        mPresenter.getApproveWfInstancesList(page,isTopAdd);
    }

    /**
     * 上拉刷新回调
     * */
    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = false;
        page++;
        mPresenter.getApproveWfInstancesList(page,isTopAdd);
    }

    @Override
    public void showProgress() {
        showLoading("");
    }

    @Override
    public void hideProgress() {
        cancelLoading();
    }

    @Override
    public void showMsg(String message) {
        Toast(message);
    }

    /**
     * 刷新下拉数据
     * */
    @Override
    public void setPullDownToRefresh() {
        onPullDownToRefresh(expandableListView);
    }

    /**
     * 停止下拉数据
     * */
    @Override
    public void setListRefreshComplete() {
        expandableListView.onRefreshComplete();
    }

    /**
     * 绑定数据
     * */
    @Override
    public void bindListData(ArrayList<WflnstanceItemData> datas) {
        mAdapter.setData(datas);
        expand(datas);
    }

    /**
     * 跳转Item操作
     * */
    @Override
    public void openItemEmbl(int groupPosition, int childPosition) {
        WflnstanceListItem item = (WflnstanceListItem) mAdapter.getChild(groupPosition, childPosition);
        Intent intent = new Intent();
        intent.putExtra(ExtraAndResult.EXTRA_ID, item.id);
        //有红点需要刷新
        if (!item.viewed) {
            intent.putExtra(ExtraAndResult.IS_UPDATE, true);
        }
        intent.setClass(mActivity, WfinstanceInfoActivity_.class);
        startActivityForResult(intent, ExtraAndResult.REQUEST_CODE);
        getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
    }

    /**
     * Ui刷新回调
     * */
    @Subscribe
    public void onRushListData(BizForm bizForm){
        isTopAdd = true;
        page = 1;
        mPresenter.getApproveWfInstancesList(page,isTopAdd);
    }

/*    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            switch (requestCode) {
                case ExtraAndResult.REQUEST_CODE:
                    isTopAdd = true;
                    page = 1;
                    mPresenter.getApproveWfInstancesList(page,isTopAdd);
                    break;
                case 0x09:
                    isTopAdd = true;
                    page = 1;
                    mPresenter.getApproveWfInstancesList(page,isTopAdd);
                    break;
            }
        }
    }*/
}
