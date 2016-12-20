package com.loyo.oa.v2.activityui.worksheet.presenter.impl;

import android.content.Context;

import com.loyo.oa.upload.UploadController;
import com.loyo.oa.upload.UploadTask;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetTemplate;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetConfig;
import com.loyo.oa.v2.activityui.worksheet.presenter.OrderWorksheetAddPresenter;
import com.loyo.oa.v2.activityui.worksheet.viewcontrol.OrderWorksheetAddView;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.customview.PaymentPopView;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yyy on 16/10/22.
 */

public class OrderWorksheetAddPresenterImpl implements OrderWorksheetAddPresenter{

    private Context mContext;
    private OrderWorksheetAddView crolView;
    private ArrayList<AttachmentBatch> attachment = new ArrayList<>();
    private List<String> mFilePath;

    public OrderWorksheetAddPresenterImpl(Context mContext,OrderWorksheetAddView crolView){
        this.mContext = mContext;
        this.crolView = crolView;
    }


    /**
     * 获取工单类型
     * */
    @Override
    public void getWorkSheetType(final SweetAlertDialogView sweetAlertDialogView) {
        final ArrayList<WorksheetTemplate> types = WorksheetConfig.getWorksheetTypes(true);
        if (types == null || types.size() == 0) {
            sweetAlertDialogView.alertIcon("无可选工单类型",null);
            return;
        }

        String[] list = new String[types.size()];
        for(int i = 0; i < types.size(); i++) {
            list[i] = types.get(i).name;
        }

        final PaymentPopView popViewKind = new PaymentPopView(mContext, list, "选择工单类型");
        popViewKind.show();
        popViewKind.setCanceledOnTouchOutside(true);
        popViewKind.setCallback(new PaymentPopView.VaiueCallback() {
            @Override
            public void setValue(String value, int index) {
                crolView.getWorkSheetTypeEmbl(index,value,sweetAlertDialogView,types);
            }
        });
    }

    /**
     * 附件删除刷新
     * */
    @Override
    public void removeAttachmentAt(int index, UploadController controller) {
        controller.removeTaskAt(index);
        controller.reloadGridView();
    }

    /**
     * 附件添加
     * */
    @Override
    public void addPhoto(List<String> photos,UploadController controller,String uuid) {
        for (String path : photos) {
            controller.addUploadTask("file://" + path, null, uuid);
        }
        controller.reloadGridView();
    }

    @Override
    public void uploadAttachmentAt(UploadController controller,String uuid,int bizType) {
        buildAttachment(controller,uuid,bizType);
//        IAttachment service = RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT())
//                .create(IAttachment.class);
//        service.setAttachementData(attachment, new Callback<ArrayList<AttachmentForNew>>() {
//            @Override
//            public void success(ArrayList<AttachmentForNew> attachmentForNew, Response response) {
//                HttpErrorCheck.checkResponse("上传附件信息", response);
//                crolView.setUploadAttachmentEmbl(attachmentForNew.size(),mFilePath);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkError(error);
//            }
//        });

        AttachmentService.setAttachementData(attachment)
                .subscribe(new DefaultLoyoSubscriber<AttachmentForNew>() {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        crolView.hideProgress();
                    }

                    @Override
                    public void onNext(AttachmentForNew aNew) {
                        crolView.setUploadAttachmentEmbl(1,mFilePath);
                    }
                });

        crolView.showProgress("");
}

    private void buildAttachment(UploadController controller,String uuid,int bizType) {
        ArrayList<UploadTask> list = controller.getTaskList();
        mFilePath = new ArrayList<>();
        attachment = new ArrayList<AttachmentBatch>();
        for (int i = 0; i < list.size(); i++) {
            UploadTask task = list.get(i);
            AttachmentBatch attachmentBatch = new AttachmentBatch();
            attachmentBatch.UUId = uuid;
            attachmentBatch.bizType = bizType;
            attachmentBatch.mime = Utils.getMimeType(task.getValidatePath());
            attachmentBatch.name = task.getKey();
            attachmentBatch.size = Integer.parseInt(task.size + "");
            attachment.add(attachmentBatch);
            mFilePath.add(task.getValidatePath());
        }
    }
}
