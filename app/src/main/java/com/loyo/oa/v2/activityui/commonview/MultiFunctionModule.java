package com.loyo.oa.v2.activityui.commonview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.os.StatFs;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;

import java.io.File;

/**
 * 多功能模块【录音】【图片】【地址】【@】
 * Created by xeq on 16/11/11.
 */

public class MultiFunctionModule extends LinearLayout {

    private Context context;
    private LinearLayout ll_record_keyboard, ll_picture, ll_location, ll_at;
    private ImageView ll_action_record;
    static long currentTimeMillis = 0;
    private static boolean mVoiceButtonTouched;
    private RecordUtils voice;
    private RelativeLayout dialog;

    public MultiFunctionModule(Context context) {
        super(context);
        this.context = context;
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
        dialog = (RelativeLayout) view.findViewById(R.id.dialog);
        this.removeAllViews();
        this.addView(view);
        ll_action_record.setOnTouchListener(mOnVoiceRecTouchListener);
        initRecord(context);
    }


    public void setRecordClick(OnClickListener recordClick) {
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
                    //mVoiceRecord.setEnabled(false);
//                    if (mChattingFooterLinstener != null) {
//                        mChattingFooterLinstener.OnVoiceRcdInitReuqest();
//                    }
                    voice.setStart(true);
                    voice.setAUDIO_ROOTPATH("");
                    voice.startRecorder();

//                    bt_voice.setBackgroundDrawable(ResourceHelper.getDrawableById(ct, R.drawable.rectangle_white));
//                    bt_voice.setText(R.string.chatfooter_releasetofinish);
//                    bt_voice.setTextColor(ct.getResources().getColor(R.color.black_text));
                    dialog.setBackgroundColor(Color.parseColor("#4ec469"));
//                    start();
                    break;

                case MotionEvent.ACTION_MOVE:
                    dialog.setVisibility(VISIBLE);
                    if (event.getX() <= 0.0F || event.getY() <= -60 || event.getX() >= ll_action_record.getWidth()) {
//                        tv_voiceMsg.setText("放弃 录音");
//                        iv_end.setVisibility(View.VISIBLE);
//                        ll_start.setVisibility(View.GONE);
//                        stop();//停止动画
                        dialog.setBackgroundColor(Color.parseColor("#fdb485"));
                        Global.Toast("停止动画");
                    } else {
//                        start();//开始动画
//                        tv_voiceMsg.setText("手指上滑，取消录音");
//                        iv_end.setVisibility(View.GONE);
//                        ll_start.setVisibility(View.VISIBLE);
                        dialog.setBackgroundColor(Color.parseColor("#4ec469"));
                        Global.Toast("开始动画");
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
                        voice.stopRecorder();
                    }
                    dialog.setVisibility(GONE);

//                    stop();
//完成录音
//                    callback.onComplete(voice.getFormat(voice.getEndTime() - voice.getStartTime()));
                    break;
            }

            return false;
        }
    };

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
