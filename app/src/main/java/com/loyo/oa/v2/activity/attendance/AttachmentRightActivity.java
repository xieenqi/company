package com.loyo.oa.v2.activity.attendance;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 附件权限设置
 */

@EActivity(R.layout.activity_attachment_right_setting)
public class AttachmentRightActivity extends BaseActivity {

    @Extra("users")
    ArrayList<NewUser> users;

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
        LogUtil.dll("附件权限设置 :" + MainApp.gson.toJson(users));

        super.setTitle("权限设置");
        rv_user.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        rv_user.setLayoutManager(mLayoutManager);
        rv_user.setAdapter(adapter);

        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    layout_type1.setEnabled(false);
                    for (CheckBox cb : listCb) {
                        if (cb.isChecked()) {
                            cb.setChecked(false);
                        }
                    }

                    RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).pub(mAttachment.getId(), 1, new RCallback<Attachment>() {
                        @Override
                        public void success(Attachment o, Response response) {
                            Toast("设置成功");
                            mAttachment.SetIsPublic(true);
                            mAttachment.getViewers().clear();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            super.failure(error);
                            HttpErrorCheck.checkError(error);
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
    void toggleCbAll()
    {
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

        public UserViewHolder(View view) {
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
        public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listview_attachment_right_setting, parent, false);
            return new UserViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final UserViewHolder holder, int position) {
            final NewUser user = users.get(position);

            if (user != null) {
                holder.tv_title.setText(user.getRealname());

                if (!mAttachment.isPublic() && mAttachment.getViewers() != null) {
                    //回显
                    for (NewUser u : mAttachment.getViewers()) {
                        if (u.equals(user)) {
                            holder.cb.setChecked(true);
                            break;
                        }
                    }
                }

                holder.layout_title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.cb.toggle();

                        if (holder.cb.isChecked() && cb1.isChecked()) {
                            cb1.setChecked(false);
                        }
                    }
                });

                holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).addViewer(mAttachment.getId(), user.getId(), new RCallback<Attachment>() {
                                @Override
                                public void success(Attachment o, Response response) {
                                    LogUtil.dll("设置" + user.getRealname() + "附件权限成功!");
                                    mAttachment.SetIsPublic(false);

                                    if (mAttachment.getViewers() == null) {
                                        mAttachment.setViewers(new ArrayList<NewUser>());
                                    }

                                    mAttachment.getViewers().add(user);
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    super.failure(error);
                                    HttpErrorCheck.checkError(error);
                                }
                            });
                        } else {
                            RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).removeViewer(mAttachment.getId(), user.getId(), new RCallback<Attachment>() {
                                @Override
                                public void success(Attachment o, Response response) {
                                    LogUtil.dll("删除" + user.getRealname() + "附件权限成功!");
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

                                @Override
                                public void failure(RetrofitError error) {
                                    super.failure(error);
                                    HttpErrorCheck.checkError(error);
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
