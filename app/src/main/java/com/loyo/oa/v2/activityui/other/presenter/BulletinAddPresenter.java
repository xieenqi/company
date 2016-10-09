package com.loyo.oa.v2.activityui.other.presenter;

import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by loyo_dev1 on 16/10/9.
 */

public interface BulletinAddPresenter {

    /*格式验证*/
    void verifyText(String title,String content,Members membe);

    /*提交通知公告*/
    void requestBulletinAdd(HashMap<String,Object> map);

    /*上传附件*/
    void uploadAttachement(SweetAlertDialogView sweetAlertDialogView,ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots,HashMap<String,Object> map,String uuid);

}
