package com.loyo.oa.v2.activityui.followup;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.MultiFunctionModule;
import com.loyo.oa.v2.activityui.commonview.RecordUtils;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.AnimationCommon;
import com.loyo.oa.v2.tool.Utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 【评论Menu】拜访,跟进 列表与详情公用
 * Created by yyy on 16/11/16.
 */

public class MsgAudiomMenu extends LinearLayout implements View.OnClickListener {

    private Context mContext;

    private View mView;
    private LinearLayout layout_voice, layout_keyboard, layout_voicemenu, keyboardView;
    private ImageView iv_voice, iv_keyboard;
    private EditText edit_comment;
    private TextView tv_send_message;
    private MsgAudioMenuCallBack callBack;

    public interface MsgAudioMenuCallBack {
        void sendMsg(EditText editText);
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            if (msg.what == 0x01) {
//                layout_voice.setAnimation(AnimationCommon.inFromBottomAnimation(150));
//                layout_voice.setVisibility(View.VISIBLE);
//            } else if (msg.what == 0x02) {
//                layout_voice.setVisibility(View.GONE);
//            }
        }
    };

    public MsgAudiomMenu(Context context, MsgAudioMenuCallBack callBack) {
        super(context);
        this.callBack = callBack;
        this.mContext = context;
        initView();
        this.addView(mView);
    }

    public void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.customerview_list_voicemenu2, null);

//        layout_voice = (LinearLayout) mView.findViewById(R.id.layout_voice);
        layout_keyboard = (LinearLayout) mView.findViewById(R.id.layout_keyboard);
        layout_voicemenu = (LinearLayout) mView.findViewById(R.id.layout_voicemenu);
        iv_voice = (ImageView) mView.findViewById(R.id.iv_voice);
//        iv_keyboard = (ImageView) mView.findViewById(R.id.iv_keyboard);
        edit_comment = (EditText) mView.findViewById(R.id.edit_comment);
        tv_send_message = (TextView) mView.findViewById(R.id.tv_send_message);
        iv_voice.setOnClickListener(this);
//        iv_keyboard.setOnClickListener(this);
        tv_send_message.setOnClickListener(this);

        edit_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    tv_send_message.setTextColor(getResources().getColor(R.color.white));
                    tv_send_message.setBackgroundResource(R.drawable.comment_sendmsg_green);
                } else {
                    tv_send_message.setTextColor(getResources().getColor(R.color.text99));
                    tv_send_message.setBackgroundResource(R.drawable.comment_sendmsg_white);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        initMultiFunctionModule();
    }

    /**
     * 初始化底部多功能部件
     */
    private void initMultiFunctionModule() {
        final MultiFunctionModule mfmodule = new MultiFunctionModule(mContext);
        layout_keyboard.addView(mfmodule);
        mfmodule.setEnableModle(true, false, false, false);
        /*录音*/
        keyboardView = mfmodule.setRecordClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout parent = (LinearLayout) v.getParent().getParent().getParent().getParent();
                if (RecordUtils.permissionRecord()) {
                    if ((boolean) v.getTag()) {
                        showInputKeyboard(edit_comment);
                        mfmodule.setIsRecording(false);
                        v.setTag(false);
                        layout_voicemenu.setVisibility(View.VISIBLE);
                        parent.setVisibility(View.GONE);
                    } else {
                        hideInputKeyboard(edit_comment);
                        mfmodule.setIsRecording(true);
                        v.setTag(true);
                        layout_voicemenu.setVisibility(View.GONE);
                        parent.setVisibility(View.VISIBLE);
                    }
                } else {
                    Global.Toast("你没有配置录音或者储存权限");
                }

            }
        });
//        edit_comment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mfmodule.setIsRecording(false);
//            }
//        });
        /*录音完成回调*/
        mfmodule.setRecordComplete(new MultiFunctionModule.RecordComplete() {
            @Override
            public void recordComplete(String recordPath, String tiem) {
//                ll_record.addView(new CommonRecordItem(SignInActivity.this, recordPath, tiem, uuid, new CommonRecordItem.RecordUploadingCallback() {
//                    @Override
//                    public void Success(Record record) {//上传录音完成回调
//                        audioInfo.add(record);
//                    }
//                }));
            }
        });
    }

    /**
     * 打开Menu
     */
    public void openMenu() {

    }

    /**
     * 收起Menu
     */
    public void closeMenu() {
        hideInputKeyboard(edit_comment);
        edit_comment.setText("");
//        layout_voice.setVisibility(View.GONE);
    }

    /**
     * 关闭软键盘
     */
    public void hideInputKeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    /**
     * 手动 显示软键盘
     */
    public void showInputKeyboard(final EditText view) {
        final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        final Handler handler = new Handler();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (imm != null) {
                            view.requestFocus();
                            imm.showSoftInput(view, 0);
                        }
                    }
                });
            }
        }, 100);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

             /*切换录音*/
            case R.id.iv_voice:
                layout_keyboard.setVisibility(View.VISIBLE);
                layout_voicemenu.setVisibility(View.GONE);
                hideInputKeyboard(edit_comment);
//                new Handler().postDelayed(new Runnable() {
//                    public void run() {
//                        mHandler.sendEmptyMessage(0x01);
//                    }
//                }, 100);
                keyboardView.performClick();
                break;

//            /*切换软键盘*/
//            case R.id.iv_keyboard:
//                layout_keyboard.setVisibility(View.GONE);
////                layout_voice.setVisibility(View.GONE);
//                layout_voicemenu.setVisibility(View.VISIBLE);
//                Utils.autoKeyBoard(mContext, edit_comment);
//                break;

            /*发送评论*/
            case R.id.tv_send_message:
                callBack.sendMsg(edit_comment);
                break;
            case R.id.layout_voicemenu:
//                keyboardView.performClick();
                break;
        }
    }
}
