package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.activityui.order.bean.EstimatePlanAdd;
import com.loyo.oa.v2.activityui.order.bean.PlanEstimateList;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IOrder;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【回款计划】 列表页面
 * Created by xeq on 16/8/4.
 */
public class OrderPlanListActivity extends BaseLoadingActivity implements View.OnClickListener {

    private TextView tv_title;
    private LinearLayout ll_back, ll_add;
    private ListView lv_list;
    private OrderPlanAdapter adapter;
    private String orderId;
    private Intent mIntent;
    private Bundle mBundle;
    private int status;
    private int pagForm;//1 审批过来
    private boolean isAdd;//需要编辑就传true
//    private LinearLayout ll_state;
//    private ViewStub emptyView;

    private ArrayList<PlanEstimateList> mPlanEstimateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_order_plan_list);
    }

    @Override
    public void getPageData() {
        getPlanList();
    }

    private void init() {

        mIntent = getIntent();
        if (null != mIntent) {
            orderId = mIntent.getStringExtra("orderId");
            status = mIntent.getIntExtra("status", 0);
            pagForm = mIntent.getIntExtra(ExtraAndResult.TOKEN_START, 0);
            isAdd = mIntent.getBooleanExtra(ExtraAndResult.EXTRA_ADD, false);
        }

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("回款计划");
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnTouchListener(Global.GetTouch());
        ll_back.setOnClickListener(this);
        ll_add = (LinearLayout) findViewById(R.id.ll_add);
        ll_add.setOnTouchListener(Global.GetTouch());
        ll_add.setOnClickListener(this);
        lv_list = (ListView) findViewById(R.id.lv_list);
//        ll_state = (LinearLayout) findViewById(R.id.ll_state_baebae);
//        emptyView = (ViewStub) findViewById(R.id.vs_nodata);


//        if (status == 1 || status == 2) {
////            ll_state.setVisibility(isAdd ? View.GONE : View.VISIBLE);
//        } else if (status == 4 || status == 5) {
////            lv_list.setEmptyView(emptyView);
//        }

        ll_add.setVisibility(isAdd ? View.VISIBLE : View.GONE);
        getPageData();
    }


    public void rushAdapter() {
        if (null == adapter) {
            adapter = new OrderPlanAdapter();
            lv_list.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 删除回款计划
     */
    public void deletePlanList(String id) {
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IOrder.class)
                .deletePlanEsstimateList(id, new Callback<EstimatePlanAdd>() {
                    @Override
                    public void success(EstimatePlanAdd estimatePlanAdd, Response response) {
                        HttpErrorCheck.checkResponse("删除回款计划", response);
                        getPlanList();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
    }

    /**
     * 获取回款计划列表
     */
    public void getPlanList() {
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IOrder.class)
                .getPlanEstimateList(map, new Callback<ArrayList<PlanEstimateList>>() {
                    @Override
                    public void success(ArrayList<PlanEstimateList> planEstimateList, Response response) {
                        ll_loading.setStatus(LoadingLayout.Success);
                        HttpErrorCheck.checkResponse("计划回款列表", response);
                        mPlanEstimateList.clear();
                        mPlanEstimateList.addAll(planEstimateList);
                        if (status == 1 || status == 2) {
                            ll_loading.setStatus(LoadingLayout.Empty);
                            ll_loading.setEmptyText("订单待审核或未通过，无法添加回款计划");
                            return;
                        }
                        if (mPlanEstimateList.size() == 0)
                            ll_loading.setStatus(LoadingLayout.Empty);

                        rushAdapter();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error,ll_loading);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //返回
            case R.id.ll_back:
                onBackPressed();
                break;

            //新增
            case R.id.ll_add:
                Bundle mBundle = new Bundle();
                mBundle.putString("orderId", orderId);
                app.startActivityForResult(OrderPlanListActivity.this, OrderAddPlanActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_PRODUCT, mBundle);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {

            case ExtraAndResult.REQUEST_CODE_PRODUCT:
                getPlanList();
                break;

        }
    }

    class OrderPlanAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mPlanEstimateList.size();
        }

        @Override
        public Object getItem(int position) {
            return mPlanEstimateList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PlanEstimateList planEstimateList = mPlanEstimateList.get(position);
            HolderView holderView;
            if (null == convertView) {
                convertView = LayoutInflater.from(OrderPlanListActivity.this).inflate(R.layout.item_order_plan, null);
                holderView = new HolderView();
                holderView.tv_index = (TextView) convertView.findViewById(R.id.tv_index);
                holderView.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holderView.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
                holderView.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
                holderView.tv_tx = (TextView) convertView.findViewById(R.id.tv_tx);
                holderView.tv_memo = (TextView) convertView.findViewById(R.id.tv_memo);
                holderView.tv_memo = (TextView) convertView.findViewById(R.id.tv_memo);
                holderView.ll_delete = (LinearLayout) convertView.findViewById(R.id.ll_delete);
                holderView.ll_edit = (LinearLayout) convertView.findViewById(R.id.ll_edit);
                holderView.ll_add = (LinearLayout) convertView.findViewById(R.id.ll_add);
                holderView.ll_function = (LinearLayout) convertView.findViewById(R.id.ll_function);
                holderView.iv_tx = (ImageView) convertView.findViewById(R.id.iv_tx);
                convertView.setTag(holderView);
            } else {
                holderView = (HolderView) convertView.getTag();
            }
            holderView.setContentView(position, planEstimateList);
            return convertView;
        }
    }

    class HolderView {
        public TextView tv_index, tv_time, tv_money, tv_mode, tv_tx, tv_memo;
        public LinearLayout ll_delete, ll_edit, ll_add, ll_function;
        public ImageView iv_tx;

        public void setContentView(final int position, final PlanEstimateList planEstimateList) {
            tv_index.setText("计划" + (position + 1));
            ll_delete.setOnTouchListener(Global.GetTouch());
            ll_edit.setOnTouchListener(Global.GetTouch());
            ll_add.setOnTouchListener(Global.GetTouch());
            ll_function.setVisibility(isAdd ? View.VISIBLE : View.GONE);
            tv_time.setText(app.df4.format(new Date(Long.valueOf(planEstimateList.planAt + "") * 1000)));
            tv_money.setText("￥" + planEstimateList.planMoney);

            switch (planEstimateList.payeeMethod) {

                case 1:
                    tv_mode.setText("现金");
                    break;

                case 2:
                    tv_mode.setText("支票");
                    break;

                case 3:
                    tv_mode.setText("银行转账");
                    break;

                case 4:
                    tv_mode.setText("其他");
                    break;
            }

            switch (planEstimateList.remindType) {

                case 1:
                    tv_tx.setText("计划前1天提醒");
                    break;

                case 2:
                    tv_tx.setText("计划前2天提醒");
                    break;

                case 3:
                    tv_tx.setText("计划前3天提醒");
                    break;

                case 4:
                    tv_tx.setText("计划前1周提醒");
                    break;

                case 5:
                    tv_tx.setText("不提醒");
                    iv_tx.setVisibility(View.GONE);
                    break;
            }

            tv_memo.setText(planEstimateList.remark);

            /*删除*/
            ll_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletePlanList(planEstimateList.id);
                }
            });
            /*编辑*/
            ll_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBundle = new Bundle();
                    mBundle.putSerializable("data", planEstimateList);
                    mBundle.putString("orderId", orderId);
                    app.startActivityForResult(OrderPlanListActivity.this, OrderAddPlanActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_PRODUCT, mBundle);
                }
            });

            ll_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EstimateAdd data = new EstimateAdd();
                    data.receivedAt = planEstimateList.planAt;
                    data.receivedMoney = (int) planEstimateList.planMoney;
                    data.payeeMethod = planEstimateList.payeeMethod;
                    data.remark = planEstimateList.remark;
                    mBundle = new Bundle();
                    mBundle.putString("orderId", orderId);
                    mBundle.putString("planId", planEstimateList.id);
                    mBundle.putInt("fromPage", OrderEstimateListActivity.ORDER_PLAN);
                    mBundle.putSerializable(ExtraAndResult.RESULT_DATA, data);
                    app.startActivityForResult(OrderPlanListActivity.this, OrderAddEstimateActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, mBundle);
                }
            });
        }
    }
}
