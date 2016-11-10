package com.loyo.oa.v2.activityui.worksheet.viewcontrol;

/**
 * Created by yyy on 16/10/24.
 */

public interface OrderWorksheetListView {

    /*删除工单*/
    void deleteWorkSheet(int position);

    /*编辑工单*/
    void editWorkSheet(int position);

    /*查看附件*/
    void intentAttachment(int position);

}
