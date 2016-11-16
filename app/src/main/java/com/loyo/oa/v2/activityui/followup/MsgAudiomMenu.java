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
import com.loyo.oa.v2.tool.AnimationCommon;
import com.loyo.oa.v2.tool.Utils;

/**
 * 【评论Menu】拜访,跟进 列表与详情公用
 * Created by yyy on 16/11/16.
 */

public class MsgAudiomMenu extends LinearLayout implements View.OnClickListener {

    private Context mContext;

    private View mView;
    private LinearLayout layout_voice, layout_keyboard, layout_voicemenu;
    private ImageView iv_voice, iv_keyboard;
    private EditText edit_comment;
    private TextView tv_send_message;
    private MsgAudioMenuCallBack callBack;

    public interface MsgAudioMenuCallBack{
        void sendMsg(EditText editText);
    }


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

    public MsgAudiomMenu(Context context,MsgAudioMenuCallBack callBack) {
        super(context);
        this.callBack = callBack;
        this.mContext = context;
        initView();
        this.addView(mView);
    }

    public void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.customerview_list_voicemenu, null);

        layout_voice = (LinearLayout) mView.findViewById(R.id.layout_voice);
        layout_keyboard = (LinearLayout) mView.findViewById(R.id.layout_keyboard);
        layout_voicemenu = (LinearLayout) mView.findViewById(R.id.layout_voicemenu);
        iv_voice = (ImageView) mView.findViewById(R.id.iv_voice);
        iv_keyboard = (ImageView) mView.findViewById(R.id.iv_keyboard);
        edit_comment = (EditText) mView.findViewById(R.id.edit_comment);
        tv_send_message = (TextView) mView.findViewById(R.id.tv_send_message);
        iv_voice.setOnClickListener(this);
        iv_keyboard.setOnClickListener(this);
        tv_send_message.setOnClickListener(this);

        edit_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s)){
                    tv_send_message.setTextColor(getResources().getColor(R.color.white));
                    tv_send_message.setBackgroundResource(R.drawable.comment_sendmsg_green);
                }else{
                    tv_send_message.setTextColor(getResources().getColor(R.color.text99));
                    tv_send_message.setBackgroundResource(R.drawable.comment_sendmsg_white);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 打开Menu
     * */
    public void openMenu(){

    }

    /**
     * 收起Menu
     * */
    public void closeMenu(){
        hideInputKeyboard(edit_comment);
        edit_comment.setText("");
        layout_voice.setVisibility(View.GONE);
    }

    /**
     * 关闭软键盘
     */
    public void hideInputKeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

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
                Utils.autoKeyBoard(mContext, edit_comment);
                break;

            /*发送评论*/
            case R.id.tv_send_message:
                callBack.sendMsg(edit_comment);
                break;
        }
    }
}
