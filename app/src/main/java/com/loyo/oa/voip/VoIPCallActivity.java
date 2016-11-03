package com.loyo.oa.voip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.voip.callback.OnRespond;
import com.yzx.api.UCSCall;
import com.yzx.api.UCSCameraType;
import com.yzx.listenerInterface.CallStateListener;
import com.yzx.tools.PhoneNumberTools;
import com.yzxtcp.data.UcsReason;

import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by EthanGong on 2016/11/3.
 */
public class VoIPCallActivity extends Activity implements View.OnClickListener, CallStateListener {

    static public String CALLEE_NAME_KEY = "com.loyo.voip.callee.name";
    static public String CALLEE_PHONE_KEY = "com.loyo.voip.callee.phone";

    LinearLayout callingContainer;
    private TextView
            calleeView,
            calleeName, statusView;        /* 被呼者 */

    private View
            speakPhone,                    /* 免提 */
            padUp,                         /* 键盘弹出 */
            mute;                          /* 静音 */

    LinearLayout padContainer;
    private TextView
            calleeName2, statusView2;       /* 被呼者 */
    private Button
            keyOne,   keyTwo,   keyThree,   /* 1 2 3 */
            keyFour,  keyFive,  keySix,     /* 4 5 6 */
            keySeven, keyEight, keyNine,    /* 7 8 9 */
            keyStar,  keyZero,  keyHash;    /* * 0 # */

    private View
            padDown,                        /* 键盘收起 */
            hangUp;                         /* 挂断 */

    /* Data */
    private boolean isSpeakPhoneOn= false;
    private boolean isMuteOn = false;
    private boolean isAnswering;
    private String callee;
    private String phone;
    private long startTimestamp;

    /**/
    private Timer timer;
    SweetAlertDialogView dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voip_call);
        Intent mIntent = getIntent();
        callee = mIntent.getStringExtra(CALLEE_NAME_KEY);
        phone = mIntent.getStringExtra(CALLEE_PHONE_KEY);
        initUI();

        UCSCall.addCallStateListener(this);
        loadData();
        // 拨打
        dial("18502818409");
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        UCSCall.removeCallStateListener(this);
        VoIPManager.getInstance().hangUp();
        super.onDestroy();
    }

    private void initTimer() {
        if (timer != null) {
            timer.cancel();
        }
        startTimestamp = System.currentTimeMillis();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long currentTimestamp = System.currentTimeMillis();
                long diff = (currentTimestamp -startTimestamp)/1000;
                long hour = diff/3600;
                long minute = (diff%3600)/60;
                long second = ((diff%3600)%60);
                final String time = ""+hour+":"+String.format("%02d", minute)+":"+String.format("%02d", second);
                VoIPCallActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        statusView.setText(time);
                        statusView2.setText(time);
                    }
                });
            }
        }, 1000, 1000);
    }

    private void initUI() {

        /**/
        callingContainer = (LinearLayout)findViewById(R.id.calling_container);
        //
        calleeView = (TextView)findViewById(R.id.tv_callee);
        calleeName = (TextView)findViewById(R.id.tv_callee_name);
        statusView = (TextView)findViewById(R.id.tv_calling_status);
        //
        speakPhone = findViewById(R.id.speak_phone);
        padUp = findViewById(R.id.pad_up);
        mute = findViewById(R.id.mute);

        /**/
        padContainer = (LinearLayout) findViewById(R.id.pad_container);
        //
        calleeName2 = (TextView)findViewById(R.id.tv_callee_name2);
        statusView2 = (TextView)findViewById(R.id.tv_calling_status2);
        //
        keyOne = (Button)findViewById(R.id.key_one);
        keyTwo = (Button)findViewById(R.id.key_two);
        keyThree = (Button)findViewById(R.id.key_three);
        //
        keyFour = (Button)findViewById(R.id.key_four);
        keyFive = (Button)findViewById(R.id.key_five);
        keySix = (Button)findViewById(R.id.key_six);
        //
        keySeven = (Button)findViewById(R.id.key_seven);
        keyEight = (Button)findViewById(R.id.key_eight);
        keyNine = (Button)findViewById(R.id.key_nine);
        //
        keyStar = (Button)findViewById(R.id.key_star);
        keyZero = (Button)findViewById(R.id.key_zero);
        keyHash = (Button)findViewById(R.id.key_hash);


        /**/
        padDown = findViewById(R.id.pad_down);
        hangUp = findViewById(R.id.img_hang_up);

        padDown.setVisibility(View.INVISIBLE);
        callingContainer.setVisibility(View.VISIBLE);
        padContainer.setVisibility(View.INVISIBLE);

        padUp.setOnClickListener(this);
        padDown.setOnClickListener(this);
        speakPhone.setOnClickListener(this);
        mute.setOnClickListener(this);
        hangUp.setOnClickListener(this);

        Global.SetTouchView(hangUp);
        Global.SetTouchView(padDown);
        Global.SetTouchView(padUp);

        initKeyListener();
    }

    private void initKeyListener() {
        keyOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDTMF(DTMF.ONE);
            }
        });
        keyTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDTMF(DTMF.TWO);
            }
        });
        keyThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDTMF(DTMF.THREE);
            }
        });
        keyFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDTMF(DTMF.FOUR);
            }
        });
        keyFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDTMF(DTMF.FIVE);
            }
        });
        keySix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDTMF(DTMF.SIX);
            }
        });
        keySeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDTMF(DTMF.SEVEN);
            }
        });
        keyEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDTMF(DTMF.EIGHT);
            }
        });
        keyNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDTMF(DTMF.NINE);
            }
        });
        keyStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDTMF(DTMF.STAR);
            }
        });
        keyZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDTMF(DTMF.ZERO);
            }
        });
        keyHash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDTMF(DTMF.HASH);
            }
        });
    }

    private void sendDTMF(DTMF dtmf) {
        if (!isAnswering) {
            return;
        }
        VoIPManager.getInstance().sendDTMF(dtmf);
    }

    private void loadData() {
        if (callee.length() > 0) {
            String name = callee.substring(0, 1);
            calleeView.setText(name);
        }
        else {
            calleeView.setText(" ");
        }
        calleeName.setText(callee);
        calleeName2.setText(callee);
    }

    private void dial(String number) {
        if (!PhoneNumberTools.checkMobilePhoneNumber(number) &&
                !PhoneNumberTools.checkTelphoneNumber(number)) {
            final SweetAlertDialogView dialog = new SweetAlertDialogView(VoIPCallActivity.this);
            dialog.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    dialog.sweetAlertDialog.dismiss();
                    finish();
                }
            }, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    dialog.sweetAlertDialog.dismiss();
                    finish();
                }
            },"提示","电话号码「 "+number+" 」不正确");
            return;
        }

        VoIPManager.getInstance().dialNumber(number, new OnRespond() {
            @Override
            public void onPaymentDeny() {
                // 余额不足
                Log.v("yzx", "余额不足");
                final SweetAlertDialogView dialog = new SweetAlertDialogView(VoIPCallActivity.this);
                dialog.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        dialog.sweetAlertDialog.dismiss();
                        finish();
                    }
                }, new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        dialog.sweetAlertDialog.dismiss();
                        finish();
                    }
                },"提示","余额不足，请提醒管理员充值！");
            }

            @Override
            public void onNetworkError() {
                // 网络连接出错
                Log.v("yzx", "网络连接出错");
                final SweetAlertDialogView dialog = new SweetAlertDialogView(VoIPCallActivity.this);
                dialog.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        dialog.sweetAlertDialog.dismiss();
                        finish();
                    }
                }, new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        dialog.sweetAlertDialog.dismiss();
                        finish();
                    }
                },"提示","网络连接出错,请检查网络然后重试！");
            }

            @Override
            public void onRespond(Object userInfo) {
                UcsReason reason = (UcsReason)userInfo;
                if (reason!=null && reason.getReason() == 300107) {
                    // 连接成功
                    Log.v("yzx", "连接成功");
                }
                else {
                    // 连接失败
                    Log.v("yzx", "连接失败");
                    final SweetAlertDialogView dialog = new SweetAlertDialogView(VoIPCallActivity.this);
                    dialog.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            dialog.sweetAlertDialog.dismiss();
                            finish();
                        }
                    }, new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            dialog.sweetAlertDialog.dismiss();
                            finish();
                        }
                    },"提示",reason.getMsg()!=null?reason.getMsg():"连接失败");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.speak_phone:{
                isSpeakPhoneOn = !isSpeakPhoneOn;
                speakPhone.setAlpha((float) (isSpeakPhoneOn?1.0:0.5));
                VoIPManager.getInstance().switchSpeackPhone(isSpeakPhoneOn);
            }
            break;
            case R.id.mute:{
                isMuteOn = !isMuteOn;
                mute.setAlpha((float) (isMuteOn?1.0:0.5));
                VoIPManager.getInstance().switchMicMute(isMuteOn);
            }
            break;
            case R.id.pad_up:{
                callingContainer.setVisibility(View.INVISIBLE);
                padContainer.setVisibility(View.VISIBLE);
                padDown.setVisibility(View.VISIBLE);
            }
            break;
            case R.id.pad_down:{
                callingContainer.setVisibility(View.VISIBLE);
                padContainer.setVisibility(View.INVISIBLE);
                padDown.setVisibility(View.INVISIBLE);
            }
            break;
            case R.id.img_hang_up:{
                VoIPManager.getInstance().hangUp();
            }
            break;
        }
    }

    @Override
    public void onDialFailed(String s, final UcsReason reason) {
        Log.v("yzx", "onDialFailed------------");

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final SweetAlertDialogView dialog = new SweetAlertDialogView(VoIPCallActivity.this);
                dialog.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        dialog.sweetAlertDialog.dismiss();
                        finish();
                    }
                }, new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        dialog.sweetAlertDialog.dismiss();
                        finish();
                    }
                },"提示",reason.getMsg()!=null?reason.getMsg():"拨打失败");
            }
        });
    }

    @Override
    public void onIncomingCall(String s, String s1, String s2, String s3, String s4) {
        Log.v("yzx", "onIncomingCall------------");
    }

    @Override
    public void onHangUp(String s, UcsReason reason) {
        Log.v("yzx", "onHangUp------------");

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusView.setText("已挂断");
                statusView2.setText("已挂断");
            }
        });
    }

    @Override
    public void onAlerting(String s) {
        Log.v("yzx", "onAlerting------------");
    }

    @Override
    public void onAnswer(String s) {
        Log.v("yzx", "onAnswer------------");
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isAnswering = true;
                statusView.setText("00:00:00");
                statusView2.setText("00:00:00");
                initTimer();
            }
        });
    }

    @Override
    public void onNetWorkState(int i, String s) {
        Log.v("yzx", "onNetWorkState------------");
    }

    @Override
    public void onDTMF(int i) {
        Log.v("yzx", "onDTMF------------");
    }

    @Override
    public void onCameraCapture(String s) {
        Log.v("yzx", "onCameraCapture------------");
    }

    @Override
    public void singlePass(int i) {
        Log.v("yzx", "singlePass------------");
    }

    @Override
    public void onRemoteCameraMode(UCSCameraType type) {
        Log.v("yzx", "onRemoteCameraMode------------");
    }

    @Override
    public void onEncryptStream(byte[] bytes, byte[] bytes1, int i, int[] ints) {
        Log.v("yzx", "onEncryptStream------------");
    }

    @Override
    public void onDecryptStream(byte[] bytes, byte[] bytes1, int i, int[] ints) {
        Log.v("yzx", "onDecryptStream------------");
    }

    @Override
    public void initPlayout(int i, int i1, int i2) {
        Log.v("yzx", "initPlayout------------");
    }

    @Override
    public void initRecording(int i, int i1, int i2) {
        Log.v("yzx", "initRecording------------");
    }

    @Override
    public int writePlayoutData(byte[] bytes, int i) {
        return 0;
    }

    @Override
    public int readRecordingData(byte[] bytes, int i) {
        return 0;
    }
}
