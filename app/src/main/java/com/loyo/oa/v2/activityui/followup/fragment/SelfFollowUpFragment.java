package com.loyo.oa.v2.activityui.followup.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.audio.player.AudioPlayerView;
import com.loyo.oa.common.utils.UmengAnalytics;
import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.DynamicFilterTimeModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.MsgAudiomMenu;
import com.loyo.oa.v2.activityui.followup.FollowSelectActivity;
import com.loyo.oa.v2.activityui.followup.adapter.FollowUpListAdapter;
import com.loyo.oa.v2.activityui.followup.common.FollowFilterMenuModel;
import com.loyo.oa.v2.activityui.followup.event.FollowUpRushEvent;
import com.loyo.oa.v2.activityui.followup.model.FolloUpConfig;
import com.loyo.oa.v2.activityui.followup.model.FollowFilter;
import com.loyo.oa.v2.activityui.followup.model.FollowUpListModel;
import com.loyo.oa.v2.activityui.followup.persenter.impl.FollowUpFragPresenterImpl;
import com.loyo.oa.v2.activityui.followup.viewcontrol.AudioPlayCallBack;
import com.loyo.oa.v2.activityui.followup.viewcontrol.FollowUpListView;
import com.loyo.oa.v2.activityui.signin.bean.AudioModel;
import com.loyo.oa.v2.activityui.signin.bean.CommentModel;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Record;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 【我的跟进】列表
 * Created by yyy on 16/6/1.
 */
public class SelfFollowUpFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2,
        FollowUpListView, View.OnClickListener, MsgAudiomMenu.MsgAudioMenuCallBack, AudioPlayCallBack {

    private View mView;
    private Button btn_add;
    private TextView voiceView;
    private DropDownMenu filterMenu;
    private PullToRefreshListView listView;
    private LinearLayout layout_bottom_menu;

    private ArrayList<FollowFilter> mTags;
    private String menuTimeKey = "0"; /*时间*/
    private String method = "", typeId = "", activityType = ""; /*筛选*/

    private boolean isPullOrDown;
    private int commentPosition;

    private ArrayList<FollowUpListModel> listModel = new ArrayList<>();
    private PaginationX<FollowUpListModel> mPagination = new PaginationX<>(20);

    private FollowUpListAdapter mAdapter;
    private FollowUpFragPresenterImpl mPresenter;
    private MsgAudiomMenu msgAudiomMenu;
    private String uuid = StringUtil.getUUID();

    /*录音播放相关*/
    private LinearLayout layout_bottom_voice;
    private int playVoiceSize = 0;
    private TextView lastView;
    private String lastUrl = "";
    private LoadingLayout ll_loading;
    private AudioPlayerView audioPlayer;


    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_followup_self, null);
            initView(mView);
            loadFilterOptions();
        }
        return mView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != voiceView)
            audioPlayer.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        audioPlayer.onStop();
        layout_bottom_voice.setVisibility(View.GONE);
        layout_bottom_voice.removeAllViews();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        isPullOrDown = true;
        mPagination.setPageIndex(1);
        getData(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isPullOrDown = false;
        mPagination.setPageIndex(mPagination.getPageIndex() + 1);
        getData(true);
    }

    public void initView(View view) {
        ll_loading = (LoadingLayout) view.findViewById(R.id.ll_loading);
        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                initPageData();
            }
        });
        mTags = FolloUpConfig.getFolloUpStageCache();
        mPresenter = new FollowUpFragPresenterImpl(this, getActivity());
        audioPlayer = new AudioPlayerView(getActivity());
        //audioPlayer.onInit();
        btn_add = (Button) view.findViewById(R.id.btn_add);
        filterMenu = (DropDownMenu) view.findViewById(R.id.drop_down_menu);

        layout_bottom_menu = (LinearLayout) view.findViewById(R.id.layout_bottom_menu);
        layout_bottom_voice = (LinearLayout) view.findViewById(R.id.layout_bottom_voice);
        listView = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        btn_add.setOnClickListener(this);
        btn_add.setOnTouchListener(Global.GetTouch());
        if (msgAudiomMenu == null) {
            msgAudiomMenu = new MsgAudiomMenu(getActivity(), this, uuid,this);
            layout_bottom_menu.addView(msgAudiomMenu);
        }
        Utils.btnSpcHideForListViewTest(getActivity(), listView.getRefreshableView(),
                btn_add,
                layout_bottom_menu, msgAudiomMenu.getEditComment());
    }

    /**
     * 加载顶部菜单
     */
    private void loadFilterOptions() {
        List<FilterModel> options = new ArrayList<>();
        options.add(DynamicFilterTimeModel.getFilterModel());     //时间
        options.add(FollowFilterMenuModel.getFilterModel(mTags));  //筛选
        DefaultMenuAdapter adapter = new DefaultMenuAdapter(getContext(), options);
        filterMenu.setMenuAdapter(adapter);
        adapter.setCallback(new OnMenuModelsSelected() {
            @Override
            public void onMenuModelsSelected(int menuIndex, List<MenuModel> selectedModels, Object userInfo) {
                filterMenu.close();

                switch (menuIndex) {

                    /*时间*/
                    case 0:
                        MenuModel model = selectedModels.get(0);
                        menuTimeKey = selectedModels.get(0).getKey();
                        filterMenu.headerTabBar.setTitleAtPosition(model.getValue(), menuIndex);
                        UmengAnalytics.umengSend(mActivity, UmengAnalytics.timeFollow);
                        break;

                    /*筛选*/
                    case 1:
                        String clas = userInfo.getClass() + "";
                        if (userInfo != null && clas.contains("HashMap")) {
                            HashMap<String, MenuModel> map = (HashMap<String, MenuModel>) userInfo;
                            MenuModel field1 = map.get("activityType");
                            MenuModel field2 = map.get("typeId");
                            MenuModel field3 = map.get("method");
                            activityType = field1.getKey();  //跟进类型
                            typeId = field2.getKey();        //跟进方式
                            method = field3.getKey();        //跟进对象
                        } else {
                            method = "";
                            typeId = "";
                            activityType = "";
                        }
                        UmengAnalytics.umengSend(mActivity, UmengAnalytics.filterFollow);
                        break;
                }
                initPageData();
            }
        });
        initPageData();
    }


    private void initPageData() {
        ll_loading.setStatus(LoadingLayout.Loading);
        mPagination.setPageIndex(1);
        isPullOrDown = true;
        getData(true);
    }


    /**
     * 数据绑定
     */
    public void bindData() {
        LogUtil.d("开始: " + System.currentTimeMillis());
        if (null == mAdapter) {
            mAdapter = new FollowUpListAdapter(getActivity(), listModel, this, this);
            listView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
        LogUtil.d("结束: " + System.currentTimeMillis());
    }


    /**
     * 发送评论文字
     */
    private void requestComment(String content) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizzId", listModel.get(commentPosition).id);
        map.put("title", content);
        map.put("commentType", 1); //1文本 2语音
        map.put("bizzType", 2);   //1拜访 2跟进
        LogUtil.dee("评论参数:" + MainApp.gson.toJson(map));
        mPresenter.requestComment(map);
    }

    /**
     * 发送语音
     */
    private void requestComment(Record record) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizzId", listModel.get(commentPosition).id);
        map.put("commentType", 2); //1文本 2语音
        map.put("bizzType", 2);    //1拜访 2跟进
        map.put("audioInfo", record);//语音信息
        LogUtil.dee("评论参数:" + MainApp.gson.toJson(map));
        mPresenter.requestComment(map);
    }

    /**
     * 获取Self列表数据
     */
    private void getData(boolean isPullOrDown) {
        if (null == MainApp.user || null == MainApp.user.id) {
            Toast("正在拉去数据,请稍后..");
            getActivity().finish();
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", MainApp.user.id);//我的传id,团队则空着
        map.put("xpath", "");
        map.put("timeType", menuTimeKey);//时间查询
        map.put("method", method); //跟进类型0:全部 2:线索 1:客户
        map.put("typeId", typeId);
        map.put("activityType", activityType);
        map.put("split", true);
        map.put("pageIndex", mPagination.getPageIndex());
        map.put("pageSize", isPullOrDown ? listModel.size() >= 5 ? listModel.size() : 5 : 5);
        LogUtil.d("发送数据:" + MainApp.gson.toJson(map));
        mPresenter.getListData(map, mPagination.getPageIndex());
    }

    /**
     * 回调刷新
     */
    @Subscribe
    public void onFollowUpRushEvent(FollowUpRushEvent event) {
        msgAudiomMenu = null;
        msgAudiomMenu = new MsgAudiomMenu(getActivity(), this, uuid,this);
        layout_bottom_menu.removeAllViews();
        layout_bottom_menu.addView(msgAudiomMenu);
        getData(false);
    }

    /**
     * 点击评论回调
     */
    @Override
    public void commentEmbl(int position) {
        layout_bottom_voice.setVisibility(View.GONE);
        layout_bottom_voice.removeAllViews();
        commentPosition = position;
        layout_bottom_menu.setVisibility(View.VISIBLE);
        btn_add.setVisibility(View.GONE);
        msgAudiomMenu.commentEmbl();
        UmengAnalytics.umengSend(mActivity, UmengAnalytics.replyFollow);
    }

    /**
     * 长按评论删除
     */
    @Override
    public void deleteCommentEmbl(final String id) {
        ActionSheetDialog dialog = new ActionSheetDialog(mActivity).builder();
        dialog.addSheetItem("删除评论", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                mPresenter.deleteComment(id);
            }
        });
        dialog.show();
    }

    /**
     * 刷新列表数据
     */
    @Override
    public void rushListData(boolean shw) {
        isPullOrDown = true;
        getData(shw);
    }

    /**
     * 评论成功操作
     */
    @Override
    public void commentSuccessEmbl(CommentModel modle) {
        layout_bottom_menu.setVisibility(View.GONE);
        msgAudiomMenu.commentSuccessEmbl();
        listModel.get(commentPosition).comments.add(modle);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 获取列表数据成功
     */
    @Override
    public void getListDataSuccesseEmbl(BaseBeanT<PaginationX<FollowUpListModel>> paginationX) {
        LogUtil.d("数据: " + System.currentTimeMillis());
        listView.onRefreshComplete();
        if (isPullOrDown) {
            listModel.clear();
        }
        if (paginationX == null) {
            return;
        }
        mPagination = paginationX.data;
        listModel.addAll(paginationX.data.getRecords());
        bindData();
        ll_loading.setStatus(LoadingLayout.Success);
        if (isPullOrDown && listModel.size() == 0)
            ll_loading.setStatus(LoadingLayout.Empty);
    }

    /**
     * 获取列表数据失败
     */
    @Override
    public void getListDataErrorEmbl() {
        listView.onRefreshComplete();
    }

    @Override
    public LoadingLayout getLoadingLayout() {
        return ll_loading;
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

    @Override
    public void sebdRecordInfo(Record record) {
        requestComment(record);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            //新建跟进
            case R.id.btn_add:
                startActivityForResult(new Intent(getActivity(), FollowSelectActivity.class), Activity.RESULT_FIRST_USER);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                UmengAnalytics.umengSend(mActivity, UmengAnalytics.addFollow);
                break;
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
