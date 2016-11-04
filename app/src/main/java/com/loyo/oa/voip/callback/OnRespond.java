package com.loyo.oa.voip.callback;

/**
 * Created by EthanGong on 2016/11/2.
 */

public interface OnRespond {
    void onPaymentDeny();
    void onNetworkError();
    void onRespond(Object userInfo);
}
