package com.loyo.oa.voip;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.voip.api.IVOIP;
<<<<<<< 897ca90f9f300811cf5d97686e6e8ccf502f2ac6
import com.loyo.oa.voip.model.ResponseBase;
import com.loyo.oa.voip.model.VoipToken;
=======
import com.loyo.oa.voip.callback.OnRespond;
import com.loyo.oa.voip.model.ResponseBase;
import com.loyo.oa.voip.model.VoipToken;
import com.yzx.api.CallType;
>>>>>>> voip 初始化sdk
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
<<<<<<< 897ca90f9f300811cf5d97686e6e8ccf502f2ac6
                Log.v("yzx", "onLogin");
=======
>>>>>>> voip 初始化sdk
                if (reason.getReason() == 300107) {

                }
                else {
                    String msg = reason.getMsg();
                }
            }
        });
    }

<<<<<<< 897ca90f9f300811cf5d97686e6e8ccf502f2ac6
=======
    private void connect(String token , final OnRespond callback) {
        UCSManager.connect(token, new ILoginListener(){

            @Override
            public void onLogin(UcsReason reason) {
                if (callback != null) {
                    callback.onRespond(reason);
                }
                if (reason.getReason() == 300107) {
                }
                else {
                    String msg = reason.getMsg();
                }
            }
        });
    }

    public void connectVoipServer(final OnRespond callback) {
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
                            connect(token, callback);
                        }
                    }
                });

    }

>>>>>>> voip 初始化sdk
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

<<<<<<< 897ca90f9f300811cf5d97686e6e8ccf502f2ac6
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

=======
    public void dialNumber(final String phone) {
        if (phone == null || phone.length() <= 0) {
            return;
        }

        //
        if (UCSService.isConnected() ) {
            _dial(phone);

        }
        else {
            connectVoipServer(new OnRespond() {
                @Override
                public void onRespond(Object userInfo) {
                    UcsReason reason = (UcsReason) userInfo;
                    if (reason.getReason() == 300107) {
                        _dial(phone);
                    }
                    else {
                        String msg = reason.getMsg();
                    }
                }
            });
        }
    }

    private void _dial(String phone) {
        UCSCall.dial(mContext, CallType.DIRECT, phone, "");
    }

    public void hangUp() {
        UCSCall.hangUp(mContext, "");
    }

    public void switchSpeackPhone(boolean on) {
        UCSCall.setSpeakerphone(mContext, on);
    }

    public void switchMicMute(boolean on) {
        UCSCall.setMicMute(on);
>>>>>>> voip 初始化sdk
    }

    public void sendDTMF(int dtmfCode) {
        UCSCall.sendDTMF(mContext, dtmfCode, null);
    }

    public void sendDTMF(DTMF dtmf) {
        UCSCall.sendDTMF(mContext, dtmf.getCode(), null);
    }

    public void startCallRinging() {
<<<<<<< 897ca90f9f300811cf5d97686e6e8ccf502f2ac6
=======
        // TODO:
>>>>>>> voip 初始化sdk
        UCSCall.startCallRinging(mContext, null);
    }

    public void stopCallRinging() {
        UCSCall.stopCallRinging(mContext);
    }

    public void startRecording() {
<<<<<<< 897ca90f9f300811cf5d97686e6e8ccf502f2ac6
=======
        // TODO:
>>>>>>> voip 初始化sdk
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
<<<<<<< 897ca90f9f300811cf5d97686e6e8ccf502f2ac6

=======
        Log.v("yzx", "onDialFailed");
>>>>>>> voip 初始化sdk
    }

    @Override
    public void onIncomingCall(String s, String s1, String s2, String s3, String s4) {
<<<<<<< 897ca90f9f300811cf5d97686e6e8ccf502f2ac6

=======
        Log.v("yzx", "onIncomingCall");
>>>>>>> voip 初始化sdk
    }

    @Override
    public void onHangUp(String s, UcsReason reason) {
<<<<<<< 897ca90f9f300811cf5d97686e6e8ccf502f2ac6

    }

    @Override
    public void onAlerting(String s) {
=======
        Log.v("yzx", "onHangUp");

    }

    /* 未调用 ？？？ */
    @Override
    public void onAlerting(String s) {
        Log.v("yzx", "onAlerting");
>>>>>>> voip 初始化sdk

    }

    @Override
    public void onAnswer(String s) {
<<<<<<< 897ca90f9f300811cf5d97686e6e8ccf502f2ac6

    }

    @Override
    public void onNetWorkState(int i, String s) {
=======
        Log.v("yzx", "onAnswer");

    }


    /* 0 无法获取网络状态,
       1 网络状态极好,
       2 网络状态良好,
       3 网络状态一般,
       4 网络状态极差 */
    @Override
    public void onNetWorkState(int i, String s) {
        Log.v("yzx", "onNetWorkState");
>>>>>>> voip 初始化sdk

    }

    @Override
    public void onDTMF(int i) {
<<<<<<< 897ca90f9f300811cf5d97686e6e8ccf502f2ac6
=======
        Log.v("yzx", "onDTMF");
>>>>>>> voip 初始化sdk

    }

    @Override
    public void onCameraCapture(String s) {
<<<<<<< 897ca90f9f300811cf5d97686e6e8ccf502f2ac6

=======
        Log.v("yzx", "onCameraCapture");
>>>>>>> voip 初始化sdk
    }

    @Override
    public void singlePass(int i) {
<<<<<<< 897ca90f9f300811cf5d97686e6e8ccf502f2ac6

=======
        Log.v("yzx", "singlePass");
>>>>>>> voip 初始化sdk
    }

    @Override
    public void onRemoteCameraMode(UCSCameraType type) {
<<<<<<< 897ca90f9f300811cf5d97686e6e8ccf502f2ac6
=======
        Log.v("yzx", "onRemoteCameraMode");
>>>>>>> voip 初始化sdk

    }

    @Override
    public void onEncryptStream(byte[] bytes, byte[] bytes1, int i, int[] ints) {
<<<<<<< 897ca90f9f300811cf5d97686e6e8ccf502f2ac6
=======
        Log.v("yzx", "onEncryptStream");
>>>>>>> voip 初始化sdk

    }

    @Override
    public void onDecryptStream(byte[] bytes, byte[] bytes1, int i, int[] ints) {
<<<<<<< 897ca90f9f300811cf5d97686e6e8ccf502f2ac6
=======
        Log.v("yzx", "onDecryptStream");
>>>>>>> voip 初始化sdk

    }

    @Override
    public void initPlayout(int i, int i1, int i2) {
<<<<<<< 897ca90f9f300811cf5d97686e6e8ccf502f2ac6
=======
        Log.v("yzx", "initPlayout");
>>>>>>> voip 初始化sdk

    }

    @Override
    public void initRecording(int i, int i1, int i2) {
<<<<<<< 897ca90f9f300811cf5d97686e6e8ccf502f2ac6
=======
        Log.v("yzx", "initRecording");
>>>>>>> voip 初始化sdk

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
