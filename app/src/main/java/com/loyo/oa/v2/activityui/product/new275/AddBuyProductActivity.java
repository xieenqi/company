package com.loyo.oa.v2.activityui.product.new275;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.other.PreviewImageListActivity;
import com.loyo.oa.v2.activityui.product.new275.adapter.ProductPicAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.tool.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class AddBuyProductActivity extends BaseActivity {


    private TextView tvTitle;
    private ImageView ivSubmit;
    private LinearLayout llMoreInfoBtn,llMoreInfoShow;
    private ImageView ivMore;
    private GridView gridViewPic;
    private EditText etBuyNum;
    private  LinearLayout llDefinedHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_buy_product);
        initView();
    }

    private void initView(){
        tvTitle= (TextView) findViewById(R.id.tv_title);
        ivSubmit= (ImageView) findViewById(R.id.iv_submit);
        llMoreInfoBtn= (LinearLayout) findViewById(R.id.add_buy_product_ll_4);
        llMoreInfoShow= (LinearLayout) findViewById(R.id.add_buy_product_more_ll);
        ivMore=(ImageView)findViewById(R.id.add_buy_product_iv_1);
        gridViewPic= (GridView) findViewById(R.id.add_buy_product_more_grid_1);
        etBuyNum= (EditText) findViewById(R.id.add_buy_product_et_2);
        llDefinedHolder= (LinearLayout) findViewById(R.id.add_buy_product_more_definde);


        etBuyNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!TextUtils.isEmpty(s+"")){
                    int num= Integer.parseInt(s+"");
                    if(num>10){
                        Toast("库存不足");
                    }
                }
            }
        });

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

        //查看产品详细的点击事件
        llMoreInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(llMoreInfoShow.getVisibility()==View.VISIBLE){
                    llMoreInfoShow.setVisibility(View.GONE);
                    ivMore.setImageResource(R.drawable.product_more);
                }else{
                    llMoreInfoShow.setVisibility(View.VISIBLE);
                    ivMore.setImageResource(R.drawable.product_less);
                }
            }
        });
        tvTitle.setText("新增购买产品");
        ivSubmit.setVisibility(View.VISIBLE);

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