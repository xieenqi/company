package com.loyo.oa.v2.activityui.signin.presenter;

import android.app.Activity;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.signin.bean.SigninPictures;
import com.loyo.oa.v2.activityui.signin.contract.SigninContract;
import com.loyo.oa.v2.activityui.signin.model.SigninModelImpl;
import com.loyo.oa.v2.beans.LegWork;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;

import java.io.File;
import java.text.DecimalFormat;
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

    public LoyoProgressHUD getHUD() {
        return view.getHUD();
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

    @Override
    public String getDefaultContact(ArrayList<Contact> data) {
        for (Contact ele : data) {
            if (!ele.isDefault()) {
                continue;
            } else {
                return ele.getName();
            }
        }
        return "";
    }

    @Override
    public String getDeviationDistance(double la, double lo, double laPosition, double loPosition) {
        LatLng ll = new LatLng(laPosition, loPosition);
        LatLng llCustomer = new LatLng(lo, la);// 地点的纬度，在-90 与90 之间的double 型数值。、地点的经度，在-180 与180 之间的double 型数值。
        LogUtil.d("偏差距离:" + AMapUtils.calculateLineDistance(ll, llCustomer));
        Double distance = Double.valueOf(Utils.setValueDouble2(AMapUtils.calculateLineDistance(ll, llCustomer)));
        DecimalFormat df = new DecimalFormat("0.00");
        String distanceText;
        if (distance <= 1000) {
            distanceText = Utils.setValueDouble2(distance) + "m";
        } else {
            distanceText = df.format(distance / 1000) + "km";
        }

        return distanceText;
    }//  104.073255,30.689493

}