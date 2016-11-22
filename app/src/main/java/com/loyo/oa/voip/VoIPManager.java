package com.loyo.oa.voip;

import android.content.Context;
import android.util.Log;

import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.voip.api.IVoIP;
import com.loyo.oa.voip.callback.OnRespond;
import com.loyo.oa.voip.exception.PermissionException;
import com.loyo.oa.voip.model.RequestAccess;
import com.loyo.oa.voip.model.ResponseBase;
import com.loyo.oa.voip.model.VoIPToken;
import com.yzx.api.CallType;
import com.yzx.api.UCSCall;
import com.yzx.api.UCSCameraType;
import com.yzx.api.UCSService;
import com.yzx.listenerInterface.CallStateListener;
import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ILoginListener;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by EthanGong on 2016/11/2.
 */
public class VoIPManager implements CallStateListener {

    static private String TOKEN_KEY = "com.loyo.voip.token";
    private static VoIPManager ourInstance = new VoIPManager();

    public static VoIPManager getInstance() {
        return ourInstance;
    }


    private Context mContext;
    private boolean isInitialized = false;
    private String cacheToken;

    private String customerId, salesleadId;
    private String userId;
    private String phone;
    private int callType;
    private RequestAccess requestAccess;

    private VoIPManager() {
    }

    public VoIPManager init(Context context) {
        mContext = context;
        cacheToken = "";
        //getCacheToken();
        /** Fix samsung crash */
//        UCSService.initAction(context);
//        UCSService.init(context, true);
//        UCSCall.addCallStateListener(this);
        return this;
    }

    public void initUCSIfNeeded() throws PermissionException {
        if (isInitialized) {
            return;
        }
        try {
            UCSService.initAction(mContext);
            UCSService.init(mContext, true);
            UCSCall.addCallStateListener(this);
            isInitialized = true;
        } catch (Exception e) {
            throw new PermissionException();
        }
    }

    private ResponseBase<RequestAccess> getPaymentAccess() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("customerId", customerId);
        params.put("salesleadId", salesleadId);
        params.put("contactId", userId);
        params.put("type", callType);
        params.put("mobile", phone);
        ResponseBase<RequestAccess> access = RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(IVoIP.class).getRequestAccess(params);
        requestAccess = access.data;
        return access;
    }

    //    private String getCacheToken() {
//
//        SharedPreferences base_share = mContext.getSharedPreferences(FinalVariables.BASE_SHARE, Context.MODE_PRIVATE);
//        return base_share.getString(TOKEN_KEY, null);
//    }
//
    private void saveToken(String token) {
        if (token != null && !token.equals(cacheToken)) {
            cacheToken = token;
            // save
//            SharedPreferences base_share = mContext.getSharedPreferences(FinalVariables.BASE_SHARE, Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = base_share.edit();
//            editor.putString(TOKEN_KEY, token);
//            editor.apply();
        }
    }

    private String getToken() {
        ResponseBase<VoIPToken> data = RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(IVoIP.class).getVoipToken();

        if (data != null && data.data != null) {
            return data.data.loginToken;
        } else {
            return null;
        }
    }

    //    300001	连接失败
    //    300107	连接服务器成功
    //    300108	TCP 连接成功
    private void connect(String token, final OnRespond callback) {
        UCSManager.connect(token, new ILoginListener() {

            @Override
            public void onLogin(UcsReason reason) {
                if (callback != null) {
                    callback.onRespond(reason);
                }
            }
        });
    }

    public void connectVoipServer(final OnRespond callback) {
        Observable.just("connect")
                .map(new Func1<String, ResponseBase<RequestAccess>>() {
                    @Override
                    public ResponseBase<RequestAccess> call(String text) {
                        return getPaymentAccess();
                    }
                })
                .subscribeOn(Schedulers.io())
                .map(new Func1<ResponseBase<RequestAccess>, String>() {
                    @Override
                    public String call(ResponseBase<RequestAccess> access) {

                        if (access == null) {
                            return null;
                        }
                        if (access.errcode != 0) {
                            return "";
                        }
                        if (cacheToken != null && cacheToken.length() > 0) {
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
                            if (callback != null) {
                                callback.onNetworkError();
                            }
                        } else if (token.length() <= 0) {
                            // 余额
                            Log.v("yzx", "余额");
                            if (callback != null) {
                                callback.onPaymentDeny();
                            }
                        } else {
                            saveToken(token);
                            connect(token, callback);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                    }
                });

    }

    public void dialNumber(final String phone, final String customerId, final String userId, int callType, String salesleadId, final OnRespond callback) {
        if (phone == null || phone.length() <= 0) {
            return;
        }
        this.customerId = customerId;
        this.userId = userId;
        this.callType = callType;
        this.phone = phone;
        this.salesleadId = salesleadId;
        //
        if (UCSService.isConnected()) {
            Observable.just("connect")
                    .map(new Func1<String, ResponseBase<RequestAccess>>() {
                        @Override
                        public ResponseBase<RequestAccess> call(String text) {
                            return getPaymentAccess();
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ResponseBase<RequestAccess>>() {
                        @Override
                        public void call(ResponseBase<RequestAccess> accessResponseBase) {
                            if (accessResponseBase.errcode == 0) {
                                if (callback != null) {
                                    callback.onRespond(new UcsReason(300107));
                                }
                                _dial(phone);
                            } else {
                                if (callback != null) {
                                    callback.onPaymentDeny();
                                }
                            }
                        }
                    });

        } else {
            connectVoipServer(new OnRespond() {
                @Override
                public void onPaymentDeny() {
                    if (callback != null) {
                        callback.onPaymentDeny();
                    }
                }

                @Override
                public void onNetworkError() {
                    if (callback != null) {
                        callback.onNetworkError();
                    }
                }

                @Override
                public void onRespond(Object userInfo) {
                    UcsReason reason = (UcsReason) userInfo;
                    if (reason.getReason() == 300107) {
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                _dial(phone);
                            }
                        }, 3000);
                    }
                    if (callback != null) {
                        callback.onRespond(userInfo);
                    }
                }
            });
        }
    }

    private void _dial(String phone) {
        String userInfo = "";
        if (requestAccess != null) {
            userInfo = requestAccess.callLogId;
        }
        UCSCall.dial(mContext, CallType.DIRECT, phone, userInfo);
    }

    public void hangUp() {
        UCSCall.hangUp(mContext, "");
    }

    public void switchSpeackPhone(boolean on) {
        UCSCall.setSpeakerphone(mContext, on);
    }

    public void switchMicMute(boolean on) {
        UCSCall.setMicMute(on);
    }

    public void sendDTMF(int dtmfCode) {
        UCSCall.sendDTMF(mContext, dtmfCode, null);
    }

    public void sendDTMF(DTMF dtmf) {
        UCSCall.sendDTMF(mContext, dtmf.getCode(), null);
    }

    public void startCallRinging() {
        // TODO:
        UCSCall.startCallRinging(mContext, null);
    }

    public void stopCallRinging() {
        UCSCall.stopCallRinging(mContext);
    }

    public void startRecording() {
        // TODO:
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
        Log.v("yzx", "onDialFailed");
    }

    @Override
    public void onIncomingCall(String s, String s1, String s2, String s3, String s4) {
        Log.v("yzx", "onIncomingCall");
    }

    @Override
    public void onHangUp(String s, UcsReason reason) {
        Log.v("yzx", "onHangUp");

    }

    /* 未调用 ？？？ */
    @Override
    public void onAlerting(String s) {
        Log.v("yzx", "onAlerting");
    }

    @Override
    public void onAnswer(String s) {
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
    }

    @Override
    public void onDTMF(int i) {
        Log.v("yzx", "onDTMF");
    }

    @Override
    public void onCameraCapture(String s) {
        Log.v("yzx", "onCameraCapture");
    }

    @Override
    public void singlePass(int i) {
        Log.v("yzx", "singlePass");
    }

    @Override
    public void onRemoteCameraMode(UCSCameraType type) {
        Log.v("yzx", "onRemoteCameraMode");

    }

    @Override
    public void onEncryptStream(byte[] bytes, byte[] bytes1, int i, int[] ints) {
        Log.v("yzx", "onEncryptStream");
    }

    @Override
    public void onDecryptStream(byte[] bytes, byte[] bytes1, int i, int[] ints) {
        Log.v("yzx", "onDecryptStream");

    }

    @Override
    public void initPlayout(int i, int i1, int i2) {
        Log.v("yzx", "initPlayout");

    }

    @Override
    public void initRecording(int i, int i1, int i2) {
        Log.v("yzx", "initRecording");

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
