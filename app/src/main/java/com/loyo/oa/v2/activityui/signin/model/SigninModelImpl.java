package com.loyo.oa.v2.activityui.signin.model;

import android.app.Activity;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.signin.bean.SigninPictures;
import com.loyo.oa.v2.activityui.signin.contract.SigninContract;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.beans.LegWork;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.CommonSubscriber;
import com.loyo.oa.v2.tool.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

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
        CustomerService.getSigninUploadPhotoConfig(map)
                .subscribe(new DefaultLoyoSubscriber<SigninPictures>(LoyoErrorChecker.SILENCE) {
                    @Override
                    public void onNext(SigninPictures signinPictures) {
                        presenter.isPhoto(signinPictures);
                    }
                });
    }

    @Override
    public void creatSigninSend(HashMap<String, Object> map) {
        CustomerService.addSignIn(map)
                .subscribe(new DefaultLoyoSubscriber<LegWork>(presenter.getHUD()) {
                    @Override
                    public void onNext(LegWork legWork) {
                        presenter.creatSuccess(legWork);
                    }
                });
    }

    @Override
    public void getAttachmentSend(String uuid) {
        AttachmentService.getAttachments(uuid)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<Attachment>>() {
                    @Override
                    public void onNext(ArrayList<Attachment> attachments) {
                        presenter.getAttachmentSuccess(attachments);
                    }
                });
    }

    @Override
    public void deleteAttachmentSend(HashMap<String, Object> map, final Attachment delAttachment) {
        AttachmentService.remove(String.valueOf(delAttachment.getId()), map)
                .subscribe(new DefaultLoyoSubscriber<Attachment>() {
                    @Override
                    public void onNext(Attachment attachment) {
                        presenter.deleteAttachmentSuccess(delAttachment);
                    }
                });
    }

}