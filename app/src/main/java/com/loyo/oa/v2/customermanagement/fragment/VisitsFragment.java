package com.loyo.oa.v2.customermanagement.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
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
import com.loyo.oa.v2.activityui.customer.adapter.CustomerSigninNewGroupAdapter;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.customer.model.SigninNewGroupModel;
import com.loyo.oa.v2.activityui.customer.presenter.SigninListFragPresenter;
import com.loyo.oa.v2.activityui.customer.presenter.impl.SigninListFragPresenterImpl;
import com.loyo.oa.v2.activityui.customer.viewcontrol.CustomerSigninNewListView;
import com.loyo.oa.v2.activityui.followup.viewcontrol.AudioPlayCallBack;
import com.loyo.oa.v2.activityui.signin.SignInActivity;
import com.loyo.oa.v2.activityui.signin.bean.AudioModel;
import com.loyo.oa.v2.activityui.signin.bean.CommentModel;
import com.loyo.oa.v2.activityui.signin.event.SigninRushEvent;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Record;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.CustomerAction;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by EthanGong on 2017/2/9.
 */

public class VisitsFragment extends CustomerChildFragment
        implements PullToRefreshBase.OnRefreshListener2,
        CustomerSigninNewListView, MsgAudiomMenu.MsgAudioMenuCallBack, AudioPlayCallBack, View.OnClickListener
{

    View view;
    public LoadingLayout ll_loading;
    private TextView voiceView;
    private PullToRefreshListView listView;
    private ViewGroup layout_add;
    private Customer mCustomer;
    private boolean canAdd;
    private boolean isPullOrDown;
    private boolean isChanged;
    private String id;
    private int parent, child;

    private PaginationX<SigninNewGroupModel> mPagination = new PaginationX<>(20);
    private ArrayList<SigninNewGroupModel> listModel = new ArrayList<>();
    private CustomerSigninNewGroupAdapter mAdapter;


    /*录音 评论 播放相关*/
    private LinearLayout layout_bottom_voice;
    private LinearLayout layout_bottom_menu;
    private int playVoiceSize = 0;
    private AudioPlayerView audioPlayer;
    private TextView lastView;
    private String lastUrl = "";
    private MsgAudiomMenu msgAudiomMenu;
    private String uuid = StringUtil.getUUID();
    private SigninListFragPresenter mPresenter;
    private int pageSize = 5;

    public VisitsFragment(Customer customer, int index, OnTotalCountChangeListener listener, String title) {
        super(customer, index, listener, title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_visits, container, false);

            initView();
        }
        return view;
    }

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
    public void onDestroy() {
        super.onDestroy();
        audioPlayer.onStop();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        //TODO 临时这样写后期重构
        int size = listModel.size();
        for (int i = 0; i < size; i++) {
            pageSize += listModel.get(i).activities.size();
        }
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

    public void setCustomer(Customer customer) {
        mCustomer = customer;
        canAdd = customer != null &&
                PermissionManager.getInstance().hasCustomerAuthority(
                        customer.relationState,
                        customer.state,
                        CustomerAction.APPROVAL_ADD);
        this.totalCount = customer.counter.getVisit();
    }

    public void reloadWithCustomer(Customer customer) {
        mCustomer = customer;
        canAdd = customer != null &&
                PermissionManager.getInstance().hasCustomerAuthority(
                        customer.relationState,
                        customer.state,
                        CustomerAction.APPROVAL_ADD);
        this.totalCount = customer.counter.getVisit();
        if (view == null) {
            return;
        }
        layout_add.setVisibility(canAdd ? View.VISIBLE : View.GONE);
    }

    private void initView() {

        if (mCustomer == null) {
            return;
        }

        ll_loading = (LoadingLayout) view.findViewById(R.id.ll_loading);
        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                getPageData();
            }
        });
        mPresenter = new SigninListFragPresenterImpl(this);
        audioPlayer = new AudioPlayerView(getActivity());
        //audioPlayer.onInit();
        layout_add = (ViewGroup) view.findViewById(R.id.layout_add);
        listView = (PullToRefreshListView) view.findViewWithTag("listView_visit");

        layout_bottom_voice = (LinearLayout) view.findViewById(R.id.layout_bottom_voice);
        layout_bottom_menu = (LinearLayout) view.findViewById(R.id.layout_bottom_menu);

        layout_add.setOnClickListener(this);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);

        msgAudiomMenu = new MsgAudiomMenu(getActivity(), this, uuid,this);
        layout_bottom_menu.addView(msgAudiomMenu);
        canAdd = mCustomer != null &&
                PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                        mCustomer.state, CustomerAction.VISIT);
        if (!canAdd) {
            layout_add.setVisibility(View.GONE);
        } else {
            layout_add.setOnTouchListener(Global.GetTouch());
            Utils.btnSpcHideForListViewCus(getActivity(), listView.getRefreshableView(),
                    layout_add,
                    layout_bottom_menu, msgAudiomMenu.getEditComment());
        }
        getPageData();
    }

    /**
     * 数据绑定
     */
    public void bindData() {
        if (null == mAdapter) {
            mAdapter = new CustomerSigninNewGroupAdapter(getActivity(), listModel, this, this);
            listView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
        ll_loading.setStatus(LoadingLayout.Success);
        if (isPullOrDown && listModel.size() == 0)
            ll_loading.setStatus(LoadingLayout.Empty);
    }

    /**
     * 获取列表数据
     */
    private void getData(boolean isPullOrDown) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("split", true);
        map.put("customerId", mCustomer.id);
        map.put("pageIndex", mPagination.getPageIndex());
        map.put("pageSize", isPullOrDown ? listModel.size() >= pageSize ? listModel.size() : pageSize : pageSize);
        LogUtil.dee("发送数据:" + MainApp.gson.toJson(map));
        mPresenter.getListData(map, mPagination.getPageIndex());
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
    public void onSigninNewRushEvent(SigninRushEvent event) {
        LogUtil.dee("onFollowUpRushEvent");
        if (view == null) {
            return;
        }
        msgAudiomMenu = null;
        msgAudiomMenu = new MsgAudiomMenu(getActivity(), this, uuid,this);
        layout_bottom_menu.removeAllViews();
        layout_bottom_menu.addView(msgAudiomMenu);
        onPullDownToRefresh(listView);
        notifyDropRemindRefresh();
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
        ActionSheetDialog dialog = new ActionSheetDialog(getActivity()).builder();
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
    }

    /**
     * 获取列表数据成
     */
    @Override
    public void getListDataSuccesseEmbl(BaseBeanT<PaginationX<SigninNewGroupModel>> paginationX) {
        listView.onRefreshComplete();
        VisitsFragment.this.totalCount = paginationX.data.totalRecords;
        notifyTotalCountChange();
        if (isPullOrDown) {
            listModel.clear();
        }
        mPagination = paginationX.data;
        listModel.addAll(paginationX.data.getRecords());
        String dateIndex = "";
        for (int i = 0; i < listModel.size(); i++) {
            SigninNewGroupModel model = listModel.get(i);
            if (dateIndex.equals(model.date)) {
                listModel.get(i - 1).activities.addAll(model.activities);
                listModel.remove(model);
            }
            dateIndex = model.date;
        }
        bindData();
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
    public LoadingLayout getLoading() {
        return ll_loading;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /*新建*/
            case R.id.layout_add:
                onAddVisit();
                break;
        }
    }

    public void onAddVisit() {
        canAdd = mCustomer != null &&
                PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                        mCustomer.state, CustomerAction.VISIT);
        if (!canAdd) {
            sweetAlertDialogView.alertIcon("提示", "你没有拜访权限");
        }
        else if (!PermissionManager.getInstance().hasPermission(BusinessOperation.CUSTOMER_VISIT)) {
            sweetAlertDialogView.alertIcon(null, "此功能权限已关闭\n请联系管理员开启后再试!");
        } else {
            Bundle b = new Bundle();
            b.putSerializable("data", mCustomer);
            app.startActivityForResult(getActivity(), SignInActivity.class, MainApp.ENTER_TYPE_RIGHT,
                    FinalVariables.REQUEST_CREATE_LEGWORK, b);
        }
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

        audioPlayer.onInit(textView);
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
