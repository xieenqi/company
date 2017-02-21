package com.loyo.oa.v2.activityui.signin.model;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.signin.bean.SigninPictures;
import com.loyo.oa.v2.activityui.signin.contract.SigninContract;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.beans.LegWork;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;

import java.util.ArrayList;
import java.util.HashMap;

import rx.subscriptions.CompositeSubscription;

import static com.loyo.oa.v2.customermanagement.api.CustomerService.getSigninUploadPhotoConfig;

/**
 * Created by xeq on 2016/12/15
 */

public class SigninModelImpl implements SigninContract.Model {
    private SigninContract.Presenter presenter;
    private CompositeSubscription subscriptions;

    public SigninModelImpl(SigninContract.Presenter presenter) {
        this.presenter = presenter;
        subscriptions = new CompositeSubscription();
    }

    /**
     * 获取签到是否需要传递图片
     */
    @Override
    public void isPhotoSend() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("key", "need_pictures_switcher");
        subscriptions.add(CustomerService.getSigninUploadPhotoConfig(map)
                .subscribe(new DefaultLoyoSubscriber<SigninPictures>(LoyoErrorChecker.SILENCE) {
                    @Override
                    public void onNext(SigninPictures signinPictures) {
                        presenter.isPhoto(signinPictures);
                    }
                }));
    }

    @Override
    public void creatSigninSend(HashMap<String, Object> map) {
        subscriptions.add(CustomerService.addSignIn(map)
                .subscribe(new DefaultLoyoSubscriber<LegWork>(presenter.getHUD()) {
                    @Override
                    public void onError(Throwable e) {
                        presenter.isSubmitEableView(true);
                        super.onError(e);
                    }

                    @Override
                    public void onNext(LegWork legWork) {
                        presenter.isSubmitEableView(true);
                        presenter.creatSuccess(legWork);
                    }
                }));
    }

    @Override
    public void getAttachmentSend(String uuid) {
        subscriptions.add(AttachmentService.getAttachments(uuid)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<Attachment>>() {
                    @Override
                    public void onNext(ArrayList<Attachment> attachments) {
                        presenter.getAttachmentSuccess(attachments);
                    }
                }));
    }

    @Override
    public void deleteAttachmentSend(HashMap<String, Object> map, final Attachment delAttachment) {
        subscriptions.add(AttachmentService.remove(String.valueOf(delAttachment.getId()), map)
                .subscribe(new DefaultLoyoSubscriber<Object>() {
                    @Override
                    public void onNext(Object attachment) {
                        presenter.deleteAttachmentSuccess(delAttachment);
                    }
                }));
    }

    @Override
    public void destory() {
        subscriptions.unsubscribe();
    }

}