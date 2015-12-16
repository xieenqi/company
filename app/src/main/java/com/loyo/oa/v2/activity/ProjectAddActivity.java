package com.loyo.oa.v2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.ProjectMemberListViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.ProjectMember;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.point.IProject;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.RCallback;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

@EActivity(R.layout.activity_project_add)
public class ProjectAddActivity extends BaseActivity {

    final static int REQUEST_MANAGERS = 100;
    final static int REQUEST_MEMBERS = 200;

    @ViewById TextView tv_managers;
    @ViewById TextView tv_members;

    @ViewById EditText edt_title;
    @ViewById EditText edt_content;

    @ViewById ViewGroup layout_managers;
    @ViewById ViewGroup layout_members;

    @ViewById ListView lv_project_members;

    @Extra("project") Project mProject;

    ProjectMemberListViewAdapter mAdapter;
    ArrayList<ProjectMember> mProjectMember;

    String mManagerIds, mManagerNames, mMemberIds = "", mMemberNames = "";
    boolean mUpdate = false;

    @AfterViews
    void initViews() {
        super.setTitle(mUpdate ? "编辑项目" : "新建项目");

        layout_managers.setOnTouchListener(Global.GetTouch());
        layout_members.setOnTouchListener(Global.GetTouch());

        findViewById(R.id.img_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (mProject != null && !TextUtils.isEmpty(mProject.getId())) {
            mUpdate = true;

            if (!mProject.isCreator()) {
                layout_managers.setOnClickListener(null);
            }
        }

        if (mUpdate) {
            app.getRestAdapter().create(IProject.class).getProjectById(mProject.getId(), new RCallback<Project>() {
                @Override
                public void success(Project project, Response response) {
                    mProject = project;
                    setProjectExtra();
                }
            });
        }
    }

    void setProjectExtra() {
        if (!mUpdate || mProject == null || TextUtils.isEmpty(mProject.getId())) {
            return;
        }

        edt_title.setText(mProject.getTitle());
        edt_content.setText(mProject.getContent());

        mManagerIds = ProjectMember.GetUserIds(mProject.getManagers());
        mManagerNames = ProjectMember.GetUserNames(mProject.getManagers());
        mMemberIds = ProjectMember.GetUserIds(mProject.getMembers());
        mMemberNames = ProjectMember.GetUserNames(mProject.getMembers());

        tv_managers.setText(mManagerNames);
        setMemberOnActivityResult();
    }

    @Click(R.id.img_title_left)
    void close() {
        onBackPressed();
    }

    @Click(R.id.layout_managers)
    void ManagersClick() {
        Bundle bundle1 = new Bundle();
        bundle1.putInt(DepartmentUserActivity.STR_SHOW_TYPE, DepartmentUserActivity.TYPE_SHOW_USER);
        bundle1.putInt(DepartmentUserActivity.STR_SELECT_TYPE, DepartmentUserActivity.TYPE_SELECT_MULTUI);
        app.startActivityForResult(this, DepartmentUserActivity.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_MANAGERS, bundle1);
    }

    @Click(R.id.layout_members)
    void MembersClick() {
        Bundle bundle1 = new Bundle();
        bundle1.putInt(DepartmentUserActivity.STR_SHOW_TYPE, DepartmentUserActivity.TYPE_SHOW_USER);
        bundle1.putInt(DepartmentUserActivity.STR_SELECT_TYPE, DepartmentUserActivity.TYPE_SELECT_MULTUI);
        app.startActivityForResult(this, DepartmentUserActivity.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_MEMBERS, bundle1);
    }

    @OnActivityResult(REQUEST_MANAGERS)
    void OnResultManagers(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        mManagerIds = data.getStringExtra(DepartmentUserActivity.CC_USER_ID);
        mManagerNames = data.getStringExtra(DepartmentUserActivity.CC_USER_NAME);

        tv_managers.setText(mManagerNames);

        ArrayList<ProjectMember> projectManagers = getProjectManager();
        if (mProject != null && !ListUtil.IsEmpty(projectManagers)) {
            mProject.setManagers(projectManagers);
        }

        if (mProject != null && !ListUtil.IsEmpty(mProject.getManagers())) {
            if (mProjectMember.removeAll(mProject.getManagers())) {
                setMemberOnActivityResult();
            }
        }
    }

    @OnActivityResult(REQUEST_MEMBERS)
    void OnResultMembers(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        String memberIds = data.getStringExtra(DepartmentUserActivity.CC_USER_ID);
        String memberNames = data.getStringExtra(DepartmentUserActivity.CC_USER_NAME);

        mMemberIds = TextUtils.isEmpty(memberIds) ? memberIds : mMemberIds + "," + memberIds;
        mMemberNames = TextUtils.isEmpty(memberNames) ? memberNames : mMemberNames + "," + memberNames;

        setMemberOnActivityResult();
    }

    void setMemberOnActivityResult() {
        if (mProjectMember == null) {
            mProjectMember = new ArrayList<>();
        } else {
            mProjectMember.clear();
        }

        if (mUpdate) {
            if (mProject != null && !ListUtil.IsEmpty(mProject.getMembers())) {
                mProjectMember = mProject.getMembers();
            }
        }

        if (!TextUtils.isEmpty(mMemberIds)) {
            String[] memberIds = mMemberIds.split(",");
            String[] memberNames = mMemberNames.split(",");

            for (int i = 0; i < memberIds.length; i++) {
                if (TextUtils.isEmpty(memberIds[i])) {
                    continue;
                }
                String uId =memberIds[i];
                if (!TextUtils.isEmpty(uId)) {
                    User u = new User();
                    u.setId(uId);
                    u.setRealname(memberNames[i]);

                    ProjectMember member = new ProjectMember(uId, false);
                    member.setUserId(uId);
                    member.setUser(u);
                    if (!mProjectMember.contains(member)) {
                        mProjectMember.add(member);
                    }
                }
            }
        }

        // 从Members中去掉与Manager重复的用户
        if (mProject != null && !ListUtil.IsEmpty(mProject.getManagers()) && !ListUtil.IsEmpty(mProject.getMembers())) {
            mProjectMember.removeAll(mProject.getManagers());
        } else {
            ArrayList<ProjectMember> projectManagers = getProjectManager();
            if (!ListUtil.IsEmpty(projectManagers)) {
                mProjectMember.removeAll(projectManagers);
            }
        }

        if (mProject != null) {
            mProject.setMembers(mProjectMember);
        }

        mAdapter = new ProjectMemberListViewAdapter(this, mProjectMember);
        mAdapter.SetAction(new ProjectMemberListViewAdapter.ProjectMemberAction() {
            @Override
            public void DeleteMember() {
                mProjectMember = mAdapter.GetProjectMembers();
                mAdapter.notifyDataSetInvalidated();
            }
        });

        lv_project_members.setAdapter(mAdapter);
        Global.setListViewHeightBasedOnChildren(lv_project_members);
    }

    @Click(R.id.img_title_right)
    void CreateOrUpdateProject() {
        if (TextUtils.isEmpty(mManagerIds)) {
            Global.Toast("项目负责人不能为空!");
            return;
        }

        String title = edt_title.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            Global.Toast("项目标题不能为空!");
            return;
        }

        ProjectTransObj projectTransObj = new ProjectTransObj();
        projectTransObj.title = title;

        String content = edt_content.getText().toString().trim();
        if (!TextUtils.isEmpty(content)) {
            projectTransObj.content = content;
        }

        projectTransObj.managers = getProjectManager();

        if (!TextUtils.isEmpty(mMemberIds) && mAdapter != null) {
            projectTransObj.members = mAdapter.GetProjectMembers();
        }

        if (mUpdate) {
            UpdateProject(projectTransObj);
        } else {
            CreateProject(projectTransObj);
        }
    }

    @Background
    void CreateProject(ProjectTransObj obj) {
        app.getRestAdapter().create(IProject.class).Create(obj, new RCallback<Project>() {
            @Override
            public void success(Project project, Response response) {
                Global.ToastLong("新增项目成功");
                Intent intent = new Intent();
                intent.putExtra("data", project);
                app.finishActivity(ProjectAddActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
            }


            @Override
            public void failure(RetrofitError error) {
                if(error.getKind() == RetrofitError.Kind.NETWORK){
                    Toast("请检查您的网络连接");
                }
                else if(error.getResponse().getStatus() == 500){
                    Toast("网络异常500，请稍候再试");
                }
            }
        });
    }

    @Background
    void UpdateProject(ProjectTransObj obj) {
        app.getRestAdapter().create(IProject.class).Update(mProject.getId(), obj, new RCallback<Project>() {
            @Override
            public void success(Project project, Response response) {
                Global.ToastLong("编辑项目成功");

                Intent intent = new Intent();
                intent.putExtra("data", project);

                app.finishActivity(ProjectAddActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
            }
        });
    }

    ArrayList<ProjectMember> getProjectManager() {
        if (TextUtils.isEmpty(mManagerIds)) {
            return new ArrayList<>();
        }

        String[] arr = mManagerIds.split(",");
        ArrayList<ProjectMember> members = new ArrayList<>();

        for (String a : arr) {
            if (!TextUtils.isEmpty(a)) {
                members.add(new ProjectMember(a, true));
            }
        }

        return members;
    }

    public class ProjectTransObj {
        public String title;
        public String content;
        public List<ProjectMember> members;
        public List<ProjectMember> managers;
    }
}
