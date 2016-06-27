package com.loyo.oa.v2.ui.activity.commonview;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.ui.activity.signin.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.FeedBackCommit;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.IFeedback;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.CommonSubscriber;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
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

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 意见反馈
 */

@EActivity(R.layout.activity_feedback)
public class FeedbackActivity extends BaseActivity {

    @ViewById EditText et_content;
    @ViewById GridView gridView_photo;
    @ViewById ViewGroup layout_back;
    @ViewById ImageView iv_submit;
    @ViewById TextView tv_title;

    private String uuid = StringUtil.getUUID();
    private ArrayList<Attachment> attachments = new ArrayList<>();
    private SignInGridViewAdapter signInGridViewAdapter;
    Handler han = new Handler();

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
        Utils.getAttachments(uuid, new RCallback<ArrayList<Attachment>>() {
            @Override
            public void success(final ArrayList<Attachment> _attachments, final Response response) {
                HttpErrorCheck.checkResponse(response);
                attachments = _attachments;
                init_gridView_photo();
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
                Toast("获取附件失败");
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

        HashMap<String, Object> map = new HashMap<>();
        map.put("attachmentUUid", uuid);
        map.put("content", comment);

        RestAdapterFactory.getInstance().build(FinalVariables.URL_FEEDBACK).create(IFeedback.class).create(map, new RCallback<FeedBackCommit>() {
            @Override
            public void success(final FeedBackCommit feedBackCommit, final Response response) {

                showSuccessDialog();
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                Toast("提交失败");
                super.failure(error);
            }
        });
    }

    /**
     * 显示发送反馈成功的对话框
     */
    private void showSuccessDialog() {
        hideInputKeyboard(et_content);

        String message = "感谢您反馈的宝贵意见\n我们一定认真对待\n努力优化快启的产品与服务\n祝您生活愉快";
        showGeneralDialog(false, false, message);
        generalPopView.setNoCancelOnclick(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                finish();
            }
        });

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                han.post(new Runnable() {
                    @Override
                    public void run() {
                        finish();
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
            case SelectPicPopupWindow.GET_IMG://上传附件
                try {
                    ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots = (ArrayList<SelectPicPopupWindow.ImageInfo>) data.getSerializableExtra("data");
                    for (SelectPicPopupWindow.ImageInfo item : pickPhots) {
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
                    RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).
                            create(IAttachment.class).remove(String.valueOf(delAttachment.getId()), map, new RCallback<Attachment>() {
                        @Override
                        public void success(final Attachment attachment, final Response response) {
                            Toast("删除附件成功!");
                            attachments.remove(delAttachment);
                            signInGridViewAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void failure(final RetrofitError error) {
                            HttpErrorCheck.checkError(error);
                            Toast("删除附件失败!");
                            super.failure(error);
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
