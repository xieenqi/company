package com.loyo.oa.v2.activityui.signin.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.audio.player.AudioPlayerView;
import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.DynamicFilterTimeModel;
import com.loyo.oa.dropdownmenu.filtermenu.OrganizationFilterModel;
import com.loyo.oa.dropdownmenu.filtermenu.SigninFilterSortModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.MsgAudiomMenu;
import com.loyo.oa.v2.activityui.followup.viewcontrol.AudioPlayCallBack;
import com.loyo.oa.v2.activityui.signin.adapter.SigninListAdapter;
import com.loyo.oa.v2.activityui.signin.bean.AudioModel;
import com.loyo.oa.v2.activityui.signin.bean.CommentModel;
import com.loyo.oa.v2.activityui.signin.bean.SigninNewListModel;
import com.loyo.oa.v2.activityui.signin.persenter.TeamSigninListFragPresenter;
import com.loyo.oa.v2.activityui.signin.persenter.TeamSigninListFragPresenterImpl;
import com.loyo.oa.v2.activityui.signin.viewcontrol.SigninListView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Record;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
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


/**
 * 【团队拜访】列表
 * Created by yyy on 16/11/10.
 */
public class TeamSigninFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2, SigninListView, View.OnClickListener, MsgAudiomMenu.MsgAudioMenuCallBack, AudioPlayCallBack {

    private String menuTimekey = "0";        /*时间*/
    private String menuSortkey = "0";        /*排序*/
    private String departmentId = "";        /*部门id*/
    private String userId = "";              /*userid*/

    private boolean isPullOrDown;
    private int commentPosition;

    private View mView;
    private Button btn_add;
    private TextView voiceView;
    private ViewStub emptyView;
    private DropDownMenu filterMenu;
    private PullToRefreshListView listView;
    private LinearLayout layout_bottom_menu;

    private PaginationX<SigninNewListModel> mPagination = new PaginationX<>(20);
    private ArrayList<SigninNewListModel> listModel = new ArrayList<>();
    private SigninListAdapter mAdapter;
    private TeamSigninListFragPresenter mPresenter;
    private MsgAudiomMenu msgAudiomMenu;
    private String uuid = StringUtil.getUUID();

    /*录音播放相关*/
    private LinearLayout layout_bottom_voice;
    private int playVoiceSize = 0;
    private AudioPlayerView audioPlayer;
    private TextView lastView;
    private String lastUrl = "";
    private LoadingLayout ll_loading;
    private int pageSize = 5;


    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_newsignin_team, null);
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
        pageSize = listModel.size();
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

    public void initView(View view) {
        mPresenter = new TeamSigninListFragPresenterImpl(this);
        audioPlayer = new AudioPlayerView(getActivity());
        //audioPlayer.onInit();
        ll_loading = (LoadingLayout) view.findViewById(R.id.ll_loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                initPageData();
            }
        });
        btn_add = (Button) view.findViewById(R.id.btn_add);
        emptyView = (ViewStub) mView.findViewById(R.id.vs_nodata);
        filterMenu = (DropDownMenu) view.findViewById(R.id.drop_down_menu);
        layout_bottom_menu = (LinearLayout) view.findViewById(R.id.layout_bottom_menu);
        layout_bottom_voice = (LinearLayout) view.findViewById(R.id.layout_bottom_voice);

        listView = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        listView.setEmptyView(emptyView);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);

        btn_add.setOnClickListener(this);
        btn_add.setOnTouchListener(Global.GetTouch());

        msgAudiomMenu = new MsgAudiomMenu(getActivity(), this, uuid);
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
        if (PermissionManager.getInstance().dataRange(BusinessOperation.CUSTOMER_VISIT)
                == Permission.COMPANY) {
            depts.addAll(OrganizationManager.shareManager().allDepartments());
            title = "人员";
        }
        //权限为部门 展示我的部门
        else if (PermissionManager.getInstance().dataRange(BusinessOperation.CUSTOMER_VISIT)
                == Permission.TEAM) {
            depts.addAll(OrganizationManager.shareManager().currentUserDepartments());
            title = "人员";
        } else {
            title = "人员";
            depts.add(OrganizationFilterModel.selfDepartment());
        }

        List<FilterModel> options = new ArrayList<>();
        options.add(DynamicFilterTimeModel.getFilterModel());     //时间
        options.add(SigninFilterSortModel.getFilterModel());      //排序
        options.add(new OrganizationFilterModel(depts, title));   //人员
        DefaultMenuAdapter adapter = new DefaultMenuAdapter(getContext(), options);
        filterMenu.setMenuAdapter(adapter);
        adapter.setCallback(new OnMenuModelsSelected() {
            @Override
            public void onMenuModelsSelected(int menuIndex, List<MenuModel> selectedModels, Object userInfo) {
                filterMenu.close();
                MenuModel model = selectedModels.get(0);
                switch (menuIndex) {

                    /*时间*/
                    case 0:
                        menuTimekey = selectedModels.get(0).getKey();
                        filterMenu.headerTabBar.setTitleAtPosition(model.getValue(), menuIndex);
                        break;

                    /*排序*/
                    case 1:
                        menuSortkey = model.getKey();
                        filterMenu.headerTabBar.setTitleAtPosition(model.getValue(), menuIndex);
                        break;

                    /*人员*/
                    case 2:
                        filterMenu.headerTabBar.setTitleAtPosition(model.getValue(), menuIndex);
                        if (model.getClass().equals(OrganizationFilterModel.DepartmentMenuModel.class)) {
                            departmentId = model.getKey();
                            userId = "";
                        } else if (model.getClass().equals(OrganizationFilterModel.UserMenuModel.class)) {
                            departmentId = "";
                            userId = model.getKey();
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
        isPullOrDown = true;
        mPagination.setPageIndex(1);
        getData(true);
    }

    /**
     * 数据绑定
     */
    public void bindData() {
        if (null == mAdapter) {
            mAdapter = new SigninListAdapter(getActivity(), listModel, this, this);
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
        map.put("bizzId", listModel.get(commentPosition).id);
        map.put("title", content);
        map.put("commentType", 1); //1文本 2语音
        map.put("bizzType", 1);   //1拜访 2跟进
        //map.put("audioInfo", "");//语音信息
        LogUtil.dee("评论参数:" + MainApp.gson.toJson(map));
        mPresenter.requestComment(map);
    }

    /**
     * 评论语音
     */
    private void requestComment(Record record) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizzId", listModel.get(commentPosition).id);
        map.put("commentType", 2); //1文本 2语音
        map.put("bizzType", 1);   //1拜访 2跟进
        map.put("audioInfo", record);//语音信息
        LogUtil.dee("评论参数:" + MainApp.gson.toJson(map));
        mPresenter.requestComment(map);
    }

    /**
     * 获取Self列表数据
     */
    private void getData(boolean isPullOrDown) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("timeType", Integer.parseInt(menuTimekey));
        map.put("xpath", departmentId);
        map.put("userId", userId);
        map.put("orderType", Integer.parseInt(menuSortkey));
        map.put("split", true);
        map.put("pageIndex", mPagination.getPageIndex());
        map.put("pageSize", isPullOrDown ? listModel.size() >= pageSize ? listModel.size() : pageSize : pageSize);
        LogUtil.dee("团队拜访,发送数据:" + MainApp.gson.toJson(map));
        mPresenter.getListData(map, mPagination.getPageIndex());
    }

    /**
     * 点击评论回调
     */
    @Override
    public void commentEmbl(int position) {
        commentPosition = position;
        layout_bottom_menu.setVisibility(View.VISIBLE);
        btn_add.setVisibility(View.GONE);
        msgAudiomMenu.commentEmbl();
    }

    /**
     * 评论删除
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
     * 刷新列表
     */
    @Override
    public void rushListData(boolean shw) {
        onPullDownToRefresh(listView);
    }

    /**
     * 评论成功操作
     */
    @Override
    public void commentSuccessEmbl(CommentModel model) {
        layout_bottom_menu.setVisibility(View.GONE);
        msgAudiomMenu.commentSuccessEmbl();
        listModel.get(commentPosition).comments.add(model);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 获取列表数据成功
     */
    @Override
    public void getListDataSuccesseEmbl(BaseBeanT<PaginationX<SigninNewListModel>> paginationX) {
        listView.onRefreshComplete();
        if (isPullOrDown) {
            listModel.clear();
        }
        mPagination = paginationX.data;
        listModel.addAll(paginationX.data.getRecords());
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
    public LoadingLayout getLoadingView() {
        return ll_loading;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //新建跟进
            case R.id.btn_add:

                break;
        }
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
