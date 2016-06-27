package com.loyo.oa.v2.ui.activity.commonview;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.ui.activity.discuss.adapter.DiscussionAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Discussion;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IDiscuss;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.HaitHelper;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;

import org.androidannotations.annotations.AfterExtras;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【讨论 界面】
 */
@EActivity(R.layout.activity_discussion)
public class DiscussionActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2 {

    public static final int REQUEST_PREVIEW_DISCUSS = 121;

    @Extra("attachmentUUId")
    String attachmentUUId;
    @Extra("isMyUser")
    boolean isMyUser;
    @Extra("status")
    int status;
    @Extra("bizType")
    int bizType;


    @ViewById
    PullToRefreshListView listView_discussion;
    @ViewById
    EditText et_comment;
    private boolean isPullUp = false;

    private PaginationX<Discussion> mPageDiscussion = new PaginationX<>(20);
    private DiscussionAdapter adapter;
    private LinearLayout layout_comment;
    private HaitHelper mHaitHelper;

    @AfterViews
    void init() {
        super.setTitle("讨论");
        listView_discussion.setOnRefreshListener(this);
        listView_discussion.setMode(PullToRefreshBase.Mode.BOTH);

        mHaitHelper = new HaitHelper(et_comment);

        getDDiscussion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mHaitHelper.onActivityResult(requestCode, resultCode, data);
    }

    void getDDiscussion() {

        layout_comment = (LinearLayout) findViewById(R.id.layout_comment);

        if (status == 3) {
            layout_comment.setVisibility(View.GONE);
        }

        final IDiscuss t = RestAdapterFactory.getInstance().build(Config_project.API_URL_EXTRA()).create(IDiscuss.class);
        HashMap<String, Object> body = new HashMap<>();
        body.put("pageIndex", mPageDiscussion.getPageIndex());
        body.put("pageSize", mPageDiscussion.getPageSize());
        body.put("attachmentUUId", attachmentUUId);
        t.getDiscussions(body, new RCallback<PaginationX<Discussion>>() {
            @Override
            public void success(final PaginationX<Discussion> d, final Response response) {
                HttpErrorCheck.checkResponse(response);
                listView_discussion.onRefreshComplete();
                if (isPullUp) {
                    mPageDiscussion.getRecords().addAll(d.getRecords());
                } else {
                    mPageDiscussion = d;
                }
                bindDiscussion();
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                listView_discussion.onRefreshComplete();
                super.failure(error);
            }
        });
    }

    void bindDiscussion() {
        ArrayList<Discussion> sortDiscussion = mPageDiscussion.getRecords();
        Collections.sort(sortDiscussion);
        if (null == adapter) {
            adapter = new DiscussionAdapter(mContext, sortDiscussion);
            adapter.setSelectUserCallback(new DiscussionAdapter.OnSelectUserCallback() {
                @Override
                public void onCallback(HaitHelper.SelectUser user) {
                    mHaitHelper.addSelectUser(user);
                }
            });
            listView_discussion.setAdapter(adapter);
        } else {
            adapter.setmDatas(sortDiscussion);
            adapter.notifyDataSetChanged();
        }
    }

    @AfterExtras
    void initExtra() {
        if (mPageDiscussion == null) {
            mPageDiscussion = new PaginationX<>();
        }
    }

    @Click(R.id.img_title_left)
    void click() {
        onBackPressed();
    }

    @Click(R.id.tv_send)
    void sendDiscussion() {
        String comment = et_comment.getText().toString().trim();
        et_comment.setText("");
        if (StringUtil.isEmpty(comment)) {
            return;
        }

        final IDiscuss t = RestAdapterFactory.getInstance().build(Config_project.API_URL_EXTRA()).create(IDiscuss.class);
        HashMap<String, Object> body = new HashMap<>();
        body.put("attachmentUUId", attachmentUUId);
        body.put("content", comment);
        body.put("bizType", bizType);

        body.put("mentionedUserIds", mHaitHelper.getSelectUser(comment));
        mHaitHelper.clear();

        LogUtil.dll("发送的数据:" + MainApp.gson.toJson(body));
        t.createDiscussion(body, new RCallback<Discussion>() {
            @Override
            public void success(final Discussion d, final Response response) {
                HttpErrorCheck.checkResponse(response);
                onPullDownToRefresh(listView_discussion);
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("data", mPageDiscussion);
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        isPullUp = false;
        mPageDiscussion.setPageIndex(1);
        getDDiscussion();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isPullUp = true;
        mPageDiscussion.setPageIndex(mPageDiscussion.getPageIndex() + 1);
        mPageDiscussion.setPageSize(20);
        getDDiscussion();
    }
}
