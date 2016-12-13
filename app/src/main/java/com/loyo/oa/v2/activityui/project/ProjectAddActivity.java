package com.loyo.oa.v2.activityui.project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.contactpicker.ContactPickerActivity;
import com.loyo.oa.contactpicker.model.event.ContactPickedEvent;
import com.loyo.oa.contactpicker.model.result.StaffMember;
import com.loyo.oa.contactpicker.model.result.StaffMemberCollection;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.activityui.project.adapter.ProjectMemberListViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.ProjectMember;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.CountTextWatcher;
import com.loyo.oa.v2.point.IProject;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【新建项目】
 */

@EActivity(R.layout.activity_project_add)
public class ProjectAddActivity extends BaseActivity {
    @ViewById
    TextView wordcount;
    @ViewById
    TextView tv_managers;
    @ViewById
    TextView tv_members;
    @ViewById
    EditText edt_title;
    @ViewById
    EditText edt_content;
    @ViewById
    ViewGroup layout_managers;
    @ViewById
    ViewGroup layout_members;
    @ViewById
    ViewGroup ll_managerGon;
    @ViewById
    ListView lv_project_members;
    @Extra(ExtraAndResult.EXTRA_OBJ)
    HttpProject mProject;
    @Extra("mUpdate")
    boolean mUpdate;

    List<HttpProject.ProjectManaer> selectedManagers;
    List<HttpProject.ProjectMember> selectedMembers;

    ProjectMemberListViewAdapter mAdapter;

    private ArrayList<HttpProject.ProjectMember> membersNowData = new ArrayList<>();//当前参与人数据

    @AfterViews
    void initViews() {
        super.setTitle(mUpdate ? "编辑项目" : "新建项目");
        layout_managers.setOnTouchListener(Global.GetTouch());
        layout_members.setOnTouchListener(Global.GetTouch());
        findViewById(R.id.img_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onBackPressed();
            }
        });

        if (mProject != null && !TextUtils.isEmpty(mProject.getId())) {
            mUpdate = true;
            if (!mProject.isCreator()) {//非创建者不能修改负责人
                layout_managers.setOnClickListener(null);
            }

            selectedManagers = mProject.managers;
            selectedMembers = mProject.members;

            edt_title.setText(mProject.title);
            edt_content.setText(mProject.content);
            if (mProject.isManager() && !mProject.isCreator()) {
                ll_managerGon.setVisibility(View.GONE);
            }
            String managerNames = ProjectMember.getManagersName(mProject.managers);
            tv_managers.setText(managerNames);

            membersNowData.clear();
            membersNowData.addAll(selectedMembers);
            setMemberOnActivityResult();
        }
        edt_content.addTextChangedListener(new CountTextWatcher(wordcount));
    }


    @Click(R.id.img_title_left)
    void close() {
        onBackPressed();
    }

    //选【负责人】
    @Click(R.id.layout_managers)
    void selectManagersClick() {
        StaffMemberCollection collection = convertProjectManagerListToStaffMemberCollection(selectedManagers);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContactPickerActivity.SINGLE_SELECTION_KEY, false);
        bundle.putBoolean(ContactPickerActivity.DEPARTMENT_SELECTION_KEY, false);
        if (collection != null) {
            bundle.putSerializable(ContactPickerActivity.STAFF_COLLECTION_KEY, collection);
        }
        bundle.putSerializable(ContactPickerActivity.REQUEST_KEY, FinalVariables.PICK_RESPONSIBLE_USER_REQUEST);
        Intent intent = new Intent();
        intent.setClass(this, ContactPickerActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //选【参与人】
    @Click(R.id.layout_members)
    void selectMembersClick() {
        StaffMemberCollection collection = convertProjectMemberListToStaffMemberCollection(selectedMembers);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContactPickerActivity.SINGLE_SELECTION_KEY, false);
        if (collection != null) {
            bundle.putSerializable(ContactPickerActivity.STAFF_COLLECTION_KEY, collection);
        }
        bundle.putSerializable(ContactPickerActivity.REQUEST_KEY, FinalVariables.PICK_INVOLVE_USER_REQUEST);
        Intent intent = new Intent();
        intent.setClass(this, ContactPickerActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 选人回调
     */
    @Subscribe
    public void onContactPicked(ContactPickedEvent event) {

        if (FinalVariables.PICK_RESPONSIBLE_USER_REQUEST.equals(event.request)) {
            StaffMemberCollection collection = event.data;
            List<HttpProject.ProjectManaer> selected = convertStaffMemberCollectionToProjectManagerList(collection);
            if (selected == null){
                return;
            }
            selectedManagers = selected;


            tv_managers.setText(ProjectMember.getManagersName((ArrayList<HttpProject.ProjectManaer>)selectedManagers));

            if (mProject != null) {
                mProject.setManagers((ArrayList<HttpProject.ProjectManaer>)selectedManagers);
            }
        }
        else if (FinalVariables.PICK_INVOLVE_USER_REQUEST.equals(event.request)) {
            StaffMemberCollection collection = event.data;
            List<HttpProject.ProjectMember> selected = convertStaffMemberCollectionToProjectMemberList(collection);
            selectedMembers = selected;
            if (null == selectedMembers || (selectedMembers.size()==0)) {
                tv_members.setText("无参与人");
            } else {
            }
            membersNowData.clear();
            membersNowData.addAll(selectedMembers);
            setMemberOnActivityResult();
        }
    }


    void setMemberOnActivityResult() {

        /**
         * 新建编辑，给adapter不同的数据源
         * */
        if (mAdapter == null) {
            mAdapter = new ProjectMemberListViewAdapter(this, membersNowData);
            mAdapter.setAction(new ProjectMemberListViewAdapter.ProjectMemberAction() {
                @Override
                public void deleteMemberAtIndex(int index) {
                    mAdapter.notifyDataSetInvalidated();
                    if (index>=0 && index<selectedMembers.size()) {
                        selectedMembers.remove(index);
                    }
                }
            });

            lv_project_members.setAdapter(mAdapter);
        }

        mAdapter.notifyDataSetInvalidated();
        Global.setListViewHeightBasedOnChildren(lv_project_members);
    }

    @Click(R.id.img_title_right)
    void createOrUpdateProject() {
        if (selectedManagers== null || selectedManagers.size() == 0) {
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
        projectTransObj.content = content;

        projectTransObj.managers = selectedManagers;
        if(mAdapter!=null)
        projectTransObj.members = mAdapter.getProjectMembers();

        if (mUpdate) {
            updateProject(projectTransObj);
        } else {
            createProject(projectTransObj);
        }
    }

    /**
     * 项目创建
     */
    void createProject(final ProjectTransObj obj) {
        showStatusLoading(false);
        app.getRestAdapter().create(IProject.class).Create(obj, new RCallback<Project>() {
            @Override
            public void success(final Project project, final Response response) {
                HttpErrorCheck.checkCommitSus(response);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cancelStatusLoading();
                        Intent intent = new Intent();
                        intent.putExtra("data", project);
                        app.finishActivity(ProjectAddActivity.this, MainApp.ENTER_TYPE_LEFT, 0x09, intent);
                    }
                },1000);
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkCommitEro(error);
            }
        });
    }

    /**
     * 项目编辑
     */
    void updateProject(final ProjectTransObj obj) {
        showStatusLoading(false);
        app.getRestAdapter().create(IProject.class).Update(mProject.getId(), obj, new RCallback<Project>() {
            @Override
            public void success(final Project project, final Response response) {
                HttpErrorCheck.checkCommitSus(response);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cancelStatusLoading();
                        Intent intent = new Intent();
                        intent.putExtra("data", project);
                        app.finishActivity(ProjectAddActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                    }
                },1000);
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkCommitEro(error);
            }
        });
    }


    public class ProjectTransObj {
        public String title;
        public String content;
        public List<HttpProject.ProjectMember> members;
        public List<HttpProject.ProjectManaer> managers;
    }

    public class ManagersMembers {
        public boolean canReadAll;
        public NewUser user = new NewUser();
        public NewUser dept = new NewUser();
    }

    public class NewUser {
        public String avatar;
        public String id;
        public String name;
        public String xpath;
    }

    private static StaffMemberCollection
    convertProjectManagerListToStaffMemberCollection(List<HttpProject.ProjectManaer> managers) {

        if (managers == null) {
            return null;
        }

        StaffMemberCollection collection = new StaffMemberCollection();

        for (HttpProject.ProjectManaer manager:managers) {
            if (manager.user != null) {
                StaffMember staffMember = new StaffMember();
                staffMember.id = manager.user.getId();
                staffMember.name = manager.user.getName();
                staffMember.avatar = manager.user.getAvatar();
                collection.users.add(staffMember);
            }
        }
        return collection;
    }

    private static List<HttpProject.ProjectManaer>
    convertStaffMemberCollectionToProjectManagerList(StaffMemberCollection collection) {

        if (collection == null) {
            return null;
        }

        ArrayList<HttpProject.ProjectManaer> result = new ArrayList<>();

        for (StaffMember user:collection.users) {
            HttpProject.ProjectManaer manager = new HttpProject.ProjectManaer();
            User innerUser = new User();
            innerUser.id = user.id;
            innerUser.name = user.name;
            innerUser.avatar = user.avatar;
            manager.user = innerUser;
            result.add(manager);
        }

        return result;
    }

    private static StaffMemberCollection
    convertProjectMemberListToStaffMemberCollection(List<HttpProject.ProjectMember> members) {
        if (members == null) {
            return null;
        }

        StaffMemberCollection collection = new StaffMemberCollection();

        for (HttpProject.ProjectMember member:members) {
            if (member.user != null) {
                StaffMember staffMember = new StaffMember();
                staffMember.id = member.user.getId();
                staffMember.name = member.user.getName();
                staffMember.avatar = member.user.getAvatar();
                collection.users.add(staffMember);
            }
            else if (member.dept != null) {
                StaffMember staffMember = new StaffMember();
                staffMember.id = member.dept.id;
                staffMember.name = member.dept.name;
                staffMember.xpath = member.dept.xpath;
                collection.depts.add(staffMember);
            }
        }
        return collection;
    }

    private static List<HttpProject.ProjectMember>
    convertStaffMemberCollectionToProjectMemberList(StaffMemberCollection collection) {
        if (collection == null) {
            return null;
        }

        ArrayList<HttpProject.ProjectMember> result = new ArrayList<>();

        for (StaffMember dept:collection.depts) {
            HttpProject.ProjectMember member = new HttpProject.ProjectMember();
            HttpProject.Dept innerDept = new HttpProject.Dept();
            innerDept.id = dept.id;
            innerDept.name = dept.name;
            innerDept.xpath = dept.xpath;
            member.dept = innerDept;
            result.add(member);
        }

        for (StaffMember user:collection.users) {
            HttpProject.ProjectMember member = new HttpProject.ProjectMember();
            User innerUser = new User();
            innerUser.id = user.id;
            innerUser.name = user.name;
            innerUser.avatar = user.avatar;
            member.user = innerUser;
            result.add(member);
        }

        return result;
    }
}
