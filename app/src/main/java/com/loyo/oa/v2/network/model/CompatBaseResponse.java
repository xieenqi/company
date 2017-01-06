package com.loyo.oa.v2.network.model;

/**
 * Created by EthanGong on 2017/1/6.
 */


/**
 *  当网络接口返回正常结果是无
 *  {
 *      errcode:
 *      errmsg:
 *      data: { 「model」}
 *  }
 *  样式包裹，且返回错误是有
 *  {
 *      errcode:
 *      errmsg:
 *  }
 *  样式时，Model类可以选择继承此类，由网络层处理错误
 *
 */

public class CompatBaseResponse {
    public int errcode;
    public String errmsg;
}
