package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.audio.player.AudioPlayerView;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.MsgAudiomMenu;
import com.loyo.oa.v2.activityui.customer.adapter.CustomerFollowUpGroupAdapter;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.customer.model.FollowUpGroupModel;
import com.loyo.oa.v2.activityui.customer.presenter.CustomerFollowUpListPresenter;
import com.loyo.oa.v2.activityui.customer.presenter.impl.CustomerFollowUpListPresenterImpl;
import com.loyo.oa.v2.activityui.customer.viewcontrol.CustomerFollowUpListView;
import com.loyo.oa.v2.activityui.followup.FollowAddActivity;
import com.loyo.oa.v2.activityui.followup.event.FollowUpRushEvent;
import com.loyo.oa.v2.activityui.followup.viewcontrol.AudioPlayCallBack;
import com.loyo.oa.v2.activityui.signin.bean.AudioModel;
import com.loyo.oa.v2.activityui.signin.bean.CommentModel;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Record;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.permission.CustomerAction;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 【客户下】跟进动态
 * Created by yyy on 16/11/18.
 */

public class CustomerFollowUpListActivity extends BaseLoadingActivity implements PullToRefreshBase.OnRefreshListener2,
        CustomerFollowUpListView, MsgAudiomMenu.MsgAudioMenuCallBack, AudioPlayCallBack, View.OnClickListener {

    public static final int ACTIVITIES_ADD = 101;

    private ViewGroup layout_back;
    private TextView tv_title;
    private TextView voiceView;
    private PullToRefreshListView listView;
    private ViewGroup layout_add;
    private Customer mCustomer;
    private boolean canAdd;
    private boolean isPullOrDown;
    private boolean isChanged;
    private String id;
    private int parent, child;

    /*录音 评论 播放相关*/
    private LinearLayout layout_bottom_voice;
    private LinearLayout layout_bottom_menu;
    private int playVoiceSize = 0;
    private AudioPlayerView audioPlayer;
    private TextView lastView;
    private String lastUrl = "";
    private MsgAudiomMenu msgAudiomMenu;
    private String uuid = StringUtil.getUUID();
    private CustomerFollowUpListPresenter mPresenter;
    private CustomerFollowUpGroupAdapter mAdapter;

    private ArrayList<FollowUpGroupModel> listModel = new ArrayList<>();
    private PaginationX<FollowUpGroupModel> mPagination = new PaginationX<>(20);
    private int pageSize = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_customer_follow);
    }

    @Override
    public void getPageData() {
        isPullOrDown = true;
        mPagination.setPageIndex(1);
        getData(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != voiceView)
            audioPlayer.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioPlayer.onStop();
        layout_bottom_voice.setVisibility(View.GONE);
        layout_bottom_voice.removeAllViews();
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        pageSize=listModel.size();
        isPullOrDown = true;
        mPagination.setPageIndex(1);
        getData(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isPullOrDown = false;
        mPagination.setPageIndex(mPagination.getPageIndex() + 1);
        getData(false);
    }

    private void initView() {
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            mCustomer = (Customer) bundle.getSerializable("mCustomer");
        }

        if (null == mCustomer) {
            Toast("参数异常");
            finish();
        }

        mPresenter = new CustomerFollowUpListPresenterImpl(this, mContext);
        audioPlayer = new AudioPlayerView(this);
        //audioPlayer.onInit();
        layout_back = (ViewGroup) findViewById(R.id.layout_back);
        layout_add = (ViewGroup) findViewById(R.id.layout_add);
        tv_title = (TextView) findViewById(R.id.tv_title);
        listView = (PullToRefreshListView) findViewById(R.id.listView_legworks);
        tv_title = (TextView) findViewById(R.id.tv_title);

        layout_bottom_voice = (LinearLayout) findViewById(R.id.layout_bottom_voice);
        layout_bottom_menu = (LinearLayout) findViewById(R.id.layout_bottom_menu);

        layout_add.setOnClickListener(this);
        layout_back.setOnClickListener(this);
        layout_back.setOnTouchListener(Global.GetTouch());
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);

        msgAudiomMenu = new MsgAudiomMenu(mContext, this, uuid);
        layout_bottom_menu.addView(msgAudiomMenu);

        canAdd = mCustomer != null && mCustomer.state == Customer.NormalCustomer &&
                PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                        mCustomer.state, CustomerAction.FOLLOWUP_ADD);
        if (!canAdd) {
            layout_add.setVisibility(View.GONE);
        } else {
            Utils.btnSpcHideForListViewCus(mContext, listView.getRefreshableView(),
                    layout_add,
                    layout_bottom_menu, msgAudiomMenu.getEditComment());
        }

        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("跟进动态");
        getPageData();
    }

    /**
     * 获取列表数据
     */
    private void getData(boolean isPullOrDown) {
        if (mCustomer == null) {
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("split", true);
        map.put("pageIndex", mPagination.getPageIndex());
        map.put("pageSize", isPullOrDown ? listModel.size() >= pageSize ? listModel.size() : pageSize : pageSize);
        LogUtil.dee("发送数据:" + MainApp.gson.toJson(map));
        mPresenter.getListData(map, mCustomer.id, mPagination.getPageIndex());
    }

    @Override
    public void onBackPressed() {
        if (isChanged) {
            Intent intent = new Intent();
            app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, FinalVariables.REQUEST_CREATE_TASK, intent);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            /*返回*/
            case R.id.layout_back:
                onBackPressed();
                break;

            /*新建*/
            case R.id.layout_add:
                Bundle bundle = new Bundle();
                bundle.putSerializable(Customer.class.getName(), mCustomer);
                bundle.putInt(ExtraAndResult.DYNAMIC_ADD_ACTION, ExtraAndResult.DYNAMIC_ADD_CUSTOMER);
                bundle.putBoolean("isDetail", true);
                app.startActivityForResult(this, FollowAddActivity.class, MainApp.ENTER_TYPE_RIGHT, ACTIVITIES_ADD, bundle);
                break;
        }
    }

    /**
     * 数据绑定
     */
    public void bindData() {
        if (null == mAdapter) {
            mAdapter = new CustomerFollowUpGroupAdapter(mContext, listModel, this, this);
            listView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
        ll_loading.setStatus(LoadingLayout.Success);
        if (isPullOrDown && listModel.size() == 0)
            ll_loading.setStatus(LoadingLayout.Empty);
    }


    /**
     * 评论操作
     */
    private void requestComment(String content) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizzId", id);
        map.put("title", content);
        map.put("commentType", 1); //1文本 2语音
        map.put("bizzType", 1);   //1拜访 2跟进
        LogUtil.dee("评论参数:" + MainApp.gson.toJson(map));
        mPresenter.requestComment(map);
    }

    /**
     * 评论语音
     */
    private void requestComment(Record record) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizzId", id);
        map.put("commentType", 2); //1文本 2语音
        map.put("bizzType", 1);   //1拜访 2跟进
        map.put("audioInfo", record);//语音信息
        LogUtil.dee("评论参数:" + MainApp.gson.toJson(map));
        mPresenter.requestComment(map);
    }

    @Subscribe
    public void onFollowUpRushEvent(FollowUpRushEvent event) {
        LogUtil.dee("onFollowUpRushEvent");
        msgAudiomMenu = null;
        msgAudiomMenu = new MsgAudiomMenu(mContext, this, uuid);
        layout_bottom_menu.removeAllViews();
        layout_bottom_menu.addView(msgAudiomMenu);
        onPullDownToRefresh(listView);
    }

    /**
     * 回调发送评论
     */
    @Override
    public void sendMsg(EditText editText) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            Toast("请输入评论内容!");
            return;
        }
        requestComment(editText.getText().toString());
    }

    public void sebdRecordInfo(Record record) {
        requestComment(record);
    }

    /**
     * 点击评论回调
     */
    @Override
    public void commentEmbl(String id, int parent, int child) {
        this.id = id;
        layout_bottom_menu.setVisibility(View.VISIBLE);
        layout_add.setVisibility(View.GONE);
        msgAudiomMenu.commentEmbl();
        this.parent = parent;
        this.child = child;
    }

    /**
     * 评论删除
     */
    @Override
    public void deleteCommentEmbl(final String id) {
        ActionSheetDialog dialog = new ActionSheetDialog(mContext).builder();
        dialog.addSheetItem("删除评论", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                mPresenter.deleteComment(id);
            }
        });
        dialog.show();
    }

    @Override
    public void rushListData(boolean shw) {
        onPullDownToRefresh(listView);
    }

    /**
     * 评论成功操作
     */
    @Override
    public void commentSuccessEmbl(CommentModel model) {
        if (canAdd) {
            layout_add.setVisibility(View.VISIBLE);
        }
        layout_bottom_menu.setVisibility(View.GONE);
        msgAudiomMenu.commentSuccessEmbl();
        listModel.get(parent).activities.get(child).comments.add(model);
        mAdapter.notifyDataSetChanged();
//        isPullOrDown = true;
//        getData(false);
    }

    /**
     * 获取列表数据成功
     */
    @Override
    public void getListDataSuccesseEmbl(PaginationX<FollowUpGroupModel> paginationX) {
        if (isPullOrDown) {
            listModel.clear();
        }
        mPagination = paginationX;
        listModel.addAll(paginationX.getRecords());
        String dateIndex = "";
        for (int i = 0; i < listModel.size(); i++) {
            FollowUpGroupModel model = listModel.get(i);
            if (dateIndex.equals(model.date)) {
                listModel.get(i - 1).activities.addAll(model.activities);
                listModel.remove(model);
            }
            dateIndex = model.date;
        }
        bindData();
        listView.onRefreshComplete();
    }

    /**
     * 获取列表数据失败
     */
    @Override
    public void getListDataErrorEmbl() {
        listView.onRefreshComplete();
    }

    @Override
    public ViewGroup getBottomMenuLayout() {
        return layout_bottom_menu;
    }

    @Override
    public LoadingLayout getLoadingLayout() {
        return ll_loading;
    }

    /**
     * 列表播放语音回调
     */
    @Override
    public void playVoice(AudioModel audioModel, TextView textView) {
        if (TextUtils.isEmpty(audioModel.url)) {
            Toast("无录音资源!");
            return;
        }
        voiceView = textView;
        layout_bottom_voice.setVisibility(View.VISIBLE);
        layout_bottom_voice.removeAllViews();
        layout_bottom_voice.addView(audioPlayer);
        /*关闭上一条TextView动画*/
        if (playVoiceSize > 0) {
            if (null != lastView)
                MainApp.getMainApp().stopAnim(lastView);
        }

        audioPlayer.onInit();
        if (audioPlayer.isPlaying()) {
            /*点击同一条则暂停播放*/
            if (lastView == textView) {
                LogUtil.dee("同一条");
                MainApp.getMainApp().stopAnim(textView);
                audioPlayer.onPause(textView);
                lastView = null;
            } else {
                LogUtil.dee("另一条");
                //audioPlayer.onResume(textView);
                audioPlayer.onStart(audioModel, textView);
                lastUrl = audioModel.url;
                lastView = textView;
            }
        } else {
            LogUtil.dee("第一次播放");
            //audioPlayer.onResume(textView);
            audioPlayer.onStart(audioModel, textView);
            lastUrl = audioModel.url;
            lastView = textView;
        }
        playVoiceSize++;
    }
}
