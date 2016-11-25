package com.loyo.oa.v2.activityui.commonview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.AnimationCommon;
import com.loyo.oa.v2.tool.LogUtil;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 多功能模块【录音】【图片】【地址】【@】
 * Created by xeq on 16/11/11.
 */

public class MultiFunctionModule extends LinearLayout {

    private LinearLayout ll_record_keyboard, ll_picture, ll_location, ll_at, dialog, ll_record;
    private ImageView ll_action_record, iv_record, iv_record_keyboard, iv_picture;
    private TextView tv_record_action, tv_record_number;
    private long recordTime;
    private boolean isRecordCancle, isEffective = false, isActionMove;//录音是否达到有效值
    private RecordUtils voice;
    private RecordComplete callbackComplete;
    Handler handler = new Handler();
    Timer timer;
    TimerTask task;
    AnimationDrawable mAnimationDrawable;

    public MultiFunctionModule(Context context) {
        super(context);
        this.setBackgroundColor(Color.parseColor("#00000000"));
        initView(context);
    }

    public MultiFunctionModule(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiFunctionModule(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_multi_function, null);
        LinearLayout.LayoutParams pl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(pl);
        ll_record_keyboard = (LinearLayout) view.findViewById(R.id.ll_record_keyboard);
        ll_picture = (LinearLayout) view.findViewById(R.id.ll_picture);
        ll_location = (LinearLayout) view.findViewById(R.id.ll_location);
        ll_at = (LinearLayout) view.findViewById(R.id.ll_at);
        ll_action_record = (ImageView) view.findViewById(R.id.ll_action_record);
        ll_action_record.setOnTouchListener(mOnVoiceRecTouchListener);
        dialog = (LinearLayout) view.findViewById(R.id.dialog);
        iv_record = (ImageView) view.findViewById(R.id.iv_record);
        tv_record_action = (TextView) view.findViewById(R.id.tv_record_action);
        tv_record_number = (TextView) view.findViewById(R.id.tv_record_number);
        ll_record = (LinearLayout) view.findViewById(R.id.ll_record);
        iv_record_keyboard = (ImageView) view.findViewById(R.id.iv_record_keyboard);
        iv_record_keyboard.setImageResource(R.drawable.icon_record);
        iv_picture = (ImageView) view.findViewById(R.id.iv_picture);
        this.removeAllViews();
        this.addView(view);
        initRecord(context);
        Global.SetTouchView(ll_record_keyboard, ll_picture, ll_location, ll_at);
        mAnimationDrawable = (AnimationDrawable) iv_record.getBackground();
    }

    /**
     * 设置启用的模块
     */
    public void setEnableModle(boolean record, boolean picture, boolean location, boolean at) {
        ll_record_keyboard.setVisibility(record ? VISIBLE : GONE);
        ll_picture.setVisibility(picture ? VISIBLE : GONE);
        ll_location.setVisibility(location ? VISIBLE : GONE);
        ll_at.setVisibility(at ? VISIBLE : GONE);
    }

    /**
     * 拜访签到要单独设置图片
     *
     * @param icon
     */
    public void setPictureIcon(int icon) {
        iv_picture.setImageResource(icon);
    }

    /**
     * 设置是否需要录音
     */
    public void setIsRecording(boolean isRecording) {
        if (isRecording) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
//                            ll_record.setAnimation(AnimationCommon.inFromBottomAnimation(80));
                            ll_record.setVisibility(VISIBLE);
                        }
                    });
                }
            }, 300);
        } else {
            ll_record.setAnimation(AnimationCommon.inFromTopeAnimation(80));
            ll_record.setVisibility(GONE);
        }
        iv_record_keyboard.setImageResource(isRecording ? R.drawable.icon_keyboard : R.drawable.icon_record);
    }

    public LinearLayout setRecordClick(OnClickListener recordClick) {
        ll_record_keyboard.setTag(false);//默认是键盘
        ll_record_keyboard.setOnClickListener(recordClick);
        return ll_record_keyboard;
    }


    public void setLocationClick(OnClickListener locationClick) {
        ll_location.setOnClickListener(locationClick);
    }

    public void setPictureClick(OnClickListener pictureClick) {
        ll_picture.setOnClickListener(pictureClick);

    }

    public void setAtClick(OnClickListener atClick) {
        ll_at.setOnClickListener(atClick);
    }

    public void setRecordComplete(RecordComplete callbackComplete) {
        this.callbackComplete = callbackComplete;
    }

    private void initRecord(Context context) {
        voice = RecordUtils.getInstance(context);
        final File cache = StorageUtils.getOwnCacheDirectory(MainApp.getMainApp(), "imageloader/Cache");
        voice.setAUDIO_ROOTPATH(cache.getPath());
        voice.setCallbackMicStatus(new RecordUtils.CallbackMicStatus() {
            @Override
            public void setMicData(double db) {
                if(db>20)
                    isEffective=true;
                refreshRecordIcon(db);
            }
        });
    }

    //TODO 此处逻辑比较乱 需要重构下review
    private View.OnTouchListener mOnVoiceRecTouchListener
            = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (getAvailaleSize() < 10) {
                Global.Toast("储存不足");
                return false;
            }
            if (recordTime == 0 && voice.getStartTime() != 0) {
                stratRecordingTime();
                isRecordCancle = false;
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setAlpha(0.6f);
                    dialog.setVisibility(VISIBLE);
                    voice.initStaratRecord();
                    // 此处兼容魅族手机
                    if (isRecordCancle || (recordTime == 0 && voice.getStartTime() != 0)) {
                        stratRecordingTime();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    isActionMove = true;// 此处兼容魅族手机
                    dialog.setVisibility(VISIBLE);
                    if (event.getX() <= 0.0F || event.getY() <= -100 || event.getX() >= ll_action_record.getWidth()) {
                        //停止动画
//                        puaseRecordingTime();
                        cancleRecord();
//                        voice.pauseRcord();
                        v.setAlpha(1f);
                    } else {
                        // 开始动画
                        if (isRecordCancle || (recordTime == 0 && voice.getStartTime() != 0)) {
                            stratRecordingTime();
                        }
                        recordOngoing();
                        v.setAlpha(0.6f);
                    }
                    if (recordTime >= 60) {//此处过了一分钟
                        dialog.setVisibility(GONE);
                        completeRecord();
                        Global.Toast("录音时间只能在一分钟内");
                        return false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    dialog.setVisibility(GONE);
                    v.setAlpha(1f);
                    completeRecord();
                    break;
            }
            return true;
        }
    };


    /**
     * 录音完成的操作
     */
    private void completeRecord() {
        if (!isRecordCancle && isEffective && recordTime > 1) {
            callbackComplete.recordComplete(voice.getOutPath(), recordTime + "");
            //恢复默认录音状态是键盘
            ll_record_keyboard.setTag(false);
            setIsRecording(false);
        } else {
            if (!isRecordCancle)
                Global.Toast("好像你没有说话哦!");
        }
        cancleRecordingTime();
        voice.stopRecord();
    }

    /*录音时间开始*/
    private void stratRecordingTime() {
        voice.resumRcord();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                recordTime++;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (recordTime >= 50) {
                            tv_record_number.setText("录音倒计时" + (60 - recordTime) + "'");
                        } else {
                            tv_record_number.setText(recordTime + "'");
                        }
                        // 此处兼容魅族手机
                        if (recordTime >= 60 && !isActionMove) {//此处过了一分钟
                            isRecordCancle = false;
                            dialog.setVisibility(GONE);
                            completeRecord();
                            Global.Toast("录音时间只能在一分钟内");
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, 1000);

    }

    /*录音时间 暂停*/
    private void puaseRecordingTime() {
        if (task != null)
            task.cancel();
        if (timer != null)
            timer.cancel();
    }

    /*录音时间  取消*/
    private void cancleRecordingTime() {
        if (timer != null)
            timer.cancel();
        recordTime = 0;
        if (task != null)
            task.cancel();
        task = null;
        timer = null;
        tv_record_number.setText("");
    }

    /**
     * 取消录音
     */
    private void cancleRecord() {
        isRecordCancle = true;
        mAnimationDrawable.selectDrawable(10);
        tv_record_action.setText("松开手指取消语音");
        tv_record_action.setTextColor(Color.parseColor("#f5625a"));
        tv_record_number.setTextColor(Color.parseColor("#f5625a"));
    }

    /**
     * 录音 进行中
     */
    private void recordOngoing() {
        isRecordCancle = false;
        tv_record_action.setText("滑动至此处可取消录音");
        tv_record_action.setTextColor(Color.parseColor("#ffffff"));
        tv_record_number.setTextColor(Color.parseColor("#ffffff"));
    }

    /**
     * 获取储存大小
     */
    public static long getAvailaleSize() {

        File path = Environment.getExternalStorageDirectory(); //取得sdcard文件路径
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return (availableBlocks * blockSize) / 1024 / 1024;//  MIB单位
    }


    private void refreshRecordIcon(double db) {
        if (isRecordCancle) {
            return;
        }
        LogUtil.d("分贝值 : " + db);
        if (db < 10) {
            mAnimationDrawable.selectDrawable(0);
        } else if (db > 10 && db < 20) {
            mAnimationDrawable.selectDrawable(1);
        } else if (db > 20 && db < 30) {
            mAnimationDrawable.selectDrawable(2);
        } else if (db > 30 && db < 40) {
            mAnimationDrawable.selectDrawable(3);
        } else if (db > 40 && db < 50) {
            mAnimationDrawable.selectDrawable(4);
        } else if (db > 50 && db < 60) {
            mAnimationDrawable.selectDrawable(5);
        } else if (db > 60 && db < 70) {
            mAnimationDrawable.selectDrawable(6);
            iv_record.clearAnimation();
        } else if (db > 70 && db < 80) {
            mAnimationDrawable.selectDrawable(7);
        } else if (db > 80 && db < 90) {
            mAnimationDrawable.selectDrawable(8);
        } else if (db > 90 && db < 100) {
            mAnimationDrawable.selectDrawable(9);
        }

    }

    /**
     * 录音完成的回调
     */
    public interface RecordComplete {
        void recordComplete(String recordPath, String tiem);
    }
}
