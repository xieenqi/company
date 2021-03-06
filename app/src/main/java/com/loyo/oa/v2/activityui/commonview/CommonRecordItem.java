package com.loyo.oa.v2.activityui.commonview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.loyo.oa.upload.UploadTask;
import com.loyo.oa.upload.alioss.AliOSSManager;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.Record;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;

import java.io.File;

/**
 * Created by xeq on 16/11/12.
 */

public class CommonRecordItem extends LinearLayout implements View.OnClickListener {

    private Context context;
    private String path, time;
    private TextView tv_record_length, tv_record_number;
    private ImageView iv_delete, iv_uploading_fial;
    private String uuid;
    private ProgressBar pb_progress;
    private AnimationDrawable mAnimationDrawable;
    private RecordUploadingCallback recordUploadingCallback;
    private RecordUtils rs;
    private boolean isPlay;
    Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case AliOSSManager.OSS_SUCCESS:
                    pb_progress.setVisibility(GONE);
                    recordUploadingCallback.Success((Record) msg.obj);
                    break;
                case AliOSSManager.OSS_ERROR1:
                    Global.Toast("连接异常");
                    pb_progress.setVisibility(GONE);
                    iv_uploading_fial.setVisibility(VISIBLE);
                    break;
                case AliOSSManager.OSS_ERROR2:
                    Global.Toast("录音服务端异常");
                    pb_progress.setVisibility(GONE);
                    iv_uploading_fial.setVisibility(VISIBLE);
                    break;
            }
        }
    };


    public CommonRecordItem(Context context, String path, String time, String uuid, RecordUploadingCallback recordUploadingCallback) {
        super(context);
        this.context = context;
        this.path = path;
        this.time = time;
        this.uuid = uuid;
        this.recordUploadingCallback = recordUploadingCallback;
        this.setTag(path);
        initView();
    }

    public CommonRecordItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonRecordItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.view_common_record_item, null);
        LinearLayout.LayoutParams pl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(pl);
        tv_record_length = (TextView) view.findViewById(R.id.tv_record_length);
        tv_record_number = (TextView) view.findViewById(R.id.tv_record_number);
        iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
        pb_progress = (ProgressBar) view.findViewById(R.id.pb_progress);
        iv_uploading_fial = (ImageView) view.findViewById(R.id.iv_uploading_fial);
        iv_delete.setOnClickListener(this);
        tv_record_length.setOnClickListener(this);
        iv_uploading_fial.setOnClickListener(this);
        this.removeAllViews();
        this.addView(view);
        setRecordLength();
        Global.SetTouchView(iv_delete);
        uploadingRecord();
        mAnimationDrawable = (AnimationDrawable) tv_record_length.getBackground();
        tv_record_length.setTag(mAnimationDrawable);
        rs = RecordUtils.getInstance(context);
    }

    private void setRecordLength() {
        tv_record_number.setText(time + "\"");
        int timeNumber = Integer.parseInt(time);
        if (timeNumber > 0 && timeNumber <= 10) {
            tv_record_length.setText("000");
        } else if (timeNumber > 10 && timeNumber <= 20) {
            tv_record_length.setText("00000");
        } else if (timeNumber > 20 && timeNumber <= 30) {
            tv_record_length.setText("000000");
        } else if (timeNumber > 30 && timeNumber <= 40) {
            tv_record_length.setText("0000000");
        } else if (timeNumber > 40 && timeNumber <= 50) {
            tv_record_length.setText("000000000");
        } else if (timeNumber > 50 && timeNumber <= 60) {
            tv_record_length.setText("0000000000");
        } else {
            tv_record_length.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_record_length:
                cleanOtherRecordAnimation();
                mAnimationDrawable.start();
                if (isPlay) {
                    cleanPlayRecord();
                } else {
                    rs.voicePlay(path).setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            cleanOtherRecordAnimation();
                            cleanCuurentRecordAnimation();
                        }
                    });
                    isPlay = true;
                }
                break;
            case R.id.iv_delete:
                LinearLayout parentView = (LinearLayout) CommonRecordItem.this.getParent();
                parentView.removeView(CommonRecordItem.this);
                RecordUtils.getInstance(context).clean_play();
                String tag = (String) CommonRecordItem.this.getTag();
                recordUploadingCallback.deleteRecord(tag.substring(tag.length() - 10, tag.length()));
                break;
            case R.id.iv_uploading_fial:
                uploadingRecord();
                break;
        }
    }

    /**
     * 清理 【其它】录音在播放状态的动画
     */
    private void cleanOtherRecordAnimation() {
        LinearLayout parentView = (LinearLayout) CommonRecordItem.this.getParent();
        for (int i = 0; i < parentView.getChildCount(); i++) {
            AnimationDrawable animatiob = ((AnimationDrawable) ((LinearLayout) ((LinearLayout) parentView.
                    getChildAt(i)).getChildAt(0)).getChildAt(1).getTag());
            if (animatiob != null) {
                animatiob.stop();
                animatiob.selectDrawable(0);
                if (animatiob.isRunning()) {
                    ((CommonRecordItem) parentView.getChildAt(i)).isPlay = false;
                }

            }
        }
    }

    /**
     * 清理 【当前】录音在播放状态的动画
     */
    private void cleanCuurentRecordAnimation() {
        mAnimationDrawable.stop();
        mAnimationDrawable.selectDrawable(0);
    }

    private void uploadingRecord() {
        iv_uploading_fial.setVisibility(GONE);
        pb_progress.setVisibility(VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final UploadTask task = new UploadTask(path, uuid);
                task.name = new File(path).getName();
                // 构造上传请求
                LogUtil.d("录音key:  " + task.getKey());

                PutObjectRequest put = new PutObjectRequest(Config_project.OSS_UPLOAD_BUCKETNAME(),
                        task.getKey(), path);
                put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                    @Override
                    public void onProgress(PutObjectRequest putObjectRequest, long l, long l1) {
                        LogUtil.d(l1 + "上传进度: " + l);
                        if (l == l1) {
                            Message msg = new Message();
                            msg.what = AliOSSManager.OSS_SUCCESS;
                            msg.obj = new Record(task.getKey(), Integer.parseInt(time));
                            handler.sendMessage(msg);
                        }
                    }
                });
                try {
                    AliOSSManager.getInstance().getOss().putObject(put);
                } catch (ClientException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(AliOSSManager.OSS_ERROR1);

                } catch (ServiceException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(AliOSSManager.OSS_ERROR2);

                }
            }
        }).start();
    }

    /**
     * 清理在播放的 【状态】 【动画】
     */
    public void cleanPlayRecord() {
        cleanOtherRecordAnimation();
        cleanCuurentRecordAnimation();
        rs.clean_play();
        isPlay = false;
    }

    public interface RecordUploadingCallback {
        void Success(Record record);//上传录音成功

        void deleteRecord(String tag);//删除录音
    }
}
