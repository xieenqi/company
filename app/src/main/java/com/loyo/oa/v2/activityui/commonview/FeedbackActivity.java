package com.loyo.oa.v2.activityui.commonview;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.other.model.CellInfo;
import com.loyo.oa.v2.activityui.signin.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.beans.FeedBackCommit;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.point.IFeedback;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.CommonSubscriber;
import com.loyo.oa.v2.tool.ImageInfo;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 意见反馈
 */

@EActivity(R.layout.activity_feedback)
public class FeedbackActivity extends BaseActivity {

    @ViewById
    EditText et_content;
    @ViewById
    GridView gridView_photo;
    @ViewById
    ViewGroup layout_back;
    @ViewById
    ImageView iv_submit;
    @ViewById
    TextView tv_title;

    private String uuid = StringUtil.getUUID();
    private ArrayList<Attachment> attachments = new ArrayList<>();
    private SignInGridViewAdapter signInGridViewAdapter;
    Handler han = new Handler();
    private boolean isClick = false;//反馈成功过后是否点击确定了

    @AfterViews
    void init() {
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("意见反馈");
        Global.SetTouchView(layout_back, iv_submit);
        iv_submit.setVisibility(View.VISIBLE);
        init_gridView_photo();
    }

    /**
     * 获取附件
     */
    private void getAttachments() {

        AttachmentService.getAttachments(uuid)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<Attachment>>() {
                    @Override
                    public void onNext(ArrayList<Attachment> result) {
                        attachments = result;
                        init_gridView_photo();
                    }
                });
    }

    /**
     * 显示附件
     */
    private void init_gridView_photo() {
        signInGridViewAdapter = new SignInGridViewAdapter(this, attachments, true, true, true, 0);
        SignInGridViewAdapter.setAdapter(gridView_photo, signInGridViewAdapter);
    }

    @Click(R.id.layout_back)
    void click() {
        onBackPressed();
    }

    @Click(R.id.iv_submit)
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

            RestAdapterFactory.getInstance().build(FinalVariables.URL_FEEDBACK).create(IFeedback.class).create(map, new RCallback<FeedBackCommit>() {
                @Override
                public void success(final FeedBackCommit feedBackCommit, final Response response) {
                    HttpErrorCheck.checkResponse("意见反馈：", response);
                    showSuccessDialog();
                }

                @Override
                public void failure(final RetrofitError error) {
                    HttpErrorCheck.checkError(error);
                    Toast("提交失败");
                    super.failure(error);
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
            case MainApp.GET_IMG://上传附件
                try {
                    ArrayList<ImageInfo> pickPhots = (ArrayList<ImageInfo>) data.getSerializableExtra("data");
                    for (ImageInfo item : pickPhots) {
                        Uri uri = Uri.parse(item.path);
                        File newFile = Global.scal(this, uri);
                        if (newFile != null && newFile.length() > 0) {
                            if (newFile.exists()) {
                                Utils.uploadAttachment(uuid, 0, newFile).subscribe(new CommonSubscriber(this) {
                                    @Override
                                    public void onNext(final Serializable serializable) {
                                        getAttachments();
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception ex) {
                    Global.ProcException(ex);
                }
                break;
            case FinalVariables.REQUEST_DEAL_ATTACHMENT://删除附件
                try {
                    final Attachment delAttachment = (Attachment) data.getSerializableExtra("delAtm");
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("bizType", 0);
                    map.put("uuid", uuid);
                    AttachmentService.remove(String.valueOf(delAttachment.getId()), map)
                            .subscribe(new DefaultLoyoSubscriber<Attachment>() {
                                @Override
                                public void onNext(Attachment attachment) {
                                    Toast("删除附件成功!");
                                    attachments.remove(delAttachment);
                                    signInGridViewAdapter.notifyDataSetChanged();
                                }
                            });

                } catch (Exception e) {
                    Global.ProcException(e);
                }
                break;
            default:

                break;
        }
    }
}
