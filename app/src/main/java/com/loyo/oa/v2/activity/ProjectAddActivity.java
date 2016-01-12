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
import com.loyo.oa.v2.activity.project.HttpProject;
import com.loyo.oa.v2.adapter.ProjectMemberListViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.ProjectMember;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IProject;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.LogUtil;
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

/**
 * 新建项目
 * */

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
    @ViewById ViewGroup ll_managerGon;

    @ViewById ListView lv_project_members;

    @Extra(ExtraAndResult.EXTRA_OBJ) HttpProject mProject;
    @Extra("mUpdate") boolean mUpdate;

    ProjectMemberListViewAdapter mAdapter;
    ArrayList<HttpProject.ProjectMember> mProjectMember = new ArrayList<>();
    private boolean isEditMember = false;

    String mManagerIds = "", mManagerNames = "", mMemberIds = "", mMemberNames = "";
    // boolean mUpdate = false;

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
            if (!mProject.isCreator()) {//非创建者不能修改负责人
                layout_managers.setOnClickListener(null);
            }
            setProjectExtra();
        }
    }

    void setProjectExtra() {
        if (TextUtils.isEmpty(mProject.getId())) {
            return;
        }
        edt_title.setText(mProject.title);
        edt_content.setText(mProject.content);
        if (mProject.isManager() && !mProject.isCreator()) {
            ll_managerGon.setVisibility(View.GONE);
        }
        mManagerIds = ProjectMember.GetMnagerUserIds(mProject.managers);
        LogUtil.d(mManagerIds + " deao得到的负责人： " + MainApp.gson.toJson(mProject.members));
        if (mProject.members != null) {
            mMemberIds = ProjectMember.GetMenberUserIds(mProject.members);
        }
        mManagerNames = ProjectMember.getManagersName(mProject.managers);
        mMemberNames = ProjectMember.GetUserNames(mProject.members);

        tv_managers.setText(mManagerNames);
        setMemberOnActivityResult();
    }

    @Click(R.id.img_title_left)
    void close() {
        onBackPressed();
    }

    //选【负责人】
    @Click(R.id.layout_managers)
    void ManagersClick() {
        Bundle bundle1 = new Bundle();
        bundle1.putInt(DepartmentUserActivity.STR_SHOW_TYPE, DepartmentUserActivity.TYPE_SHOW_USER);
        bundle1.putInt(DepartmentUserActivity.STR_SELECT_TYPE, DepartmentUserActivity.TYPE_SELECT_MULTUI);
        app.startActivityForResult(this, DepartmentUserActivity.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_MANAGERS, bundle1);
    }

    //选【参与人】
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

        ArrayList<HttpProject.ProjectManaer> projectManagers = getProjectManager();
        if (mProject != null && !ListUtil.IsEmpty(projectManagers)) {
            mProject.setManagers(projectManagers);
        }

        if (mProject != null && !ListUtil.IsEmpty(mProject.managers)) {
            if (mProjectMember.removeAll(mProject.managers)) {
                setMemberOnActivityResult();
            }
        }
    }

    @OnActivityResult(REQUEST_MEMBERS)
    void OnResultMembers(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        isEditMember = true;
        mMemberIds = data.getStringExtra(DepartmentUserActivity.CC_USER_ID);
        mMemberNames = data.getStringExtra(DepartmentUserActivity.CC_USER_NAME);
        setMemberOnActivityResult();
    }

    void setMemberOnActivityResult() {
        if (mProjectMember == null) {
            mProjectMember = new ArrayList<>();
        } else {
            mProjectMember.clear();
        }
        if (!TextUtils.isEmpty(mMemberIds)) {
            String[] memberIds = mMemberIds.split(",");
            String[] memberNames = mMemberNames.split(",");

            for (int i = 0; i < memberIds.length; i++) {
                String uId = memberIds[i];
                if (!TextUtils.isEmpty(uId)) {
                    HttpProject.ProjectMember member = new HttpProject().new ProjectMember();
                    if (!isEditMember) {
                        member.canReadAll = mProject.members.get(i).canReadAll;
                    }
                    User uu = new User();
                    uu.id = uId;
                    uu.name = memberNames[i];
                    member.user = uu;
                    mProjectMember.add(member);
                }
            }
        }

        // 从Members中去掉与Manager重复的用户
        if (mProject != null && !ListUtil.IsEmpty(mProject.managers) && !ListUtil.IsEmpty(mProjectMember)) {
            mProjectMember.removeAll(mProject.managers);
        } else {
            ArrayList<HttpProject.ProjectManaer> projectManagers = getProjectManager();
            if (!ListUtil.IsEmpty(projectManagers)) {
                mProjectMember.removeAll(projectManagers);
            }
        }
        mAdapter = new ProjectMemberListViewAdapter(this, createData());//?????mProjectMember
        mAdapter.SetAction(new ProjectMemberListViewAdapter.ProjectMemberAction() {
            @Override
            public void DeleteMember() {
                for (HttpProject.ProjectMember ele : mAdapter.GetProjectMembers()) {
                    HttpProject.ProjectMember mm = new HttpProject().new ProjectMember();
                    mm.canReadAll = ele.canReadAll;
                    mm.user.id = ele.user.id;
                    mm.user.name = ele.user.realname;
                    mProjectMember.add(mm);
                }
                mAdapter.notifyDataSetInvalidated();
            }
        });

        lv_project_members.setAdapter(mAdapter);
        Global.setListViewHeightBasedOnChildren(lv_project_members);
    }

    /**
     * 参与人的数据  adapter列表
     *
     * @return
     */
    public ArrayList<HttpProject.ProjectMember> createData() {
        ArrayList<HttpProject.ProjectMember> obj = new ArrayList<>();
        for (HttpProject.ProjectMember ele : mProjectMember) {
            HttpProject.ProjectMember pm = new HttpProject().new ProjectMember();
            pm.canReadAll = ele.canReadAll;
            User uu = new User();
            uu.id = ele.user.id;
            uu.name = ele.user.name;
            pm.user = uu;
            obj.add(pm);
        }
        return obj;
    }

    @Click(R.id.img_title_right)
    void CreateOrUpdateProject() {
        if (TextUtils.isEmpty(mManagerIds)) {
            Toast("项目负责人不能为空!");
            return;
        }

        String title = edt_title.getText().toString().trim();//标题
        if (TextUtils.isEmpty(title)) {
            Toast("项目标题不能为空!");
            return;
        }

        ProjectTransObj projectTransObj = new ProjectTransObj();
        projectTransObj.title = title;

        String content = edt_content.getText().toString().trim();//内容
        if (!TextUtils.isEmpty(content)) {
            projectTransObj.content = content;
            Toast("项目内容不能为空!");
        }

        projectTransObj.managers = getProjectManager();

        if (!TextUtils.isEmpty(mMemberIds) && mAdapter != null) {
            projectTransObj.members = getProjectMenbers(mAdapter.GetProjectMembers());
        }

        if (mUpdate) {
            UpdateProject(projectTransObj);
        } else {
            CreateProject(projectTransObj);
        }
    }

    @Background
    void CreateProject(ProjectTransObj obj) {
        LogUtil.d(" 创建项目传递数据： " + MainApp.gson.toJson(obj));
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
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    @Background
    void UpdateProject(ProjectTransObj obj) {
        LogUtil.d(" 编辑项目传递数据: " + MainApp.gson.toJson(obj));
        app.getRestAdapter().create(IProject.class).Update(mProject.getId(), obj, new RCallback<Project>() {
            @Override
            public void success(Project project, Response response) {
                Global.ToastLong("编辑项目成功");
                Intent intent = new Intent();
                intent.putExtra("data", project);
                app.finishActivity(ProjectAddActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 组装 【负责人】
     *
     * @return
     */
    ArrayList<HttpProject.ProjectManaer> getProjectManager() {
        if (TextUtils.isEmpty(mManagerIds)) {
            return new ArrayList<>();
        }
        String[] arr = mManagerIds.split(",");
        String[] arrName = mManagerNames.split(",");
        ArrayList<HttpProject.ProjectManaer> mManager = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            if (!TextUtils.isEmpty(arr[i])) {
                HttpProject.ProjectManaer mm = new HttpProject().new ProjectManaer();
                mm.canReadAll = true;
                mm.user.id = arr[i];
                mm.user.name = arrName[i];
                mManager.add(mm);
            }
        }
        return mManager;
    }

    /**
     * 组装 【参与人】
     *
     * @return
     */
    public List<HttpProject.ProjectMember> getProjectMenbers(ArrayList<HttpProject.ProjectMember> data) {
        ArrayList<HttpProject.ProjectMember> newData = new ArrayList<>();
        for (HttpProject.ProjectMember element : data) {
            HttpProject.ProjectMember menb = new HttpProject().new ProjectMember();
            menb.canReadAll = element.canReadAll;
            User userOlde = element.user;
            menb.user.id = userOlde.id;
            menb.user.avatar = userOlde.avatar;
            menb.user.name = userOlde.name;
            newData.add(menb);
        }
        return newData;
    }

    public class ProjectTransObj {
        public String title;
        public String content;
        public List<HttpProject.ProjectMember> members;
        public List<HttpProject.ProjectManaer> managers;
    }

    public class ManagersMembers {
        public boolean canreadall;
        public NewUser user = new NewUser();

    }

    public class NewUser {
        public String avatar;
        public String id;
        public String name;
    }
}
