package com.loyo.oa.v2.ui.activity.other;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.INotice;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.ui.activity.other.BulletinAddActivity_;
import com.loyo.oa.v2.ui.customview.CusGridView;
import com.loyo.oa.v2.ui.customview.RoundImageView;
import com.loyo.oa.v2.ui.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.ui.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.ui.customview.pullToRefresh.PullToRefreshRecycleView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 描述 :通知公告页
 * com.loyo.oa.v2.ui.activity
 * 作者 : ykb
 * 时间 : 15/8/28.
 */
@EActivity(R.layout.activity_notice)
public class BulletinManagerActivity extends BaseActivity implements PullToRefreshListView.OnRefreshListener2 {

    @ViewById ViewGroup img_title_left;
    @ViewById TextView tv_title_1;
    @ViewById PullToRefreshRecycleView lv_notice;
    @ViewById Button btn_notice_add;
    private ArrayList<Bulletin> bulletins = new ArrayList<>();
    protected PaginationX<Bulletin> mPagination = new PaginationX(20);
    private int mIndex = 1;
    private boolean isTopAdd = true;
    private NoticeAdapter adapter;
    public final int REQUEST_NEW = 1;
    private LinearLayoutManager layoutManager;
    private Permission permission;

    @AfterViews
    void initViews() {
        setTouchView(-1);

        //超级管理员\权限判断
        if (!MainApp.user.isSuperUser()) {
            try {
                permission = (Permission) MainApp.rootMap.get("0402");
                if (!permission.isEnable()) {
                    btn_notice_add.setVisibility(View.INVISIBLE);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                Toast("发布公告权限,code错误:0402");
            }
        }

        img_title_left.setOnTouchListener(Global.GetTouch());
        btn_notice_add.setOnTouchListener(Global.GetTouch());
        lv_notice.setMode(PullToRefreshBase.Mode.BOTH);
        lv_notice.setOnRefreshListener(this);
        tv_title_1.setText("公告通知");

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lv_notice.getRefreshableView().setLayoutManager(layoutManager);
        lv_notice.setMode(PullToRefreshBase.Mode.BOTH);
        getData();
    }

    /**
     * 获取通知列表
     */
    @UiThread
    void getData() {
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", mPagination.getPageIndex());
        map.put("pageSize", isTopAdd ? mPagination.getPageSize() >= 20 ? mPagination.getPageSize() : 20 : 20);
        app.getRestAdapter().create(INotice.class).getNoticeList(map, new RCallback<PaginationX<Bulletin>>() {
            @Override
            public void success(final PaginationX<Bulletin> pagination, final Response response) {
                HttpErrorCheck.checkResponse(response);
                HttpErrorCheck.checkResponse(response);
                if (!PaginationX.isEmpty(pagination)) {
                    ArrayList<Bulletin> lstData_bulletin_current = pagination.getRecords();
                    mPagination = pagination;

                    if (isTopAdd) {
                        bulletins.clear();
                    }
                    bulletins.addAll(lstData_bulletin_current);

                    bindData();
                } else {
                    Global.Toast(!isTopAdd ? R.string.app_list_noMoreData : R.string.app_no_newest_data);
                }
                lv_notice.onRefreshComplete();
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
                lv_notice.onRefreshComplete();
            }
        });
    }

    /**
     * 绑定数据
     */
    private void bindData() {
        if (null == adapter) {
            adapter = new NoticeAdapter(bulletins);
            lv_notice.getRefreshableView().setAdapter(adapter);

        } else {
            adapter.setmDatas(bulletins);
        }
    }


    @Click(R.id.img_title_left)
    void onClick(final View v) {
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, 0, null);
    }


    /**
     * 添加 通知 公告
     */
    @Click(R.id.btn_notice_add)
    void onAddNew() {
        app.startActivityForResult(this, BulletinAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_NEW, null);
    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        isTopAdd = true;
        mPagination.setPageIndex(1);
        getData();
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        isTopAdd = false;
        mPagination.setPageIndex(mPagination.getPageIndex() + 1);
        getData();
    }

    @OnActivityResult(REQUEST_NEW)
    void onCreateResult(final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        Bulletin b = (Bulletin) data.getSerializableExtra("data");
        if (b != null) {
            isTopAdd = true;
            mPagination.setPageIndex(1);
            getData();
        }
    }

    private class BulletinViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_time;
        private TextView tv_title;
        private TextView tv_content;
        private TextView tv_name;
        private RoundImageView iv_avatar;
        private CusGridView gridView;

        public BulletinViewHolder(final View itemView) {
            super(itemView);
            tv_time = (TextView) itemView.findViewById(R.id.tv_notice_time);
            tv_title = (TextView) itemView.findViewById(R.id.tv_notice_title);
            tv_content = (TextView) itemView.findViewById(R.id.tv_notice_content);
            tv_name = (TextView) itemView.findViewById(R.id.tv_notice_publisher);
            iv_avatar = (RoundImageView) itemView.findViewById(R.id.iv_notice_publisher_avatar);
            gridView = (CusGridView) itemView.findViewById(R.id.gv_notice_attachemnts);
        }
    }

    private class NoticeAdapter extends RecyclerView.Adapter<BulletinViewHolder> {
        private ArrayList<Bulletin> mBulletins;

        public NoticeAdapter(final ArrayList<Bulletin> bulletins) {
            mBulletins = bulletins;
        }

        private void setmDatas(final ArrayList<Bulletin> bulletins) {
            mBulletins = bulletins;
            notifyDataSetChanged();
        }

        @Override
        public BulletinViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            View view = LayoutInflater.from(BulletinManagerActivity.this).inflate(R.layout.item_notice_layout, parent, false);
            return new BulletinViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final BulletinViewHolder holder, final int position) {
            final Bulletin bulletin = mBulletins.get(position);
            holder.tv_time.setText(app.df3.format(new Date(bulletin.createdAt * 1000)));
            holder.tv_title.setText(bulletin.title);
            holder.tv_content.setText(bulletin.content);
            holder.tv_name.setText(bulletin.getUserName() + " " + (
                    creatorIsEmpty(bulletin.creator) ? bulletin.creator.depts.get(0).getShortDept().getName() : "")
                    + " " + (creatorIsEmpty(bulletin.creator) ? bulletin.creator.depts.get(0).getShortDept().title : ""));

            ImageLoader.getInstance().displayImage(bulletin.creator.avatar, holder.iv_avatar);
            ArrayList<Attachment> attachments = bulletin.attachments;
            if (null != attachments && !attachments.isEmpty()) {
                holder.gridView.setVisibility(View.VISIBLE);
                SignInGridViewAdapter adapter = new SignInGridViewAdapter(BulletinManagerActivity.this, attachments, false, true, true, 0);
                SignInGridViewAdapter.setAdapter(holder.gridView, adapter);
            } else {
                holder.gridView.setVisibility(View.GONE);
            }
        }

        /*
        判断创建人部门是数据是否有空
         */
        private boolean creatorIsEmpty(User creator) {
            if (null == creator.depts) {
                return false;
            } else if (0 == creator.depts.size()) {
                return false;
            } else if (TextUtils.isEmpty(creator.depts.get(0).getShortDept().title) ||
                    TextUtils.isEmpty(creator.depts.get(0).getShortDept().getName())) {
                return false;
            }
            return true;
        }

        @Override
        public int getItemCount() {
            return mBulletins.size();
        }
    }
}
