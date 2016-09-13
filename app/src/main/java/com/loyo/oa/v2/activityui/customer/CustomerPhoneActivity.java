package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.DownloadProgressListener;
import com.loyo.oa.v2.tool.FileDownloader;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Player;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 【内嵌打电话】
 * Created by yyy on 16/9/8.
 */
public class CustomerPhoneActivity extends BaseActivity{

    private Button btn_start;
    private Button btn_stop;
    private ImageView iv_bg_phone;
    private AnimationDrawable animationDrawable;

    private static final int PROCESSING = 1;
    private static final int FAILURE = -1;

    private EditText pathText; // url��ַ
    private TextView resultView;
    private Button downloadButton;
    private Button stopButton;
    private ProgressBar progressBar;
    private Button playBtn;
    private Player player;
    private SeekBar musicProgress;

    private Intent mIntent;
    private String audioUrl = "http://abv.cn/music/光辉岁月.mp3";
    private String intentUrl;

    private Handler handler = new UIHandler();

    private final class UIHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROCESSING: // ���½���
                    progressBar.setProgress(msg.getData().getInt("size"));
                    float num = (float) progressBar.getProgress()
                            / (float) progressBar.getMax();
                    int result = (int) (num * 100); // �������
                    resultView.setText(result + "%");
                    if (progressBar.getProgress() == progressBar.getMax()) { // �������
                        Toast.makeText(getApplicationContext(),"success",
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case FAILURE: // ����ʧ��
                    Toast.makeText(getApplicationContext(), "error",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_phone);
        initUI();
    }

    private void initUI(){
        mIntent = getIntent();
        intentUrl = mIntent.getStringExtra("url");
        if(!TextUtils.isEmpty(intentUrl)){
            audioUrl = intentUrl;
        }
        LogUtil.dee("audio:"+audioUrl);

        btn_start = (Button)findViewById(R.id.btn_start);
        btn_stop = (Button)findViewById(R.id.btn_stop);
        iv_bg_phone = (ImageView)findViewById(R.id.iv_bg_phone);

        pathText = (EditText) findViewById(R.id.path);
        resultView = (TextView) findViewById(R.id.resultView);
        downloadButton = (Button) findViewById(R.id.downloadbutton);
        stopButton = (Button) findViewById(R.id.stopbutton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ButtonClickListener listener = new ButtonClickListener();
        downloadButton.setOnClickListener(listener);
        stopButton.setOnClickListener(listener);
        playBtn = (Button) findViewById(R.id.btn_online_play);
        playBtn.setOnClickListener(listener);
        musicProgress = (SeekBar) findViewById(R.id.music_progress);
        player = new Player(musicProgress);
        musicProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());

        pathText.setText(audioUrl);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_bg_phone.setImageResource(R.drawable.animation_phone);
                animationDrawable = (AnimationDrawable) iv_bg_phone.getDrawable();
                animationDrawable.start();
            }
        });


        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animationDrawable = (AnimationDrawable) iv_bg_phone.getDrawable();
                animationDrawable.stop();
            }
        });
    }

    private final class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.downloadbutton: // ��ʼ����
                    // http://abv.cn/music/�������.mp3�����Ի��������ļ����ص�����
                    String filename = audioUrl.substring(audioUrl.lastIndexOf('/') + 1);

                    try {
                        // URL���루������Ϊ�˽����Ľ���URL���룩
                        filename = URLEncoder.encode(filename, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    audioUrl = audioUrl.substring(0, audioUrl.lastIndexOf("/") + 1) + filename;
                    if (Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        // File savDir =
                        // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                        // ����·��
                        File savDir = Environment.getExternalStorageDirectory();
                        download(audioUrl, savDir);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "没有SD卡", Toast.LENGTH_LONG).show();
                    }
                    downloadButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    break;
                case R.id.stopbutton: // ��ͣ����
                    exit();
                    Toast.makeText(getApplicationContext(),
                            "Now thread is Stopping!!", Toast.LENGTH_LONG).show();
                    downloadButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    break;
                case R.id.btn_online_play:
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            player.playUrl(pathText.getText().toString());
                        }
                    }).start();
                    break;
            }
        }

        /*
         * �����û��������¼�(���button, ������Ļ....)�������̸߳�����ģ�������̴߳��ڹ���״̬��
         * ��ʱ�û������������¼����û����5���ڵõ�����ϵͳ�ͻᱨ��Ӧ������Ӧ������
         * ���������߳��ﲻ��ִ��һ���ȽϺ�ʱ�Ĺ���������������߳��������޷������û��������¼���
         * ���¡�Ӧ������Ӧ������ĳ��֡���ʱ�Ĺ���Ӧ�������߳���ִ�С�
         */
        private DownloadTask task;

        private void exit() {
            if (task != null)
                task.exit();
        }

        private void download(String path, File savDir) {
            task = new DownloadTask(path, savDir);
            new Thread(task).start();
        }

        /**
         *
         * UI�ؼ�������ػ�(����)�������̸߳�����ģ���������߳��и���UI�ؼ���ֵ�����º��ֵ�����ػ浽��Ļ��
         * һ��Ҫ�����߳������UI�ؼ���ֵ��������������Ļ����ʾ���������������߳��и���UI�ؼ���ֵ
         *
         */
        private final class DownloadTask implements Runnable {
            private String path;
            private File saveDir;
            private FileDownloader loader;

            public DownloadTask(String path, File saveDir) {
                this.path = path;
                this.saveDir = saveDir;
            }

            /**
             * �˳�����
             */
            public void exit() {
                if (loader != null)
                    loader.exit();
            }

            DownloadProgressListener downloadProgressListener = new DownloadProgressListener() {
                @Override
                public void onDownloadSize(int size) {
                    Message msg = new Message();
                    msg.what = PROCESSING;
                    msg.getData().putInt("size", size);
                    handler.sendMessage(msg);
                }
            };

            public void run() {
                try {
                    // ʵ����һ���ļ�������
                    loader = new FileDownloader(getApplicationContext(), path,
                            saveDir, 3);
                    // ���ý��������ֵ
                    progressBar.setMax(loader.getFileSize());
                    loader.download(downloadProgressListener);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendMessage(handler.obtainMessage(FAILURE)); // ����һ������Ϣ����
                }
            }
        }
    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // ԭ����(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
            this.progress = progress * player.mediaPlayer.getDuration()
                    / seekBar.getMax();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // seekTo()�Ĳ����������ӰƬʱ������֣���������seekBar.getMax()��Ե�����
            player.mediaPlayer.seekTo(progress);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player = null;
        }
    }

}
