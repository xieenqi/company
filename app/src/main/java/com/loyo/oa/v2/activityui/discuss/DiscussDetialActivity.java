package com.loyo.oa.v2.activityui.discuss;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.contactpicker.ContactPickerActivity;
import com.loyo.oa.contactpicker.model.event.ContactPickedEvent;
import com.loyo.oa.contactpicker.model.result.StaffMemberCollection;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshRecycleView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.discuss.api.DiscussService;
import com.loyo.oa.v2.activityui.discuss.bean.Discussion;
import com.loyo.oa.v2.activityui.discuss.bean.HttpCrecter;
import com.loyo.oa.v2.activityui.project.ProjectInfoActivity_;
import com.loyo.oa.v2.activityui.tasks.TasksInfoActivity_;
import com.loyo.oa.v2.activityui.work.WorkReportsInfoActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.OrganizationalMember;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.compat.Compat;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.tool.HaitHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 【讨论详情界面】
 * create by libo 2016/03/10
 */

public class DiscussDetialActivity extends BaseLoadingActivity implements View.OnLayoutChangeListener, View.OnClickListener {

    private static final char SCANNER_HAIT_TRIM = '\u2005';
    public static final String REQUEST_GET_USER = "com.loyo.oa.DiscussDetialActivity.GET_USER";
    private final List<HaitHelper.SelectUser> mHaitSelectUsers = new ArrayList<>(); // 选择用于艾特的用户列表

    private PullToRefreshRecycleView lv_notice;
    private EditText et_discuss;
    private LinearLayout layout_back;
    private ImageView img_back;
    private TextView tv_title;
    private TextView tv_edit;
    private ImageView iv_submit;
    private TextView tv_send;
    private RelativeLayout rl_root;
    private LinearLayout ll_edit;

    private DiscussDetAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int screenHeight;
    private int keyHeight;
    private int screenWidth;
    private String oldScanner;
    private LinearLayout ll_scanner;
    private int mBizType;
    private String mAttachmentUUId, bizTypeId, summaryId;
    private int pageIndex = 1;
    public PaginationX<HttpDiscussDet> mPageDiscussion = new PaginationX<>();
    private Map<Long, String> messages = new HashMap<>();
    private int mStatus;
    private boolean isOnce = true; // 让数据第一次定位的底部

    /**
     * 启动当前页面
     *
     * @param act
     * @param mBizType
     * @param mAttachmentUUId
     * @param status
     * @param requestCode
     */
    public static void startThisActivity(final Activity act,
                                         final int mBizType,
                                         final String mAttachmentUUId,
                                         final int status,
                                         final int requestCode) {
        Intent intent = new Intent(act, DiscussDetialActivity.class);
        intent.putExtra(ExtraAndResult.EXTRA_TYPE, mBizType);
        intent.putExtra("status", status);
        intent.putExtra(ExtraAndResult.EXTRA_UUID, mAttachmentUUId);
        act.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE /*| WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN*/);
        initData();
        initView();
        initListener();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_discuss_det);
    }

    @Override
    public void getPageData() {
        loadMessage(true);
    }

    private void initData() {
        mBizType = getIntent().getIntExtra(ExtraAndResult.EXTRA_TYPE, -1);
        mAttachmentUUId = getIntent().getStringExtra(ExtraAndResult.EXTRA_UUID);
        bizTypeId = getIntent().getStringExtra(ExtraAndResult.EXTRA_TYPE_ID);
        mStatus = getIntent().getIntExtra("status", -1);
        summaryId = getIntent().getStringExtra(ExtraAndResult.EXTRA_ID);
        //获取屏幕高度
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
        //阀值设置为屏幕高度的1/3, 用于判断软件盘的弹起和收起
        keyHeight = screenHeight / 3;
        if (!TextUtils.isEmpty(bizTypeId)) {
            refreshRedDot();
        }
    }

    private void initView() {
        assignViews();
        tv_title.setText("讨论");
        switch (mBizType) {
            case 1:
                tv_edit.setText("查看报告");
                break;
            case 2:
                tv_edit.setText("查看任务");
                break;
            case 5:
                tv_edit.setText("查看项目");
                break;
            default:
                break;
        }

        tv_title.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(bizTypeId)) {
            tv_edit.setVisibility(View.VISIBLE);
        }

        /**
         * 说 明: 取消讨论权限
         * 时 间:2016.4.11
         * */
/*        if (mStatus == 3) {
            ll_edit.setVisibility(View.GONE);
        }*/

        linearLayoutManager = new LinearLayoutManager(this);
        // linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lv_notice.getRefreshableView().setLayoutManager(linearLayoutManager);

        lv_notice.getFooterLayout().setPullLabel("上拉刷新");
        lv_notice.getFooterLayout().setReleaseLabel("松开刷新");
        lv_notice.getFooterLayout().setRefreshingLabel("正在刷新...");

        lv_notice.getHeaderLayout().setPullLabel("下拉加载");
        lv_notice.getHeaderLayout().setReleaseLabel("松开加载");
        lv_notice.getHeaderLayout().setRefreshingLabel("正在加载...");

        lv_notice.setMode(PullToRefreshBase.Mode.BOTH);
        bindDiscussion();
        getPageData();
    }

    private void assignViews() {
        lv_notice = (PullToRefreshRecycleView) findViewById(R.id.lv_notice);
        et_discuss = (EditText) findViewById(R.id.et_discuss);
        tv_send = (TextView) findViewById(R.id.tv_send);
        layout_back = (LinearLayout) findViewById(R.id.layout_back);
        img_back = (ImageView) findViewById(R.id.img_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_edit = (TextView) findViewById(R.id.tv_edit);
        iv_submit = (ImageView) findViewById(R.id.iv_submit);
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
        ll_scanner = (LinearLayout) findViewById(R.id.rl_scanner);

        ll_edit = (LinearLayout) findViewById(R.id.ll_edit);
    }

    private void initListener() {
        layout_back.setOnClickListener(this);
        ll_scanner.setOnClickListener(this);
        tv_edit.setOnClickListener(this);
        tv_send.setOnClickListener(this);
        lv_notice.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(final PullToRefreshBase<RecyclerView> refreshView) {
                pageIndex++;
                loadMessage(false);
            }

            @Override
            public void onPullUpToRefresh(final PullToRefreshBase<RecyclerView> refreshView) {
                pageIndex = 1;
                loadMessage(true);
            }
        });
        et_discuss.addTextChangedListener(new UserScannerTextWatcher()); //监听用户输入
        et_discuss.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(final View view, final int keyCode, final KeyEvent event) {
                int selection = et_discuss.getSelectionStart();
                if (keyCode == KeyEvent.KEYCODE_DEL
                        && selection > 0
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String str = et_discuss.getText().toString();
                    char delChar = str.charAt(selection - 1);
                    if (delChar == SCANNER_HAIT_TRIM) {
                        int aiTePrefixIndex = str.lastIndexOf("@", selection - 1);
                        if (aiTePrefixIndex > -1) {
                            int index = et_discuss.getSelectionStart();
                            Editable editable = et_discuss.getText();
                            String delName = str.substring(aiTePrefixIndex + 1, index - 1);
                            delSelectUser(delName);
                            editable.delete(aiTePrefixIndex, index);
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    //返回页面先调用读取接口
    @Override
    public void onBackPressed() {
        finishActivity();
    }

    private void finishActivity() {
        Intent intent = new Intent();
        intent.putExtra("data", mPageDiscussion);
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                onBackPressed();
                break;
            case R.id.rl_scanner:
                showKeyBoard(et_discuss);
                break;
            case R.id.tv_edit://查看具体详情
                Intent intent = new Intent();
                switch (mBizType) {
                    case 1:
                        intent.setClass(DiscussDetialActivity.this, WorkReportsInfoActivity_.class);
                        intent.putExtra(ExtraAndResult.EXTRA_TYPE, "discuss");
                        intent.putExtra(ExtraAndResult.EXTRA_ID, bizTypeId);
                        startActivity(intent);
                        break;
                    case 2:
                        intent.setClass(DiscussDetialActivity.this, TasksInfoActivity_.class);
                        intent.putExtra(ExtraAndResult.EXTRA_TYPE, "discuss");
                        intent.putExtra(ExtraAndResult.EXTRA_ID, bizTypeId);
                        startActivity(intent);
                        break;
                    case 5:
                        intent.setClass(DiscussDetialActivity.this, ProjectInfoActivity_.class);
                        intent.putExtra(ExtraAndResult.EXTRA_TYPE, "discuss");
                        intent.putExtra("projectId", bizTypeId);
                        startActivity(intent);
                        break;
                    default:

                        break;
                }
                break;
            case R.id.tv_send:
                String mineMessage = et_discuss.getText().toString().trim();
                if (TextUtils.isEmpty(mineMessage)) {
                    return;
                }
                et_discuss.getText().clear();

                long time = System.currentTimeMillis() / 1000;

                addMineMessge(time, mineMessage);

                sendMessage(time, mineMessage);
                break;
            default:

                break;
        }
    }

    /**
     * 加载讨论信息...
     *
     * @param isPull 是否是上拉
     */
    private void loadMessage(final boolean isPull) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", pageIndex + "");
        map.put("pageSize", 5);
        map.put("attachmentUUId", mAttachmentUUId);
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_EXTRA()).
//                create(MyDiscuss.class).getDiscussDetail(map, new RCallback<PaginationX<HttpDiscussDet>>() {
//            @Override
//            public void success(final PaginationX<HttpDiscussDet> d, final Response response) {
//                HttpErrorCheck.checkResponse("讨论详情：", response);
//                Collections.reverse(d.getRecords());
//                if (isPull) {
//                    mPageDiscussion.getRecords().clear();
//                    mPageDiscussion.getRecords().addAll(0, d.getRecords());
//                } else {
//                    mPageDiscussion.getRecords().addAll(0, d.getRecords());
//                }
//                bindDiscussion();
//                lv_notice.onRefreshComplete();
//                if (d != null && d.getRecords().size() == 0 && isPull) {
//                    ll_loading.setStatus(LoadingLayout.Empty);
//                } else {
//                    ll_loading.setStatus(LoadingLayout.Success);
//                }
//
//            }
//
//            @Override
//            public void failure(final RetrofitError error) {
//                lv_notice.onRefreshComplete();
//                HttpErrorCheck.checkError(error, ll_loading,pageIndex==1?true:false);
//                super.failure(error);
//            }
//        });

        DiscussService.getDiscussDetail(map).subscribe(new DefaultLoyoSubscriber<PaginationX<HttpDiscussDet>>() {
            @Override
            public void onError(Throwable e) {
                lv_notice.onRefreshComplete();
                @LoyoErrorChecker.CheckType
                int type = pageIndex != 1 ? LoyoErrorChecker.TOAST : LoyoErrorChecker.LOADING_LAYOUT;
                LoyoErrorChecker.checkLoyoError(e, type, ll_loading);
            }

            @Override
            public void onNext(PaginationX<HttpDiscussDet> d) {
                Collections.reverse(d.getRecords());
                if (isPull) {
                    mPageDiscussion.getRecords().clear();
                    mPageDiscussion.getRecords().addAll(0, d.getRecords());
                } else {
                    mPageDiscussion.getRecords().addAll(0, d.getRecords());
                }
                bindDiscussion();
                lv_notice.onRefreshComplete();
                if (d != null && d.getRecords().size() == 0 && isPull) {
                    ll_loading.setStatus(LoadingLayout.Empty);
                } else {
                    ll_loading.setStatus(LoadingLayout.Success);
                }
            }
        });
    }

    /**
     * 绑定数据到adapter
     */
    private void bindDiscussion() {
        if (adapter == null) {
            adapter = new DiscussDetAdapter();
            lv_notice.getRefreshableView().setAdapter(adapter);
            adapter.updataList(mPageDiscussion.getRecords());
        } else {
            adapter.notifyDataSetChanged();
            if (adapter.getItemCount() > 0 && isOnce) {
                isOnce = false;
                lv_notice.getRefreshableView().smoothScrollToPosition(adapter.getItemCount() - 1);
            }
        }
    }


    /**
     * 预显示我发送的消息
     *
     * @param time
     * @param mineMessage
     */
    private void addMineMessge(final long time, final String mineMessage) {
        //页面没有数据的时候发送信息就展现页面
        if (mPageDiscussion.getRecords().size() == 0)
            ll_loading.setStatus(LoadingLayout.Success);
        messages.put(time, mineMessage);
        HttpDiscussDet discussion = new HttpDiscussDet();
        HttpCrecter creacter = new HttpCrecter();
        creacter.id = MainApp.user.id;
        creacter.name = MainApp.user.name;
        creacter.avatar = MainApp.user.avatar;
        discussion.creator = creacter;
        discussion.createdAt = time;
        discussion.content = mineMessage;
        adapter.addMineMessage(discussion);
    }


    /**
     * 发送讨论信息
     */
    private void sendMessage(final long time, final String message) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("attachmentUUId", mAttachmentUUId);
        body.put("content", message);
        body.put("bizType", mBizType);
        body.put("mentionedUserIds", getAndClearSelectUser(message));
        mHaitSelectUsers.clear();
        DiscussService.createDiscussion(body).subscribe(new DefaultLoyoSubscriber<Discussion>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.removeAtTime(time);

                    }
                }, 800);
            }

            @Override
            public void onNext(Discussion discussion) {
            }
        });
    }

    /**
     * 删除
     *
     * @param delName
     */
    private void delSelectUser(final String delName) {
        for (int i = 0; i < mHaitSelectUsers.size(); i++) {
            HaitHelper.SelectUser selectUser = mHaitSelectUsers.get(i);
            if (selectUser.matchName(delName)) {
                mHaitSelectUsers.remove(i);
                break;
            }
        }
    }

    private List<String> getAndClearSelectUser(final String message) {
        List<String> ids = new ArrayList();
        if (mHaitSelectUsers.size() == 0) {
            return ids;
        }
        for (int i = 0; i < mHaitSelectUsers.size(); i++) {
            HaitHelper.SelectUser user = mHaitSelectUsers.get(i);
            if (!message.contains(user.name) || ids.contains(user.id)) {
                continue;
            }
            ids.add(user.id);
        }
        return ids;
    }

    @Override
    protected void onResume() {
        super.onResume();
        rl_root.addOnLayoutChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        rl_root.removeOnLayoutChangeListener(this);

    }

    @Override
    public void onLayoutChange(final View v,
                               final int left,
                               final int top,
                               final int right,
                               final int bottom,
                               final int oldLeft,
                               final int oldTop,
                               final int oldRight,
                               final int oldBottom) {
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
            scrollToBottom();
        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {

        }
    }

    /**
     * 定位到最后一项
     */
    public void scrollToBottom() {
        if (adapter != null && adapter.getItemCount() > 0) {
            lv_notice.getRefreshableView().scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            OrganizationalMember user = (OrganizationalMember) data.getSerializableExtra("data");
            if (user != null) {
                String id = user.getId();
                if (TextUtils.isEmpty(id) || id.equals(MainApp.user.id)) {
                    Toast("不能@自己");
                    return;
                }
                String name = user.getName();
                mHaitSelectUsers.add(new HaitHelper.SelectUser(name, id));
                String selectName = add$Name(name);
                int index = et_discuss.getSelectionStart();
                Editable editable = et_discuss.getText();
                editable.insert(index, selectName);
                showKeyBoard(et_discuss);
            }
        }
    }

    /**
     * 选人回调
     */
    @Subscribe
    public void onContactPicked(ContactPickedEvent event) {
        if (!REQUEST_GET_USER.equals(event.request)) {
            return;
        }
        StaffMemberCollection collection = event.data;
        OrganizationalMember user = Compat.convertStaffCollectionToNewUser(collection);
        if (user == null) {
            return;
        }

        String id = user.getId();
        if (TextUtils.isEmpty(id) || id.equals(MainApp.user.id)) {
            Toast("不能@自己");
            return;
        }
        String name = user.getName();
        mHaitSelectUsers.add(new HaitHelper.SelectUser(name, id));
        String selectName = add$Name(name);
        int index = et_discuss.getSelectionStart();
        Editable editable = et_discuss.getText();
        editable.insert(index, selectName);
        showKeyBoard(et_discuss);
    }

    /**
     * 组合@的用户名 -- xxx + '\u2005'
     *
     * @param selectName
     * @return
     */
    private String add$Name(final String selectName) {
        return selectName + SCANNER_HAIT_TRIM;
    }

//    /**
//     * 隐藏输入法
//     *
//     * @param view
//     */
//    public void hitKeyBoard(final EditText view) {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }

    /**
     * 弹出输入法
     *
     * @param view
     */
    public void showKeyBoard(final EditText view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    /**
     * 用于监听用户输入
     */
    private class UserScannerTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

        }

        @Override
        public void onTextChanged(final CharSequence s, final int i, final int i1, final int i2) {
            String newString = s.toString();
            boolean addNewEditAiTe = TextUtils.isEmpty(oldScanner) ? true : newString.length() > oldScanner.length();
            oldScanner = newString;
            int selection = et_discuss.getSelectionStart();
            int frontCharIndex = selection - 2;
            boolean isLetterDigit = frontCharIndex < 0 ?
                    false : isLetterDigit(oldScanner.charAt(frontCharIndex) + ""); // 判断'@'之前 的是否是字母和文字
            if (addNewEditAiTe
                    && oldScanner.length() != 0
                    && selection > 0
                    && '@' == oldScanner.charAt(selection - 1)
                    && !isLetterDigit) {
                toSelectUserByHait(); //跳转选择要艾特用户的界面
            }
        }

        public boolean isLetterDigit(final String str) {
            String regex = "^[a-z0-9A-Z_\\-]+$";
            return str.matches(regex);
        }

        /**
         * 当用户输入'@'是跳转选择艾特用户的界面
         */
        private void toSelectUserByHait() {
            Bundle bundle = new Bundle();
            bundle.putBoolean(ContactPickerActivity.SINGLE_SELECTION_KEY, true);
            bundle.putSerializable(ContactPickerActivity.REQUEST_KEY, REQUEST_GET_USER);
            Intent intent = new Intent();
            intent.setClass(DiscussDetialActivity.this, ContactPickerActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public void afterTextChanged(final Editable editable) {

        }
    }

    private class DiscussDetMineViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMineTime;
        private TextView tvMine;
        private TextView tvContent;
        private RoundImageView ivMineAvatar;

        public DiscussDetMineViewHolder(final View itemView) {
            super(itemView);
            tvMineTime = (TextView) itemView.findViewById(R.id.tv_mine_time);
            tvMine = (TextView) itemView.findViewById(R.id.tv_mine);
            tvContent = (TextView) itemView.findViewById(R.id.tv_mine_content);
            tvContent.setMaxWidth(screenWidth / 2);
//          tvContent.setAutoLinkMask(Linkify.WEB_URLS);
            ivMineAvatar = (RoundImageView) itemView.findViewById(R.id.iv_mine_avatar);
        }
    }

    private class DiscussDetOtherViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvOtherTime;
        private TextView mTvOtherName;
        private TextView mTvOtherContent;
        private RoundImageView mIvOtherAvatar;

        public DiscussDetOtherViewHolder(final View itemView) {
            super(itemView);
            mTvOtherTime = (TextView) itemView.findViewById(R.id.tv_other_time);
            mTvOtherName = (TextView) itemView.findViewById(R.id.tv_other_name);
            mTvOtherContent = (TextView) itemView.findViewById(R.id.tv_other_content);
            mTvOtherContent.setMaxWidth((int) (screenWidth / 1.6f));
//          mTvOtherContent.setAutoLinkMask(Linkify.PHONE_NUMBERS | Linkify.WEB_URLS);
            mIvOtherAvatar = (RoundImageView) itemView.findViewById(R.id.iv_other_avatar);
        }
    }

    private class DiscussDetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<HttpDiscussDet> datas = new ArrayList<>();
        private View.OnLongClickListener onAvaterLongClicklistener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                HaitHelper.SelectUser user = (HaitHelper.SelectUser) view.getTag();
                if (user == null) {
                    return false;
                }
                int selection = et_discuss.getSelectionStart();
                et_discuss.getText().insert(selection, add$Name_real(user.name));
                mHaitSelectUsers.add(user);
                showKeyBoard(et_discuss);
                return true;
            }
        };
        View.OnClickListener userClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HaitHelper.SelectUser user = (HaitHelper.SelectUser) view.getTag();
                if (user == null) {
                    Toast("没有信息");
                    return;
                }
                Common.getUserInfo(DiscussDetialActivity.this, app, user.id);
            }
        };

        public void updataList(List<HttpDiscussDet> data) {
            if (data == null)
                data = new ArrayList<>();
            this.datas = data;
            this.notifyDataSetChanged();
//            scrollToBottom();
        }

        public void addMineMessage(final HttpDiscussDet info) {
            datas.add(info);
            notifyItemChanged(getItemCount());
            scrollToBottom();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            View mView = null;
            RecyclerView.ViewHolder holder = null;
            switch (viewType) {
                case DiscussSendMode.mine:
                    mView = View.inflate(DiscussDetialActivity.this, R.layout.item_discuss_det_mine, null);
                    holder = new DiscussDetMineViewHolder(mView);
                    return holder;
                case DiscussSendMode.other:
                    mView = View.inflate(DiscussDetialActivity.this, R.layout.item_discuss_det_other, null);
                    holder = new DiscussDetOtherViewHolder(mView);
                    return holder;
            }
            return null;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            if (holder == null)
                return;
            final HttpDiscussDet info = datas.get(position);
            if (holder.getClass() == DiscussDetMineViewHolder.class) {
                DiscussDetMineViewHolder mineHolder = (DiscussDetMineViewHolder) holder;
//                mineHolder.tvMineTime.setText(app.df3.format(new Date(info.createdAt * 1000)));
                mineHolder.tvMineTime.setText(DateTool.getDateTimeFriendly(info.createdAt));
//                mineHolder.tvContent.setAutoLinkMask(0x01);
                try {
                    mineHolder.tvContent.setText(info.content);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
//                mineHolder.tvContent.setMovementMethod(LinkMovementMethod.getInstance());
//                Linkify.addLinks(mineHolder.tvMineTime, Linkify.PHONE_NUMBERS | Linkify.WEB_URLS);

                HaitHelper.SelectUser selectUser = new HaitHelper.SelectUser(info.creator.name, info.creator.id);
                mineHolder.ivMineAvatar.setTag(selectUser);

                ImageLoader.getInstance().displayImage(info.creator.avatar, mineHolder.ivMineAvatar);
            } else if (holder.getClass() == DiscussDetOtherViewHolder.class) {
                DiscussDetOtherViewHolder otherHolder = (DiscussDetOtherViewHolder) holder;
                otherHolder.mTvOtherName.setText(info.creator.name);
                try {
                    otherHolder.mTvOtherContent.setText(info.content);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
//                otherHolder.mTvOtherTime.setText(app.df3.format(new Date(info.createdAt * 1000)));
                otherHolder.mTvOtherTime.setText(DateTool.getDateTimeFriendly(info.createdAt));
                ImageLoader.getInstance().displayImage(info.creator.avatar, otherHolder.mIvOtherAvatar);
                HaitHelper.SelectUser selectUser = new HaitHelper.SelectUser(info.creator.name, info.creator.id);
                otherHolder.mIvOtherAvatar.setTag(selectUser);

                otherHolder.mIvOtherAvatar.setOnLongClickListener(onAvaterLongClicklistener);
                otherHolder.mIvOtherAvatar.setOnClickListener(userClick);
            }
        }

        /**
         * 按照时间删除预发送信息
         *
         * @param time
         */
        public void removeAtTime(final long time) {
            for (int i = 0; i < getItemCount(); i++) {
                HttpDiscussDet discussion = datas.get(i);
                if (discussion.createdAt == time) {
                    datas.remove(i);
                    linearLayoutManager.removeViewAt(i);
                    break;
                }
            }
        }

//        public void updata(final long time, final Discussion discussion) {
//            for (int i = 0; i < getItemCount(); i++) {
//                HttpDiscussDet dis = datas.get(i);
//                if (discussion.getCreatedAt() == time) {
//                    dis.id = discussion.getId();
//                    dis.createdAt = discussion.getCreatedAt();
//                    break;
//                }
//            }
//        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        @Override
        public int getItemViewType(final int position) {
            String id = datas.get(position).creator.id;
            boolean isMine = TextUtils.isEmpty(id) ? false : id.equals(MainApp.user.getId());
            return isMine ? DiscussSendMode.mine : DiscussSendMode.other;
        }
    }

    private String add$Name_real(final String name) {
        return "@" + name + SCANNER_HAIT_TRIM;
    }

    private static class DiscussSendMode {
        private static final int other = 0x0001;
        private static final int mine = 0x0002;
    }

    /**
     * 刷新红点
     */
    /* TODO: 请求出错 */
    private void refreshRedDot() {
        setResult(Activity.RESULT_OK);
        HashMap<String, Object> map = new HashMap<>();
        map.put("summaryId", summaryId);
        DiscussService.updateReadDot(map).subscribe(new DefaultLoyoSubscriber<Object>(LoyoErrorChecker.SILENCE) {
//            @Override
//            public void onError(Throwable e) {
//                super.onError(e);
//                finishActivity();
//            }

            @Override
            public void onNext(Object o) {
            }
        });
    }

}
