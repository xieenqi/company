package com.loyo.oa.v2.activityui.sale.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.bean.Role;
import com.loyo.oa.v2.activityui.sale.AddMySaleActivity;
import com.loyo.oa.v2.activityui.sale.SaleDetailsActivity;
import com.loyo.oa.v2.activityui.sale.SaleOpportunitiesManagerActivity;
import com.loyo.oa.v2.activityui.sale.adapter.AdapterSaleTeam;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.activityui.sale.bean.SaleTeamList;
import com.loyo.oa.v2.activityui.sale.bean.SaleTeamScreen;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.customer.bean.Department;
import com.loyo.oa.v2.activityui.other.bean.SaleStage;
import com.loyo.oa.v2.activityui.other.bean.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ISale;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.customview.SaleCommPopupView;
import com.loyo.oa.v2.customview.ScreenDeptPopupView;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【团队机会】列表
 * Created by yyy on 16/5/17.
 */
public class TeamSaleFragment extends BaseFragment implements View.OnClickListener, PullToRefreshListView.OnRefreshListener2 {

    /**
     * 部门筛选回调
     */
    public final static int SALETEAM_SCREEN_TAG1 = 0X01;

    /**
     * 销售阶段回调
     */
    public final static int SALETEAM_SCREEN_TAG2 = 0X02;

    /**
     * 排序回调
     */
    public final static int SALETEAM_SCREEN_TAG3 = 0X03;

    private View mView;
    private Button btn_add;
    private Intent mIntent;
    private SaleTeamScreen saleTeamScreen;
    private ViewStub emptyView;
    private LinearLayout screen1;
    private LinearLayout screen2;
    private LinearLayout screen3;
    private RelativeLayout rl_nodata;
    private TextView saleteam_screen1_commy;
    private ImageView tagImage1;
    private ImageView tagImage2;
    private ImageView tagImage3;
    private AdapterSaleTeam adapterSaleTeam;
    private PullToRefreshListView listView;
    private SaleCommPopupView saleCommPopupView;
    private ScreenDeptPopupView saleScreenPopupView;
    private List<Department> mDeptSource;  //部门和用户集合
    private List<Department> newDeptSource = new ArrayList<>();//我的部门
    private List<SaleTeamScreen> data = new ArrayList<>();
    private ArrayList<SaleStage> mSaleStages;
    private ArrayList<SaleRecord> mData = new ArrayList<>();
    private ArrayList<SaleTeamScreen> sortData = new ArrayList<>();
    private ArrayList<SaleTeamScreen> stageData = new ArrayList<>();
    private String[] sort = {"按最近创建时间", "按照最近更新", "按照最高金额"};
    private boolean isPull = false;
    private boolean isKind;
    private int requestPage = 1;
    private String xPath = "";
    private String sortType = "";
    private String userId = "";
    private String stageId = "";
    private int stageIndex = 0, sortIndex = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case ExtraAndResult.MSG_SEND: {
                    saleScreenPopupView = new ScreenDeptPopupView(mActivity, data, mHandler);
                    break;
                }
                case SALETEAM_SCREEN_TAG1:
                    isPull = false;
                    saleTeamScreen = (SaleTeamScreen) msg.getData().getSerializable("data");
                    saleteam_screen1_commy.setText(saleTeamScreen.getName());
                    isKind = msg.getData().getBoolean("kind");
                    if (isKind) {
                        userId = "";
                        xPath = saleTeamScreen.getxPath();
                    } else {
                        xPath = "";
                        userId = saleTeamScreen.getId();
                    }
                    getRefershData();
                    break;

                case SALETEAM_SCREEN_TAG2:
                    isPull = false;
                    stageId = msg.getData().getString("data");
                    stageIndex = (int) msg.getData().get("index");
                    getRefershData();
                    break;

                case SALETEAM_SCREEN_TAG3:
                    isPull = false;
                    sortType = msg.getData().getString("data");
                    sortIndex = (int) msg.getData().get("index");
                    getRefershData();
                    break;

                default:
                    break;

            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_team_sale, null);
            initView(mView);
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void initView(View view) {

        mSaleStages = (ArrayList<SaleStage>) getArguments().get("stage");

        for (int i = 0; i < sort.length; i++) {
            saleTeamScreen = new SaleTeamScreen();
            saleTeamScreen.setName(sort[i]);
            saleTeamScreen.setIndex(false);
            sortData.add(saleTeamScreen);
        }

        setStageData();
        listView = (PullToRefreshListView) view.findViewById(R.id.saleteam_list);
        screen1 = (LinearLayout) view.findViewById(R.id.saleteam_screen1);
        screen2 = (LinearLayout) view.findViewById(R.id.saleteam_screen2);
        screen3 = (LinearLayout) view.findViewById(R.id.saleteam_screen3);
        emptyView = (ViewStub) view.findViewById(R.id.vs_nodata);
        rl_nodata = (RelativeLayout) view.findViewById(R.id.rl_nodata);
        saleteam_screen1_commy = (TextView) view.findViewById(R.id.saleteam_screen1_commy);
        tagImage1 = (ImageView) view.findViewById(R.id.saleteam_screen1_iv1);
        tagImage2 = (ImageView) view.findViewById(R.id.saleteam_screen1_iv2);
        tagImage3 = (ImageView) view.findViewById(R.id.saleteam_screen1_iv3);
        btn_add = (Button) view.findViewById(R.id.btn_add);

        btn_add.setOnTouchListener(Global.GetTouch());
        btn_add.setOnClickListener(this);
        screen1.setOnClickListener(this);
        screen2.setOnClickListener(this);
        screen3.setOnClickListener(this);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        listView.setEmptyView(emptyView);

        showLoading("");
        mDeptSource = Common.getLstDepartment();
        new Thread(new Runnable() {
            @Override
            public void run() {
                wersi();
            }
        }).start();
        /**
         * 列表监听
         * */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mIntent = new Intent();
                mIntent.putExtra(ExtraAndResult.IS_TEAM, true);
                mIntent.putExtra("id", adapterSaleTeam.getData().get(position - 1).getId());
                mIntent.setClass(getActivity(), SaleDetailsActivity.class);
                startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
            }
        });
        getData();
    }

    public void wersi() {
        try {
            //为超管或权限为全公司 展示全公司成员
            if (MainApp.user.isSuperUser() || MainApp.user.role.getDataRange() == Role.ALL) {
                setUser(mDeptSource);
            }
            //权限为部门 展示我的部门
            else if (MainApp.user.role.getDataRange() == Role.DEPT_AND_CHILD) {
                deptSort();
            }
            //权限为个人 展示自己
            else if (MainApp.user.role.getDataRange() == Role.SELF) {
                data.clear();
                saleTeamScreen = new SaleTeamScreen();
                saleTeamScreen.setId(MainApp.user.getId());
                saleTeamScreen.setName(MainApp.user.name);
                saleTeamScreen.setxPath(MainApp.user.depts.get(0).getShortDept().getXpath());
                data.add(saleTeamScreen);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally { /** 子线程读数据，主线程加载数据 */
            Message msg = new Message();
            msg.what = ExtraAndResult.MSG_SEND;
            mHandler.sendMessage(msg);
        }
    }

    private void getRefershData() {
        requestPage = 1;
        isPull = false;
        getData();
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
     * 组装销售阶段筛选数据
     */
    public void setStageData() {
        saleTeamScreen = new SaleTeamScreen();
        saleTeamScreen.setName("全部阶段");
        saleTeamScreen.setId("");
        saleTeamScreen.setIndex(false);
        stageData.add(saleTeamScreen);
        for (int i = 0; i < mSaleStages.size(); i++) {
            saleTeamScreen = new SaleTeamScreen();
            saleTeamScreen.setName(mSaleStages.get(i).getName());
            saleTeamScreen.setId(mSaleStages.get(i).getId());
            saleTeamScreen.setIndex(false);
            stageData.add(saleTeamScreen);
        }
    }

    /**
     * 获取 团队机会列表
     */
    public void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", requestPage);
        map.put("pageSize", 15);
        map.put("stageId", stageId);
        map.put("sortType", sortType);
        map.put("xpath", xPath);
        map.put("userId", userId);
        LogUtil.d("团队机会列表 请求数据:" + MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISale.class).getSaleTeamList(map, new RCallback<SaleTeamList>() {
            @Override
            public void success(SaleTeamList saleTeamList, Response response) {
                HttpErrorCheck.checkResponse("客户列表", response);
                if (null == saleTeamList.records || saleTeamList.records.size() == 0) {
                    if (isPull) {
                        Toast("没有更多数据了!");
                    } else {
                        mData.clear();
                        rl_nodata.setVisibility(View.VISIBLE);
                    }
                    listView.onRefreshComplete();
                } else {
                    if (isPull) {
                        mData.addAll(saleTeamList.records);
                    } else {
                        mData.clear();
                        mData = saleTeamList.records;
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

    public void bindData() {
        if (null == adapterSaleTeam) {
            adapterSaleTeam = new AdapterSaleTeam(getActivity());
            adapterSaleTeam.setData(mData);
            listView.setAdapter(adapterSaleTeam);
        } else {
            adapterSaleTeam.setData(mData);
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
     * 组装部门格式
     */
    private void setUser(List<Department> values) {
        data.clear();
        for (Department department : values) {
            saleTeamScreen = new SaleTeamScreen();
            saleTeamScreen.setId(department.getId());
            saleTeamScreen.setName(department.getName());
            saleTeamScreen.setxPath(department.getXpath());
            data.add(saleTeamScreen);
        }
    }

    /**
     * 过滤出我的部门
     */
    private void deptSort() {
        newDeptSource.clear();
        User user = MainApp.user;
        for (Department department : mDeptSource) {
            for (int i = 0; i < user.getDepts().size(); i++) {
                if (department.getId().contains(user.getDepts().get(i).getShortDept().getId())) {
                    newDeptSource.add(department);
                }
            }
        }
        setUser(newDeptSource);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //新建机会
            case R.id.btn_add:

                mIntent = new Intent();
                mIntent.setClass(getActivity(), AddMySaleActivity.class);
                startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);

                break;


            //全公司筛选
            case R.id.saleteam_screen1:
                if (saleScreenPopupView != null) {
                    saleScreenPopupView.showAsDropDown(screen1);
                    openPopWindow(tagImage1);
                    saleScreenPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            closePopupWindow(tagImage1);
                        }
                    });
                }
                break;
            //销售阶段
            case R.id.saleteam_screen2:
                saleCommPopupView = new SaleCommPopupView(getActivity(), mHandler, stageData, SaleOpportunitiesManagerActivity.SCREEN_STAGE, true, stageIndex);
                saleCommPopupView.showAsDropDown(screen2);
                openPopWindow(tagImage2);
                saleCommPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        closePopupWindow(tagImage2);
                    }
                });

                break;
            //排序
            case R.id.saleteam_screen3:
                saleCommPopupView = new SaleCommPopupView(getActivity(), mHandler, sortData, SaleOpportunitiesManagerActivity.SCREEN_SORT, false, sortIndex);
                saleCommPopupView.showAsDropDown(screen3);
                openPopWindow(tagImage3);
                saleCommPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        closePopupWindow(tagImage3);
                    }
                });

                break;

            default:
                break;
        }
    }
}
