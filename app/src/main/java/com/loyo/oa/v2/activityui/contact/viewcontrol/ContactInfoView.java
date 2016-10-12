package com.loyo.oa.v2.activityui.contact.viewcontrol;

import android.os.Handler;

import com.loyo.oa.v2.common.BaseView;

/**
 * Created by yyy on 16/10/12.
 */

public interface ContactInfoView extends BaseView{

    /*编辑个人资料成功处理*/
    void updateProfileEmbl();

    /*设置生日*/
    void setBrithday(Handler mHandler,String birthStr);

    /*弹出框响应*/
    void leaveDialogEmbl();

}
