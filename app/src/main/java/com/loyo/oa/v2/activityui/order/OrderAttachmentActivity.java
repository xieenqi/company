package com.loyo.oa.v2.activityui.order;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.swipelistview.SwipeListView;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 【订单附件】
 * Created by yyy on 16/8/2.
 */
public class OrderAttachmentActivity extends BaseActivity implements View.OnClickListener{

    private LinearLayout img_title_left;
    private TextView  tv_title;
    private TextView  tv_upload;
    private SwipeListView listView_attachment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_attachment);
        initUI();
    }

    public void initUI(){
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        tv_title = (TextView)  findViewById(R.id.tv_title_1);
        tv_upload = (TextView)  findViewById(R.id.tv_upload);
        listView_attachment = (SwipeListView) findViewById(R.id.listView_attachment);
        tv_title.setText("附件");

        img_title_left.setOnTouchListener(Global.GetTouch());
        tv_upload.setOnClickListener(this);
        img_title_left.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            //后退
            case R.id.img_title_left:
                onBackPressed();
                break;

            //上传
            case R.id.tv_upload:
                Toast("上传");
                break;

        }
    }
}
