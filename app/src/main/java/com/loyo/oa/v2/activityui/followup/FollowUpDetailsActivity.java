package com.loyo.oa.v2.activityui.followup;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.CommonHtmlUtils;
import com.loyo.oa.v2.activityui.commonview.CommonImageView;
import com.loyo.oa.v2.activityui.commonview.CommonTextVew;
import com.loyo.oa.v2.activityui.customer.model.ImgAndText;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsCommentAdapter;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsGridViewAdapter;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsOptionsAdapter;
import com.loyo.oa.v2.customview.CusGridView;
import com.loyo.oa.v2.customview.CustomerListView;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.tool.AnimationCommon;
import com.loyo.oa.v2.tool.BaseActivity;


/**
 * 【跟进详情】
 * Created by yyy on 16/11/10.
 */

public class FollowUpDetailsActivity extends BaseActivity implements View.OnClickListener {


    private ScrollView layout_scrollview;

    private TextView tv_title;
    private TextView tv_name;              /* 姓名 */
    private TextView tv_kind;              /* 方式 */
    private RoundImageView iv_heading;     /* 头像 */

    private CustomerListView lv_comment;
    private CustomerListView lv_options;
    private CusGridView gv_image;

    private LinearLayout layout_voice;
    private LinearLayout layout_touch;
    private LinearLayout ll_web;
    private LinearLayout layout_enclosure;
    private EditText edit_comment;
    private ImageView iv_voice;
    private TextView tv_send_message;

    private float mPosX;
    private float mPosY;
    private float mCurPosX;
    private float mCurPosY;

    private ListOrDetailsCommentAdapter commentAdapter;  /* 评论区Adapter */
    private ListOrDetailsGridViewAdapter imageAdapter;   /* 图片9宫格Adapter */
    private ListOrDetailsOptionsAdapter  optionAdapter;  /* 附件列表Adapter */


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x01) {
                layout_voice.setAnimation(AnimationCommon.inFromBottomAnimation(150));
                layout_voice.setVisibility(View.VISIBLE);
            } else if (msg.what == 0x02) {
                layout_voice.setVisibility(View.GONE);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_details);
        initUI();
    }

    private void initUI() {
        commentAdapter = new ListOrDetailsCommentAdapter(mContext);
        optionAdapter = new ListOrDetailsOptionsAdapter(mContext);
        imageAdapter = new ListOrDetailsGridViewAdapter(mContext);

        layout_voice = (LinearLayout) findViewById(R.id.layout_voice);
        layout_touch = (LinearLayout) findViewById(R.id.layout_touch);
        layout_enclosure = (LinearLayout) findViewById(R.id.layout_enclosure);
        ll_web = (LinearLayout) findViewById(R.id.ll_web);
        edit_comment = (EditText) findViewById(R.id.edit_comment);
        tv_send_message = (TextView) findViewById(R.id.tv_send_message);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_kind = (TextView) findViewById(R.id.tv_kind);
        iv_voice = (ImageView) findViewById(R.id.iv_voice);
        iv_heading = (RoundImageView) findViewById(R.id.iv_heading);
        layout_scrollview = (ScrollView) findViewById(R.id.layout_scrollview);
        lv_comment = (CustomerListView) findViewById(R.id.lv_comment);
        lv_options = (CustomerListView) findViewById(R.id.lv_options);
        gv_image = (CusGridView) findViewById(R.id.layout_gridview);

        iv_voice.setOnClickListener(this);
        tv_send_message.setOnClickListener(this);
        edit_comment.setOnClickListener(this);

        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("跟进详情");

        layout_touch.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    /* 按下 */
                    case MotionEvent.ACTION_DOWN:
                        mPosX = event.getX();
                        mPosY = event.getY();
                        hideInputKeyboard(edit_comment);
                        layout_voice.setVisibility(View.GONE);
                        break;
                    /* 移动 */
                    case MotionEvent.ACTION_MOVE:
                        mCurPosX = event.getX();
                        mCurPosY = event.getY();
                        break;
                    /* 离开 */
                    case MotionEvent.ACTION_UP:
                        //向下滑動
                        if (mCurPosY - mPosY > 0 && (Math.abs(mCurPosY - mPosY) > 25)) {
                        }
                        //向上滑动
                        else if (mCurPosY - mPosY < 0 && (Math.abs(mCurPosY - mPosY) > 25)) {
                        }
                        break;
                }
                return true;
            }
        });

        lv_comment.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast("长按监听");
                return false;
            }
        });

        lv_comment.setAdapter(commentAdapter);
        lv_options.setAdapter(optionAdapter);
        gv_image.setAdapter(imageAdapter);
        setContent(ll_web," ");
    }

    /**
     * 设置图文混编
     */
    public void setContent(LinearLayout layout, String content) {
        layout.removeAllViews();
        for (final ImgAndText ele : CommonHtmlUtils.Instance().checkContentList(content)) {
            if (ele.type.startsWith("img")) {
                CommonImageView img = new CommonImageView(FollowUpDetailsActivity.this, ele.data);
                layout.addView(img);
            } else {
                CommonTextVew tex = new CommonTextVew(FollowUpDetailsActivity.this, ele.data);
                layout.addView(tex);
            }
        }
        layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            /*录音*/
            case R.id.iv_voice:

                hideInputKeyboard(edit_comment);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mHandler.sendEmptyMessage(0x01);
                    }
                }, 100);

                break;

            /*编辑*/
            case R.id.edit_comment:
                layout_voice.setVisibility(View.GONE);
                break;

            /*发送*/
            case R.id.tv_send_message:

                break;
        }
    }
}
