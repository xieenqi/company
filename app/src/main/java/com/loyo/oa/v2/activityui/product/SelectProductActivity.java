package com.loyo.oa.v2.activityui.product;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.product.adapter.SelectProductAdapter;
import com.loyo.oa.v2.activityui.product.view.SelectProductMenu;
import com.loyo.oa.v2.activityui.product.viewcontrol.SelectProMenuView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 选择产品
 * Created by yyy on 16/12/21.
 */

public class SelectProductActivity extends BaseActivity implements View.OnClickListener,SelectProMenuView {

    private ImageView img_back;
    private TextView  tv_title,tv_add;
    private LinearLayout ll_search;
    private ListView lv_listview;
    private SelectProductMenu productMenu;
    private SelectProductAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);
        initUI();
    }

    void bindAdapter(){
        if(null == mAdapter){
            mAdapter = new SelectProductAdapter(this);
            lv_listview.setAdapter(mAdapter);
        }else{
            mAdapter.notifyDataSetChanged();
        }
    }

    void initUI() {
        ll_search = (LinearLayout) findViewById(R.id.ll_search);
        img_back = (ImageView) findViewById(R.id.img_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_add   = (TextView) findViewById(R.id.tv_add);
        lv_listview   = (ListView) findViewById(R.id.lv_listview);

        tv_title.setText("选择产品");
        tv_add.setText("分类");

        bindOnClick();
        productMenu = new SelectProductMenu(this,this);
        bindAdapter();
    }

    void bindOnClick(){
        img_back.setOnClickListener(this);
        ll_search.setOnClickListener(this);
        tv_add.setOnClickListener(this);

        Global.SetTouchView(img_back,tv_add,ll_search);
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){

            // 返回
            case R.id.img_back:
                Toast("返回");
                break;

            // 分类
            case R.id.tv_add:
                productMenu.showPopupWindow(v);
                break;

            // 搜索
            case R.id.ll_search:

                app.startActivity(this, ProductSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                break;

        }
    }

    @Override
    public void popWindowShowEmbl() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
    }

    @Override
    public void popWindowDimsEmbl() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1f;
        getWindow().setAttributes(lp);
    }
}
