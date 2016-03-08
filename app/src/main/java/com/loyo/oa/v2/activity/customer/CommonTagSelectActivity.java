package com.loyo.oa.v2.activity.customer;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.CommonTag;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import retrofit.client.Response;

/**
 * com.loyo.oa.v2.activity
 * 描述 :输单原因选择界面
 * 作者 : ykb
 * 时间 : 15/10/8.
 */
@EActivity(R.layout.activity_lose_reason)
public class CommonTagSelectActivity extends BaseActivity {
    public static final int REQUEST_TAGS = 210;

    public static final int SELECT_TYPE_LOSE_REASON = 1;
    public static final int SELECT_TYPE_SALE_ACTIVE_ACTION = SELECT_TYPE_LOSE_REASON + 1;

    public static final int SELECT_MODE_SINGLE = 1;
    public static final int SELECT_MODE_MULTIPLE = SELECT_MODE_SINGLE + 1;

    @Extra("data")
    ArrayList<CommonTag> results;
    @Extra
    String title;
    @Extra
    int mode, type;


    @ViewById
    RecyclerView rv_reason;

    @ViewById
    ViewGroup img_title_left, img_title_right;
    @ViewById
    TextView tv_title_1;

    private ArrayList<CommonTag> commonTags = new ArrayList<>();
    ;
    private RecyclerView.LayoutManager mLayoutManager;

    @AfterViews
    void initViews() {
        setTouchView(NO_SCROLL);
        tv_title_1.setText(title);
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        if (null == results) {
            results = new ArrayList<>();
        }
        getData();
    }

    @UiThread
    void getData() {
        if (type == SELECT_TYPE_LOSE_REASON) {
            RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getLoseReasons(new RCallback<ArrayList<CommonTag>>() {
                @Override
                public void success(ArrayList<CommonTag> tags, Response response) {
                    processData(tags);
                }
            });
        } else if (type == SELECT_TYPE_SALE_ACTIVE_ACTION) {
            RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getSaleactivitytypes(new RCallback<ArrayList<CommonTag>>() {
                @Override
                public void success(ArrayList<CommonTag> tags, Response response) {
                    processData(tags);
                }
            });
        }
    }

    @Click(R.id.img_title_left)
    void back() {
        onBackPressed();
    }

    @Click(R.id.img_title_right)
    void backWidthSelect() {
        if (results.size() == 0 || null == results) {
            Toast("请选择跟进方式!");
        } else {
            Intent intent = new Intent();
            intent.putExtra("data", results);
            setResult(RESULT_OK, intent);
            back();
        }
    }


    /**
     * 处理数据
     *
     * @param tags
     */
    private void processData(ArrayList<CommonTag> tags) {
        commonTags.addAll(tags);
        for (int i = 0; i < commonTags.size(); i++) {
            if (results.contains(commonTags.get(i))) {
                commonTags.get(i).setIsChecked(true);
            }
        }
        binData();
    }


    /**
     * 绑定数据
     */
    private void binData() {
        mLayoutManager = new LinearLayoutManager(this);
        rv_reason.setLayoutManager(mLayoutManager);
        rv_reason.setAdapter(new CommonTagAdapter());
    }

    class CommonTagViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public CheckBox cb;
        public TextView tv_title;
        public TextView tv_content;
        public View layout;

        public CommonTagViewHolder(View view) {
            super(view);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            cb = (CheckBox) view.findViewById(R.id.cb);
            layout = view;
        }
    }

    private class CommonTagAdapter extends RecyclerView.Adapter<CommonTagViewHolder> {
        private boolean onBind;

        @Override
        public CommonTagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lose_reason, parent, false);
            return new CommonTagViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final CommonTagViewHolder holder, final int position) {
            final CommonTag reson = commonTags.get(position);
            holder.tv_title.setText(reson.getName());
            onBind = true;
            holder.cb.setChecked(reson.isChecked());
            onBind = false;

            holder.layout.setOnTouchListener(Global.GetTouch());
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.cb.toggle();
                }
            });

            holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (!onBind) {
                        reson.setIsChecked(b);
                        if (mode == SELECT_MODE_MULTIPLE) {
                            if (b) {
                                if (!results.contains(reson)) {
                                    results.add(reson);
                                }
                            } else {
                                if (results.contains(reson)) {
                                    results.remove(reson);
                                }
                            }
                        } else if (mode == SELECT_MODE_SINGLE) {
                            results.clear();
                            if (b) {
                                results.add(reson);
                                for (CommonTag tag : commonTags) {
                                    if (tag.equals(reson)) {
                                        continue;
                                    }
                                    tag.setIsChecked(false);
                                }
                            }
                        }
                        notifyDataSetChanged();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return commonTags.size();
        }
    }
}
