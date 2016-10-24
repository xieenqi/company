package com.loyo.oa.v2.activityui.worksheet.viewcontrol;

import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetTemplate;
import com.loyo.oa.v2.common.BaseView;
import com.loyo.oa.v2.customview.SweetAlertDialogView;

import java.util.ArrayList;

/**
 * Created by yyy on 16/10/22.
 */

public interface OrderWorksheetAddView extends BaseView{

    /*选择工单类型处理*/
    void getWorkSheetTypeEmbl(int index,String value,SweetAlertDialogView sweetAlertDialogView,ArrayList<WorksheetTemplate> types);

    /*上传附件成功处理*/
    void setUploadAttachmentEmbl(int size);
}
