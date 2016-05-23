package com.loyo.oa.v2.activity.sale;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.sale.bean.SaleFild;
import com.loyo.oa.v2.activity.sale.bean.SaleStage;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ISale;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.customview.GeneralPopView;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【销售阶段】【机会来源】【机会类型】
 * Created by xeq on 16/5/18.
 */
public class ActivitySaleStage extends BaseActivity {
    private TextView tv_title;
    private LinearLayout ll_back;
    private ListView lv_list;
    private SaleStageAdapter adapterStage;
    private SourceTypeAdapter adapterSourceType;

    public static final int SALE_STAGE = 1;//销售阶段
    public static final int SALE_TYPE = 2;//机会类型
    public static final int SALE_SOURCE = 3;//机会来源

    private int type;
    private String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_stage);
        Intent intent = getIntent();
        type = intent.getIntExtra(ExtraAndResult.EXTRA_TYPE, -1);
        title = intent.getStringExtra(ExtraAndResult.EXTRA_NAME);
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
                finish();
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

    public void getData2() {
        showLoading("");
        HashMap<String, String> map = new HashMap<>();
        map.put("name", SALE_TYPE == type ? "change_type" : "change_source");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(ISale.class).getSaleSystem(map, new Callback<SaleFild>() {
            @Override
            public void success(SaleFild saleFilds, Response response) {
                HttpErrorCheck.checkResponse("销售阶段xt系统 类型 来源", response);
                adapterSourceType.setData(saleFilds.defVal);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });
    }

    public void getData() {
        showLoading("");//HttpSaleBuild.buildSale().
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(ISale.class).getSaleStage(new Callback<ArrayList<SaleStage>>() {
            @Override
            public void success(ArrayList<SaleStage> saleStage, Response response) {
                HttpErrorCheck.checkResponse("销售阶段", response);
                adapterStage.setData(saleStage);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });
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
                convertView = LayoutInflater.from(ActivitySaleStage.this).inflate(R.layout.item_sale_stage, null);
            }
            final TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            ImageView iv_img = (ImageView) convertView.findViewById(R.id.iv_img);
            if (data.get(position).isSelect) {
                iv_img.setVisibility(View.VISIBLE);
            } else {
                iv_img.setVisibility(View.INVISIBLE);
            }
            tv_name.setText(data.get(position).name);
            convertView.setOnTouchListener(Global.GetTouch());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tv_name.getText().toString().contains("赢单")) {
                        final GeneralPopView dialog = showGeneralDialog(false, true, "赢单提交后不能修改,请确认赢单产品金额和数量是否正确！\n客户名称\n产品总金额");

                        dialog.setSureOnclick(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.putExtra(ExtraAndResult.EXTRA_DATA, data.get(position));
                                setResult(RESULT_OK, intent);
                                dialog.dismisDialog();
                                finish();
                            }
                        }).setCancelOnclick(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismisDialog();
                            }
                        });
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

//        public void setSelect(int indext) {
//            for (int i = 0; i < data.size(); i++) {
//                if (indext == i) {
//                    data.get(i).isSelect = true;
//                } else {
//                    data.get(i).isSelect = false;
//                }
//            }
//            notifyDataSetChanged();
//        }

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
                convertView = LayoutInflater.from(ActivitySaleStage.this).inflate(R.layout.item_sale_stage, null);
            }
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            ImageView iv_img = (ImageView) convertView.findViewById(R.id.iv_img);
//            if (data.get(position).isSelect) {
//                iv_img.setVisibility(View.VISIBLE);
//            } else {
//                iv_img.setVisibility(View.INVISIBLE);
//            }
            tv_name.setText(data.get(position));
            convertView.setOnTouchListener(Global.GetTouch());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(ExtraAndResult.EXTRA_DATA, data.get(position));
                    setResult(RESULT_OK, intent);
                    finish();
//                    Toast(data.get(position));
                }
            });
            return convertView;
        }
    }
}
