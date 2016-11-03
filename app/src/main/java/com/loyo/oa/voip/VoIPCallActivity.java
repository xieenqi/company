package com.loyo.oa.voip;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;

/**
 * Created by EthanGong on 2016/11/3.
 */
public class VoIPCallActivity extends Activity implements View.OnClickListener{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voip_call);
        initUI();
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
        VoIPManager.getInstance().sendDTMF(dtmf);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
}
