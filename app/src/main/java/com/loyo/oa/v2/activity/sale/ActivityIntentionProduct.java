package com.loyo.oa.v2.activity.sale;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 【意向产品】
 * Created by xeq on 16/5/20.
 */
public class ActivityIntentionProduct extends BaseActivity {

    private TextView tv_title;
    private LinearLayout ll_back, ll_add;
    private ListView lv_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intention_product);
        init();
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
        lv_list = (ListView) findViewById(R.id.lv_list);
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
                    app.startActivityForResult(ActivityIntentionProduct.this, ActivityAddIntentionProduct.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_PRODUCT, product);
                    break;
            }

        }
    };
}
