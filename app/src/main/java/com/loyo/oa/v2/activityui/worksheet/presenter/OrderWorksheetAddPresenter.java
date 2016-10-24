package com.loyo.oa.v2.activityui.worksheet.presenter;

import com.loyo.oa.upload.UploadController;
import com.loyo.oa.v2.customview.SweetAlertDialogView;

import java.util.List;

/**
 * Created by loyo_dev1 on 16/10/22.
 */

public interface OrderWorksheetAddPresenter {

    /*获取工单类型*/
    void getWorkSheetType(SweetAlertDialogView sweetAlertDialogView);

    /*附件删除刷新*/
    void removeAttachmentAt(int index,UploadController controller);

    /*附件添加*/
    void addPhoto(List<String> photos,UploadController controller,String uuid);

    /*上传附件*/
    void uploadAttachmentAt(UploadController controller,String uuid,int bizType);

}
