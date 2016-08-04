package com.loyo.oa.v2.activityui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 回款计划 列表页面
 * Created by xeq on 16/8/4.
 */
public class OrderPlanListActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_title;
    private LinearLayout ll_back, ll_add;
    private ListView lv_list;
    private OrderPlanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_plan_list);
        init();
    }

    private void init() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("回款计划");
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnTouchListener(Global.GetTouch());
        ll_back.setOnClickListener(this);
        ll_add = (LinearLayout) findViewById(R.id.ll_add);
        ll_add.setOnTouchListener(Global.GetTouch());
        ll_add.setOnClickListener(this);
        adapter = new OrderPlanAdapter();
        lv_list = (ListView) findViewById(R.id.lv_list);
        lv_list.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.ll_add:
                Bundle product = new Bundle();
//                product.putString("saleId", saleId);
//                product.putInt("data", fromPage);
                app.startActivityForResult(OrderPlanListActivity.this, OrderAddPlanActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_PRODUCT, product);
                break;
        }
    }

    class OrderPlanAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 3;
        }

        public void setData(SaleIntentionalProduct product) {
//            listData.add(product);
            notifyDataSetChanged();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
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
            holderView.setContentView(position);
            return convertView;
        }
    }

    class HolderView {
        public TextView tv_index, tv_time, tv_money, tv_mode, tv_tx, tv_memo;
        public LinearLayout ll_delete, ll_edit, ll_add;

        public void setContentView(final int position) {
            tv_index.setText("计划" + (position + 1));
//            final SaleIntentionalProduct item = listData.get(position);
//            tv_product.setText(item.name);
//            tv_toal_price.setText(Utils.setValueDouble(item.costPrice + ""));
//            tv_sale_price.setText(Utils.setValueDouble(item.salePrice + ""));
//            tv_number.setText(Utils.setValueDouble(item.quantity + ""));
//            tv_discount.setText(Utils.setValueDouble(item.discount) + "%");
//            tv_total_money.setText(Utils.setValueDouble(item.totalMoney + ""));
//            tv_memo.setText(item.memo);
//            if (!TextUtils.isEmpty(item.unit)) {
//                tv_oldePrice.setText("产品原价(" + item.unit + ")");
//                tv_salePrice.setText("销售价格(" + item.unit + ")");
//            }
            ll_delete.setOnTouchListener(Global.GetTouch());
            ll_edit.setOnTouchListener(Global.GetTouch());
            ll_add.setOnTouchListener(Global.GetTouch());
            /*意向产品 删除*/
            ll_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    if (fromPage == ActionCode.SALE_FROM_DETAILS) {
//                        deleteProduct(item.id);
//                        listData.remove(position);
//                        saleProductAdapter.notifyDataSetChanged();
//                    } else {
//                        listData.remove(position);
//                        saleProductAdapter.notifyDataSetChanged();
//                    }
//                    hadler.sendEmptyMessage(0);

                }
            });
            /*意向产品 编辑*/
            ll_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    editItemIndex = position;
//                    Bundle product = new Bundle();
//                    product.putString("saleId", saleId);
//                    product.putInt("data", ActionCode.SALE_PRO_EDIT);
//                    product.putSerializable(ExtraAndResult.EXTRA_DATA, item);
//                    app.startActivityForResult(IntentionProductActivity.this, AddIntentionProductActivity.class,
//                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_EDIT, product);
                }
            });
            ll_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    editItemIndex = position;
//                    Bundle product = new Bundle();
//                    product.putString("saleId", saleId);
//                    product.putInt("data", ActionCode.SALE_PRO_EDIT);
//                    product.putSerializable(ExtraAndResult.EXTRA_DATA, item);
//                    app.startActivityForResult(IntentionProductActivity.this, AddIntentionProductActivity.class,
//                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_EDIT, product);
                }
            });
        }
    }
}
