package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.bean.EstimatePlanAdd;
import com.loyo.oa.v2.activityui.order.bean.PlanEstimateList;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IOrder;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 回款计划 列表页面
 * Created by xeq on 16/8/4.
 */
public class OrderPlanListActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_title;
    private LinearLayout ll_back, ll_add;
    private ListView lv_list;
    private OrderPlanAdapter adapter;

    private String orderId;
    private Intent mIntent;

    private ArrayList<PlanEstimateList> mPlanEstimateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_plan_list);
        init();
    }

    private void init() {

        mIntent = getIntent();
        if(null != mIntent){
            orderId = mIntent.getStringExtra("orderId");
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
        getPlanList();
    }


    public void rushAdapter(){
        if(null == adapter){
            adapter = new OrderPlanAdapter();
            lv_list.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
    }


    public void getPlanList(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("orderId",orderId);

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IOrder.class)
                .getPlanEstimateList(map, new Callback<ArrayList<PlanEstimateList>>() {
                    @Override
                    public void success(ArrayList<PlanEstimateList> planEstimateList, Response response) {
                        HttpErrorCheck.checkResponse("计划回款列表", response);
                        mPlanEstimateList = planEstimateList;
                        rushAdapter();
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

            //返回
            case R.id.ll_back:
                onBackPressed();
                break;

            //新增
            case R.id.ll_add:
                Bundle mBundle = new Bundle();
                mBundle.putString("orderId",orderId);
                app.startActivityForResult(OrderPlanListActivity.this, OrderAddPlanActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_PRODUCT, mBundle);
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
                convertView.setTag(holderView);
            } else {
                holderView = (HolderView) convertView.getTag();
            }
            holderView.setContentView(position,planEstimateList);
            return convertView;
        }
    }

    class HolderView {
        public TextView tv_index, tv_time, tv_money, tv_mode, tv_tx, tv_memo;
        public LinearLayout ll_delete, ll_edit, ll_add;

        public void setContentView(final int position,PlanEstimateList planEstimateList) {
            tv_index.setText("计划" + (position + 1));
            ll_delete.setOnTouchListener(Global.GetTouch());
            ll_edit.setOnTouchListener(Global.GetTouch());
            ll_add.setOnTouchListener(Global.GetTouch());

            tv_time.setText(DateTool.timet(planEstimateList.planAt + "", "yyyy.MM.dd"));
            tv_money.setText("￥"+planEstimateList.planMoney);

            switch (planEstimateList.payeeMethod){

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

            switch (planEstimateList.remindType){

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
                    break;
            }

            tv_memo.setText(planEstimateList.remark);

            /*意向产品 删除*/
            ll_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });
            /*意向产品 编辑*/
            ll_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            ll_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
