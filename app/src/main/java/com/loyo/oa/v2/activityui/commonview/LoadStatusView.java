package com.loyo.oa.v2.activityui.commonview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.loyo.oa.v2.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yyy on 16/12/2.
 */

public class LoadStatusView extends Dialog{

    private ImageView image;
    private TextView  text;
    private Context mContext;

    private Button btn1,btn2;

    AnimationDrawable waitAnim; //等待
    AnimationDrawable sucsAnim; //成功
    AnimationDrawable erroAnim; //失败

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){

            switch (msg.what){

                /*成功动画*/
                case 0x01:
                    sucsAnim();
                    break;

                /*失败动画*/
                case 0x02:
                    erroAnim();
                    break;

            }
        }
    };


    public LoadStatusView(Context context) {
        super(context,R.style.CustomProgressDialog);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_loadview);
        image = (ImageView) findViewById(R.id.image);
        text  = (TextView) findViewById(R.id.text);

        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);

        waitAnim();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sucsAnim();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                erroAnim();
            }
        });

    }

    public void animSuccessEmbl(){
        mHandler.sendEmptyMessage(0x01);
    }

    public void animErrorEmbl(){
        mHandler.sendEmptyMessage(0x02);
    }

    /*等待动画*/
    void waitAnim(){
        waitAnim = new AnimationDrawable();
        for (int i = 1; i <= 10; i++) {
            int id = mContext.getResources().getIdentifier("loadwait"+i,"drawable", mContext.getPackageName());
            Drawable drawable = mContext.getResources().getDrawable(id);
            waitAnim.addFrame(drawable, 50);
        }
        waitAnim.setOneShot(false);
        image.setBackgroundDrawable(waitAnim);
        waitAnim.start();
    }

    /*成功动画*/
    void sucsAnim(){
        sucsAnim = new AnimationDrawable();
        for (int i = 1; i <= 10; i++) {
            int id = mContext.getResources().getIdentifier("loadsucs"+i,"drawable", mContext.getPackageName());
            Drawable drawable = mContext.getResources().getDrawable(id);
            sucsAnim.addFrame(drawable, 50);
        }
        sucsAnim.setOneShot(true);
        image.setBackgroundDrawable(sucsAnim);
        sucsAnim.start();
        text.setText("提交成功!");
        dismiss();
    }

    /*失败动画*/
    void erroAnim(){
        erroAnim = new AnimationDrawable();
        for (int i = 1; i <= 10; i++) {
            int id = mContext.getResources().getIdentifier("loaderro"+i,"drawable", mContext.getPackageName());
            Drawable drawable = mContext.getResources().getDrawable(id);
            erroAnim.addFrame(drawable, 50);
        }
        erroAnim.setOneShot(true);
        image.setBackgroundDrawable(erroAnim);
        erroAnim.start();
        text.setText("提交失败!");
        dismiss();
    }
}
