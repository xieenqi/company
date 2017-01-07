package com.loyo.oa.v2.activityui.sale;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CommonTagSelectActivity;
import com.loyo.oa.v2.activityui.customer.CommonTagSelectActivity_;
import com.loyo.oa.v2.activityui.sale.api.SaleService;
import com.loyo.oa.v2.activityui.sale.bean.ActionCode;
import com.loyo.oa.v2.activityui.sale.bean.CommonTag;
import com.loyo.oa.v2.activityui.sale.bean.SaleField;
import com.loyo.oa.v2.activityui.sale.bean.SaleStage;
import com.loyo.oa.v2.activityui.sale.model.SaleStageConfig;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 【销售阶段】【机会来源】【机会类型】
 * Created by xeq on 16/5/18.
 */
public class SaleStageActivity extends BaseActivity {


    private TextView tv_title;
    private LinearLayout ll_back;
    private ListView lv_list;
    private SaleStage saleStage;
    private SaleStageAdapter adapterStage;
    private SourceTypeAdapter adapterSourceType;

    public static final int SALE_STAGE = 1;//销售阶段
    public static final int SALE_TYPE = 2;//机会类型
    public static final int SALE_SOURCE = 3;//机会来源

    private int type;
    private String title, dataName = "", saleName, salePrice;
    private ArrayList<CommonTag> loseResons = new ArrayList<>();
    private boolean isProduct;//机会详情过来是否有意向产品


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_stage);
        getIntentData();
        init();
    }

    private void init() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(title);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnTouchListener(Global.GetTouch());
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        lv_list = (ListView) findViewById(R.id.lv_list);
        if (SALE_STAGE == type) {
            adapterStage = new SaleStageAdapter();
            lv_list.setAdapter(adapterStage);
            getData();
        } else {
            adapterSourceType = new SourceTypeAdapter();
            lv_list.setAdapter(adapterSourceType);
            getData2();
        }
    }

    private void getIntentData() {
        Intent intent = getIntent();
        type = intent.getIntExtra(ExtraAndResult.EXTRA_TYPE, -1);
        title = intent.getStringExtra(ExtraAndResult.EXTRA_NAME);
        dataName = intent.getStringExtra(ExtraAndResult.EXTRA_DATA);
        saleName = intent.getStringExtra(ExtraAndResult.CC_USER_NAME);
        salePrice = intent.getStringExtra(ExtraAndResult.EXTRA_BOOLEAN);
        isProduct = intent.getBooleanExtra(ExtraAndResult.EXTRA_STATUS, false);
    }

    public void getData2() {
        showLoading2("");
        HashMap<String, String> map = new HashMap<>();
        map.put("name", SALE_TYPE == type ? "chance_type" : "chance_source");

        SaleService.getSaleSystem(map).subscribe(new DefaultLoyoSubscriber<SaleField>(hud) {

            @Override
            public void onNext(SaleField saleFilds) {
                adapterSourceType.setData(saleFilds.defVal);
            }
        });
    }

    /**
     * 获取销售阶段 数据
     */
    public void getData() {
        adapterStage.setData(SaleStageConfig.getSaleStage(true));
    }

    /**
     * 销售阶段adapter
     */
    class SaleStageAdapter extends BaseAdapter {
        private ArrayList<SaleStage> data = new ArrayList<>();
        private int selectIndext = -1;

        public void setData(ArrayList<SaleStage> newData) {
            if (null != newData) {
                this.data = newData;
                notifyDataSetChanged();
            }
        }

        public void setSelect(int indext) {
            for (int i = 0; i < data.size(); i++) {
                if (indext == i) {
                    data.get(i).isSelect = true;
                } else {
                    data.get(i).isSelect = false;
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = LayoutInflater.from(SaleStageActivity.this).inflate(R.layout.item_sale_stage, null);
            }
            final TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            ImageView iv_img = (ImageView) convertView.findViewById(R.id.iv_img);
            if (data.get(position).isSelect) {
                iv_img.setVisibility(View.VISIBLE);
            } else {
                iv_img.setVisibility(View.INVISIBLE);
            }
            tv_name.setText(data.get(position).name);
            if (!TextUtils.isEmpty(dataName) && dataName.equals(data.get(position).name)) {
                setSelect(position);
            }
            convertView.setOnTouchListener(Global.GetTouch());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tv_name.getText().toString().contains("赢单") && !TextUtils.isEmpty(saleName)) {
                        if (!isProduct) {
                            Toast("赢单必须添加意向产品");
                            return;
                        }
                        String tt = "赢单提交后不能修改,请确认赢单产品金额和数量是否正确！\n客户名称：" + saleName + "\n产品总金额：￥" + salePrice;
                        sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                dismissSweetAlert();
                            }
                        }, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Intent intent = new Intent();
                                intent.putExtra(ExtraAndResult.EXTRA_DATA, data.get(position));
                                setResult(RESULT_OK, intent);
                                dismissSweetAlert();
                                finish();
                            }
                        },"提示", Utils.modifyTextColor(tt, getResources().getColor(R.color.red1), 0, 10).toString());

                        return;
                    } else if (tv_name.getText().toString().contains("输单") && !TextUtils.isEmpty(saleName)) {

                        setSelect(position);
                        Bundle loseBundle = new Bundle();
                        loseBundle.putSerializable("data", loseResons);
                        loseBundle.putSerializable("mono", data.get(position));
                        loseBundle.putString("title", "输单原因");
                        loseBundle.putInt("mode", CommonTagSelectActivity.SELECT_MODE_MULTIPLE);
                        loseBundle.putInt("type", CommonTagSelectActivity.SELECT_TYPE_LOSE_REASON);
                        loseBundle.putInt("kind", ActionCode.SALE_DETAILS_STATE_EDIT);
                        app.startActivityForResult(SaleStageActivity.this, CommonTagSelectActivity_.class,
                                0, CommonTagSelectActivity.REQUEST_TAGS, loseBundle);

                        return;
                    }
                    setSelect(position);
                    Intent intent = new Intent();
                    intent.putExtra(ExtraAndResult.EXTRA_DATA, data.get(position));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
            return convertView;
        }
    }

    class SourceTypeAdapter extends BaseAdapter {
        private ArrayList<String> data = new ArrayList<>();

        public void setData(ArrayList<String> newData) {
            if (null != newData) {
                this.data = newData;
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return data.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = LayoutInflater.from(SaleStageActivity.this).inflate(R.layout.item_sale_stage, null);
            }
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            ImageView iv_img = (ImageView) convertView.findViewById(R.id.iv_img);

            tv_name.setText(data.get(position));
            if (dataName.equals(data.get(position))) {
                iv_img.setVisibility(View.VISIBLE);
            } else {
                iv_img.setVisibility(View.INVISIBLE);
            }
            convertView.setOnTouchListener(Global.GetTouch());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(ExtraAndResult.EXTRA_DATA, data.get(position));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
            return convertView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActionCode.SALE_DETAILS_STATE_EDIT) {
            loseResons = (ArrayList<CommonTag>) data.getSerializableExtra("data");
            saleStage = (SaleStage) data.getSerializableExtra("mono");

            Intent intent = new Intent();
            intent.putExtra(ExtraAndResult.EXTRA_DATA, saleStage);
            intent.putExtra(ExtraAndResult.RESULT_NAME, loseResons);
            setResult(RESULT_OK, intent);
            finish();

        }
    }
}
