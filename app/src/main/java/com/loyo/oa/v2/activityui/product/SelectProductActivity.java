package com.loyo.oa.v2.activityui.product;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.product.view.RouteControlsView;
import com.loyo.oa.v2.activityui.product.view.SelectProductMenu;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 选择产品
 * Created by yyy on 16/12/21.
 */

public class SelectProductActivity extends BaseActivity implements View.OnClickListener{

    private ImageView img_back;
    private TextView  tv_title,tv_add;
    private LinearLayout ll_search;
    private SelectProductMenu productMenu;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);
        initUI();
    }

    void initUI() {
        ll_search = (LinearLayout) findViewById(R.id.ll_search);
        img_back = (ImageView) findViewById(R.id.img_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_add   = (TextView) findViewById(R.id.tv_add);

        tv_title.setText("选择产品");
        tv_add.setText("分类");

        bindOnClick();
        productMenu = new SelectProductMenu(this);
    }

    void bindOnClick(){
        img_back.setOnClickListener(this);
        ll_search.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        img_back.setOnTouchListener(Global.GetTouch());
        tv_add.setOnTouchListener(Global.GetTouch());
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

                break;

        }
    }
}
