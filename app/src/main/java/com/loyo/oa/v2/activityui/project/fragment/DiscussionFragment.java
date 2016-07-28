package com.loyo.oa.v2.activityui.project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.discuss.bean.Discussion;
import com.loyo.oa.v2.activityui.other.bean.User;
import com.loyo.oa.v2.activityui.project.HttpProject;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.point.IDiscuss;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.HaitHelper;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.TimeFormatUtil;
import com.loyo.oa.v2.tool.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * com.loyo.oa.v2.ui.fragment
 * 描述 :项目讨论页面
 * 作者 : ykb
 * 时间 : 15/9/8.
 */
public class DiscussionFragment extends BaseFragment implements PullToRefreshListView.OnRefreshListener2 {

    private View mView;
    private PullToRefreshListView lv_discuss;
    private ArrayList<Discussion> discussions = new ArrayList<>();
    protected PaginationX<Discussion> mPagination = new PaginationX(20);
    private boolean isTopAdd = true;
    private DiscussionAdapter adapter;
    private HttpProject project;
    private LayoutInflater mInflater;
    private EditText et_comment;
    private TextView tv_send;
    public ViewGroup layout_discuss_action;
    private HaitHelper mHaitHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("project")) {
            project = (HttpProject) getArguments().getSerializable("project");
        }
    }

    @Override
    public void onProjectChange(int status) {
        if (null != project) {
            project.status = status;
        }
        if (layout_discuss_action == null) {
            return;
        }

        /**
         * 说 明: 取消讨论权限
         * 时 间:2016.4.11
         * */
/*        if (status == Project.STATUS_FINISHED) {
            layout_discuss_action.setVisibility(View.GONE);
        } else {
            layout_discuss_action.setVisibility(View.VISIBLE);
        }*/
    }

    /**
     * 绑定数据
     */
    private void bindData() {
        if (null == adapter) {
            if (null == adapter) {
                adapter = new DiscussionAdapter();
                lv_discuss.setAdapter(adapter);
            }
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 定位到最后一项
     */
    public void scrollToBottom() {
        if (adapter != null && adapter.getCount() > 0) {
            lv_discuss.getRefreshableView().setSelection(adapter.getCount() - 1);
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("attachmentUUId", project.attachmentUUId);
        map.put("pageIndex", mPagination.getPageIndex());
        map.put("pageSize", isTopAdd ? discussions.size() >= 20 ? discussions.size() : 20 : 20);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_EXTRA()).create(IDiscuss.class).getDiscussions(map, new RCallback<PaginationX<Discussion>>() {
            @Override
            public void success(PaginationX<Discussion> pagination, Response response) {
                if (!PaginationX.isEmpty(pagination)) {
                    ArrayList<Discussion> lstData_bulletin_current = pagination.getRecords();
                    mPagination = pagination;
                    if (isTopAdd) {
                        discussions.clear();
                    }
                    Collections.reverse(lstData_bulletin_current);
                    discussions.addAll(lstData_bulletin_current);
                    onLoadSuccess(pagination.getTotalRecords());
                    bindData();
                }
                lv_discuss.onRefreshComplete();
                scrollToBottom();
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
                lv_discuss.onRefreshComplete();
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = true;
        mPagination.setPageIndex(1);
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = false;
        mPagination.setPageIndex(mPagination.getPageIndex() + 1);
        getData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mInflater = inflater;
            mView = inflater.inflate(R.layout.fragment_discussion, container, false);
            lv_discuss = (PullToRefreshListView) mView.findViewById(R.id.lv_discussion);
            et_comment = (EditText) mView.findViewById(R.id.et_comment);
            tv_send = (TextView) mView.findViewById(R.id.tv_send);
            tv_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendDiscussion();
                }
            });
            lv_discuss.setMode(PullToRefreshBase.Mode.BOTH);
            lv_discuss.setOnRefreshListener(this);
            layout_discuss_action = (ViewGroup) mView.findViewById(R.id.layout_discuss_action);

            mHaitHelper = new HaitHelper(this, et_comment);
            getData();
        }
        return mView;
    }

    public HaitHelper getHaitHelper() {
        return mHaitHelper;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         * 说 明: 取消讨论权限
         * 时 间:2016.4.11
         * */
        /*if (project != null && project.status == Project.STATUS_FINISHED) {
            layout_discuss_action.setVisibility(View.GONE);
        } else {
            layout_discuss_action.setVisibility(View.VISIBLE);
        }*/
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        mHaitHelper.onActivityResult(requestCode, resultCode, data);
//    }

    /**
     * 发表讨论
     */
    void sendDiscussion() {
        String comment = et_comment.getText().toString().trim();
        et_comment.setText("");
        if (StringUtil.isEmpty(comment)) {
            return;
        }

        HashMap<String, Object> body = new HashMap<>();
        body.put("attachmentUUId", project.attachmentUUId);
        body.put("content", comment);
        body.put("bizType", 5);

        body.put("mentionedUserIds", mHaitHelper.getSelectUser(comment));
        mHaitHelper.clear();

        LogUtil.dll("发送的数据:" + MainApp.gson.toJson(body));

        RestAdapterFactory.getInstance().build(Config_project.API_URL_EXTRA()).create(IDiscuss.class).createDiscussion(body, new RCallback<Discussion>() {
            @Override
            public void success(Discussion d, Response response) {
                isTopAdd = true;
                mPagination.setPageIndex(1);
                getData();
            }
        });
    }

    private class DiscussionAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return discussions.size();
        }

        @Override
        public Discussion getItem(int i) {
            return discussions.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            User creator = discussions.get(position).getCreator();
            if (null == creator) {
                return super.getItemViewType(position);
            }

            if (creator.isCurrentUser()) {
                return 0;
            }
            return 1;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final Discussion discussion = getItem(i);
            if (null == view) {
                if (getItemViewType(i) == 0) {
                    view = mInflater.inflate(R.layout.item_discussion_send, viewGroup, false);
                } else {
                    view = mInflater.inflate(R.layout.item_discussion_receive, viewGroup, false);
                }
            }
            RoundImageView iv = ViewHolder.get(view, R.id.iv_avatar);
            TextView time = ViewHolder.get(view, R.id.tv_discuss_time);
            TextView name = ViewHolder.get(view, R.id.tv_discuss_sender);
            TextView content = ViewHolder.get(view, R.id.tv_discuss_content);

            time.setText(TimeFormatUtil.toMd_Hm(discussion.getCreatedAt()));
            name.setText(discussion.getCreator().name);
            content.setText(discussion.getContent());
            ImageLoader.getInstance().displayImage(discussion.getCreator().avatar, iv);
            iv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (TextUtils.isEmpty(discussion.getCreator().getId()) || discussion.getCreator().id.equals(MainApp.user.id)) {
                        return false;
                    }
                    mHaitHelper.addSelectUser(new HaitHelper.SelectUser(discussion.getCreator().getRealname()
                            , discussion.getCreator().getId()));
                    return true;
                }
            });
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(discussion.getCreator().getId()) || discussion.getCreator().id.equals(MainApp.user.id)) {
                        return;
                    }
                    showLoading("");
                    Common.getUserInfo(getActivity(), app, discussion.getCreator().getId());
                }
            });
            return view;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            switch (requestCode) {
                case ExtraAndResult.REQUEST_CODE:
                    getHaitHelper().onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }

    }
}