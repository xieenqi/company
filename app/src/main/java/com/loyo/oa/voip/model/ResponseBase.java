package com.loyo.oa.voip.model;

/**
 * Created by EthanGong on 2016/11/2.
 */

public class ResponseBase<T>{
    public int errcode;
    public String errmsg;
    public T data;
}
