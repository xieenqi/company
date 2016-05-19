package com.loyo.oa.v2.activity.sale;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 机会详情
 * Created by yyy on 16/5/19.
 */
public class ActivitySaleDetails extends BaseActivity {

    private LinearLayout img_title_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saledetails);
        setTitle("机会详情");
        initView();
    }

    public void initView(){
        img_title_left = (LinearLayout)findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(click);
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.img_title_left:
                    finish();
                    break;
            }
        }
    };
}
