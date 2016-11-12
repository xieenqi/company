package com.loyo.oa.v2.activityui.commonview;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
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
import com.loyo.oa.v2.tool.LogUtil;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * 多功能模块【录音】【图片】【地址】【@】
 * Created by xeq on 16/11/11.
 */

public class MultiFunctionModule extends LinearLayout {

    private Context context;
    private LinearLayout ll_record_keyboard, ll_picture, ll_location, ll_at, dialog, ll_record;
    private ImageView ll_action_record, iv_record, iv_record_keyboard;
    private TextView tv_record_action, tv_record_number;
    static long currentTimeMillis = 0;
    private static boolean mVoiceButtonTouched;
    private RecordUtils voice;

    public MultiFunctionModule(Context context) {
        super(context);
        this.context = context;
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
        this.removeAllViews();
        this.addView(view);
        initRecord(context);
        ll_at.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                voice.voicePlay(voice.getOutPath());
            }
        });
    }

    /**
     * 设置是否需要录音
     */
    public void setIsRecording(boolean isRecording) {
        ll_record.setVisibility(isRecording ? VISIBLE : GONE);
        iv_record_keyboard.setImageResource(isRecording ? R.drawable.icon_keyboard : R.drawable.icon_record);
    }

    public void setRecordClick(OnClickListener recordClick) {
        ll_record_keyboard.setTag(false);//默认是键盘
        ll_record_keyboard.setOnClickListener(recordClick);
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


    private void initRecord(Context context) {
        voice = RecordUtils.getInstance(context);
        final File cache = StorageUtils.getOwnCacheDirectory(MainApp.getMainApp(), "imageloader/Cache");
        voice.setAUDIO_ROOTPATH(cache.getPath());
        voice.initRecord();
    }

    private View.OnTouchListener mOnVoiceRecTouchListener
            = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (getAvailaleSize() < 10) {
                Global.Toast("储存不足");
                return false;
            }
            long time = System.currentTimeMillis() - currentTimeMillis;
            if (time <= 300) {
//				LogUtil.d(LogUtil.getLogUtilsTag(CCPChattingFooter2.class), "Invalid click ");
                currentTimeMillis = System.currentTimeMillis();
                return false;
            }

//            if (mChattingFooterLinstener != null) {
//                mChattingFooterLinstener.OnVoiceRcdStartRequest();
//            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dialog.setVisibility(VISIBLE);
                    mVoiceButtonTouched = true;
                    voice.startRecord();
                    break;
                case MotionEvent.ACTION_MOVE:
                    dialog.setVisibility(VISIBLE);
                    if (event.getX() <= 0.0F || event.getY() <= -100 || event.getX() >= ll_action_record.getWidth()) {
//                        stop();//停止动画
                        cancleRecord();
                        voice.stopRecord();
                    } else {
//                        start();//开始动画
                        recordOngoing();
//					mVoiceRcdHitCancelView.setVisibility(View.GONE);
//					mVoiceHintAnimArea.setVisibility(View.VISIBLE);
                    }
                    break;
                case MotionEvent.ACTION_UP:
//                    resetVoiceRecordingButton();
//                    tv_voiceMsg.setVisibility(View.INVISIBLE);
//                    iv_end.setVisibility(View.GONE);
//                    ll_start.setVisibility(View.VISIBLE);

                    if (voice.isStart()) {
                        voice.stopRecord();
                    }
                    dialog.setVisibility(GONE);

//                    stop();
//完成录音
//                    callback.onComplete(voice.getFormat(voice.getEndTime() - voice.getStartTime()));
                    break;
            }
            return true;
        }
    };

    /**
     * 取消录音
     */
    private void cancleRecord() {
        iv_record.setImageResource(R.drawable.icon_record_no);
        tv_record_action.setText("松开手指取消语音");
        tv_record_action.setTextColor(Color.parseColor("#f5625a"));
        tv_record_number.setTextColor(Color.parseColor("#f5625a"));
    }

    /**
     * 录音 进行中
     */
    private void recordOngoing() {
        iv_record.setImageResource(R.drawable.icon_record_ok1);
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

}
