package com.loyo.oa.v2.activity.sale;

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
import com.loyo.oa.v2.activity.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;

/**
 * 【意向产品】
 * Created by xeq on 16/5/20.
 */
public class ActivityIntentionProduct extends BaseActivity {

    private int resultAction = 0;
    private int fromPage = 0;
    private TextView tv_title;
    private LinearLayout ll_back, ll_add;
    private ListView lv_list;
    ArrayList<SaleIntentionalProduct> listData = new ArrayList<>();
    SaleProductAdapter saleProductAdapter;
    private int editItemIndex;//改变item的位置记录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intention_product);
        init();
        getIntentData();
    }

    private void init() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("意向产品");
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnTouchListener(Global.GetTouch());
        ll_back.setOnClickListener(click);
        ll_add = (LinearLayout) findViewById(R.id.ll_add);
        ll_add.setOnTouchListener(Global.GetTouch());
        ll_add.setOnClickListener(click);
        saleProductAdapter = new SaleProductAdapter();
        lv_list = (ListView) findViewById(R.id.lv_list);
        lv_list.setAdapter(saleProductAdapter);
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_back:
                    onBackPressed();
                    break;
                case R.id.ll_add:
                    Bundle product = new Bundle();
                    product.putInt("data",fromPage);
                    app.startActivityForResult(ActivityIntentionProduct.this, ActivityAddIntentionProduct.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_PRODUCT, product);
                    break;
            }

        }
    };

    /**
     * 获得传递过来的数据
     */
    private void getIntentData() {
        fromPage = getIntent().getIntExtra("data",0);
        ArrayList<SaleIntentionalProduct> intentData = (ArrayList<SaleIntentionalProduct>) getIntent().getSerializableExtra(ExtraAndResult.EXTRA_DATA);
        if (null != intentData && intentData.size() > 0) {
            listData = intentData;
            saleProductAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(ExtraAndResult.STR_SELECT_TYPE,resultAction);
        intent.putExtra(ExtraAndResult.RESULT_DATA, listData);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //新增产品
                case ExtraAndResult.REQUEST_CODE_PRODUCT:
                    resultAction = data.getIntExtra(ExtraAndResult.STR_SHOW_TYPE,0);
                    SaleIntentionalProduct product = (SaleIntentionalProduct) data.getSerializableExtra(ExtraAndResult.EXTRA_DATA);
                    saleProductAdapter.setData(product);
                    break;
                //编辑产品
                case ExtraAndResult.REQUEST_EDIT:
                    SaleIntentionalProduct productEdit = (SaleIntentionalProduct) data.getSerializableExtra(ExtraAndResult.EXTRA_DATA);
                    listData.remove(editItemIndex);
                    listData.add(editItemIndex, productEdit);
                    saleProductAdapter.notifyDataSetChanged();
                    break;
            }

        }
    }

    class SaleProductAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listData.size();
        }

        public void setData(SaleIntentionalProduct product) {
            listData.add(product);
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
                convertView = LayoutInflater.from(ActivityIntentionProduct.this).inflate(R.layout.item_intention_product, null);
                holderView = new HolderView();
                holderView.tv_index = (TextView) convertView.findViewById(R.id.tv_index);
                holderView.tv_product = (TextView) convertView.findViewById(R.id.tv_product);
                holderView.tv_toal_price = (TextView) convertView.findViewById(R.id.tv_toal_price);
                holderView.tv_sale_price = (TextView) convertView.findViewById(R.id.tv_sale_price);
                holderView.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
                holderView.tv_discount = (TextView) convertView.findViewById(R.id.tv_discount);
                holderView.tv_total_money = (TextView) convertView.findViewById(R.id.tv_total_money);
                holderView.tv_memo = (TextView) convertView.findViewById(R.id.tv_memo);
                holderView.ll_delete = (LinearLayout) convertView.findViewById(R.id.ll_delete);
                holderView.ll_edit = (LinearLayout) convertView.findViewById(R.id.ll_edit);
                convertView.setTag(holderView);
            } else {
                holderView = (HolderView) convertView.getTag();
            }
            holderView.setContentView(position);
            return convertView;
        }
    }

    class HolderView {
        public TextView tv_index, tv_product, tv_toal_price, tv_sale_price, tv_number, tv_discount, tv_total_money, tv_memo;
        public LinearLayout ll_delete, ll_edit;

        public void setContentView(final int position) {
            tv_index.setText("意向产品" + (position + 1));
            final SaleIntentionalProduct item = listData.get(position);
            tv_product.setText(item.name);
            tv_toal_price.setText(item.costPrice + "");
            tv_sale_price.setText(item.salePrice + "");
            tv_number.setText(item.quantity + "");
            tv_discount.setText(item.discount + "%");
            tv_total_money.setText(item.totalMoney + "");
            tv_memo.setText(item.memo);
            ll_delete.setOnTouchListener(Global.GetTouch());
            ll_edit.setOnTouchListener(Global.GetTouch());
            ll_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listData.remove(position);
                    saleProductAdapter.notifyDataSetChanged();
                }
            });
            ll_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editItemIndex = position;
                    Bundle product = new Bundle();
                    product.putSerializable(ExtraAndResult.EXTRA_DATA, item);
                    app.startActivityForResult(ActivityIntentionProduct.this, ActivityAddIntentionProduct.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_EDIT, product);
                }
            });
        }
    }
}
