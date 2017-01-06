package com.loyo.oa.v2.activityui.commonview;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.hud.toast.LoyoToast;
import com.loyo.oa.photo.PhotoPicker;
import com.loyo.oa.photo.PhotoPreview;
import com.loyo.oa.upload.UploadController;
import com.loyo.oa.upload.UploadControllerCallback;
import com.loyo.oa.upload.UploadTask;
import com.loyo.oa.upload.view.ImageUploadGridView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.commonview.api.FeedBackService;
import com.loyo.oa.v2.activityui.other.model.CellInfo;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.FeedBackCommit;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 意见反馈
 */

@EActivity(R.layout.activity_feedback)
public class FeedbackActivity extends BaseActivity implements UploadControllerCallback {

    @ViewById
    EditText et_content;
    @ViewById(R.id.image_upload_grid_view)
    ImageUploadGridView gridView;
    @ViewById
    ViewGroup layout_back;
    @ViewById
    ImageView iv_submit;
    @ViewById
    TextView tv_title;

    UploadController controller;

    private String uuid = StringUtil.getUUID();
    private ArrayList<Attachment> attachments = new ArrayList<>();
    Handler han = new Handler();
    private boolean isClick = false;//反馈成功过后是否点击确定了

    @AfterViews
    void init() {
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("意见反馈");
        Global.SetTouchView(layout_back, iv_submit);
        iv_submit.setVisibility(View.VISIBLE);
        controller = new UploadController(this, 9);
        controller.setObserver(this);
        controller.loadView(gridView);
    }

    @Click(R.id.layout_back)
    void click() {
        onBackPressed();
    }

    @Click(R.id.iv_submit)
    void commit() {
        showCommitLoading();
        if (controller.count() > 0) {
            controller.startUpload();
            controller.notifyCompletionIfNeeded();
        }
        else {
            sendFeedback();
        }
    }

    void sendFeedback() {
        String comment = et_content.getText().toString();
        if (StringUtil.isEmpty(comment)) {
            Toast("请输入内容");
            return;
        }
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);

            CellInfo cellInfo = Utils.getCellInfo();
            String androidInfo = "Android" + "品牌：" + cellInfo.getLoyoAgent() + "<->" + "版本：" + cellInfo.getLoyoOSVersion() +
                    "<->" + "设备硬件版本:" + cellInfo.getLoyoHVersion() + "<->" + "app版本:" + pi.versionName + "<->";
            HashMap<String, Object> map = new HashMap<>();
            map.put("attachmentUUid", uuid);
            map.put("content", comment);
            map.put("operationSystem", androidInfo);
            map.put("userAgent", android.os.Build.MODEL);
            FeedBackService.create(map).subscribe(new DefaultLoyoSubscriber<FeedBackCommit>(hud) {
                @Override
                public void onNext(FeedBackCommit feedBackCommit) {
                    showSuccessDialog();
                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            Global.ProcException(e);
        }
    }

    /**
     * 显示发送反馈成功的对话框
     */
    private void showSuccessDialog() {
        hideInputKeyboard(et_content);

        String message = "感谢您反馈的宝贵意见\n我们一定认真对待\n努力优化快启的产品与服务\n祝您生活愉快!!!";

        sweetAlertDialogView.alertMessageClick(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissSweetAlert();
                onBackPressed();
                isClick = true;
            }
        }, "提示", message);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                han.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isClick)
                            onBackPressed();
                    }
                });
            }
        }, 3000);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            /*相册选择 回调*/
            case PhotoPicker.REQUEST_CODE:
                /*相册选择 回调*/
                if (data != null) {
                    List<String> mSelectPath = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    for (String path : mSelectPath) {
                        controller.addUploadTask("file://" + path, null, uuid);
                    }
                    controller.reloadGridView();
                }
                break;

            /*附件删除回调*/
            case PhotoPreview.REQUEST_CODE:
                if (data != null) {
                    int index = data.getExtras().getInt(PhotoPreview.KEY_DELETE_INDEX);
                    if (index >= 0) {
                        controller.removeTaskAt(index);
                        controller.reloadGridView();
                    }
                }
                break;
            default:

                break;
        }
    }

    /**
     * 上传附件信息
     */
    public void postAttaData() {
        ArrayList<UploadTask> list = controller.getTaskList();
        ArrayList<AttachmentBatch> attachment = new ArrayList<AttachmentBatch>();
        for (int i = 0; i < list.size(); i++) {
            UploadTask task = list.get(i);
            AttachmentBatch attachmentBatch = new AttachmentBatch();
            attachmentBatch.UUId = uuid;
            attachmentBatch.bizType = 0;
            attachmentBatch.mime = Utils.getMimeType(task.getValidatePath());
            attachmentBatch.name = task.getKey();
            attachmentBatch.size = Integer.parseInt(task.size + "");
            attachment.add(attachmentBatch);
        }
        AttachmentService.setAttachementData2(attachment)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<Attachment>>(hud, true) {
                    @Override
                    public void onNext(ArrayList<Attachment> news) {
                        sendFeedback();
                    }
                });
    }

    @Override
    public void onRetryEvent(UploadController controller, UploadTask task) {
        controller.retry();
    }

    @Override
    public void onAddEvent(UploadController controller) {
        PhotoPicker.builder()
                .setPhotoCount(9-controller.count())
                .setShowCamera(true)
                .setPreviewEnabled(false)
                .start(this);
    }

    @Override
    public void onItemSelected(UploadController controller, int index) {
        ArrayList<UploadTask> taskList = controller.getTaskList();
        ArrayList<String> selectedPhotos = new ArrayList<>();

        for (int i = 0; i < taskList.size(); i++) {
            String path = taskList.get(i).getValidatePath();
            if (path.startsWith("file://"));
            {
                path = path.replace("file://", "");
            }
            selectedPhotos.add(path);
        }
        PhotoPreview.builder()
                .setPhotos(selectedPhotos)
                .setCurrentItem(index)
                .setShowDeleteButton(true)
                .start(this);
    }

    @Override
    public void onAllUploadTasksComplete(UploadController controller, ArrayList<UploadTask> taskList) {

        int count = controller.failedTaskCount();
        if (count > 0) {
            cancelCommitLoading();
            LoyoToast.info(this, count + "个附件上传失败，请重试或者删除");
            return;
        }
        if (taskList.size() > 0) {
            postAttaData();
        } else {
            sendFeedback();
        }
    }
}
