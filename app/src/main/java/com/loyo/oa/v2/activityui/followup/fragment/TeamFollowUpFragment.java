package com.loyo.oa.v2.activityui.followup.fragment;

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

import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.DynamicFilterTimeModel;
import com.loyo.oa.dropdownmenu.filtermenu.OrganizationFilterModel;
import com.loyo.oa.dropdownmenu.filtermenu.TagMenuModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.followup.AudioPlayer;
import com.loyo.oa.v2.activityui.followup.MsgAudiomMenu;
import com.loyo.oa.v2.activityui.followup.adapter.FollowUpListAdapter;
import com.loyo.oa.v2.activityui.followup.model.FollowUpListModel;
import com.loyo.oa.v2.activityui.followup.persenter.FollowUpFragPresenter;
import com.loyo.oa.v2.activityui.followup.persenter.impl.FollowUpFragPresenterImpl;
import com.loyo.oa.v2.activityui.followup.viewcontrol.AudioPlayCallBack;
import com.loyo.oa.v2.activityui.followup.viewcontrol.FollowUpListView;
import com.loyo.oa.v2.activityui.other.model.Tag;
import com.loyo.oa.v2.activityui.signinnew.model.AudioModel;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.beans.Record;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 【团队跟进】列表
 * Created by yyy on 16/6/1.
 */
public class TeamFollowUpFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2, FollowUpListView, View.OnClickListener, MsgAudiomMenu.MsgAudioMenuCallBack, AudioPlayCallBack {

    private View mView;
    private Button btn_add;
    private ViewStub emptyView;
    private PullToRefreshListView listView;
    private ArrayList<Tag> mTags;
    private DropDownMenu filterMenu;

    private LinearLayout layout_bottom_menu;

    private String menuTimekey = "";        /*时间*/
    private String menuChoskey = "";        /*筛选*/
    private String menuGuykey = "";         /*人员*/
    private boolean isTopAdd;
    private int commentPosition;

    private Permission permission;

    private ArrayList<FollowUpListModel> listModel = new ArrayList<>();
    private PaginationX<FollowUpListModel> mPagination = new PaginationX<>(20);

    private FollowUpListAdapter mAdapter;
    private FollowUpFragPresenter mPresenter;
    private MsgAudiomMenu msgAudiomMenu;
    private String uuid = StringUtil.getUUID();

    private LinearLayout layout_bottom_voice;
    private int playVoiceSize = 0;
    private AudioPlayer audioPlayer;
    private TextView lastView;
    private String lastUrl = "";


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
    public void onDestroy() {
        super.onDestroy();
        audioPlayer.killPlayer();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = true;
        mPagination.setPageIndex(1);
        getData(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = false;
        mPagination.setPageIndex(mPagination.getPageIndex() + 1);
        getData(true);
    }

    public void initView(View view) {
        mTags = (ArrayList<Tag>) getArguments().getSerializable("tag");
        permission = (Permission) getArguments().getSerializable("permission");
        mPresenter = new FollowUpFragPresenterImpl(this, getActivity());
        audioPlayer = new AudioPlayer(getActivity());
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

        Utils.btnSpcHideForListViewTest(getActivity(), listView.getRefreshableView(),
                btn_add,
                layout_bottom_menu, msgAudiomMenu.getEditComment());

    }

    /**
     * 加载顶部菜单
     */
    private void loadFilterOptions() {

        List<DBDepartment> depts = new ArrayList<>();
        String title = "人员";
        //为超管或权限为全公司 展示全公司成员
        if (permission != null && permission.dataRange == Permission.COMPANY) {
            depts.addAll(OrganizationManager.shareManager().allDepartments());
            title = "人员";
        }
        //权限为部门 展示我的部门
        else if (permission != null && permission.dataRange == Permission.TEAM) {
            depts.addAll(OrganizationManager.shareManager().currentUserDepartments());
            title = "人员";
        } else {
            title = "人员";
            depts.add(OrganizationFilterModel.selfDepartment());
        }

        List<FilterModel> options = new ArrayList<>();
        options.add(DynamicFilterTimeModel.getFilterModel());     //时间
        options.add(TagMenuModel.getTagFilterModel(mTags));       //筛选
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
                        Toast("key:" + menuTimekey + " value" + model.getValue());
                        break;

                    /*筛选*/
                    case 1:
                        menuChoskey = model.getKey();
                        Toast("key:" + menuChoskey + " value" + model.getValue());
                        break;

                    /*人员*/
                    case 2:
                        menuGuykey = model.getKey();
                        filterMenu.headerTabBar.setTitleAtPosition(model.getValue(), menuIndex);
                        Toast("key:" + menuGuykey + " value" + model.getValue());
                        break;

                }
                getData(false);
            }
        });
        getData(false);
    }


    /**
     * 数据绑定
     */
    public void bindData() {
        if (null == mAdapter) {
            mAdapter = new FollowUpListAdapter(getActivity(), listModel, this, this);
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
        map.put("bizzId", listModel.get(commentPosition).id);
        map.put("title", content);
        map.put("commentType", 1); //1文本 2语音
        map.put("bizzType", 2);   //1拜访 2跟进
        //map.put("audioInfo", "");//语音信息
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
        map.put("bizzType", 2);   //1拜访 2跟进
        map.put("audioInfo", record);//语音信息
        LogUtil.dee("评论参数:" + MainApp.gson.toJson(map));
        mPresenter.requestComment(map);
    }

    /**
     * 获取Self列表数据
     */
    private void getData(boolean isPullOrDown) {
        if (!isPullOrDown) {
            showLoading("");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", "");//我的传id,团队则空着
        map.put("xpath", "");
        map.put("timeType", 5);//时间查询
        map.put("method", 0); //跟进类型0:全部 1:线索 2:客户
        map.put("typeId", "");
        map.put("split", true);
        map.put("pageIndex", mPagination.getPageIndex());
        map.put("pageSize", isTopAdd ? listModel.size() >= 5 ? listModel.size() : 5 : 5);
        LogUtil.dee("发送数据:" + MainApp.gson.toJson(map));
        mPresenter.getListData(map);
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
     * 刷新列表数据
     */
    @Override
    public void rushListData(boolean shw) {
        getData(shw);
    }

    /**
     * 评论成功操作
     */
    @Override
    public void commentSuccessEmbl() {
        layout_bottom_menu.setVisibility(View.GONE);
        msgAudiomMenu.commentSuccessEmbl();
        getData(false);
    }

    /**
     * 获取列表数据成功
     */
    @Override
    public void getListDataSuccesseEmbl(BaseBeanT<PaginationX<FollowUpListModel>> paginationX) {
        listView.onRefreshComplete();
        if (isTopAdd) {
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
     * */
    @Override
    public void playVoice(AudioModel audioModel,TextView textView) {
        if(TextUtils.isEmpty(audioModel.url)){
            Toast("无录音资源!");
            return;
        }

        layout_bottom_voice.setVisibility(View.VISIBLE);
        layout_bottom_voice.removeAllViews();
        layout_bottom_voice.addView(audioPlayer);
        /*关闭上一条TextView动画*/
        if(playVoiceSize > 0){
            if(null != lastView)
                MainApp.getMainApp().stopAnim(lastView);
        }

        /*点击同一条则暂停播放*/
        if(lastView == textView){
            MainApp.getMainApp().stopAnim(textView);
            audioPlayer.audioPause(textView);
            lastView = null;
        }else{
            audioPlayer.audioStart(textView);
            audioPlayer.threadPool(audioModel,textView);
            lastUrl = audioModel.url;
            lastView = textView;
        }

        playVoiceSize++;
    }
}
