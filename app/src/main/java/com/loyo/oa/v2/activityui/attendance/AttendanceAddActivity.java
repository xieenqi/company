package com.loyo.oa.v2.activityui.attendance;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.hud.toast.LoyoToast;
import com.loyo.oa.photo.PhotoCapture;
import com.loyo.oa.photo.PhotoPicker;
import com.loyo.oa.photo.PhotoPreview;
import com.loyo.oa.upload.UploadController;
import com.loyo.oa.upload.UploadControllerCallback;
import com.loyo.oa.upload.UploadTask;
import com.loyo.oa.upload.view.ImageUploadGridView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.attendance.event.AttendanceAddEevent;
import com.loyo.oa.v2.activityui.attendance.model.AttendanceRecord;
import com.loyo.oa.v2.activityui.attendance.presenter.AttendanceAddPresenter;
import com.loyo.oa.v2.activityui.attendance.presenter.impl.AttendanceAddPresenterImpl;
import com.loyo.oa.v2.activityui.attendance.viewcontrol.AttendanceAddView;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.UMengTools;
import com.loyo.oa.v2.tool.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import hk.ids.gws.android.sclick.SClick;

import static com.loyo.oa.v2.R.id.image_upload_grid_view;
/**
 * 【新增考勤】
 * Restruture by yyy on 16/10/11
 */
@EActivity(R.layout.activity_attendance_add)
public class AttendanceAddActivity extends BaseActivity implements LocationUtilGD.AfterLocation, AttendanceAddView, UploadControllerCallback {

    @ViewById
    ViewGroup img_title_left;
    @ViewById
    ViewGroup img_title_right;
    @ViewById
    TextView tv_title_1;
    @ViewById
    TextView tv_time_kind;//打卡时间 加班时间 种类
    @ViewById
    TextView tv_time; //打卡时间 加班时间 时间
    @ViewById
    TextView tv_count_time;
    @ViewById
    TextView tv_count_time2;
    @ViewById
    TextView tv_address;
    @ViewById
    TextView tv_result;
    @ViewById
    ImageView iv_refresh_address;
    @ViewById
    EditText et_reason;
    @ViewById
    ViewGroup layout_reason;
    @ViewById(image_upload_grid_view)
    ImageUploadGridView gridView;
    @Extra
    AttendanceRecord mAttendanceRecord;
    @Extra("isPopup")
    boolean isPopup;
    @Extra("needPhoto")
    boolean NeedPhoto;
    @Extra("outKind")
    int outKind; //0上班 1正常下班 2完成加班
    @Extra("serverTime")
    long serverTime;//当前时间
    @Extra("extraWorkStartTime")
    long extraWorkStartTime;//加班开始时间
    @Extra("lateMin")
    int lateMin;
    @Extra("earlyMin")
    int earlyMin;


    private AttendanceAddPresenter mPresenter;
    private ArrayList<Attachment> attachments = new ArrayList<>();
    private String uuid = StringUtil.getUUID();
    private static String tvTimeName;
    private int state;
    private Animation animation;

    UploadController controller;

    public static final int CLOCKIN_STATE_NO = 1; //上班打卡状态
    public static final int CLOCKIN_STATE_OFF = 1; //下班打卡状态
    public static final int CLOCKIN_STATE_OVERTIME = 5; //加班班打卡状态


    @Override
    public void OnLocationGDSucessed(final String address, final double longitude, final double latitude, final String radius) {
        iv_refresh_address.clearAnimation();
        animation.reset();
        tv_address.setText(address);
        mPresenter.refreshLocation(longitude, latitude, tv_address.getText().toString());
        LocationUtilGD.sotpLocation();
        UMengTools.sendLocationInfo(address, longitude, latitude);
    }

    @Override
    public void OnLocationGDFailed() {
        Toast(R.string.LOCATION_FAILED);
        iv_refresh_address.clearAnimation();
        animation.reset();
        LocationUtilGD.sotpLocation();
    }

    @AfterViews
    void initViews() {
        switch (outKind) {
            case 0:
                state = CLOCKIN_STATE_NO;
                tvTimeName = "打卡时间:";
                tv_title_1.setText("上班打卡");
                break;
            case 1:
                state = CLOCKIN_STATE_OFF;
                tvTimeName = "打卡时间:";
                tv_title_1.setText("下班打卡");
                break;
            case 2:
                state = CLOCKIN_STATE_OVERTIME;
                tvTimeName = "加班时间:";
                tv_title_1.setText("完成加班");
                break;
            default:
                break;
        }

        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        iv_refresh_address.setOnTouchListener(Global.GetTouch());
        animation = AnimationUtils.loadAnimation(this, R.anim.rotateanimation);
        mPresenter = new AttendanceAddPresenterImpl(mAttendanceRecord, mContext, this, AttendanceAddActivity.this);
        mPresenter.mHndler(tv_count_time, tv_count_time2, tvTimeName);
        initLogicData();

        controller = new UploadController(this, 3);
        controller.setObserver(this);
        controller.loadView(gridView);
    }

    @Override
    public void refreshLocation() {
        tv_result.setVisibility(mAttendanceRecord.getOutstate() == AttendanceRecord.OUT_STATE_OFFICE_WORK ? View.GONE : View.VISIBLE);
        String result = mAttendanceRecord.getOutstate() != AttendanceRecord.OUT_STATE_OFFICE_WORK ? "您已超出内勤范围,本次打卡将记作外勤!" : "";
        tv_result.setText(result);
        tv_address.setText(mAttendanceRecord.getAddress());
    }

    /**
     * 初始化业务数据
     */
    private void initLogicData() {
        if (null == mAttendanceRecord) {
            return;
        }

        tv_result.setVisibility(mAttendanceRecord.getOutstate() == AttendanceRecord.OUT_STATE_OFFICE_WORK ? View.GONE : View.VISIBLE);
        String result = mAttendanceRecord.getOutstate() != AttendanceRecord.OUT_STATE_OFFICE_WORK ? "您已超出内勤范围,本次打卡将记作外勤!" : "";
        tv_result.setText(result);
        tv_address.setText(mAttendanceRecord.getAddress());
        /*完成加班*/
        if (outKind == 2) {
            et_reason.setHint("请输入加班原因");
//            String time = (DateTool.timet(extraWorkStartTime + "", DateTool.DATE_FORMATE_TRANSACTION)
//                    + "-" + DateTool.timet(serverTime + "", DateTool.DATE_FORMATE_TRANSACTION));
            String time = com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(extraWorkStartTime) + "-" + com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(serverTime);
            SpannableStringBuilder builder = Utils.modifyTextColor(time, getResources().getColor(R.color.green51), 5, time.length());
            tv_time_kind.setText(tvTimeName);
            tv_time.setText(builder);
            tv_time.setTextColor(getResources().getColor(R.color.green51));
        }
        /*正常上下班*/
        else {
//            String time = tvTimeName.concat(app.df6.format(new Date(mAttendanceRecord.getCreatetime() * 1000)));
            String time = tvTimeName.concat(com.loyo.oa.common.utils.DateTool.getHourMinute(mAttendanceRecord.getCreatetime()));
            SpannableStringBuilder builder = Utils.modifyTextColor(time, getResources().getColor(R.color.green51), 5, time.length());
            tv_time.setText(builder);
            if (mAttendanceRecord.getState() == AttendanceRecord.STATE_BE_LATE || mAttendanceRecord.getState() == AttendanceRecord.STATE_LEAVE_EARLY) {
                if (mAttendanceRecord.getState() == AttendanceRecord.STATE_BE_LATE) {
                    et_reason.setHint("请输入迟到原因");
                    state = 2;
                } else {
                    et_reason.setHint("请输入早退原因");
                    state = 3;
                }
            }
        }
        //init_gridView_photo();
        mPresenter.countDown();
    }

    @Click({R.id.img_title_left, R.id.img_title_right, R.id.iv_refresh_address})
    void onClick(final View v) {
        switch (v.getId()) {
            /*返回*/
            case R.id.img_title_left:
                onBackPressed();
                break;

            /*提交*/
            case R.id.img_title_right:

                if (!mPresenter.checkAttendanceData(et_reason.getText().toString(),
                        tv_address.getText().toString(),
                        outKind, mAttendanceRecord.getState())) {
                    return;
                }

                if (NeedPhoto && controller.count() == 0) {
                    Toast("需要考勤照片，请拍照");
                    return;
                }

                /*暂时取消外勤判断 */
                /*if (mAttendanceRecord.getOutstate() != AttendanceRecord.OUT_STATE_OFFICE_WORK
                        && mAttendanceRecord.getState() != 5) {
                    showOutAttendanceDialog();
                } else {
                    commitAttendance();
                }*/

                img_title_right.setEnabled(false);
                showCommitLoading();
                controller.startUpload();
                controller.notifyCompletionIfNeeded();
                break;

            /*刷新地址*/
            case R.id.iv_refresh_address:
                iv_refresh_address.startAnimation(animation);
                new LocationUtilGD(AttendanceAddActivity.this, AttendanceAddActivity.this);
                break;
        }
    }

    /**
     * 弹出外勤确认对话框
     */
    private void showOutAttendanceDialog() {
        sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissSweetAlert();
            }
        }, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissSweetAlert();
                showCommitLoading();
                controller.startUpload();
            }
        }, "提示", getString(R.string.app_attendance_out_message));
    }

    /**
     * 提交考勤
     */
    private void commitAttendance() {
        mPresenter.commitAttendance(attachments, isPopup,
                outKind, state, uuid, tv_address.getText().toString(),
                et_reason.getText().toString(), extraWorkStartTime,
                serverTime, lateMin, earlyMin);
    }

    /**
     * 附件删除回调
     */
    @OnActivityResult(PhotoPreview.REQUEST_CODE)
    void onDealImageResult(final Intent data) {
        if (data != null) {
            int index = data.getExtras().getInt(PhotoPreview.KEY_DELETE_INDEX);
            if (index >= 0) {
                controller.removeTaskAt(index);
                controller.reloadGridView();
            }
        }
    }

    /**
     * 选择附件回调
     */
    //@OnActivityResult(MainApp.GET_IMG)
    @OnActivityResult(PhotoCapture.REQUEST_CODE)
    void onGetImageResult(final Intent data) {
        if (null == data) {
            return;
        }
        List<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
        for (String path : photos) {
            controller.addUploadTask("file://" + path, null, uuid);
        }
        controller.reloadGridView();
        //TODO:
        // mPresenter.uploadAttachments(uuid, (ArrayList<ImageInfo>) data.getSerializableExtra("data"));
    }

    /**
     * 获取附件成功处理
     */
    @Override
    public void setAttachmentEmbl(ArrayList<Attachment> mAttachment) {
        //attachments = mAttachment;
        //init_gridView_photo();
    }

    /**
     * 删除附件成功处理
     */
    @Override
    public void deleteAttaSuccessEmbl(Attachment mDelAttachment) {
        //attachments.remove(mDelAttachment);
        //init_gridView_photo();
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
            attachmentBatch.bizType = 0/*考勤*/;
            attachmentBatch.mime = Utils.getMimeType(task.getValidatePath());
            attachmentBatch.name = task.getKey();
            attachmentBatch.size = Integer.parseInt(task.size + "");
            attachment.add(attachmentBatch);
        }
        AttachmentService.setAttachementData2(attachment)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<Attachment>>(hud, true) {
                    @Override
                    public void onNext(ArrayList<Attachment> news) {
                        AttendanceAddActivity.this.attachments = news;
                        commitAttendance();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        img_title_right.setEnabled(true);
                    }
                });
    }

    /**
     * 弹窗提示
     */
    @Override
    public void showMsg(String message) {
        LoyoToast.info(this, message);
    }

    /**
     * 打卡成功
     */
    @Override
    public void attendanceSuccess() {
        AppBus.getInstance().post(new AttendanceAddEevent());
        onBackPressed();
    }

    /**
     * 打卡失败
     */
    @Override
    public void attendanceError() {
        img_title_right.setEnabled(true);
    }

    /**
     * 显示打卡超时对话框
     */
    @Override
    public void showTimeOutDialog() {
        sweetAlertDialogView.alertIconClick(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                finish();
            }
        }, getString(R.string.app_attendance_outtime_message), null);
    }

    /**
     * 关闭键盘
     */
    @Override
    public void finish() {
        mPresenter.recycle();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(findViewById(R.id.tv_address).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        super.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.recycle();
    }

    @Override
    public LoyoProgressHUD showStatusProgress() {
        showCommitLoading();
        return hud;
    }

    @Override
    public LoyoProgressHUD showProgress(String msg) {
        showLoading2(msg);
        return hud;
    }

    @Override
    public void hideProgress() {
        cancelCommitLoading();
    }

    @Override
    public void onRetryEvent(UploadController controller, UploadTask task) {
        controller.retry();
    }

    @Override
    public void onAddEvent(UploadController controller) {
        PhotoCapture.builder()
                .start(this);
    }

    @Override
    public void onItemSelected(UploadController controller, int index) {
        ArrayList<UploadTask> taskList = controller.getTaskList();
        ArrayList<String> selectedPhotos = new ArrayList<>();

        for (int i = 0; i < taskList.size(); i++) {
            String path = taskList.get(i).getValidatePath();
            if (path.startsWith("file://")) ;
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
            img_title_right.setEnabled(true);
            cancelCommitLoading();
            LoyoToast.info(this, count + "个附件上传失败，请重试或者删除");
            return;
        }
        if (taskList.size() > 0) {
            postAttaData();
        } else {
            commitAttendance();
        }
    }
}
