package com.loyo.oa.v2.activityui.product;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.other.PreviewImageListActivity;
import com.loyo.oa.v2.activityui.product.adapter.ProductPicAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.tool.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends BaseActivity {


    private TextView tvTitle;

    private GridView gridViewPic;
    private LinearLayout llDefinedHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        initView();
    }

    private void initView(){
        tvTitle= (TextView) findViewById(R.id.tv_title);
        llDefinedHolder= (LinearLayout) findViewById(R.id.product_detail_more_defined);
        gridViewPic= (GridView) findViewById(R.id.product_detail_more_grid_1);

        final List<Attachment> data=new ArrayList<>();
        ProductPicAdapter picAdapter=new ProductPicAdapter(this,data);

        /*图片预览*/
        gridViewPic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", data.get(position));
                bundle.putInt("position", position);
                bundle.putBoolean("isEdit", false);
                MainApp.getMainApp().startActivityForResult((Activity) mContext, PreviewImageListActivity.class,
                        MainApp.ENTER_TYPE_BUTTOM, FinalVariables.REQUEST_DEAL_ATTACHMENT, bundle);
            }
        });

        gridViewPic.setAdapter(picAdapter);
        tvTitle.setText("产品详细");

        addDefined();
    }

    //模拟添加自定义字段
    private void addDefined(){
        for (int i = 0; i < 5; i++) {
            View view = getLayoutInflater().inflate(R.layout.item_product_defined, null);
            TextView tvTempTitle= (TextView) view.findViewById(R.id.add_product_defined_tv_1);
            TextView tvTempText= (TextView) view.findViewById(R.id.add_product_defined_tv_2);
            tvTempTitle.setText("标题："+i);
            tvTempText.setText("content:"+i);
            llDefinedHolder.addView(view);

        }
    }

}