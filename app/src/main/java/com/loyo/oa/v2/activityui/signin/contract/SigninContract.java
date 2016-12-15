package com.loyo.oa.v2.activityui.signin.contract;

import android.app.Activity;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.signin.bean.SigninPictures;
import com.loyo.oa.v2.beans.LegWork;
import com.loyo.oa.v2.common.BaseView;
import com.loyo.oa.v2.common.base.BasePersenter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by xeq on 16/12/15.
 */

public interface SigninContract {
    interface View extends BaseView {
        void setIsPhoto(boolean isPicture);

        void creatSuccessUI(LegWork legWork);

        void uploadAttachmentSuccessUI();

        void getAttachmentSuccessUI(ArrayList<Attachment> attachments);

        void deleteAttachmentSuccessUI(Attachment delAttachment);
    }

    interface Presenter extends BasePersenter {
        void getIsPhoto();

        void isPhoto(SigninPictures result);

        void creatSignin(HashMap<String, Object> map);

        void creatSuccess(LegWork legWork);

        void uploadAttachment(String uuid, File file, Activity activity);

        void uploadAttachmentSuccess();

        void getAttachment(String uuid);

        void getAttachmentSuccess(ArrayList<Attachment> attachments);

        void deleteAttachment(HashMap<String, Object> map, Attachment delAttachment);

        void deleteAttachmentSuccess(Attachment delAttachment);

        String getDefaultContact(ArrayList<Contact> data);

        String getDeviationDistance(double la, double lo, double laPosition, double loPosition);

    }

    interface Model {
        void isPhotoSend();

        void creatSigninSend(HashMap<String, Object> map);

        void uploadAttachmentSend(String uuid, File file, Activity activity);

        void getAttachmentSend(String uuid);

        void deleteAttachmentSend(HashMap<String, Object> map, Attachment delAttachment);
    }


}