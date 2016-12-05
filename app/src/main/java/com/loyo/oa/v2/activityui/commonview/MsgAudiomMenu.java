package com.loyo.oa.v2.activityui.commonview;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.loyo.oa.upload.UploadTask;
import com.loyo.oa.upload.alioss.AliOSSManager;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.MultiFunctionModule;
import com.loyo.oa.v2.activityui.commonview.RecordUtils;
import com.loyo.oa.v2.beans.Record;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.AnimationCommon;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 【评论Menu】拜访,跟进 列表与详情公用
 * Created by yyy on 16/11/16.
 */

public class MsgAudiomMenu extends RelativeLayout implements View.OnClickListener {

    private Context mContext;
    private LinearLayout layout_keyboard, layout_voicemenu, keyboardView, parent;
    private ImageView iv_voice;
    private EditText edit_comment;
    private TextView tv_send_message;
    private MsgAudioMenuCallBack callBack;
    private String UUID;
    private SoundPoolUtils sp;

    public interface MsgAudioMenuCallBack {
        void sendMsg(EditText editText);

        void sebdRecordInfo(Record record);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AliOSSManager.OSS_SUCCESS:
                    callBack.sebdRecordInfo((Record) msg.obj);
                    sp.playRecordSengSuccess();
                    break;
                case AliOSSManager.OSS_ERROR1:
                    Global.Toast("连接异常");
                    break;
                case AliOSSManager.OSS_ERROR2:
                    Global.Toast("录音服务端异常");
                    break;
            }
        }
    };

    public MsgAudiomMenu(Context context, MsgAudioMenuCallBack callBack, String UUID) {
        super(context);
        this.callBack = callBack;
        this.mContext = context;
        this.UUID = UUID;
        this.addView(initView());
        sp = SoundPoolUtils.getInstanc();
        sp.initRecordSengSuccess();
    }

    public View initView() {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.customerview_list_voicemenu2, null);
        layout_keyboard = (LinearLayout) mView.findViewById(R.id.layout_keyboard);
        layout_voicemenu = (LinearLayout) mView.findViewById(R.id.layout_voicemenu);
        iv_voice = (ImageView) mView.findViewById(R.id.iv_voice);
        edit_comment = (EditText) mView.findViewById(R.id.edit_comment);
        tv_send_message = (TextView) mView.findViewById(R.id.tv_send_message);
        iv_voice.setOnClickListener(this);
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
        return mView;
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
                parent = (LinearLayout) v.getParent().getParent().getParent().getParent().getParent();
                if (RecordUtils.permissionRecord()) {
                    if ((boolean) v.getTag()) {
                        mfmodule.setIsRecording(false);
                        v.setTag(false);
                        layout_voicemenu.setVisibility(View.VISIBLE);//评论
                        parent.setVisibility(View.GONE);//语音
                        showInputKeyboard(edit_comment);
                    } else {
                        mfmodule.setIsRecording(true);
                        v.setTag(true);
                        parent.setVisibility(View.VISIBLE);
                        layout_voicemenu.setVisibility(View.GONE);
                        hideInputKeyboard(edit_comment);
                    }
                } else {
                    Global.Toast("你没有配置录音或者储存权限");
                }

            }
        });
        /*录音完成回调*/
        mfmodule.setRecordComplete(new MultiFunctionModule.RecordComplete() {
            @Override
            public void recordComplete(final String recordPath, final String tiem) {
                layout_voicemenu.setVisibility(View.VISIBLE);
                parent.setVisibility(View.GONE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uplodingRecord(recordPath, tiem, UUID);
                    }
                }).start();
            }
        });
    }

    private void uplodingRecord(String path, final String tiem, String uuid) {
        final UploadTask task = new UploadTask(path, uuid);
        task.name = new File(path).getName();
        // 构造上传请求
        LogUtil.d("录音 列表 key:  " + task.getKey());
        PutObjectRequest put = new PutObjectRequest(Config_project.OSS_UPLOAD_BUCKETNAME(),
                task.getKey(), path);
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest putObjectRequest, long l, long l1) {
                LogUtil.d(l1 + "上传进度: " + l);
                if (l == l1) {
                    Message msg = new Message();
                    msg.what = AliOSSManager.OSS_SUCCESS;
                    msg.obj = new Record(task.getKey(), Integer.parseInt(tiem));
                    mHandler.sendMessage(msg);
                }
            }
        });
        try {
            AliOSSManager.getInstance().getOss().putObject(put);
        } catch (ClientException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(AliOSSManager.OSS_ERROR1);
        } catch (ServiceException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(AliOSSManager.OSS_ERROR2);
        }
    }

    /**
     * 打开Menu
     */
    public void openMenu() {

    }

    public EditText getEditComment() {
        return edit_comment;
    }

    /**
     * 评论成功操作
     */
    public void commentSuccessEmbl() {
        hideInputKeyboard(edit_comment);
        edit_comment.setText("");
    }

    /**
     * 列表里点击评论操作
     */
    public void commentEmbl() {
        Utils.autoKeyBoard(mContext, edit_comment);
        layout_voicemenu.setVisibility(View.VISIBLE);
        layout_keyboard.setVisibility(View.GONE);
    }

    /**
     * 收起Menu
     */
    public void closeMenu() {
        hideInputKeyboard(edit_comment);
        edit_comment.setText("");
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
                keyboardView.performClick();
                break;
            /*发送评论*/
            case R.id.tv_send_message:
                callBack.sendMsg(edit_comment);
                break;
        }
    }
}
