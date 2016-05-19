package com.loyo.oa.v2.activity.sale;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.sale.bean.SaleStage;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ISale;
import com.loyo.oa.v2.tool.BaseActivity;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【销售阶段】
 * Created by xeq on 16/5/18.
 */
public class ActivitySaleStage extends BaseActivity {
    private TextView tv_title;
    private LinearLayout ll_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_stage);
        init();
        getData();
    }

    private void init() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("销售阶段");
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnTouchListener(Global.GetTouch());
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //    public void getData() {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("name", "stage_id");
//        HttpSaleBuild.buildSale().create(ISale.class).getSaleStage(map, new Callback<ArrayList<SaleFild>>() {
//            @Override
//            public void success(ArrayList<SaleFild> saleFilds, Response response) {
//                HttpErrorCheck.checkResponse("销售阶段", response);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkError(error);
//            }
//        });
//    }
    public void getData() {
        HttpSaleBuild.buildSale().create(ISale.class).getSaleStage(new Callback<ArrayList<SaleStage>>() {
            @Override
            public void success(ArrayList<SaleStage> saleFilds, Response response) {
                HttpErrorCheck.checkResponse("销售阶段", response);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });
    }

    class SaleStageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
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
            return null;
        }
    }
}
