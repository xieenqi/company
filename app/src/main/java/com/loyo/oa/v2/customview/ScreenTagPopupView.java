package com.loyo.oa.v2.customview;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.ui.activity.customer.CustomerManagerActivity;
import com.loyo.oa.v2.ui.activity.customer.adapter.TagScreenAdapter;
import com.loyo.oa.v2.ui.activity.customer.bean.Tag;
import com.loyo.oa.v2.ui.activity.customer.bean.TagItem;
import com.loyo.oa.v2.common.Global;
import java.util.ArrayList;

/**
 * 【客户筛选】标签View
 * Created by yyy on 16/5/18.
 */
public class ScreenTagPopupView extends PopupWindow implements View.OnClickListener {

    /**
     * 选中数目刷新
     */
    private final int RUSH_STATE = 0x01;

    /**
     * 全部取消
     */
    private final int CANCEL_STATE = 0x02;

    private Context mContext;
    private View contentView;
    private ListView listView1;
    private ListView listView2;
    private Button confirm;
    private Button cancel;
    private Bundle mBundle;
    private Message msg;
    private Tag tag;

    private TagScreenAdapter adater1;
    private TagScreenAdapter adater2;

    private ArrayList<Tag> mData;
    private ArrayList<Tag> mRightData = new ArrayList<>();

    private int selectNum;
    private int resultTag;
    private StringBuffer tagItemIds;

    private Handler vHandler;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case RUSH_STATE:
                    if (selectNum == 0) {
                        confirm.setText("确定");
                    } else {
                        confirm.setText("确定(" + selectNum + ")");
                    }
                    break;

                case CANCEL_STATE:
                    confirm.setText("确定");
                    break;
            }
        }
    };

    public ScreenTagPopupView(final Activity context, ArrayList<Tag> data, Handler handler) {
        mContext = context;
        mData = data;
        vHandler = handler;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.custag_screentag, null);
        initView();
    }

    public void initView() {

        this.setContentView(contentView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setBackgroundDrawable(new ColorDrawable(0000000000));
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        getSelectNum();
        listView1 = (ListView) contentView.findViewById(R.id.custag_screentag1_lv1);
        listView2 = (ListView) contentView.findViewById(R.id.custag_screentag1_lv2);
        confirm = (Button) contentView.findViewById(R.id.custag_screentag1_confirm);
        cancel = (Button) contentView.findViewById(R.id.custag_screentag1_cancel);
        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);
        confirm.setOnTouchListener(Global.GetTouch());
        cancel.setOnTouchListener(Global.GetTouch());
        setRightData(0);

        adater1 = new TagScreenAdapter(mContext, mData, 1);
        listView1.setAdapter(adater1);
        adater2 = new TagScreenAdapter(mContext, mRightData, 2);
        listView2.setAdapter(adater2);

        if (selectNum == 0) {
            confirm.setText("确定");
        } else {
            confirm.setText("确定(" + selectNum + ")");
        }

        /*左侧列表*/
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adater1.selectPosition(position);
                setRightData(position);
                adater1.notifyDataSetChanged();
                adater2.notifyDataSetChanged();
            }
        });

        /*右侧列表*/
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dataReque(position);
                mHandler.sendEmptyMessage(RUSH_STATE);
                adater2.notifyDataSetChanged();
            }
        });
    }

    /**
     * 设置右边列表数据
     */
    public void setRightData(int position) {
        mRightData.clear();
        for (TagItem tagItem : mData.get(position).getItems()) {
            tag = new Tag();
            tag.setId(tagItem.getId());
            tag.setName(tagItem.getName());
            tag.setIndex(tagItem.isChecked());
            mRightData.add(tag);
        }
    }

    /**
     * 记录点击位置
     */
    public void dataReque(int position) {
        for (int i = 0; i < mRightData.size(); i++) {
            if (position == i) {
                if (mRightData.get(i).isIndex()) {
                    mRightData.get(i).setIndex(false);
                } else {
                    mRightData.get(i).setIndex(true);
                }
            } else {
                mRightData.get(i).setIndex(false);
            }
        }
        selectNum = 0;
        //数据源中，刷新勾选记录
        for (Tag mtag : mData) {
            for (TagItem tagItem : mtag.getItems()) {
                for (Tag ztag : mRightData) {
                    if (ztag.getId().contains(tagItem.getId())) {
                        if (ztag.isIndex()) {
                            tagItem.setIsChecked(true);
                        } else {
                            tagItem.setIsChecked(false);
                        }
                    }
                }
            }
        }
        getSelectNum();
    }

    /**
     * 得到选中数量
     */
    private void getSelectNum() {
        for (Tag mtag : mData) {
            for (TagItem tagItem : mtag.getItems()) {
                if (tagItem.isChecked()) {
                    selectNum++;
                }
            }
        }
    }

    /**
     * 全部取消数据重置
     */
    private void canceAlldata() {
        for (Tag mtag : mData) {
            for (TagItem tagItem : mtag.getItems()) {
                tagItem.setIsChecked(false);
            }
        }
    }

    /**
     * 获取所有勾选ID
     */
    private StringBuffer getAlldata(StringBuffer tagItemIds) {
        tagItemIds = new StringBuffer();
        for (Tag mtag : mData) {
            for (TagItem tagItem : mtag.getItems()) {
                if (tagItem.isChecked()) {
                    tagItemIds.append(tagItem.getId() + ",");
                }
            }
        }
        return tagItemIds;
    }

    @Override
    public void onClick(View v) {
        String stringIds = "";
        mBundle = new Bundle();
        msg = new Message();
        switch (v.getId()) {
            //确定
            case R.id.custag_screentag1_confirm:
                try {
                    stringIds = getAlldata(tagItemIds).toString().substring(0, getAlldata(tagItemIds).toString().length() - 1);
                    mBundle.putString("tagid", stringIds);
                    resultTag = CustomerManagerActivity.CUSTOMER_TAG;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            //全部取消
            case R.id.custag_screentag1_cancel:
                canceAlldata();
                stringIds = "";
                mBundle.putString("tagid", stringIds);
                resultTag = CustomerManagerActivity.CUSTOMER_CANCEL;
                break;

            default:
                break;
        }
        msg.setData(mBundle);
        msg.what = resultTag;
        vHandler.sendMessage(msg);
        dismiss();
    }
}
