package com.loyo.oa.v2.activityui.commonview;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Environment;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.LogUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import vendor.omrecorder.AudioChunk;
import vendor.omrecorder.OmRecorder;
import vendor.omrecorder.PullTransport;
import vendor.omrecorder.Recorder;

/**
 * Created by xeq on 16/11/11.
 */

public class RecordUtils {

    Recorder recorder;
    private Context context;
    private MediaPlayer play;
    private String AUDIO_ROOTPATH, outPath, fileName;//录音存放路径、输出路径、输出文件名字
    private boolean isStart;
    private long startTime, endTime;
    CallbackMicStatus callbackMicStatus;
    private SoundPool sp;
    private int shoot;
    private static RecordUtils mInstance;

    private RecordUtils() {
    }

    public static RecordUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RecordUtils();
        }
        mInstance.setContext(context);
        return mInstance;
    }

    /**
     * 初始化录音
     */
    public void initStaratRecord() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Global.Toast("储存卡不可用");
            return;
        }
        File ff = new File(AUDIO_ROOTPATH);
        if (!ff.exists()) {
            ff.mkdirs();
        }
        fileName = getDate() + ".wav";
        outPath = AUDIO_ROOTPATH + File.separator + fileName;
        sp.play(shoot, 1f, 1f, 0, 0, 1f);
        recorder = OmRecorder.wav(
                new PullTransport.Default(mic(),
                        new PullTransport.OnAudioChunkPulledListener() {
                            @Override
                            public void onAudioChunkPulled(AudioChunk audioChunk) {
                                double ratio = (audioChunk.maxAmplitude() / 1);
                                callbackMicStatus.setMicData(ratio);
                            }
                        }), new File(outPath));
        recorder.startRecording();
        isStart = true;
        startTime = System.currentTimeMillis();
    }

    private vendor.omrecorder.AudioSource mic() {
        return new vendor.omrecorder.AudioSource.Smart(MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                AudioFormat.CHANNEL_IN_MONO, 44100);
    }


    public void stopRecord() {

        if (recorder != null) {
            isStart = false;
            startTime = 0;
            endTime = System.currentTimeMillis();
            recorder.stopRecording();
            recorder = null;
        }

    }

    public boolean isStart() {
        return isStart;
    }

    private void setStart(boolean isStart) {
        this.isStart = isStart;
    }

    private void releasaeFile(String Path) {
        if (Path == null) {
            return;
        }
        File f = new File(Path);
        if (f.exists()) {
            f.delete();
        }
    }

    private void setContext(Context context) {
        this.context = context;
        playPrompt();
    }

    private String getAUDIO_ROOTPATH() {
        return AUDIO_ROOTPATH;
    }

    public void setAUDIO_ROOTPATH(String aUDIO_ROOTPATH) {
        AUDIO_ROOTPATH = aUDIO_ROOTPATH;
    }


    public String getOutPath() {
        return outPath;
    }

    private void setOutPath(String outPath) {
        this.outPath = outPath;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    private void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setCallbackMicStatus(CallbackMicStatus callbackMicStatus) {
        this.callbackMicStatus = callbackMicStatus;
    }

    public MediaPlayer voicePlay(String playPath) {
        clean_play();

        play = new MediaPlayer();
        try {
            play.setDataSource(playPath);
            play.prepare();
            play.start();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            LogUtil.d("播放异常!!!IllegalArgumentException");
            e.printStackTrace();
        } catch (SecurityException e) {
            LogUtil.d("播放异常!!!SecurityException");
            e.printStackTrace();
        } catch (IllegalStateException e) {
            LogUtil.d("播放异常!!!IllegalStateException");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.d("播放异常!!!IOException");
        }
        return play;
    }

    public void clean_play() {
        if (play != null) {
            play.stop();
            play.reset();
            play.release();
            play = null;
        }
    }

    public String getFormat(long time) {
        SimpleDateFormat fromat = new SimpleDateFormat("ss", Locale.CHINA);
        return fromat.format(new Date(time));
    }

    public String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        return format.format(new Date());
    }


    /**
     * 更新话筒状态
     */
    private int BASE = 1;

    interface CallbackMicStatus {
        void setMicData(double db);
    }

    /**
     * 用户是否配置 录音权限     * @return
     */
    public static boolean permissionRecord() {
        if (PackageManager.PERMISSION_GRANTED == MainApp.getMainApp().getPackageManager().checkPermission("android.permission.RECORD_AUDIO", "com.loyo.oa.v2")
                && PackageManager.PERMISSION_GRANTED == MainApp.getMainApp().getPackageManager().checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", "com.loyo.oa.v2")
                && PackageManager.PERMISSION_GRANTED == MainApp.getMainApp().getPackageManager().checkPermission("android.permission.READ_PHONE_STATE", "com.loyo.oa.v2")
                ) {
            return true;
        }
        return false;
    }

    /**
     * 播放提示音 录音准备完成
     */
    private void playPrompt() {
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        shoot = sp.load(context, R.raw.dododo, 1);
    }
}
