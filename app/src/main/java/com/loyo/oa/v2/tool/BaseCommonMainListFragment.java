package com.loyo.oa.v2.tool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.PagingGroupData_;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshExpandableListView;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * com.loyo.oa.v2.tool.json
 * 描述 :项目、任务、报告、审批的统一界面
 * 作者 : ykb
 * 时间 : 15/9/7.
 */
public abstract class BaseCommonMainListFragment<T extends BaseBeans> extends BaseFragment implements
        PullToRefreshBase.OnRefreshListener2,
        AbsListView.OnScrollListener,
        View.OnTouchListener,
        Callback<PaginationX<T>> {

    protected View mView;
    protected Button btn_add;
    protected ViewGroup img_title_left;
    protected LayoutInflater mInflater;
    protected TextView tv_title_1;
    protected DropDownMenu filterMenu;
    private ViewStub emptyView;
    public static final int REQUEST_CREATE = 4;
    public static final int REQUEST_REVIEW = 5;
    protected PaginationX<T> pagination = new PaginationX<T>(20);
    protected ArrayList<PagingGroupData_<T>> pagingGroupDatas = new ArrayList<>();
    protected ArrayList<T> lstData = new ArrayList<>();
    protected PullToRefreshExpandableListView mExpandableListView;
    protected boolean isTopAdd = true;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetData();
    }

//    private Runnable UiRunner = new Runnable() {
//        @Override
//        public void run() {
//            btn_add.setVisibility(View.INVISIBLE);
//        }
//    };

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_base_new, container, false);
            filterMenu = (DropDownMenu) mView.findViewById(R.id.drop_down_menu);
            emptyView = (ViewStub) mView.findViewById(R.id.vs_nodata);

            tv_title_1 = (TextView) mView.findViewById(R.id.tv_title_1);
            tv_title_1.setText(GetTitle());

            //底部创建按钮
            btn_add = (Button) mView.findViewById(R.id.btn_add);
            btn_add.setOnTouchListener(ViewUtil.OnTouchListener_view_transparency.Instance());
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addNewItem();
                }
            });
            img_title_left = (ViewGroup) mView.findViewById(R.id.img_title_left);
            img_title_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    app.finishActivity(mActivity, MainApp.ENTER_TYPE_LEFT, Activity.RESULT_CANCELED, null);
                }
            });
            img_title_left.setOnTouchListener(ViewUtil.OnTouchListener_view_transparency.Instance());

            mExpandableListView = (PullToRefreshExpandableListView) mView.findViewById(R.id.expandableListView);
            mExpandableListView.setOnRefreshListener(this);
            mExpandableListView.setEmptyView(emptyView);

            mView.findViewById(R.id.img_title_right).setOnTouchListener(Global.GetTouch());
            mView.findViewById(R.id.img_title_right).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSearch();
                }
            });

            initTab();
            init();
        }

        return mView;
    }

    /**
     * 初始化
     */
    private void init() {

        ExpandableListView expandableListView = mExpandableListView.getRefreshableView();
        initAdapter();
        expand();

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                try {
                    openItem(groupPosition, childPosition);
                } catch (Exception e) {
                    Global.ProcException(e);
                }

                return false;
            }
        });
        btn_add.setVisibility(View.INVISIBLE);
        Permission permission = MainApp.rootMap.get("0401");
        if (permission != null && permission.isEnable() && MainApp.permissionPage == 1) {
            btn_add.setVisibility(View.VISIBLE);
            Utils.btnHideForListView(expandableListView, btn_add);
        } else if (MainApp.permissionPage != 1) {
            Utils.btnHideForListView(expandableListView, btn_add);
            btn_add.setVisibility(View.VISIBLE);
        }


    }

    /**
     * 展开listview
     */
    protected void expand() {
        for (int i = 0; i < pagingGroupDatas.size(); i++) {
            mExpandableListView.getRefreshableView().expandGroup(i, false);//true 自动滑到底部
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = true;
        pagination.setPageIndex(1);
        GetData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = false;
        pagination.setPageIndex(pagination.getPageIndex() + 1);
        GetData();
    }

    @Override
    public void success(PaginationX<T> tPaginationX, Response response) {
        HttpErrorCheck.checkResponse("项目、任务、报告、审批的统一界面 result:", response);

        mExpandableListView.onRefreshComplete();
        if (null == tPaginationX) {
            return;
        }

        pagination = tPaginationX;
        ArrayList<T> lstDataTemp = tPaginationX.getRecords();
        if (null != lstDataTemp && lstDataTemp.size() == 0 && !isTopAdd) {
            Toast("没有更多数据了");
            return;
        }
        //下接获取最新时，清空
//        if (isTopAdd) {
//            lstData.clear();
//        }
        if (!isTopAdd) {
            lstData.addAll(lstDataTemp);
        } else {
            lstData = lstDataTemp;
        }
        pagingGroupDatas = PagingGroupData_.convertGroupData(lstData);
        changeAdapter();
        expand();


    }

    @Override
    public void failure(RetrofitError error) {
        HttpErrorCheck.checkError(error);
        mExpandableListView.onRefreshComplete();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 0x09) {
            GetData();
        }

    }

    /**
     * 获取数据
     */
    public abstract void GetData();

    /**
     * 初始化适配器
     */
    public abstract void initAdapter();

    /**
     * 改变适配器内容，刷新列表
     */
    public abstract void changeAdapter();

    /**
     * 新增条目
     */
    public abstract void addNewItem();

    public abstract void openSearch();

    /**
     * 打开条目
     *
     * @param groupPosition
     * @param childPosition
     */
    public abstract void openItem(int groupPosition, int childPosition);

    /**
     * 获取标题
     *
     * @return
     */
    public abstract String GetTitle();

    /**
     * 初始化筛选tabs
     */
    public abstract void initTab();
}
