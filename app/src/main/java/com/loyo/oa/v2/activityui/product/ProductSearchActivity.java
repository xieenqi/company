package com.loyo.oa.v2.activityui.product;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshExpandableListView;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.product.adapter.SelectProductAdapter;
import com.loyo.oa.v2.tool.BaseLoadingActivity;


/**
 * 新增购买产品－产品选择－搜索产品
 */
public class ProductSearchActivity extends BaseLoadingActivity implements PullToRefreshBase.OnRefreshListener2  {

    private PullToRefreshListView listView;
    private EditText edt_search;
    private ImageView iv_clean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView(){
        edt_search = (EditText) findViewById(R.id.edt_search);

        //返回按钮
        findViewById(R.id.img_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //清空按钮
        iv_clean = (ImageView) findViewById(R.id.iv_clean);
        iv_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_search.setText("");
            }
        });

        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //开始搜索
                }
                return false;
            }
        });


        listView= (PullToRefreshListView) findViewById(R.id.lv_list);
        //首次进来不加载数据 默认成功
        ll_loading.setStatus(LoadingLayout.Success);
        getPageData();
    }
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void setLayoutView() {
        //有一个布局叫做activity_public_search，但是为了模块独立，先自己写，后面优化
        setContentView(R.layout.activity_product_search);
    }

    @Override
    public void getPageData() {
        //获取数据
        SelectProductAdapter selectProductAdapter=new SelectProductAdapter(this);
        listView.setAdapter(selectProductAdapter);

    }
}