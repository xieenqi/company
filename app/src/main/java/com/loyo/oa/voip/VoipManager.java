package com.loyo.oa.voip;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.voip.api.IVOIP;
import com.loyo.oa.voip.model.ResponseBase;
import com.loyo.oa.voip.model.VoipToken;
import com.yzx.api.UCSCall;
import com.yzx.api.UCSCameraType;
import com.yzx.api.UCSService;
import com.yzx.listenerInterface.CallStateListener;
import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ILoginListener;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by EthanGong on 2016/11/2.
 */
public class VoipManager implements CallStateListener {

    static private String TOKEN_KEY = "com.loyo.voip.token";
    private static VoipManager ourInstance = new VoipManager();

    public static VoipManager getInstance() {
        return ourInstance;
    }


    private Context mContext;
    private String cacheToken;

    private VoipManager() {
    }

    public VoipManager init(Context context)
    {
        mContext = context;
        cacheToken = getCacheToken();
        UCSService.initAction(context);
        UCSService.init(context, true);
        UCSCall.addCallStateListener(this);
        return this;
    }

    private ResponseBase<String> getPaymentAccess() {
        ResponseBase<String> access = RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(IVOIP.class).getPaymentAccess();
        return access;
    }

    private String getCacheToken() {

        SharedPreferences base_share = mContext.getSharedPreferences(FinalVariables.BASE_SHARE, Context.MODE_PRIVATE);
        return base_share.getString(TOKEN_KEY, null);
    }

    private void saveToken(String token) {
        if (token != null && ! token.equals(cacheToken)) {
            cacheToken = token;
            // save
            SharedPreferences base_share = mContext.getSharedPreferences(FinalVariables.BASE_SHARE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = base_share.edit();
            editor.putString(TOKEN_KEY, token);
            editor.apply();
        }
    }

    private String getToken() {
        ResponseBase<VoipToken> data = RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(IVOIP.class).getVoipToken();

        if (data != null && data.data != null) {
            return data.data.loginToken;
        }
        else  {
            return null;
        }
    }

//    300001	连接失败
//    300107	连接服务器成功
//    300108	TCP 连接成功
    private void connect(String token) {
        UCSManager.connect(token, new ILoginListener(){

            @Override
            public void onLogin(UcsReason reason) {
                Log.v("yzx", "onLogin");
                if (reason.getReason() == 300107) {

                }
                else {
                    String msg = reason.getMsg();
                }
            }
        });
    }

    public void connectVoipServer() {
        Observable.just("connect")
                .map(new Func1<String, ResponseBase<String>>() {
                    @Override
                    public ResponseBase<String> call(String text) {
                        return getPaymentAccess();
                    }
                })
                .subscribeOn(Schedulers.io())
                .map(new Func1<ResponseBase<String>, String>() {
                    @Override
                    public String call(ResponseBase<String> access) {

                        if (access == null) {
                            return null;
                        }
                        if (access.errcode != 0) {
                            return "";
                        }
                        if (cacheToken!= null && cacheToken.length() > 0) {
                            return cacheToken;
                        }
                        return getToken();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String token) {
                        if (token == null) {
                            // 网络
                            Log.v("yzx", "网络");
                        }
                        else if (token.length() <= 0) {
                            // 余额
                            Log.v("yzx", "余额");
                        }
                        else {
                            saveToken(token);
                            connect(token);
                        }
                    }
                });

    }

    public void dialNumber(String phone) {
        //
//        if (not connect) {
//            connectVoipServer();
//        }
//        else {
//            dial();
//        }
    }

    public void switchSpeackPhone(boolean on) {

    }

    public void switchMicMute(boolean on) {

    }

    public void sendDTMF(int dtmfCode) {
        UCSCall.sendDTMF(mContext, dtmfCode, null);
    }

    public void sendDTMF(DTMF dtmf) {
        UCSCall.sendDTMF(mContext, dtmf.getCode(), null);
    }

    public void startCallRinging() {
        UCSCall.startCallRinging(mContext, null);
    }

    public void stopCallRinging() {
        UCSCall.stopCallRinging(mContext);
    }

    public void startRecording() {
        UCSCall.StartRecord(null);
    }

    public void stopRecording() {
        UCSCall.StopRecord();
    }



    /**
     * CallStateListener
     */
    @Override
    public void onDialFailed(String s, UcsReason reason) {

    }

    @Override
    public void onIncomingCall(String s, String s1, String s2, String s3, String s4) {

    }

    @Override
    public void onHangUp(String s, UcsReason reason) {

    }

    @Override
    public void onAlerting(String s) {

    }

    @Override
    public void onAnswer(String s) {

    }

    @Override
    public void onNetWorkState(int i, String s) {

    }

    @Override
    public void onDTMF(int i) {

    }

    @Override
    public void onCameraCapture(String s) {

    }

    @Override
    public void singlePass(int i) {

    }

    @Override
    public void onRemoteCameraMode(UCSCameraType type) {

    }

    @Override
    public void onEncryptStream(byte[] bytes, byte[] bytes1, int i, int[] ints) {

    }

    @Override
    public void onDecryptStream(byte[] bytes, byte[] bytes1, int i, int[] ints) {

    }

    @Override
    public void initPlayout(int i, int i1, int i2) {

    }

    @Override
    public void initRecording(int i, int i1, int i2) {

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
