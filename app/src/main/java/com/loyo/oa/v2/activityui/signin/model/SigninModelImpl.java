package com.loyo.oa.v2.activityui.signin.model;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.signin.bean.SigninPictures;
import com.loyo.oa.v2.activityui.signin.contract.SigninContract;
import com.loyo.oa.v2.activityui.signin.event.SigninRushEvent;
import com.loyo.oa.v2.beans.LegWork;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.CommonSubscriber;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by xeq on 2016/12/15
 */

public class SigninModelImpl implements SigninContract.Model {
    SigninContract.Presenter presenter;

    public SigninModelImpl(SigninContract.Presenter presenter) {
        this.presenter = presenter;
    }

    /**
     * 获取签到是否需要传递图片
     */
    @Override
    public void isPhotoSend() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("key", "need_pictures_switcher");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getSetInfo(map, new Callback<SigninPictures>() {
            @Override
            public void success(SigninPictures result, Response response) {
                HttpErrorCheck.checkResponse("签到时必须操作？？？", response);
                presenter.isPhoto(result);
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    @Override
    public void creatSigninSend(HashMap<String, Object> map) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).addSignIn(map, new RCallback<LegWork>() {
            @Override
            public void success(final LegWork legWork, final Response response) {
                HttpErrorCheck.checkCommitSus("新建拜访", response);
                presenter.creatSuccess(legWork);
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkCommitEro(error);
            }
        });
    }

    @Override
    public void uploadAttachmentSend(String uuid, File file, Activity activity) {
        Utils.uploadAttachment(uuid, 0, file).subscribe(new CommonSubscriber(activity) {
            @Override
            public void onNext(final Serializable serializable) {
                presenter.uploadAttachmentSuccess();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    @Override
    public void getAttachmentSend(String uuid) {
        Utils.getAttachments(uuid, new RCallback<ArrayList<Attachment>>() {
            @Override
            public void success(final ArrayList<Attachment> attachments, final Response response) {
                HttpErrorCheck.checkResponse(response);
                presenter.getAttachmentSuccess(attachments);
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });
    }

    @Override
    public void deleteAttachmentSend(HashMap<String, Object> map, final Attachment delAttachment) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).remove(String.valueOf(delAttachment.getId()), map, new RCallback<Attachment>() {
            @Override
            public void success(final Attachment attachment, final Response response) {
                presenter.deleteAttachmentSuccess(delAttachment);
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
            }
        });
    }

}