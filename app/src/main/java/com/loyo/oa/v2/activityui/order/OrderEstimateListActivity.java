package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.adapter.OrderEstimateListAdapter;
import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.activityui.order.bean.EstimateList;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IOrder;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【订单回款】
 * Created by yyy on 16/8/2.
 */
public class OrderEstimateListActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll_back;
    private LinearLayout ll_add;
    private TextView tv_title;
    private TextView tv_dealprice;  //成交金额
    private TextView tv_totalprice; //交易总金额
    private TextView tv_aleryprice; //回款金额
    private TextView tv_faileprice; //未回款

    private ListView lv_listview;
    private EstimateAdd mEstimateAdd;
    private ArrayList<EstimateAdd> mData;
    private OrderEstimateListAdapter mAdapter;
    private Intent mIntent;
    private Bundle mBundle;

    private String orderId;
    private String dealPrice;

    private int    fromPage;
    private int    position;

    /**
     * 来自订单新建 新建回款
     * */
    public final static int PAGE_ORDER_ADD = 0x01;

    /**
     * 来自订单详情 新建回款
     * */
    public final static int PAGE_DETAILS_ADD = 0x02;

    /**
     * 来自编辑订单
     * */
    public final static int PAGE_EDIT = 0x03;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){

                case ExtraAndResult.MSG_WHAT_DIALOG:
                    ll_add.setVisibility(View.GONE);
                    break;

                //订单删除操作
                case ExtraAndResult.MSG_WHAT_GONG:

                    if(fromPage == PAGE_ORDER_ADD){
                        mBundle = msg.getData();
                        position = mBundle.getInt("posi");
                        mData.remove(position);
                        rushAdapter();
                        ll_add.setVisibility(View.VISIBLE);
                    }else if(fromPage == PAGE_DETAILS_ADD){
                        deleteData();
                    }

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderestimate);
        initUI();
    }

    public void initUI() {
        mIntent = getIntent();
        if (null != mIntent) {
            orderId   = mIntent.getStringExtra("orderId");
            fromPage  = mIntent.getIntExtra("fromPage", PAGE_ORDER_ADD);
            dealPrice = mIntent.getStringExtra("price");
            if(null != (ArrayList<EstimateAdd>) mIntent.getSerializableExtra("data")){
                mData = (ArrayList<EstimateAdd>) mIntent.getSerializableExtra("data");
            }
        }

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_add = (LinearLayout) findViewById(R.id.ll_add);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_totalprice = (TextView) findViewById(R.id.tv_totalprice);
        tv_aleryprice = (TextView) findViewById(R.id.tv_aleryprice);
        tv_faileprice = (TextView) findViewById(R.id.tv_faileprice);
        tv_dealprice = (TextView) findViewById(R.id.tv_dealprice);
        lv_listview = (ListView) findViewById(R.id.lv_listview);
        tv_title.setText("回款记录");
        if (null != dealPrice)
            tv_dealprice.setText("￥" + dealPrice);
        if (null != mData && mData.size() > 0)
            ll_add.setVisibility(View.GONE);
        ll_back.setOnClickListener(this);
        ll_add.setOnClickListener(this);
        ll_back.setOnTouchListener(Global.GetTouch());
        rushAdapter();

        if(fromPage == PAGE_DETAILS_ADD){
            getData();
        }
    }

    public void rushAdapter() {
        if (null == mAdapter) {
            if(null == mData){
                mData = new ArrayList<EstimateAdd>();
            }
            mAdapter = new OrderEstimateListAdapter(this, mData, mHandler,orderId);
            lv_listview.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 删除订单
     * */
    public void deleteData(){

        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IOrder.class)
                .deletePayEstimate(mData.get(position).id, new Callback<EstimateAdd>() {
                    @Override
                    public void success(EstimateAdd estimateAdds, Response response) {
                        HttpErrorCheck.checkResponse("回款记录列表", response);
                        getData();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });

    }



    /**
     * 获取收款记录列表
     * */
    public void getData(){
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IOrder.class)
                .getPayEstimate(orderId, new Callback<EstimateList>() {
                    @Override
                    public void success(EstimateList estimateList, Response response) {
                        HttpErrorCheck.checkResponse("回款记录列表", response);
                        if(null != estimateList.records){
                            mData.clear();
                            mData.addAll(estimateList.records);
                            rushAdapter();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //后退
            case R.id.ll_back:
                onBackPressed();
                break;

            //新建
            case R.id.ll_add:
                mBundle = new Bundle();
                mBundle.putString("orderId", orderId);
                mBundle.putInt("fromPage",fromPage);
                app.startActivityForResult(this, OrderAddEstimateActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, mBundle);
                break;

        }
    }


    @Override
    public void onBackPressed() {
            mIntent = new Intent();
            mIntent.putExtra("data", mData);
            app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);
            super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == ExtraAndResult.REQUEST_CODE_STAGE) {

            switch (fromPage){

                case PAGE_ORDER_ADD:
                    if(null == data){
                        return;
                    }
                    mEstimateAdd = (EstimateAdd) data.getSerializableExtra("data");
                    mData.clear();
                    mData.add(mEstimateAdd);
                    rushAdapter();
                    break;


                case PAGE_DETAILS_ADD:
                    getData();
                    break;
            }
            mHandler.sendEmptyMessage(ExtraAndResult.MSG_WHAT_DIALOG);
        }
    }
}
