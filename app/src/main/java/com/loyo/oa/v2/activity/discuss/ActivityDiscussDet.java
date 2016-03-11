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
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.customview.RoundImageView;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshRecycleView;

import java.util.ArrayList;
import java.util.List;

/**
 * 【讨论详情界面】
 * create by libo 2016/03/10
 */

public class ActivityDiscussDet extends BaseActivity implements View.OnLayoutChangeListener {

    private static final char SCANNER_HAIT_TRIM = '\u2005';

    private PullToRefreshRecycleView lvNotice;
    private EditText etDiscuss;
    private LinearLayout layoutBack;
    private ImageView imgBack;
    private TextView tvTitle;
    private TextView tvEdit;
    private ImageView ivSubmit;
    private TextView tvSend;
    private RelativeLayout rl_root;

    private DiscussDetAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private int screenHeight;
    private int keyHeight;
    private int screenWidth;
    private String oldScanner;
    private LinearLayout ll_scanner;

    public static void startThisActivity(Activity act) {
        Intent intent = new Intent(act, ActivityDiscussDet.class);
        act.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE /*| WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN*/);
        setContentView(R.layout.activity_discuss_det);
        initData();
        initView();
        initListener();
    }

    private List<DiscussDetInfo> infos = new ArrayList<>();

    private void initData() {
        for (int i = 0; i < 20; i++) {
            infos.add(new DiscussDetInfo().setIsMine(i % 2 == 0));
        }

        //获取屏幕高度
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
        //阀值设置为屏幕高度的1/3, 用于判断软件盘的弹起和收起
        keyHeight = screenHeight / 3;
    }

    private void initView() {
        assignViews();

        tvTitle.setText("讨论");
        tvEdit.setText("查看项目");

        tvTitle.setVisibility(View.VISIBLE);
        tvEdit.setVisibility(View.VISIBLE);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lvNotice.getRefreshableView().setLayoutManager(linearLayoutManager);

        adapter = new DiscussDetAdapter();
        adapter.updataList(infos);
        lvNotice.getRefreshableView().setAdapter(adapter);
    }


    private void assignViews() {
        lvNotice = (PullToRefreshRecycleView) findViewById(R.id.lv_notice);
        etDiscuss = (EditText) findViewById(R.id.et_discuss);
        tvSend = (TextView) findViewById(R.id.tv_send);

        layoutBack = (LinearLayout) findViewById(R.id.layout_back);
        imgBack = (ImageView) findViewById(R.id.img_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvEdit = (TextView) findViewById(R.id.tv_edit);
        ivSubmit = (ImageView) findViewById(R.id.iv_submit);

        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
        ll_scanner = (LinearLayout) findViewById(R.id.rl_scanner);
    }

    private void initListener() {
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast("查看项目");
            }
        });
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etDiscuss.getText().toString())) {
                    return;
                }
                DiscussDetInfo info = new DiscussDetInfo().setIsMine(true);
                info.setContent(etDiscuss.getText().toString());
                adapter.addMineMessage(info);
                etDiscuss.getText().clear();
            }
        });

        lvNotice.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lvNotice.onRefreshComplete();
                    }
                }, 2000);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lvNotice.onRefreshComplete();
                    }
                }, 2000);
            }
        });

        ll_scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showKeyBoard(etDiscuss);
            }
        });

        etDiscuss.addTextChangedListener(new UserScannerTextWatcher()); //监听用户输入
        etDiscuss.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                int selection = etDiscuss.getSelectionStart();
                if (keyCode == KeyEvent.KEYCODE_DEL
                        && selection > 0
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String str = etDiscuss.getText().toString();
                    char delChar = str.charAt(selection - 1);
                    if (delChar == SCANNER_HAIT_TRIM) {
                        int aiTePrefixIndex = str.lastIndexOf("@", selection - 1);
                        if (aiTePrefixIndex > -1) {
                            int index = etDiscuss.getSelectionStart();
                            Editable editable = etDiscuss.getText();
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
            lvNotice.getRefreshableView().smoothScrollToPosition(adapter.getItemCount());
        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            User user = (User) data.getSerializableExtra(User.class.getName());
            if (user != null) {
                String selectName = add$Name(user.toShortUser().getName());
                int index = etDiscuss.getSelectionStart();
                Editable editable = etDiscuss.getText();
                editable.insert(index, selectName);

                showKeyBoard(etDiscuss);
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
//        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);

        imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
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
            int selection = etDiscuss.getSelectionStart();
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
            String regex = "^[a-z0-9A-Z]+$";
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

            mTvOtherContent.setMaxWidth(screenWidth / 2);

            mIvOtherAvatar = (RoundImageView) itemView.findViewById(R.id.iv_other_avatar);
        }
    }

    private class DiscussDetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<DiscussDetInfo> datas = new ArrayList<>();

        public void updataList(List<DiscussDetInfo> data) {
            if (data == null)
                data = new ArrayList<>();
            this.datas = data;
            this.notifyDataSetChanged();
        }

        public void addMineMessage(DiscussDetInfo info) {
            datas.add(info);
            notifyItemChanged(getItemCount());
            lvNotice.getRefreshableView().smoothScrollToPosition(getItemCount());
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
            DiscussDetInfo info = datas.get(position);
            if (holder.getClass() == DiscussDetMineViewHolder.class) {
                DiscussDetMineViewHolder mineHolder = (DiscussDetMineViewHolder) holder;
                mineHolder.tvMineTime.setText(info.getTime());
                mineHolder.tvContent.setText(info.getContent());
            } else if (holder.getClass() == DiscussDetOtherViewHolder.class) {
                DiscussDetOtherViewHolder otherHolder = (DiscussDetOtherViewHolder) holder;
                otherHolder.mTvOtherName.setText(info.getName());
                otherHolder.mTvOtherContent.setText(info.getContent());
                otherHolder.mTvOtherTime.setText(info.getTime());
            }
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        @Override
        public int getItemViewType(int position) {
            return datas.get(position).isMine()
                    ? DiscussSendMode.mine
                    : DiscussSendMode.other;
        }
    }

    private static class DiscussSendMode {
        private static final int other = 0x0001;
        private static final int mine = 0x0002;
    }
}
