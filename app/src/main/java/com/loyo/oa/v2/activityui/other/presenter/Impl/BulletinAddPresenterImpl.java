package com.loyo.oa.v2.activityui.other.presenter.Impl;

import android.content.Context;
import android.text.TextUtils;

import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.other.presenter.BulletinAddPresenter;
import com.loyo.oa.v2.activityui.other.viewcontrol.BulletinAddView;
import com.loyo.oa.v2.announcement.api.AnnouncementService;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.beans.OrganizationalMember;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 【发布通知】Presenter
 * Created by yyy on 16/10/9
 */

public class BulletinAddPresenterImpl implements BulletinAddPresenter {

    private int bizType = 0;
    private int uploadSize;
    private int uploadNum;
    private StringBuffer joinUserId, joinName;

    private Context mContext;
    private Members member = new Members();
    private BulletinAddView mBulletinAddView;
    // private ArrayList<Attachment> mAttachment = new ArrayList<>();//照片附件的数据

    public BulletinAddPresenterImpl(BulletinAddView mBulletinAddView, Context mContext) {
        this.mBulletinAddView = mBulletinAddView;
        this.mContext = mContext;
    }

    /** 获取Members */
    @Override
    public Members getMembers() {
        return this.member;
    }

    /**
     * 通知人员回调处理
     * */
    @Override
    public void dealDepartmentResult(Members member) {
        this.member = member;
        joinName = new StringBuffer();
        joinUserId = new StringBuffer();
        if (member.users.size() == 0 && member.depts.size() == 0) {
            mBulletinAddView.setReceiver("没有选择人员");
            joinUserId.reverse();
        } else {
            if (null != member.depts) {
                for (OrganizationalMember newUser : member.depts) {
                    joinName.append(newUser.getName() + ",");
                    joinUserId.append(newUser.getId() + ",");
                }
            }
            if (null != member.users) {
                for (OrganizationalMember newUser : member.users) {
                    joinName.append(newUser.getName() + ",");
                    joinUserId.append(newUser.getId() + ",");
                }
            }
            if (!TextUtils.isEmpty(joinName)) {
                joinName.deleteCharAt(joinName.length() - 1);
            }
            mBulletinAddView.setReceiver(joinName.toString());
        }
    }

    /**
     * 组装附件
     * */
    @Override
    public ArrayList<Attachment> assembleAttachment(ArrayList<AttachmentForNew> attachments) {
        ArrayList<Attachment> newAttachment = new ArrayList<Attachment>();
        for (AttachmentForNew element : attachments) {
            Attachment obj = new Attachment();
            obj.setMime(element.getMime());
            obj.setOriginalName(element.getOriginalName());
            obj.setName(element.getName());
            newAttachment.add(obj);
        }
        return newAttachment;
    }

    /**
     * 格式验证
     * */
    @Override
    public void verifyText(String title, String content) {
        if (TextUtils.isEmpty(title)) {
            mBulletinAddView.verifyError(1);
            return;
        } else if (TextUtils.isEmpty(content)) {
            mBulletinAddView.verifyError(2);
            return;
        } else if (member.users.size() == 0 && member.depts.size() == 0) {
            mBulletinAddView.verifyError(3);
            return;
        }
        mBulletinAddView.verifySuccess(title, content);
    }

    /**
     * 提交通知
     * */
    @Override
    public void requestBulletinAdd(String title, String content, String uuid, ArrayList<AttachmentForNew> attachments) {
        LoyoProgressHUD hud = mBulletinAddView.showStatusProgress();
        HashMap<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("content", content);
        map.put("attachmentUUId", uuid);
        map.put("members", member);
        if (attachments != null) {
            map.put("attachments", assembleAttachment(attachments));
        }
        AnnouncementService.publishNotice(map)
                .subscribe(new DefaultLoyoSubscriber<Bulletin>(hud) {
                    @Override
                    public void onNext(final Bulletin bulletin) {
                        mBulletinAddView.onSuccess(bulletin);
                    }
                });
    }
}
