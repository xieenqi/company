package com.loyo.oa.voip.api;

import com.loyo.oa.voip.model.RequestAccess;
import com.loyo.oa.voip.model.ResponseBase;
import com.loyo.oa.voip.model.VoIPToken;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by EthanGong on 2016/11/2.
 */

public interface IVoIP {

    @POST("/ipvoice/direct/request")
    ResponseBase<RequestAccess> getRequestAccess(@Body HashMap<String ,String> params);

    @POST("/ipvoice/direct/request")
    void asybGetRequestAccess(@Body HashMap<String ,String> params, Callback<ResponseBase<RequestAccess>> callback);

    @GET("/payment/check")
    ResponseBase<String> getPaymentAccess();

    @GET("/payment/check")
    void asynGetPaymentAccess(Callback<ResponseBase<String>> callback);

    @GET("/ipvoice/call/client")
    ResponseBase<VoIPToken> getVoipToken();

    @GET("/ipvoice/call/client")
    void asynGetVoipToken(Callback<ResponseBase<VoIPToken>> callback);
}
