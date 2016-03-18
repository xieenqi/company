package com.loyo.oa.v2.activity.discuss;

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

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.commonview.SelectDetUserActivity;
import com.loyo.oa.v2.activity.project.ProjectInfoActivity_;
import com.loyo.oa.v2.activity.tasks.TasksInfoActivity_;
import com.loyo.oa.v2.activity.work.WorkReportsInfoActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Discussion;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IDiscuss;
import com.loyo.oa.v2.point.MyDiscuss;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.HaitHelper;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.customview.RoundImageView;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshRecycleView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【讨论详情界面】
 * create by libo 2016/03/10
 */

public class ActivityDiscussDet extends BaseActivity implements View.OnLayoutChangeListener, View.OnClickListener {

    private static final char SCANNER_HAIT_TRIM = '\u2005';
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
    private DiscussDetAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int screenHeight;
    private int keyHeight;
    private int screenWidth;
    private String oldScanner;
    private LinearLayout ll_scanner;
    private int mBizType;
    private String mAttachmentUUId, bizTypeId;
    private int pageIndex = 1;
    public PaginationX<HttpDiscussDet> mPageDiscussion = new PaginationX<>();
    private Map<Long, String> messages = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE /*| WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN*/);
        setContentView(R.layout.activity_discuss_det);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        mBizType = getIntent().getIntExtra(ExtraAndResult.EXTRA_TYPE, -1);
        mAttachmentUUId = getIntent().getStringExtra(ExtraAndResult.EXTRA_UUID);
        bizTypeId = getIntent().getStringExtra(ExtraAndResult.EXTRA_TYPE_ID);
        //获取屏幕高度
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
        //阀值设置为屏幕高度的1/3, 用于判断软件盘的弹起和收起
        keyHeight = screenHeight / 3;
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
        }

        tv_title.setVisibility(View.VISIBLE);
        tv_edit.setVisibility(View.VISIBLE);
        linearLayoutManager = new LinearLayoutManager(this);
        // linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lv_notice.getRefreshableView().setLayoutManager(linearLayoutManager);
        lv_notice.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        bindDiscussion();
        loadMessage();
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
    }

    private void initListener() {
        layout_back.setOnClickListener(this);
        ll_scanner.setOnClickListener(this);
        tv_edit.setOnClickListener(this);
        tv_send.setOnClickListener(this);
        lv_notice.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lv_notice.onRefreshComplete();
                    }
                }, 2000);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                pageIndex++;
                loadMessage();
            }
        });
        et_discuss.addTextChangedListener(new UserScannerTextWatcher()); //监听用户输入
        et_discuss.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.rl_scanner:
                showKeyBoard(et_discuss);
                break;
            case R.id.tv_edit://查看具体详情
                Intent intent = new Intent();
                switch (mBizType) {
                    case 1:
                        intent.setClass(ActivityDiscussDet.this, WorkReportsInfoActivity_.class);
                        intent.putExtra(ExtraAndResult.EXTRA_ID, bizTypeId);
                        startActivity(intent);
                        break;
                    case 2:
                        intent.setClass(ActivityDiscussDet.this, TasksInfoActivity_.class);
                        intent.putExtra(ExtraAndResult.EXTRA_ID, bizTypeId);
                        startActivity(intent);
                        break;
                    case 5:
                        intent.setClass(ActivityDiscussDet.this, ProjectInfoActivity_.class);
                        intent.putExtra("projectId", bizTypeId);
                        startActivity(intent);
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
     */
    private void loadMessage() {
        showLoading("");
        HashMap<String, Object> body = new HashMap<>();
        body.put("pageIndex", pageIndex + "");
        body.put("pageSize", 5);
        body.put("attachmentUUId", mAttachmentUUId);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_EXTRA()).
                create(MyDiscuss.class).getDiscussDetail(body, new RCallback<PaginationX<HttpDiscussDet>>() {
            @Override
            public void success(PaginationX<HttpDiscussDet> d, Response response) {
                HttpErrorCheck.checkResponse("讨论详情：", response);
                if (d == null || d.getRecords().size() == 0) {
                    Toast("没有更多信息");
                }
                mPageDiscussion.getRecords().addAll(d.getRecords());
                bindDiscussion();
                lv_notice.onRefreshComplete();
            }

            @Override
            public void failure(RetrofitError error) {
                lv_notice.onRefreshComplete();
                HttpErrorCheck.checkError(error);
                super.failure(error);
            }
        });
    }


    /**
     * 预显示我发送的消息
     *
     * @param time
     * @param mineMessage
     */
    private void addMineMessge(long time, String mineMessage) {
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
        LogUtil.d("发送的数据:" + MainApp.gson.toJson(body));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_EXTRA()).create(IDiscuss.class)
                .createDiscussion(body, new RCallback<Discussion>() {
                    @Override
                    public void success(Discussion d, Response response) {
                        HttpErrorCheck.checkResponse(response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        super.failure(error);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                adapter.removeAtTime(time);
                            }
                        }, 800);
//                et_discuss.setText(message);
//                Toast("信息上传失败请重新发送");

                    }
                });
    }

    /**
     * 删除
     *
     * @param delName
     */
    private void delSelectUser(String delName) {
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


    private void bindDiscussion() {
        if (adapter == null) {
            adapter = new DiscussDetAdapter();
            adapter.updataList(mPageDiscussion.getRecords());
            lv_notice.getRefreshableView().setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
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
    public void onLayoutChange(View v, int left, int top, int right,
                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            User user = (User) data.getSerializableExtra(User.class.getName());
            if (user != null) {
                String id = user.toShortUser().getId();
                String name = user.toShortUser().getName();

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
     * 组合@的用户名 -- xxx + '\u2005'
     *
     * @param selectName
     * @return
     */
    private String add$Name(String selectName) {
        return selectName + SCANNER_HAIT_TRIM;
    }

    /**
     * 隐藏输入法
     *
     * @param view
     */
    public void hitKeyBoard(EditText view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 弹出输入法
     *
     * @param view
     */
    public void showKeyBoard(EditText view) {
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
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
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

        public boolean isLetterDigit(String str) {
            String regex = "^[a-z0-9A-Z_\\-]+$";
            return str.matches(regex);
        }

        /**
         * 当用户输入'@'是跳转选择艾特用户的界面
         */
        private void toSelectUserByHait() {
            Bundle bundle = new Bundle();
            bundle.putInt(ExtraAndResult.STR_SELECT_TYPE, ExtraAndResult.TYPE_SELECT_SINGLE);
            app.startActivityForResult(ActivityDiscussDet.this, SelectDetUserActivity.class, MainApp.ENTER_TYPE_RIGHT,
                    ExtraAndResult.request_Code, bundle);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private class DiscussDetMineViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMineTime;
        private TextView tvMine;
        private TextView tvContent;
        private RoundImageView ivMineAvatar;

        public DiscussDetMineViewHolder(View itemView) {
            super(itemView);
            tvMineTime = (TextView) itemView.findViewById(R.id.tv_mine_time);
            tvMine = (TextView) itemView.findViewById(R.id.tv_mine);
            tvContent = (TextView) itemView.findViewById(R.id.tv_mine_content);

            tvContent.setMaxWidth(screenWidth / 2);

            ivMineAvatar = (RoundImageView) itemView.findViewById(R.id.iv_mine_avatar);
        }
    }

    private class DiscussDetOtherViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvOtherTime;
        private TextView mTvOtherName;
        private TextView mTvOtherContent;
        private RoundImageView mIvOtherAvatar;

        public DiscussDetOtherViewHolder(View itemView) {
            super(itemView);
            mTvOtherTime = (TextView) itemView.findViewById(R.id.tv_other_time);
            mTvOtherName = (TextView) itemView.findViewById(R.id.tv_other_name);
            mTvOtherContent = (TextView) itemView.findViewById(R.id.tv_other_content);

            mTvOtherContent.setMaxWidth((int) (screenWidth / 1.6f));

            mIvOtherAvatar = (RoundImageView) itemView.findViewById(R.id.iv_other_avatar);
        }
    }

    private class DiscussDetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<HttpDiscussDet> datas = new ArrayList<>();
        private View.OnLongClickListener onAvaterLongClicklistener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
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

        public void updataList(List<HttpDiscussDet> data) {
            if (data == null)
                data = new ArrayList<>();
            this.datas = data;
            this.notifyDataSetChanged();
            scrollToBottom();
        }

        public void addMineMessage(HttpDiscussDet info) {
            datas.add(info);
            notifyItemChanged(getItemCount());
            scrollToBottom();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            RecyclerView.ViewHolder holder = null;
            switch (viewType) {
                case DiscussSendMode.mine:
                    view = View.inflate(ActivityDiscussDet.this, R.layout.item_discuss_det_mine, null);
                    holder = new DiscussDetMineViewHolder(view);
                    return holder;
                case DiscussSendMode.other:
                    view = View.inflate(ActivityDiscussDet.this, R.layout.item_discuss_det_other, null);
                    holder = new DiscussDetOtherViewHolder(view);
                    return holder;
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder == null)
                return;
            final HttpDiscussDet info = datas.get(position);
            if (holder.getClass() == DiscussDetMineViewHolder.class) {
                DiscussDetMineViewHolder mineHolder = (DiscussDetMineViewHolder) holder;
                mineHolder.tvMineTime.setText(app.df3.format(new Date(info.createdAt * 1000)));
                mineHolder.tvContent.setText(info.content);

                HaitHelper.SelectUser selectUser = new HaitHelper.SelectUser(info.creator.name, info.creator.id);
                mineHolder.ivMineAvatar.setTag(selectUser);

                mineHolder.ivMineAvatar.setOnLongClickListener(onAvaterLongClicklistener);
//                ImageLoader.getInstance().displayImage(Config_project.);
            } else if (holder.getClass() == DiscussDetOtherViewHolder.class) {
                DiscussDetOtherViewHolder otherHolder = (DiscussDetOtherViewHolder) holder;
                otherHolder.mTvOtherName.setText(info.creator.name);
                otherHolder.mTvOtherContent.setText(info.content);
                otherHolder.mTvOtherTime.setText(app.df3.format(new Date(info.createdAt * 1000)));

                HaitHelper.SelectUser selectUser = new HaitHelper.SelectUser(info.creator.name, info.creator.id);
                otherHolder.mIvOtherAvatar.setTag(selectUser);

                otherHolder.mIvOtherAvatar.setOnLongClickListener(onAvaterLongClicklistener);
            }
        }

        /**
         * 按照时间删除预发送信息
         *
         * @param time
         */
        public void removeAtTime(long time) {
            for (int i = 0; i < getItemCount(); i++) {
                HttpDiscussDet discussion = datas.get(i);
                if (discussion.createdAt == time) {
                    datas.remove(i);
                    linearLayoutManager.removeViewAt(i);
                    break;
                }
            }
        }

        public void updata(long time, Discussion discussion) {
            for (int i = 0; i < getItemCount(); i++) {
                HttpDiscussDet dis = datas.get(i);
                if (discussion.getCreatedAt() == time) {
                    dis.id = discussion.getId();
                    dis.createdAt = discussion.getCreatedAt();
                    break;
                }
            }
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        @Override
        public int getItemViewType(int position) {
            String id = datas.get(position).creator.id;
            boolean isMine = TextUtils.isEmpty(id) ? false : id.equals(MainApp.user.getId());
            return isMine ? DiscussSendMode.mine : DiscussSendMode.other;
        }
    }

    private String add$Name_real(String name) {
        return "@" + name + SCANNER_HAIT_TRIM;
    }

    private static class DiscussSendMode {
        private static final int other = 0x0001;
        private static final int mine = 0x0002;
    }
}
