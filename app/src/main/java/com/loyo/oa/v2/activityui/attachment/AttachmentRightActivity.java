package com.loyo.oa.v2.activityui.attachment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.beans.OrganizationalMember;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * 附件权限设置
 */

@EActivity(R.layout.activity_attachment_right_setting)
public class AttachmentRightActivity extends BaseActivity {

    @Extra("users")
    ArrayList<OrganizationalMember> users;
    @Extra("data")
    Attachment mAttachment;
    @ViewById
    RecyclerView rv_user;
    @ViewById
    CheckBox cb1;
    @ViewById
    ViewGroup layout_type1;

    RecyclerView.LayoutManager mLayoutManager;

    @AfterViews
    void init() {
        LogUtil.dll("附件权限设置 users:" + MainApp.gson.toJson(users));

        super.setTitle("权限设置");
        rv_user.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        rv_user.setLayoutManager(mLayoutManager);
        rv_user.setAdapter(adapter);

        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton compoundButton, final boolean b) {
                if (b) {
                    layout_type1.setEnabled(false);
                    for (CheckBox cb : listCb) {
                        if (cb.isChecked()) {
                            cb.setChecked(false);
                        }
                    }
                    AttachmentService.pub(mAttachment.getId(), 1)
                            .subscribe(new DefaultLoyoSubscriber<Attachment>() {
                                @Override
                                public void onNext(Attachment attachment) {
                                    Toast("设置成功");
                                    mAttachment.SetIsPublic(true);
                                    mAttachment.getViewers().clear();
                                }
                            });

                } else {
                    layout_type1.setEnabled(true);
                }
            }
        });

        if (!mAttachment.isPublic()) {
            layout_type1.setEnabled(true);
        } else {
            cb1.setChecked(true);
            layout_type1.setEnabled(false);
        }
    }

    @Click(R.id.img_title_left)
    void click() {
        onBackPressed();
    }

    @Click(R.id.layout_type1)
    void toggleCbAll() {
        cb1.toggle();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("data", mAttachment);
        setResult(RESULT_OK, intent);
        finish();
//        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        public CheckBox cb;
        public TextView tv_title;
        public TextView tv_content;
        public ViewGroup layout_title;

        public UserViewHolder(final View view) {
            super(view);

            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            cb = (CheckBox) view.findViewById(R.id.cb);
            layout_title = (ViewGroup) view.findViewById(R.id.layout_title);

        }
    }

    ArrayList<CheckBox> listCb = new ArrayList<>();
    RecyclerView.Adapter<UserViewHolder> adapter = new RecyclerView.Adapter<UserViewHolder>() {

        @Override
        public UserViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listview_attachment_right_setting, parent, false);
            return new UserViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final UserViewHolder holder, final int position) {
            final OrganizationalMember user = users.get(position);

            if (user != null) {
                holder.tv_title.setText(user.getRealname());

                if (!mAttachment.isPublic() && mAttachment.getViewers() != null) {
                    //回显
                    for (OrganizationalMember u : mAttachment.getViewers()) {
                        if (u.equals(user)) {
                            holder.cb.setChecked(true);
                            break;
                        }
                    }
                }

                holder.layout_title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        holder.cb.toggle();

                        if (holder.cb.isChecked() && cb1.isChecked()) {
                            cb1.setChecked(false);
                        }
                    }
                });

                LogUtil.dll("SIZE:" + mAttachment.getViewers().size());

                /*勾选状态设置*/
/*                for(OrganizationalMember newUser : mAttachment.getViewers()){
                    LogUtil.dll("可以看的ID:"+newUser.getUsers().get(0).getId());
                    if(user.getId().equals(newUser.getUsers().get(0).getId())){
                        holder.cb.setChecked(true);
                        LogUtil.dll("可以看的名字:"+user.getName());
                    }
                }*/

                holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(final CompoundButton compoundButton, final boolean b) {
                        if (b) {
                            AttachmentService.addViewer(mAttachment.getId(), user.getId())
                                    .subscribe(new DefaultLoyoSubscriber<Attachment>() {
                                        @Override
                                        public void onNext(Attachment attachment) {
                                            mAttachment.SetIsPublic(false);

                                            if (mAttachment.getViewers() == null) {
                                                mAttachment.setViewers(new ArrayList<OrganizationalMember>());
                                            }

                                            mAttachment.getViewers().add(user);
                                        }
                                    });
                        } else {
                            AttachmentService.removeViewer(mAttachment.getId(), user.getId())
                                    .subscribe(new DefaultLoyoSubscriber<Attachment>() {
                                        @Override
                                        public void onNext(Attachment attachment) {
                                            mAttachment.SetIsPublic(false);

                                            if (mAttachment.getViewers() != null) {
                                                for (int i = 0; i < mAttachment.getViewers().size(); i++) {
                                                    if (mAttachment.getViewers().get(i).equals(user)) {
                                                        mAttachment.getViewers().remove(i);
                                                    }
                                                }
                                            }
                                            if (!cb1.isChecked()) {
                                                if (mAttachment.getViewers().isEmpty()) {
                                                    layout_type1.setEnabled(false);
                                                    cb1.setChecked(true);
                                                } else {
                                                    layout_type1.setEnabled(true);
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });

                listCb.add(holder.cb);
            }
        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    };
}
