package com.loyo.oa.v2.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.WXUtil;
import com.loyo.oa.v2.tool.customview.CustomProgressDialog;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * com.loyo.oa.v2.wxapi
 * 描述 :微信交互页面
 * 作者 : ykb
 * 时间 : 15/7/31.
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler
{
    private CustomProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dialog=new CustomProgressDialog(this);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        dialog.show();
    }
    /**
     * 注册微信回调
     * @param intent 信使
     */
    private void handleIntent(Intent intent)
    {
        WXUtil.getInstance().getIwxapi().handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {}

    @Override
    public void onResp(BaseResp resp) {

        String result = "";
        switch(resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result ="授权成功";
                WXUtil.wxCode=((SendAuth.Resp)resp).code;
                sendWXBroad();
                Log.e(getClass().getSimpleName(), "onResp,success,wxCode : " + WXUtil.wxCode);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "授权取消";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "授权被拒绝";
                break;
            default:
                result = "授权失败";
                break;
        }
        Toast(result);
        finish();
    }

    /**
     * 发送获得微信授权码的广播
     */
    private void sendWXBroad(){
        Intent intent=new Intent(WXUtil.ACTION_WX_CODE_RETURN);
        LocalBroadcastManager.getInstance(this).sendBroadcastSync(intent);
    }

}