package com.loyo.oa.v2.activityui.followup.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.audio.player.AudioPlayerView;
import com.loyo.oa.common.utils.UmengAnalytics;
import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.DynamicFilterTimeModel;
import com.loyo.oa.dropdownmenu.filtermenu.OrganizationFilterModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.MsgAudiomMenu;
import com.loyo.oa.v2.activityui.followup.adapter.FollowUpListAdapter;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsCommentAdapter;
import com.loyo.oa.v2.activityui.followup.common.FollowFilterMenuModel;
import com.loyo.oa.v2.activityui.followup.model.FolloUpConfig;
import com.loyo.oa.v2.activityui.followup.model.FollowFilter;
import com.loyo.oa.v2.activityui.followup.model.FollowUpListModel;
import com.loyo.oa.v2.activityui.followup.persenter.FollowUpFragPresenter;
import com.loyo.oa.v2.activityui.followup.persenter.impl.FollowUpFragPresenterImpl;
import com.loyo.oa.v2.activityui.followup.viewcontrol.AudioPlayCallBack;
import com.loyo.oa.v2.activityui.followup.viewcontrol.FollowUpListView;
import com.loyo.oa.v2.activityui.signin.bean.AudioModel;
import com.loyo.oa.v2.activityui.signin.bean.CommentModel;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Record;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.Permission;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.loyo.oa.common.utils.UmengAnalytics.roleFollowTeam;


/**
 * 【团队跟进】列表
 * Created by yyy on 16/6/1.
 */
public class TeamFollowUpFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2, FollowUpListView, MsgAudiomMenu.MsgAudioMenuCallBack, AudioPlayCallBack {

    private View mView;
    private TextView voiceView;
    private PullToRefreshListView listView;
    private ArrayList<FollowFilter> mTags;
    private DropDownMenu filterMenu;
    private LinearLayout layout_bottom_menu;

    private String menuTimekey = "";        /*时间*/
    private String method, typeId; /*筛选*/

    private String userId = "";
    private String xPath = "";

    private int commentPosition;

    private PaginationX<FollowUpListModel> mPagination = new PaginationX<>();

    private FollowUpListAdapter mAdapter;
    private FollowUpFragPresenter mPresenter;
    private MsgAudiomMenu msgAudiomMenu;
    private String uuid = StringUtil.getUUID();

    private LinearLayout layout_bottom_voice;
    private int playVoiceSize = 0;
    private AudioPlayerView audioPlayer;
    private TextView lastView;
    private LoadingLayout ll_loading;


    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_followup_team, null);
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
        mPagination.setFirstPage();
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        getData();
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
//        mTags = (ArrayList<FollowFilter>) getArguments().getSerializable("tag");
        mTags = FolloUpConfig.getFolloUpStageCache();
        for (int i = 0; i < mTags.size(); i++) {//过滤掉跟进行为
            if (mTags.get(i).fieldName.contains("activity")) {
                mTags.remove(i);
            }
        }
        mPresenter = new FollowUpFragPresenterImpl(this, getActivity());
        audioPlayer = new AudioPlayerView(getActivity());
        //audioPlayer.onInit();
        filterMenu = (DropDownMenu) view.findViewById(R.id.drop_down_menu);

        layout_bottom_menu = (LinearLayout) view.findViewById(R.id.layout_bottom_menu);
        layout_bottom_voice = (LinearLayout) view.findViewById(R.id.layout_bottom_voice);

        listView = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);

        msgAudiomMenu = new MsgAudiomMenu(getActivity(), this, uuid, this);
        layout_bottom_menu.addView(msgAudiomMenu);

        Utils.btnSpcHideForListView(getActivity(), listView.getRefreshableView(),
                layout_bottom_menu, msgAudiomMenu.getEditComment());

    }

    /**
     * 加载顶部菜单
     */
    private void loadFilterOptions() {

        List<DBDepartment> depts = new ArrayList<>();
        String title = "人员";
        //为超管或权限为全公司 展示全公司成员
        if (PermissionManager.getInstance().dataRange(BusinessOperation.VISIT_TIMELINE)
                == Permission.COMPANY) {
            depts.addAll(OrganizationManager.shareManager().allDepartments());
            title = "人员";
        }
        //权限为部门 展示我的部门
        else if (PermissionManager.getInstance().dataRange(BusinessOperation.VISIT_TIMELINE)
                == Permission.TEAM) {
            depts.addAll(OrganizationManager.shareManager().currentUserDepartments());
            title = "人员";
        } else {
            title = "人员";
            depts.add(OrganizationFilterModel.selfDepartment());
        }

        List<FilterModel> options = new ArrayList<>();
        options.add(DynamicFilterTimeModel.getFilterModel());     //时间
        options.add(FollowFilterMenuModel.getFilterModel(mTags));  //筛选
        options.add(new OrganizationFilterModel(depts, title));   //人员
        DefaultMenuAdapter adapter = new DefaultMenuAdapter(getContext(), options);
        filterMenu.setMenuAdapter(adapter);
        adapter.setCallback(new OnMenuModelsSelected() {
            @Override
            public void onMenuModelsSelected(int menuIndex, List<MenuModel> selectedModels, Object userInfo) {
                filterMenu.close();

                switch (menuIndex) {

                    /*时间*/
                    case 0: {
                        MenuModel model = selectedModels.get(0);
                        menuTimekey = selectedModels.get(0).getKey();
                        filterMenu.headerTabBar.setTitleAtPosition(model.getValue(), menuIndex);
                        UmengAnalytics.umengSend(mActivity, UmengAnalytics.timeFollowTeam);
                    }
                    break;

                    /*筛选*/
                    case 1:
                        String clas = userInfo.getClass() + "";
                        if (userInfo != null && clas.contains("HashMap")) {
                            HashMap<String, MenuModel> map = (HashMap<String, MenuModel>) userInfo;
                            MenuModel field1 = map.get("method");
                            MenuModel field2 = map.get("typeId");
                            method = field1.getKey();
                            typeId = field2.getKey();
                        } else {
                            method = "";
                            typeId = "";
                        }
                        UmengAnalytics.umengSend(mActivity, UmengAnalytics.filterFollowTeam);
                        break;

                    /*人员*/
                    case 2: {
                        MenuModel model = selectedModels.get(0);
                        if (model.getClass().equals(OrganizationFilterModel.DepartmentMenuModel.class)) {
                            LogUtil.dee("xPath");
                            xPath = model.getKey();
                            userId = "";
                        } else if (model.getClass().equals(OrganizationFilterModel.UserMenuModel.class)) {
                            LogUtil.dee("userId");
                            xPath = "";
                            userId = model.getKey();
                        }
                        filterMenu.headerTabBar.setTitleAtPosition(model.getValue(), menuIndex);
                        UmengAnalytics.umengSend(mActivity, UmengAnalytics.roleFollowTeam);
                    }
                    break;
                }
                initPageData();
            }
        });
        initPageData();
    }

    private void initPageData() {
        ll_loading.setStatus(LoadingLayout.Loading);
        mPagination.setFirstPage();
        getData();
    }

    /**
     * 数据绑定
     */
    public void bindData() {
        if (null == mAdapter) {
            mAdapter = new FollowUpListAdapter(getActivity(), mPagination.getRecords(), this, this);
            listView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 发送文字
     */
    private void requestComment(String content) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizzId", mPagination.getRecords().get(commentPosition).id);
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
        map.put("bizzId", mPagination.getRecords().get(commentPosition).id);
        map.put("commentType", 2); //1文本 2语音
        map.put("bizzType", 2);   //1拜访 2跟进
        map.put("audioInfo", record);//语音信息
        LogUtil.dee("评论参数:" + MainApp.gson.toJson(map));
        mPresenter.requestComment(map);
    }

    /**
     * 获取Team列表数据
     */
    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("xpath", xPath);
        map.put("timeType", menuTimekey); //时间查询
        map.put("method", method);        //跟进类型0:全部 1:线索 2:客户
        map.put("typeId", typeId);
        map.put("split", true);
        map.put("pageIndex", mPagination.getShouldLoadPageIndex());
        map.put("pageSize", mPagination.getPageSize());
        mPresenter.getListData(map);
    }

    /**
     * 点击评论回调
     */
    @Override
    public void commentEmbl(int position) {
        commentPosition = position;
        layout_bottom_menu.setVisibility(View.VISIBLE);
        msgAudiomMenu.commentEmbl();
        UmengAnalytics.umengSend(mActivity, UmengAnalytics.replyFollowTeam);
    }

    /**
     * 评论删除
     */
    @Override
    public void deleteCommentEmbl(final ListView adapter, final int position, final String id) {
        ActionSheetDialog dialog = new ActionSheetDialog(mActivity).builder();
        dialog.addSheetItem("删除评论", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                mPresenter.deleteComment(adapter, position, id);
            }
        });
        dialog.show();
    }

    /**
     * 刷新列表数据
     */
    @Override
    public void rushListData(ListView list, int position) {
        //删除一条评论
        ListOrDetailsCommentAdapter adapter = ((ListOrDetailsCommentAdapter) list.getAdapter());
        adapter.remove(position);
        if (0 == adapter.getCount()) {
            //如果没有评论了，就隐藏显示评论的控件
            ((ViewGroup) list.getParent()).setVisibility(View.GONE);
        }
    }

    /**
     * 评论成功操作
     */
    @Override
    public void commentSuccessEmbl(CommentModel modle) {
        layout_bottom_menu.setVisibility(View.GONE);
        msgAudiomMenu.commentSuccessEmbl();
        mPagination.getRecords().get(commentPosition).comments.add(modle);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 获取列表数据成功
     */
    @Override
    public void getListDataSuccesseEmbl(BaseBeanT<PaginationX<FollowUpListModel>> baseBeanData) {
        listView.onRefreshComplete();
        mPagination.loadRecords(baseBeanData.data);
        bindData();
        ll_loading.setStatus(LoadingLayout.Success);
        if (mPagination.isEnpty())
            ll_loading.setStatus(LoadingLayout.Empty);

    }

    /**
     * 获取列表数据失败
     */
    @Override
    public void getListDataErrorEmbl(Throwable e) {
        listView.onRefreshComplete();
        //判断，数据为空，就用ll_loading显示，否则使用toast提示
        @LoyoErrorChecker.CheckType
        int type = mPagination.isEnpty() ? LoyoErrorChecker.LOADING_LAYOUT : LoyoErrorChecker.TOAST;
        LoyoErrorChecker.checkLoyoError(e, type, ll_loading);
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

    public void sebdRecordInfo(Record record) {
        requestComment(record);
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
                lastView = textView;
            }
        } else {
            LogUtil.dee("第一次播放");
            //audioPlayer.onResume(textView);
            audioPlayer.onStart(audioModel, textView);
            lastView = textView;
        }
        playVoiceSize++;
    }
}
