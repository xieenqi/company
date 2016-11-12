package com.loyo.oa.v2.activityui.commonview;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;

import com.loyo.oa.v2.common.Global;

/**
 * Created by xeq on 16/11/11.
 */

public class RecordUtils {

    private static RecordUtils mInstance;
    private Context context;
    MediaRecorder recorder;
    MediaPlayer play;
    String AUDIO_ROOTPATH, outPath, fileName;//录音存放路径、输出路径、输出文件名字
    boolean isStart;
    long startTime, endTime;

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
    public void initRecord() {
        recorder = new MediaRecorder();
//		recorder.setAudioChannels(numChannels);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Global.Toast("储存卡不可用");
            return;
        }
        File ff = new File(AUDIO_ROOTPATH);
        if (!ff.exists()) {
            ff.mkdirs();
        }
        fileName = getDate() + ".amr";
        outPath = AUDIO_ROOTPATH + File.separator + fileName;
        recorder.setOutputFile(outPath);
         /*
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default
			 * THREE_GPP(3gp格式，H263视频
			 * /ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
			 * recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			 */
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default */
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

    }

    /**
     * 开始录音
     */
    public void startRecord() {
        try {
            isStart = true;
            recorder.prepare();
            recorder.start();
            startTime = System.currentTimeMillis();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        if (recorder != null && isStart) {
            isStart = false;
            recorder.stop();
            recorder.reset();
            recorder.release();
            endTime = System.currentTimeMillis();
        }
    }


    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean isStart) {
        this.isStart = isStart;
    }

    public void releasaeFile(String Path) {
        if (Path == null) {
            return;
        }
        File f = new File(Path);
        if (f.exists()) {
            f.delete();
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getAUDIO_ROOTPATH() {
        return AUDIO_ROOTPATH;
    }

    public void setAUDIO_ROOTPATH(String aUDIO_ROOTPATH) {
        AUDIO_ROOTPATH = aUDIO_ROOTPATH;
    }


    public String getOutPath() {
        return outPath;
    }

    public void setOutPath(String outPath) {
        this.outPath = outPath;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void voicePlay(String playPath) {
        clean_play();
        play = new MediaPlayer();
        try {
            play.setDataSource(playPath);
            play.prepare();
            play.start();
            play.prepareAsync();
            play.setOnCompletionListener(Completion);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private MediaPlayer.OnCompletionListener Completion = new
            MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.start();
                }
            };

    //	private boolean scalea(float x,float y){
//		float bx=recorder_bu.getLeft()+x;
//		float by=recorder_bu.getTop()+y;
//		RectF rectf=new RectF(img.getLeft(),img.getTop(),img.getRight(),img.getBottom());
//		return rectf.contains(bx, by);
//	}
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


//    public static void main(String[] args) {
//        actionRecordClick
//    }
}
