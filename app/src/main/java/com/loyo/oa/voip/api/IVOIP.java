package com.loyo.oa.voip.api;

import com.loyo.oa.voip.model.ResponseBase;
import com.loyo.oa.voip.model.VoipToken;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by EthanGong on 2016/11/2.
 */

public interface IVOIP {

    @GET("/payment/check")
    ResponseBase<String> getPaymentAccess();

    @GET("/payment/check")
    void asynGetPaymentAccess(Callback<ResponseBase<String>> callback);

    @GET("/ipvoice/call/client")
    ResponseBase<VoipToken> getVoipToken();

    @GET("/ipvoice/call/client")
    void asynGetVoipToken(Callback<ResponseBase<VoipToken>> callback);
}
