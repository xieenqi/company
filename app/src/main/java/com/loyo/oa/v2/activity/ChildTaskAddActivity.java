package com.loyo.oa.v2.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.beans.TaskCheckPoint;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.point.ICheckPoint;
import com.loyo.oa.v2.point.INotice;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.GridViewUtils;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.customview.MyGridView;
import com.loyo.oa.v2.tool.customview.RoundImageView;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshRecycleView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 描述 :【新建子任】页面务类
 * 作者 : ykb
 * 时间 : 15/7/20.
 */
@EActivity(R.layout.activity_childtask_add_layout)
public class ChildTaskAddActivity extends BaseActivity {

    @ViewById ViewGroup layout_child_add_responser;
    @ViewById ViewGroup img_title_left;
    @ViewById ViewGroup img_title_right;

    @ViewById TextView tv_child_add_responser_name;

    @ViewById EditText et_child_add_content;

    @ViewById Button btn_child_add_complete;
    @ViewById Button btn_child_add_cancel_complete;

    @Extra("childTask") TaskCheckPoint chidTask;
    @Extra("Task") Task mTask;

    TaskCheckPoint mChildTask = new TaskCheckPoint();

    NewUser newUser;
    ProgressDialog pd;

    @AfterViews
    void intUi() {
        pd=new ProgressDialog(this);
        setTouchView(-1);
        layout_child_add_responser.setOnTouchListener(Global.GetTouch());
        btn_child_add_complete.setOnTouchListener(Global.GetTouch());
        btn_child_add_cancel_complete.setOnTouchListener(Global.GetTouch());
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());

        if (null != chidTask) {
            mChildTask = chidTask;
            /**
             * 主任务的发布者、负责人、子任务发布者 拥有对子任务的全部编辑权限
             */
            if (mTask.getCreator().isCurrentUser() || mTask.getResponsiblePerson().isCurrentUser() || mChildTask.getCreator().isCurrentUser()) {
                ((ImageView) img_title_right.findViewById(R.id.imgview_title_right)).setImageResource(R.drawable.img_more);
            } else {
                img_title_right.setVisibility(View.GONE);
                if (!mChildTask.getResponsiblePerson().isCurrentUser()) {
                    findViewById(R.id.layout_child_add_action).setVisibility(View.GONE);
                    layout_child_add_responser.setEnabled(false);
                    et_child_add_content.setEnabled(false);
                }
            }

            tv_child_add_responser_name.setText(chidTask.getResponsiblePerson().getRealname());
            et_child_add_content.setText(chidTask.getTitle());
            ((TextView) findViewById(R.id.tv_title_1)).setText("子");
            /**
             * 只要已完成，删除、编辑都不允许，只允许改变为未完成
             */
            if (mChildTask.isAchieved()) {
                btn_child_add_complete.setVisibility(View.GONE);
                //                btn_child_delete_task.setVisibility(View.GONE);
                layout_child_add_responser.setEnabled(false);
                et_child_add_content.setEnabled(false);
                img_title_right.setVisibility(View.GONE);
            }
            /**
             * 1. 未完成子任务不能显示取消完成按钮
             * 2.未完成任务下，是主任务负责人或发布人或子任务创建人，允许所有权限；只是子任务负责人的话，只允许提交完成和未完成
             */
            else {
                btn_child_add_cancel_complete.setVisibility(View.GONE);
                if (mTask.getCreator().isCurrentUser() || mTask.getResponsiblePerson().isCurrentUser() || mChildTask.getCreator().isCurrentUser()) {

                } else if (mChildTask.getResponsiblePerson().isCurrentUser()) {
                    //                    btn_child_delete_task.setVisibility(View.GONE);
                    et_child_add_content.setEnabled(false);
                    img_title_right.setVisibility(View.GONE);
                    layout_child_add_responser.setEnabled(false);
                }
            }

        } else {
            ((TextView) findViewById(R.id.tv_title_1)).setText("新建子任务");
            findViewById(R.id.layout_child_add_action).setVisibility(View.GONE);
        }

        if (null != mTask) {
            boolean isSameUser = (null != mTask.getJoinedUsers() && mTask.getJoinedUsers().size() == 1 && mTask.getJoinedUsers().get(0).equals(mTask.getResponsiblePerson()));
            if ((null == mTask.getJoinedUsers() || mTask.getJoinedUsers().isEmpty() || isSameUser)) {
                newUser = mTask.getResponsiblePerson();
                if (null != newUser) {
                    tv_child_add_responser_name.setText(newUser.getRealname());
                }
            }
        }
    }

    @Click({R.id.layout_child_add_responser, R.id.btn_child_add_complete, R.id.btn_child_add_cancel_complete, R.id.img_title_left, R.id.img_title_right})
    void onClick(View view) {
        switch (view.getId()) {
            //选择添加负责人
            case R.id.layout_child_add_responser:

                ArrayList<NewUser> users = new ArrayList<>();
                if (null != mTask) {
                    if (null != mTask.getJoinedUsers() && !mTask.getJoinedUsers().isEmpty()) {
                        users.addAll(mTask.getJoinedUsers());
                    }
                    if (null != mTask.getResponsiblePerson()) {
                        if (!users.contains(mTask.getResponsiblePerson())) {
                            users.add(mTask.getResponsiblePerson());
                        }
                    }
                }
                Bundle b1 = new Bundle();
                b1.putSerializable("users", users);
                app.startActivityForResult(this, ChildTaskResponserSelectActivity_.class, MainApp.ENTER_TYPE_RIGHT, 300, b1);
                break;
            case R.id.btn_child_add_complete:
            case R.id.btn_child_add_cancel_complete:
                if (view.getId() == R.id.btn_child_add_complete) {
                    mChildTask.setAchieved(true);
                } else {
                    mChildTask.setAchieved(false);
                }
                update(false);
                break;
            case R.id.img_title_left:
                MainApp.getMainApp().finishActivity(this, MainApp.ENTER_TYPE_TOP, RESULT_OK, null);
                break;
            case R.id.img_title_right:
                if (null == chidTask || !mChildTask.getResponsiblePerson().equals(chidTask.getResponsiblePerson())) {
                    create();
                } else {
                    showEditPopu();
                }
                break;
        }
    }

    /**
     * 显示编辑子任务的弹出框
     */
    private void showEditPopu() {
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        View menuView = mLayoutInflater.inflate(R.layout.popu_child_task_edit_layout, null, false);
        menuView.getBackground().setAlpha(50);
        Button btn_child_delete_task = (Button) menuView.findViewById(R.id.btn_child_delete_task);
        Button btnCancel = (Button) menuView.findViewById(R.id.btn_cancel_edit);
        Button btnUpdate = (Button) menuView.findViewById(R.id.btn_child_add_update);
        btn_child_delete_task.setOnTouchListener(Global.GetTouch());
        btnCancel.setOnTouchListener(Global.GetTouch());
        btnUpdate.setOnTouchListener(Global.GetTouch());

        final PopupWindow popupWindow = new PopupWindow(menuView, -1, -1, true);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources()));// 响应键盘三个主键的必须步骤
        popupWindow.showAtLocation(findViewById(R.id.tv_title_1), Gravity.BOTTOM, 0, 0);

        menuView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupWindow.dismiss();
                return false;
            }
        });

        PopuOnClickListener listener = new PopuOnClickListener(popupWindow);
        btn_child_delete_task.setOnClickListener(listener);
        btnCancel.setOnClickListener(listener);
        btnUpdate.setOnClickListener(listener);
    }

    /**
     * 处理popuwindow里按钮的点击事件
     */
    private class PopuOnClickListener implements View.OnClickListener {
        private PopupWindow mWindow;

        PopuOnClickListener(PopupWindow window) {
            mWindow = window;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_child_delete_task:
                    delete();
                    break;
                case R.id.btn_child_add_update:
                    update(true);
                    break;

            }
            mWindow.dismiss();
        }
    }

    /**
     * 删除子任务
     */
    private void delete() {
        MainApp.getMainApp().getRestAdapter().create(ICheckPoint.class).deleteChildTask(mTask.getId(), mChildTask.getId(), new RCallback<TaskCheckPoint>() {
            @Override
            public void success(TaskCheckPoint taskCheckPoint, Response response) {
                Toast("删除子任务成功");
                Intent delIntent = new Intent();
                delIntent.putExtra("childTaskId", chidTask.getId());
                MainApp.getMainApp().finishActivity(ChildTaskAddActivity.this, MainApp.ENTER_TYPE_TOP, RESULT_OK, delIntent);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast("删除子任务失败");
                super.failure(error);
            }
        });
    }

    /**
     * 创建子任务
     */
    private synchronized void create() {
pd.show();
        if (null == newUser) {
            Toast("请选择负责人");
            return;
        }
        if (TextUtils.isEmpty(et_child_add_content.getText().toString())) {
            Toast("请填写内容");
            return;
        }
        mChildTask.setTitle(et_child_add_content.getText().toString());
        mChildTask.setResponsiblePerson(newUser);

        HashMap<String, Object> datas = new HashMap<String, Object>();
        datas.put("content", mChildTask.getTitle());
        datas.put("responsiblePerson", mChildTask.getResponsiblePerson());

        MainApp.getMainApp().getRestAdapter().create(ICheckPoint.class).createChildTask(mTask.getId(), datas, new RCallback<TaskCheckPoint>() {
            @Override
            public void success(TaskCheckPoint taskCheckPoint, Response response) {
                pd.dismiss();
                Toast("创建子任务成功");
                mChildTask = taskCheckPoint;
                Intent intent = new Intent();
                intent.putExtra("childTask", taskCheckPoint);
                MainApp.getMainApp().finishActivity(ChildTaskAddActivity.this, MainApp.ENTER_TYPE_TOP, RESULT_OK, intent);
            }

            @Override
            public void failure(RetrofitError error) {
                pd.dismiss();
                Toast("创建子任务失败");
                LogUtil.d("LOG","创建子任务失败"+error.getMessage());
                super.failure(error);
            }
        });
    }

    /**
     * 更新子任务
     *
     * @param update 是否需要更新子任务其他内容
     */
    private void update(boolean update) {
        if (TextUtils.isEmpty(tv_child_add_responser_name.getText().toString())) {
            Toast("请选择负责人");
            return;
        }
//        showDialog();
        if (update) {
            mChildTask.setTitle(et_child_add_content.getText().toString());
            if (null != newUser) {
                mChildTask.setResponsiblePerson(newUser);
            }
        }

        HashMap<String, Object> datas2 = new HashMap<String, Object>();
        datas2.put("title", mChildTask.getTitle());
        datas2.put("achieved", mChildTask.isAchieved());
        datas2.put("responsiblePersonId", mChildTask.getResponsiblePerson().getId());
        app.getRestAdapter().create(ICheckPoint.class).updateChildTask(mChildTask.getTaskId(), mChildTask.getId(), datas2, new RCallback<TaskCheckPoint>() {
            @Override
            public void success(TaskCheckPoint taskCheckPoint, Response response) {
//                dismissDialog();
                Toast("更新子任务成功");
                Intent completeIntent = new Intent();
                completeIntent.putExtra("childTask", mChildTask);
                MainApp.getMainApp().finishActivity(ChildTaskAddActivity.this, MainApp.ENTER_TYPE_TOP, RESULT_OK, completeIntent);
            }

            @Override
            public void failure(RetrofitError error) {
//                dismissDialog();
                Toast("更新子任务失败");
                super.failure(error);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case 300:
                NewUser user = (NewUser) data.getSerializableExtra("user");
                if (user != null) {
                    tv_child_add_responser_name.setText(user.getRealname());
                    newUser = user;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 描述 :通知公告页
     * com.loyo.oa.v2.activity
     * 作者 : ykb
     * 时间 : 15/8/28.
     */
     @EActivity(R.layout.activity_notice)
    public static class BulletinManagerActivity extends BaseActivity implements PullToRefreshListView.OnRefreshListener2 {
        @ViewById ViewGroup img_title_left;
        @ViewById TextView tv_title_1;

        @ViewById PullToRefreshRecycleView lv_notice;
        @ViewById Button btn_notice_add;
        private ArrayList<Bulletin> bulletins = new ArrayList<>();
        protected PaginationX<Bulletin> mPagination = new PaginationX(20);
        private int mIndex = 1;
        private boolean isTopAdd = true;
        private NoticeAdapter adapter;

        public final static int REQUEST_NEW = 1;

        private LinearLayoutManager layoutManager;

        @AfterViews
        void initViews() {
            setTouchView(-1);
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
            HashMap<String, Object> map = new HashMap<>();
            map.put("pageIndex", mPagination.getPageIndex());
            map.put("pageSize", isTopAdd ? mPagination.getPageSize() >= 20 ? mPagination.getPageSize() : 20 : 20);
            app.getRestAdapter().create(INotice.class).getNoticeList(map, new RCallback<PaginationX<Bulletin>>() {
                @Override
                public void success(PaginationX<Bulletin> pagination, Response response) {
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
                    LogUtil.d(" 通知的数据： "+MainApp.gson.toJson(pagination));
                }

                @Override
                public void failure(RetrofitError error) {
                    super.failure(error);
                    lv_notice.onRefreshComplete();
                    Toast("获取通知失败" + error.getMessage());
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
        void onClick(View v) {
            app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, 0, null);
        }

        @Click(R.id.btn_notice_add)
        void onAddNew() {
            app.startActivityForResult(this, BulletinAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_NEW, null);
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

        @OnActivityResult(REQUEST_NEW)
        void onCreateResult(int resultCode, Intent data) {
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
            private MyGridView gridView;

            public BulletinViewHolder(View itemView) {
                super(itemView);
                tv_time = (TextView) itemView.findViewById(R.id.tv_notice_time);
                tv_title = (TextView) itemView.findViewById(R.id.tv_notice_title);
                tv_content = (TextView) itemView.findViewById(R.id.tv_notice_content);
                tv_name = (TextView) itemView.findViewById(R.id.tv_notice_publisher);
                iv_avatar = (RoundImageView) itemView.findViewById(R.id.iv_notice_publisher_avatar);
                gridView = (MyGridView) itemView.findViewById(R.id.gv_notice_attachemnts);
            }
        }

        private class NoticeAdapter extends RecyclerView.Adapter<BulletinViewHolder> {
            private ArrayList<Bulletin> mBulletins;

            public NoticeAdapter(ArrayList<Bulletin> bulletins) {
                mBulletins = bulletins;
            }

            private void setmDatas(ArrayList<Bulletin> bulletins) {
                mBulletins = bulletins;
                notifyDataSetChanged();
            }

            @Override
            public BulletinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(BulletinManagerActivity.this).inflate(R.layout.item_notice_layout, parent, false);
                return new BulletinViewHolder(view);
            }

            @Override
            public void onBindViewHolder(BulletinViewHolder holder, int position) {
                final Bulletin bulletin = mBulletins.get(position);
                holder.tv_time.setText(app.df3.format(new Date(bulletin.getCreatedAt() * 1000)));
                holder.tv_title.setText(bulletin.getTitle());
                holder.tv_content.setText(bulletin.getContent());
               holder.tv_name.setText(bulletin.getUserName() + " " + bulletin.getDeptName() + " " + bulletin.getPosition());
               ImageLoader.getInstance().displayImage(bulletin.getCreator().getAvatar(), holder.iv_avatar);
                ArrayList<Attachment> attachments = bulletin.getAttachments();
                if (null != attachments && !attachments.isEmpty()) {

                    holder.gridView.setVisibility(View.VISIBLE);
                    SignInGridViewAdapter adapter = new SignInGridViewAdapter(BulletinManagerActivity.this, attachments, false, true);
                    //                SignInGridViewAdapter.setAdapter(holder.gridView,adapter);
                    holder.gridView.setAdapter(adapter);
                    GridViewUtils.updateGridViewLayoutParams(holder.gridView, 3);
                } else {
                    holder.gridView.setVisibility(View.GONE);
                }

            }

            @Override
            public int getItemCount() {
                return mBulletins.size();
            }
        }
    }
}