package com.loyo.oa.v2.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ILogin;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import java.util.Timer;
import java.util.TimerTask;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【Token刷新】获取服务
 *
 *  Created by yyy on 16/6/16.
 *  Explain:防止程序长期停留在后台，没被销毁，但是token过期，导致不能操作
 *         定期获取最新token，规避这种风险
 */
public class RushTokenService extends IntentService {

    public int timeNum;
    public int rushCycle = 103680; //调用周期 3天

    public static Timer timer;
    public static TimerTask timerTask;
    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what == 0x01)
                timeNum = 0;
                getToken();
        }
    };

    public RushTokenService() {
        super("RushTokenService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        timerInit();
    }

    public void getToken(){
        RestAdapterFactory.getInstance().build(FinalVariables.GET_TOKEN).create(ILogin.class).getNewToken(new RCallback<Token>() {
            @Override
            public void success(Token token, Response response) {
                HttpErrorCheck.checkResponse("获取Token", response);
                MainApp.setToken(token.access_token);
                LogUtil.dee("新Token:" + MainApp.getToken());
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    public void timerInit(){
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                timeNum++;
                if(timeNum == rushCycle){
                    mHandler.sendEmptyMessage(0x01);
                    cancelJc();
                    timerInit();
                }
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    public static void cancelJc(){
        timer.cancel();
        timerTask.cancel();
        timer = null;
        timerTask = null;
    }

    public class Token {
        public String access_token;
    }
}
