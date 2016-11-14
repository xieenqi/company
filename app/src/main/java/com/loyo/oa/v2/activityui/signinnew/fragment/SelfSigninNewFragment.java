package com.loyo.oa.v2.activityui.signinnew.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.DynamicFilterTimeModel;
import com.loyo.oa.dropdownmenu.filtermenu.SigninFilterKindModel;
import com.loyo.oa.dropdownmenu.filtermenu.SigninFilterSortModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.model.Tag;
import com.loyo.oa.v2.activityui.signinnew.adapter.SigninNewListAdapter;
import com.loyo.oa.v2.activityui.signinnew.model.SigninNewListModel;
import com.loyo.oa.v2.activityui.signinnew.viewcontrol.SigninNewListView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.point.ISigninNew;
import com.loyo.oa.v2.tool.AnimationCommon;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * 【我的拜访】列表
 * Created by yyy on 16/11/10.
 */
public class SelfSigninNewFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2, SigninNewListView {

    private View mView;
    private Button btn_add;
    private ViewStub emptyView;
    private PullToRefreshListView listView;
    private LinearLayout layout_bottom_menu;
    private LinearLayout layout_voice;
    private LinearLayout layout_voicemenu;
    private LinearLayout layout_keyboard;

    private EditText edit_comment;
    private ImageView iv_voice;
    private ImageView iv_keyboard;
    private TextView tv_send_message;

    private PaginationX<SigninNewListModel> mPagination = new PaginationX<>(20);
    private ArrayList<SigninNewListModel> listModel = new ArrayList<>();
    private DropDownMenu filterMenu;
    private ArrayList<Tag> mTags;

    private String menuTimekey = "0";        /*时间*/
    private String menuKindkey = "0";        /*类型*/
    private String menuSortkey = "0";        /*排序*/

    private boolean isTopAdd;
    private int commentPosition;

    private SigninNewListAdapter mAdapter;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x01) {
                layout_voice.setAnimation(AnimationCommon.inFromBottomAnimation(150));
                layout_voice.setVisibility(View.VISIBLE);
            } else if (msg.what == 0x02) {
                layout_voice.setVisibility(View.GONE);
            }
        }
    };

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_new_signin, null);
            initView(mView);
            loadFilterOptions();
        }
        return mView;
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

    /**
     * 数据绑定
     */
    public void bindData() {
        if (null == mAdapter) {
            mAdapter = new SigninNewListAdapter(getActivity(), listModel, this);
            listView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void initView(View view) {
        mTags = (ArrayList<Tag>) getArguments().getSerializable("tag");
        btn_add = (Button) view.findViewById(R.id.btn_add);
        emptyView = (ViewStub) mView.findViewById(R.id.vs_nodata);
        filterMenu = (DropDownMenu) view.findViewById(R.id.drop_down_menu);
        layout_bottom_menu = (LinearLayout) view.findViewById(R.id.layout_bottom_menu);
        layout_voice = (LinearLayout) view.findViewById(R.id.layout_voice);
        layout_keyboard = (LinearLayout) view.findViewById(R.id.layout_keyboard);
        layout_voicemenu = (LinearLayout) view.findViewById(R.id.layout_voicemenu);

        edit_comment = (EditText) view.findViewById(R.id.edit_comment);
        iv_voice = (ImageView) view.findViewById(R.id.iv_voice);
        iv_keyboard = (ImageView) view.findViewById(R.id.iv_keyboard);
        tv_send_message = (TextView) view.findViewById(R.id.tv_send_message);

        listView = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        listView.setEmptyView(emptyView);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);

        tv_send_message.setOnClickListener(click);
        tv_send_message.setOnTouchListener(Global.GetTouch());
        iv_keyboard.setOnClickListener(click);
        iv_keyboard.setOnTouchListener(Global.GetTouch());
        iv_voice.setOnClickListener(click);
        iv_voice.setOnTouchListener(Global.GetTouch());
        btn_add.setOnClickListener(click);
        btn_add.setOnTouchListener(Global.GetTouch());

        Utils.btnSpcHideForListView(getActivity(),listView.getRefreshableView(),
                btn_add,
                layout_bottom_menu,
                layout_voice,edit_comment);


    }

    /**
     * 评论删除
     * */
    private void deleteComment(String id){
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninNew.class).deleteComment(id, new RCallback<Object>() {
            @Override
            public void success(Object object, Response response) {
                HttpErrorCheck.checkResponse("评论", response);
                getData(false);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
            }
        });
    }

    /**
     * 评论操作
     * */
    private void requestComment(String content){
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizzId", listModel.get(commentPosition).id);
        map.put("title", content);
        map.put("commentType",1); //1文本 2语音
        map.put("bizzType", 1);   //1拜访 2跟进
        //map.put("audioInfo", "");//语音信息
        LogUtil.dee("评论参数:"+MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninNew.class).requestComment(map, new RCallback<Object>() {
            @Override
            public void success(Object object, Response response) {
                HttpErrorCheck.checkResponse("评论", response);
                hideInputKeyboard(edit_comment);
                edit_comment.setText("");
                layout_bottom_menu.setVisibility(View.GONE);
                layout_voice.setVisibility(View.GONE);
                getData(false);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
            }
        });
    }

    /**
     * 获取Self列表数据
     */
    private void getData(boolean isPullOrDown) {
        if(!isPullOrDown){
            showLoading("");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("timeType", Integer.parseInt(menuTimekey));
        map.put("queryType", Integer.parseInt(menuKindkey));
        map.put("orderType", Integer.parseInt(menuSortkey));
        map.put("pageIndex", mPagination.getPageIndex());
        map.put("pageSize", isTopAdd ? listModel.size() >= 20 ? listModel.size() : 20 : 20);
        LogUtil.dee("发送数据:" + MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninNew.class).selfSignin(map, new RCallback<BaseBeanT<PaginationX<SigninNewListModel>>>() {
            @Override
            public void success(BaseBeanT<PaginationX<SigninNewListModel>> paginationX, Response response) {
                HttpErrorCheck.checkResponse("我的拜访", response);
                listView.onRefreshComplete();
                if (isTopAdd) {
                    listModel.clear();
                }
                mPagination = paginationX.data;
                listModel.addAll(paginationX.data.getRecords());
                bindData();
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                listView.onRefreshComplete();
                super.failure(error);
            }
        });
    }

    /**
     * 加载顶部菜单
     */
    private void loadFilterOptions() {
        List<FilterModel> options = new ArrayList<>();
        options.add(DynamicFilterTimeModel.getFilterModel());     //时间
        options.add(SigninFilterKindModel.getFilterModel());      //类型
        options.add(SigninFilterSortModel.getFilterModel());      //排序
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

                    /*类型*/
                    case 1:
                        menuKindkey = model.getKey();
                        filterMenu.headerTabBar.setTitleAtPosition(model.getValue(), menuIndex);
                        Toast("key:" + menuKindkey + " value" + model.getValue());
                        break;

                    /*排序*/
                    case 2:
                        menuSortkey = model.getKey();
                        filterMenu.headerTabBar.setTitleAtPosition(model.getValue(), menuIndex);
                        Toast("key:" + menuSortkey + " value" + model.getValue());
                        break;
                }
                getData(false);
            }
        });
        getData(false);
    }


    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                //新建跟进
                case R.id.btn_add:

                    break;

                /*切换录音*/
                case R.id.iv_voice:
                    layout_keyboard.setVisibility(View.VISIBLE);
                    layout_voicemenu.setVisibility(View.GONE);
                    hideInputKeyboard(edit_comment);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            mHandler.sendEmptyMessage(0x01);
                        }
                    }, 100);

                    break;

                /*切换软键盘*/
                case R.id.iv_keyboard:
                    layout_keyboard.setVisibility(View.GONE);
                    layout_voice.setVisibility(View.GONE);
                    layout_voicemenu.setVisibility(View.VISIBLE);
                    Utils.autoKeyBoard(getActivity(),edit_comment);
                    break;

                /*发送评论*/
                case R.id.tv_send_message:
                    if(TextUtils.isEmpty(edit_comment.getText().toString())){
                        Toast("请输入评论内容!");
                        return;
                    }
                    requestComment(edit_comment.getText().toString());
                    break;

            }
        }
    };

    /**
     * 评论回调
     */
    @Override
    public void commentEmbl(int position) {
        commentPosition = position;
        Utils.autoKeyBoard(getActivity(),edit_comment);
        layout_voicemenu.setVisibility(View.VISIBLE);
        layout_bottom_menu.setVisibility(View.VISIBLE);
        btn_add.setVisibility(View.GONE);
        layout_keyboard.setVisibility(View.GONE);
    }

    /**
     * 评论删除
     * */
    @Override
    public void deleteCommentEmbl(final String id) {
        ActionSheetDialog dialog = new ActionSheetDialog(mActivity).builder();
            dialog.addSheetItem("删除评论", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    deleteComment(id);
                }
            });
        dialog.show();
    }
}
