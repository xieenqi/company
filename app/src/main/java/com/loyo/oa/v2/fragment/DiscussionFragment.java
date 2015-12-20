package com.loyo.oa.v2.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.Discussion;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.point.IDiscuss;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.ViewHolder;
import com.loyo.oa.v2.tool.customview.RoundImageView;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * com.loyo.oa.v2.fragment
 * 描述 :讨论页面
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
    private Project project;
    private LayoutInflater mInflater;
    private EditText et_comment;
    private TextView tv_send;

    ViewGroup layout_discuss_action;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("project")) {
            project = (Project) getArguments().getSerializable("project");
        }
    }

    @Override
    public void onProjectChange(int status) {
        if(null!=project){
            project.status=status;
        }
        if(layout_discuss_action==null){
            return;
        }
        if(status==Project.STATUS_FINISHED){
            layout_discuss_action.setVisibility(View.GONE);
        }else {
            layout_discuss_action.setVisibility(View.VISIBLE);
        }
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
                    discussions.addAll(lstData_bulletin_current);
                    onLoadSuccess(pagination.getTotalRecords());
                    bindData();
                }
                lv_discuss.onRefreshComplete();
            }

            @Override
            public void failure(RetrofitError error) {
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
            getData();
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (project != null && project.status == Project.STATUS_FINISHED) {
            layout_discuss_action.setVisibility(View.GONE);
        } else {
            layout_discuss_action.setVisibility(View.VISIBLE);
        }
    }

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

            time.setText(app.df9.format(new Date(discussion.getCreatedAt()*1000)));
            name.setText(discussion.getCreator().name);
            content.setText(discussion.getContent());
            ImageLoader.getInstance().displayImage(discussion.getCreator().avatar, iv);

            return view;
        }
    }
}