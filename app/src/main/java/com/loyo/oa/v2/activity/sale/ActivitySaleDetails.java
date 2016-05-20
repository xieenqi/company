package com.loyo.oa.v2.activity.sale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 机会详情
 * Created by yyy on 16/5/19.
 */
public class ActivitySaleDetails extends BaseActivity implements View.OnClickListener{

    private final int EDIT_POP_WINDOW = 500;
    private LinearLayout img_title_left;
    private RelativeLayout img_title_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saledetails);
        setTitle("机会详情");
        initView();
    }

    public void initView() {
        mContext = this;
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_title_right = (RelativeLayout) findViewById(R.id.img_title_right);
        img_title_left.setOnClickListener(this);
        img_title_right.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_title_left:
                finish();
                break;

            case R.id.img_title_right:
                Intent intent = new Intent(mContext, ActivitySaleEditView.class);
                startActivityForResult(intent,EDIT_POP_WINDOW);
                break;

            default:
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK){
            return;
        }

        if(requestCode == EDIT_POP_WINDOW){
            if(data.getBooleanExtra("edit",false)){
                Toast("编辑回调");
            }else if(data.getBooleanExtra("delete",false)){
                Toast("删除回调");
            }
        }
    }
}
