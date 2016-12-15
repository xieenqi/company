package com.loyo.oa.v2.activityui.signin.presenter;

import android.app.Activity;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.signin.bean.SigninPictures;
import com.loyo.oa.v2.activityui.signin.contract.SigninContract;
import com.loyo.oa.v2.activityui.signin.model.SigninModelImpl;
import com.loyo.oa.v2.beans.LegWork;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by xeq on 2016/12/15
 */

public class SigninPresenterImpl implements SigninContract.Presenter {
    SigninContract.View view;
    SigninContract.Model model;

    public SigninPresenterImpl(SigninContract.View view) {
        this.view = view;
        model = new SigninModelImpl(this);
    }

    @Override
    public void getPageData(Object... pag) {

    }

    @Override
    public void bindPageData(Object obj) {

    }

    @Override
    public void sendPageData(Object obj) {

    }

    @Override
    public void getIsPhoto() {
        model.isPhotoSend();
    }

    @Override
    public void isPhoto(SigninPictures result) {
        if (result != null && result.value.equals("1")) {
            view.setIsPhoto(true);
        } else {
            view.setIsPhoto(false);
        }
    }

    @Override
    public void creatSignin(HashMap<String, Object> map) {
        model.creatSigninSend(map);
    }

    @Override
    public void creatSuccess(LegWork legWork) {
        view.creatSuccessUI(legWork);
    }

    @Override
    public void uploadAttachment(String uuid, File file, Activity activity) {
        model.uploadAttachmentSend(uuid, file, activity);
    }

    @Override
    public void uploadAttachmentSuccess() {
        view.uploadAttachmentSuccessUI();
    }

    @Override
    public void getAttachment(String uuid) {
        model.getAttachmentSend(uuid);
    }

    @Override
    public void getAttachmentSuccess(ArrayList<Attachment> attachments) {
        view.getAttachmentSuccessUI(attachments);
    }

    @Override
    public void deleteAttachment(HashMap<String, Object> map, Attachment delAttachment) {
        model.deleteAttachmentSend(map, delAttachment);
    }

    @Override
    public void deleteAttachmentSuccess(Attachment delAttachment) {
        view.deleteAttachmentSuccessUI(delAttachment);
    }
}