package com.loyo.oa.v2.activityui.other;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.loyo.oa.contactpicker.ContactPickerActivity;
import com.loyo.oa.contactpicker.model.event.ContactPickedEvent;
import com.loyo.oa.contactpicker.model.result.StaffMemberCollection;
import com.loyo.oa.photo.PhotoPicker;
import com.loyo.oa.photo.PhotoPreview;
import com.loyo.oa.upload.UploadController;
import com.loyo.oa.upload.UploadControllerCallback;
import com.loyo.oa.upload.UploadTask;
import com.loyo.oa.upload.view.ImageUploadGridView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.presenter.BulletinAddPresenter;
import com.loyo.oa.v2.activityui.other.presenter.Impl.BulletinAddPresenterImpl;
import com.loyo.oa.v2.activityui.other.viewcontrol.BulletinAddView;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.compat.Compat;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.ImageInfo;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * 【发布通知】
 * Restructure by yyy on 2016/10/9
 */

@EActivity(R.layout.activity_bulletin_add)
public class BulletinAddActivity extends BaseActivity implements BulletinAddView ,UploadControllerCallback {

    @ViewById
    EditText edt_title;
    @ViewById
    EditText edt_content;
    @ViewById
    ImageUploadGridView photoGridView;
    @ViewById
    ViewGroup layout_recevier;
    @ViewById
    TextView tv_recevier;

    private Context mContext;
    private String uuid = StringUtil.getUUID();
    private ArrayList<ImageInfo> pickPhots = new ArrayList<>();
    private BulletinAddPresenter mBulletinAddPresenter;

    private ArrayList<AttachmentBatch> attachment = new ArrayList<>();
    ArrayList<AttachmentForNew> attachmentForNew;

    UploadController controller;
    private int bizType = 0;

    @AfterViews
    void init() {
        super.setTitle("发布通知");
        mContext = this;
        controller = new UploadController(this, 9);
        controller.setObserver(this);

        controller.loadView(photoGridView);
        mBulletinAddPresenter = new BulletinAddPresenterImpl(this, mContext);
    }

    /**
     * 通知谁看
     */
    @Click(R.id.layout_recevier)
    void receiverClick() {
        StaffMemberCollection collection =
                Compat.convertMembersToStaffCollection(mBulletinAddPresenter.getMembers());
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContactPickerActivity.SINGLE_SELECTION_KEY, false);
        if (collection != null) {
            bundle.putSerializable(ContactPickerActivity.STAFF_COLLECTION_KEY, collection);
        }
        Intent intent = new Intent();
        intent.setClass(BulletinAddActivity.this, ContactPickerActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
    }

    /**
     * 返回
     * */
    @Click(R.id.img_title_left)
    void close() {
        onBackPressed();
    }

    /**
     * 提交
     * */
    @Click(R.id.img_title_right)
    void submit() {
        mBulletinAddPresenter.verifyText(edt_title.getText().toString().trim(),
                                         edt_content.getText().toString().trim());
    }

    /**
     * 相册选择 回调
     */
    @OnActivityResult(PhotoPicker.REQUEST_CODE)
    void onPhotoResult(final Intent data) {
        /*相册选择 回调*/
        if (data != null) {
            List<String> mSelectPath = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            for (String path : mSelectPath) {
                controller.addUploadTask("file://" + path, null, uuid);
            }
            controller.reloadGridView();
        }
    }

    /**
     * 相册删除 回调
     */
    @OnActivityResult(PhotoPreview.REQUEST_CODE)
    void onDeletePhotoResult(final Intent data) {
        if (data != null){
            int index = data.getExtras().getInt(PhotoPreview.KEY_DELETE_INDEX);
            if (index >= 0) {
                controller.removeTaskAt(index);
                controller.reloadGridView();
            }
        }
    }

    /**
     * 人员选择 回调
     * */

    /**
     * 选人回调
     */
    @Subscribe
    public void onContactPicked(ContactPickedEvent event) {
        StaffMemberCollection collection = event.data;
        Members members = Compat.convertStaffCollectionToMembers(collection);
        if (members == null) {
            return;
        }
        mBulletinAddPresenter.dealDepartmentResult(members);
    }

    @OnActivityResult(FinalVariables.REQUEST_ALL_SELECT)
    void onDepartmentUserResult(final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        mBulletinAddPresenter.dealDepartmentResult((Members) data.getSerializableExtra("data"));
    }

    /**
     * 格式验证
     * */
    @Override
    public void verifyError(int code) {
        switch (code) {
            case 1:
                Toast("标题不能为空");
                break;

            case 2:
                Toast("内容不能为空");
                break;

            case 3:
                Toast("通知人员不能为空");
                break;
        }
    }

    /**
     * 格式验证通过
     * */
    @Override
    public void verifySuccess(String title,String content) {
        showStatusLoading(false);
        controller.startUpload();
        controller.notifyCompletionIfNeeded();
    }

    /**
     * 提交成功
     * */
    @Override
    public void onSuccess(Bulletin mBulletin) {
        Intent intent = new Intent();
        intent.putExtra("data", mBulletin);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 提交失败
     * */
    @Override
    public void onError() {
        Toast("提交失败");
    }

    /**
     * 打开Loading
     * */
    @Override
    public void showLoading() {
        showLoading("正在提交");
    }

    /**
     * 设置人员名字
     * */
    @Override
    public void setReceiver(String name) {
        tv_recevier.setText(name);
    }

    /**
     * 关闭弹出框
     * */
    @Override
    public void dissweetAlert() {
        dismissSweetAlert();
    }

    private void buildAttachment() {
        ArrayList<UploadTask> list = controller.getTaskList();
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
        }
    }

    private void commitAttachment() {
        showStatusLoading(false);
        buildAttachment();
        AttachmentService.setAttachementData(attachment)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<AttachmentForNew>>() {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        cancelStatusLoading();
                    }

                    @Override
                    public void onNext(final ArrayList<AttachmentForNew> news) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                cancelStatusLoading();
                                BulletinAddActivity.this.attachmentForNew = news;
                                commitAnnouncement();
                            }
                        },1000);
                    }
                });
    }

    private void commitAnnouncement() {
        mBulletinAddPresenter.requestBulletinAdd(
                edt_title.getText().toString().trim(),
                edt_content.getText().toString().trim(),
                uuid,
                attachmentForNew);
    }

    @Override
    public void onRetryEvent(UploadController controller, UploadTask task) {
        controller.retry();
    }

    @Override
    public void onAddEvent(UploadController controller) {
        PhotoPicker.builder()
                .setPhotoCount(9 - controller.count())
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
        cancelStatusLoading();
        int count = controller.failedTaskCount();
        if (count > 0) {
            Toast(count + "个附件上传失败，请重试或者删除");
            return;
        }
        if (taskList.size() > 0) {
            commitAttachment();
        } else {
            commitAnnouncement();
        }
    }
}