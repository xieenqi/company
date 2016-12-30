package com.loyo.oa.v2.activityui.product;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.sale.api.SaleService;
import com.loyo.oa.v2.activityui.sale.bean.ActionCode;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.activityui.sale.bean.SaleProductEdit;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.CustomTextView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 【意向产品】
 * Created by xeq on 16/5/20.
 */
public class IntentionProductActivity extends BaseActivity {

    public static String KEY_CAN_EDIT = "com.logo.oa.IntentionProduct.KEY_CAN_EDIT";
    public static String KEY_CAN_DELETE = "com.logo.oa.IntentionProduct.KEY_CAN_DELETE";

    private boolean canEdit = false;
    private boolean canDelete = false;
    private String saleId = "";
    private int resultAction = 0;
    private int fromPage = 0;
    private TextView tv_addpro;
    private TextView tv_title;
    private CustomTextView tv_saleToal, tv_discount;
    private LinearLayout ll_back, ll_add, ll_statistics;
    private ListView lv_list;
    ArrayList<SaleIntentionalProduct> listData = new ArrayList<>();
    SaleProductAdapter saleProductAdapter;
    private int editItemIndex;//改变item的位置记录
    private boolean isKine = false;

    Handler hadler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            if (listData.size() > 0) {
                ll_statistics.setVisibility(View.VISIBLE);
                float productTalo = 0;
                float productSale = 0;
                for (SaleIntentionalProduct ele : listData) {
                    productTalo += ele.costPrice * ele.quantity;
                    productSale += ele.salePrice * ele.quantity;
                }
                tv_saleToal.setText("¥" + Utils.setValueDouble(productSale));
                if (0 != productTalo)
                    tv_discount.setText(Utils.setValueDouble((productSale / productTalo) * 100) + "%");
            } else {
                tv_saleToal.setText("¥" + 0);
                tv_discount.setText(0 + "%");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intention_product);
        getIntentData();
        init();
    }

    /**
     * 获得传递过来的数据
     */
    private void getIntentData() {
        canEdit = getIntent().getBooleanExtra(KEY_CAN_EDIT, true/* 临时调整 */);
        canDelete = getIntent().getBooleanExtra(KEY_CAN_DELETE, true/* 临时调整 */);
        saleId = getIntent().getStringExtra("saleId");
        fromPage = getIntent().getIntExtra("data", 0);
        isKine = getIntent().getBooleanExtra("boolean", false);
        ArrayList<SaleIntentionalProduct> intentData
                = (ArrayList<SaleIntentionalProduct>) getIntent()
                .getSerializableExtra(ExtraAndResult.EXTRA_DATA);
        if (null != intentData && intentData.size() > 0) {
            listData = intentData;
        }
    }

    private void init() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_addpro = (TextView) findViewById(R.id.tv_addpro);
        if (fromPage == ActionCode.ORDER_DETAIL) {
            tv_addpro.setText("新增购买产品");
        }
        tv_title.setText(fromPage == ActionCode.ORDER_DETAIL ? "购买产品" : "意向产品");
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnTouchListener(Global.GetTouch());
        ll_back.setOnClickListener(click);
        ll_add = (LinearLayout) findViewById(R.id.ll_add);
        ll_add.setOnTouchListener(Global.GetTouch());
        ll_add.setOnClickListener(click);
        ll_add.setVisibility(canEdit ? View.VISIBLE : View.GONE);
        saleProductAdapter = new SaleProductAdapter();
        saleProductAdapter.canEdit = canEdit;
        saleProductAdapter.canDelete = canDelete;
        lv_list = (ListView) findViewById(R.id.lv_list);
        lv_list.setAdapter(saleProductAdapter);
        tv_saleToal = (CustomTextView) findViewById(R.id.tv_saleToal);
        tv_discount = (CustomTextView) findViewById(R.id.tv_discount);
        ll_statistics = (LinearLayout) findViewById(R.id.ll_statistics);
        if (fromPage == ActionCode.ORDER_DETAIL && !isKine) {
            ll_add.setVisibility(View.GONE);
        }
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
                    product.putString("saleId", saleId);
                    product.putInt("data", fromPage);
                    product.putSerializable("productList", listData);
                    app.startActivityForResult(IntentionProductActivity.this, AddIntentionProductActivity.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_PRODUCT, product);
                    break;
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        hadler.sendEmptyMessage(0);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(ExtraAndResult.STR_SELECT_TYPE, resultAction);
        intent.putExtra(ExtraAndResult.RESULT_DATA, listData);
        intent.putExtra("salePrice", tv_saleToal.getText().toString());
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    /**
     * 删除意向产品
     */
    public void deleteProduct(String pid) {
        showLoading2("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("cid", saleId);
        map.put("pid", pid);

        SaleService.deleteSaleProduct(map).subscribe(new DefaultLoyoSubscriber<SaleProductEdit>(hud) {
            @Override
            public void onNext(SaleProductEdit saleProductEdit) {
                resultAction = ActionCode.SALE_DETAILS_RUSH;
                hadler.sendEmptyMessage(0);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //新增产品
                case ExtraAndResult.REQUEST_CODE_PRODUCT:
                    resultAction = data.getIntExtra(ExtraAndResult.STR_SHOW_TYPE, 0);
                    SaleIntentionalProduct product = (SaleIntentionalProduct) data.getSerializableExtra(ExtraAndResult.EXTRA_DATA);
                    saleProductAdapter.setData(product);
                    break;
                //编辑产品
                case ExtraAndResult.REQUEST_EDIT:
                    resultAction = data.getIntExtra(ExtraAndResult.STR_SHOW_TYPE, 0);
                    SaleIntentionalProduct productEdit = (SaleIntentionalProduct) data.getSerializableExtra(ExtraAndResult.EXTRA_DATA);
                    listData.remove(editItemIndex);
                    listData.add(editItemIndex, productEdit);
                    saleProductAdapter.notifyDataSetChanged();
                    break;
            }

        }
    }

    class SaleProductAdapter extends BaseAdapter {

        public boolean canEdit;
        public boolean canDelete;

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
                convertView = LayoutInflater.from(IntentionProductActivity.this).inflate(R.layout.item_intention_product, null);
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
                holderView.tv_oldePrice = (TextView) convertView.findViewById(R.id.tv_oldePrice);
                holderView.tv_salePrice = (TextView) convertView.findViewById(R.id.tv_salePrice);
                convertView.setTag(holderView);
            } else {
                holderView = (HolderView) convertView.getTag();
            }
            holderView.setContentView(position);

            return convertView;
        }
    }

    class HolderView {
        public TextView tv_index, tv_product, tv_toal_price, tv_sale_price, tv_number, tv_discount, tv_total_money, tv_memo, tv_oldePrice, tv_salePrice;
        public LinearLayout ll_delete, ll_edit;

        public void setContentView(final int position) {
            tv_index.setText((fromPage == ActionCode.ORDER_DETAIL ? "购买产品" : "意向产品") + (position + 1));
            final SaleIntentionalProduct item = listData.get(position);
            tv_product.setText(item.name);
            tv_toal_price.setText(Utils.setValueDouble(item.costPrice + ""));
            tv_sale_price.setText(Utils.setValueDouble(item.salePrice + ""));
            tv_number.setText(Utils.setValueDouble(item.quantity + ""));
            tv_discount.setText(Utils.setValueDouble(item.discount) + "%");
            tv_total_money.setText(Utils.setValueDouble(item.totalMoney + ""));
            tv_memo.setText(item.memo);
            if (!TextUtils.isEmpty(item.unit)) {
                tv_oldePrice.setText("产品原价(" + item.unit + ")");
                tv_salePrice.setText("销售价格(" + item.unit + ")");
            }
            ll_delete.setOnTouchListener(Global.GetTouch());
            ll_edit.setOnTouchListener(Global.GetTouch());
            /*意向产品 删除*/
            ll_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (fromPage == ActionCode.SALE_FROM_DETAILS) {
                        deleteProduct(item.id);
                        listData.remove(position);
                        saleProductAdapter.notifyDataSetChanged();
                    } else {
                        listData.remove(position);
                        saleProductAdapter.notifyDataSetChanged();
                    }
                    hadler.sendEmptyMessage(0);

                }
            });
            /*意向产品 编辑*/
            ll_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editItemIndex = position;
                    Bundle product = new Bundle();
                    product.putString("saleId", saleId);
                    product.putInt("data", ActionCode.SALE_PRO_EDIT);
                    product.putSerializable(ExtraAndResult.EXTRA_DATA, item);
                    app.startActivityForResult(IntentionProductActivity.this, AddIntentionProductActivity.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_EDIT, product);
                }
            });

            ll_edit.setVisibility(canEdit ? View.VISIBLE : View.GONE);
            ll_delete.setVisibility(canDelete ? View.VISIBLE : View.GONE);

            if (fromPage == ActionCode.ORDER_DETAIL) {//此处详情过来的处理
                ll_delete.setVisibility(View.GONE);
                ll_edit.setVisibility(View.GONE);
            }
        }
    }
}
