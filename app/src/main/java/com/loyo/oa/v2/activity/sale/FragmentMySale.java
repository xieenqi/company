package com.loyo.oa.v2.activity.sale;

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
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.sale.adapter.AdapterMySaleList;
import com.loyo.oa.v2.activity.sale.bean.SaleMyList;
import com.loyo.oa.v2.activity.sale.bean.SaleRecord;
import com.loyo.oa.v2.activity.sale.bean.SaleTeamScreen;
import com.loyo.oa.v2.beans.SaleStage;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ISale;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.customview.SaleCommPopupView;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;
import java.util.ArrayList;
import java.util.HashMap;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【销售机会】我的列表
 * Created by yyy on 16/5/17.
 */
public class FragmentMySale extends BaseFragment implements PullToRefreshBase.OnRefreshListener2 {

    private View mView;
    private Button btn_add;
    private Intent mIntent;
    private LinearLayout screen1;
    private LinearLayout screen2;
    private LinearLayout topview;
    private RelativeLayout re_nodata;
    private ViewStub emptyView;
    private ImageView tagImage1;
    private ImageView tagImage2;
    private SaleCommPopupView saleCommPopupView;
    private WindowManager.LayoutParams windowParams;
    private PullToRefreshListView listView;
    private AdapterMySaleList adapter;
    private SaleTeamScreen saleTeamScreen;

    private ArrayList<SaleTeamScreen> sortData = new ArrayList<>();
    private ArrayList<SaleTeamScreen> stageData = new ArrayList<>();
    private ArrayList<SaleStage> mSaleStages;
    private ArrayList<SaleRecord> recordData = new ArrayList<>();

    private String[] sort = {"按最近创建时间", "按照最近更新", "按照最高金额"};
    private String stageId = "";
    private String sortType = "";
    private int requestPage = 1;
    private boolean isPull = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case FragmentTeamSale.SALETEAM_SCREEN_TAG2:
                    isPull = false;
                    stageId = msg.getData().get("data").toString();
                    break;

                case FragmentTeamSale.SALETEAM_SCREEN_TAG3:
                    isPull = false;
                    sortType = msg.getData().get("data").toString();
                    break;

                default:
                    break;

            }
            getData();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_my_sale, null);
            initView(mView);
        }
        getData();
        return mView;
    }

    private void initView(View view) {

        for (int i = 0; i < sort.length; i++) {
            saleTeamScreen = new SaleTeamScreen();
            saleTeamScreen.setName(sort[i]);
            saleTeamScreen.setId(i + "");
            saleTeamScreen.setIndex(false);
            sortData.add(saleTeamScreen);
        }

        mSaleStages = (ArrayList<SaleStage>) getArguments().get("stage");
        setStageData();
        listView = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        btn_add = (Button) view.findViewById(R.id.btn_add);
        screen1 = (LinearLayout) view.findViewById(R.id.salemy_screen1);
        screen2 = (LinearLayout) view.findViewById(R.id.salemy_screen2);
        topview = (LinearLayout) view.findViewById(R.id.saleteam_topview);
        re_nodata = (RelativeLayout) view.findViewById(R.id.re_nodata);
        emptyView = (ViewStub) view.findViewById(R.id.vs_nodata);
        tagImage1 = (ImageView) view.findViewById(R.id.salemy_screen1_iv1);
        tagImage2 = (ImageView) view.findViewById(R.id.salemy_screen1_iv2);

        btn_add.setOnTouchListener(Global.GetTouch());
        btn_add.setOnClickListener(click);
        screen1.setOnClickListener(click);
        screen2.setOnClickListener(click);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        listView.setEmptyView(emptyView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mIntent = new Intent();
                mIntent.putExtra("id", adapter.getData().get(position - 1).getId());
                mIntent.setClass(getActivity(), ActivitySaleDetails.class);
                startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);
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
     * 获取 我的机会列表
     */
    public void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", requestPage);
        map.put("pageSize", 15);
        map.put("stageId", stageId);
        map.put("sortType", sortType);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISale.class).getSaleMyList(map, new RCallback<SaleMyList>() {
            @Override
            public void success(SaleMyList saleMyLists, Response response) {
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
        windowParams = getActivity().getWindow().getAttributes();
        windowParams.alpha = 1f;
        getActivity().getWindow().setAttributes(windowParams);
        view.setBackgroundResource(R.drawable.arrow_down);
    }

    /**
     * PopupWindow打开，背景变暗
     */
    private void openPopWindow(ImageView view) {
        windowParams = getActivity().getWindow().getAttributes();
        windowParams.alpha = 0.9f;
        getActivity().getWindow().setAttributes(windowParams);
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
                    mIntent.setClass(getActivity(),ActivityAddMySale.class);
                    startActivityForResult(mIntent,getActivity().RESULT_FIRST_USER);

                    break;

                //销售阶段
                case R.id.salemy_screen1:
                    saleCommPopupView = new SaleCommPopupView(getActivity(), mHandler, stageData, ActivitySaleOpportunitiesManager.SCREEN_STAGE, true);
                    saleCommPopupView.showAsDropDown(screen1);
                    openPopWindow(tagImage1);
                    saleCommPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            closePopupWindow(tagImage1);
                        }
                    });
                    break;

                //排序
                case R.id.salemy_screen2:
                    saleCommPopupView = new SaleCommPopupView(getActivity(), mHandler, sortData, ActivitySaleOpportunitiesManager.SCREEN_SORT, false);
                    saleCommPopupView.showAsDropDown(screen2);
                    openPopWindow(tagImage2);
                    saleCommPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            closePopupWindow(tagImage2);
                        }
                    });

                    break;

                default:
                    break;

            }
        }
    };
}
