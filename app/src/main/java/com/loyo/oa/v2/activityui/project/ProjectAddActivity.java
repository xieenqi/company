package com.loyo.oa.v2.activityui.project;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.SelectDetUserActivity2;
import com.loyo.oa.v2.activityui.project.adapter.ProjectMemberListViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.ProjectMember;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IProject;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.customview.CountTextWatcher;

import org.androidannotations.annotations.AfterViews;
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
 * 【新建项目】
 */

@EActivity(R.layout.activity_project_add)
public class ProjectAddActivity extends BaseActivity {

    static final int REQUEST_MANAGERS = 100;
    static final int REQUEST_MEMBERS = 200;

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

    ProjectMemberListViewAdapter mAdapter;
    ArrayList<HttpProject.ProjectMember> mProjectMember = new ArrayList<>();

    private boolean isEditMember = false;
    private StringBuffer mMemberIds = new StringBuffer();
    private StringBuffer mMemberNames = new StringBuffer();

    private StringBuffer mManagerIds = new StringBuffer();
    private StringBuffer mManagerNames = new StringBuffer();
    private Members members = new Members();//参与人回传数据
    private ArrayList<ManagersMembers> membersNowData = new ArrayList<>();//当前参与人数据

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
            membersNowData.clear();
            membersNowData.addAll(getMenbersEdit());
            setProjectExtra();
        }
        edt_content.addTextChangedListener(new CountTextWatcher(wordcount));
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
        mManagerIds.append(ProjectMember.GetMnagerUserIds(mProject.managers));
        mManagerNames.append(ProjectMember.getManagersName(mProject.managers));

        if (null != mProject.members) {
            mMemberIds.append(ProjectMember.GetMenberUserIds(mProject.members));
            mMemberNames.append(ProjectMember.GetUserNames(mProject.members));
        }

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
        SelectDetUserActivity2.startThisForMulitSelect(ProjectAddActivity.this, mManagerIds == null ? null : mManagerIds.toString(), false);
    }

    //选【参与人】
    @Click(R.id.layout_members)
    void MembersClick() {
        SelectDetUserActivity2.startThisForAllSelect(ProjectAddActivity.this, mMemberIds == null ? null : mMemberIds.toString(), true);
    }

    /**
     * 负责人回调
     * 说  明:组装StringBuffer用于显示名字
     * 权限控制view设置
     */
    @OnActivityResult(SelectDetUserActivity2.REQUEST_MULTI_SELECT)
    void OnResultManagers(final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        members = (Members) data.getSerializableExtra("data");
        mManagerNames = new StringBuffer();
        mManagerIds = new StringBuffer();

        if (members != null) {
            if (null != members.depts) {
                for (com.loyo.oa.v2.beans.NewUser newUser : members.depts) {
                    mManagerNames.append(newUser.getName() + ",");
                    mManagerIds.append(newUser.getId() + ",");
                }
            }
            if (null != members.users) {
                for (com.loyo.oa.v2.beans.NewUser newUser : members.users) {
                    mManagerNames.append(newUser.getName() + ",");
                    mManagerIds.append(newUser.getId() + ",");
                }
            }
            if (!TextUtils.isEmpty(mManagerNames)) {
                mManagerNames.deleteCharAt(mManagerNames.length() - 1);
            }
        }

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

    /**
     * 参与人回调
     * 说  明:组装StringBuffer用于显示名字
     * 权限控制view设置
     */
    @OnActivityResult(SelectDetUserActivity2.REQUEST_ALL_SELECT)
    void OnResultMembers(final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        isEditMember = true;

        members = (Members) data.getSerializableExtra("data");
        if (null == members) {
            tv_members.setText("无参与人");
        } else {
            mMemberNames = new StringBuffer();
            mMemberIds = new StringBuffer();
            if (null != members.depts) {
                for (com.loyo.oa.v2.beans.NewUser newUser : members.depts) {
                    mMemberNames.append(newUser.getName() + ",");
                    mMemberIds.append(newUser.getId() + ",");
                }
            }
            if (null != members.users) {
                for (com.loyo.oa.v2.beans.NewUser newUser : members.users) {
                    mMemberNames.append(newUser.getName() + ",");
                    mMemberIds.append(newUser.getId() + ",");
                }
            }
            if (!TextUtils.isEmpty(mMemberNames)) {
                mMemberNames.deleteCharAt(mMemberNames.length() - 1);
            }
        }
        membersNowData = getMenbersAdd();
        setMemberOnActivityResult();
    }

    void setMemberOnActivityResult() {
        if (mProjectMember == null) {
            mProjectMember = new ArrayList<>();
        } else {
            mProjectMember.clear();
        }
        if (!TextUtils.isEmpty(mMemberIds)) {
            String[] memberIds = mMemberIds.toString().split(",");
            String[] memberNames = mMemberNames.toString().split(",");

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

        /**
         * 新建编辑，给adapter不同的数据源
         * */
        mAdapter = new ProjectMemberListViewAdapter(this, membersNowData);

        /**
         * 适配器初始化 删除item监听
         * */
        mAdapter.SetAction(new ProjectMemberListViewAdapter.ProjectMemberAction() {
            @Override
            public void DeleteMember() {
                mMemberIds.delete(0, mMemberIds.length());
                for (ManagersMembers ele : mAdapter.GetProjectMembers()) {
                    HttpProject.ProjectMember mm = new HttpProject().new ProjectMember();
                    mm.canReadAll = ele.canReadAll;
                    if (null != ele.user) {
                        mm.user.id = ele.user.id;
                        mm.user.name = ele.user.name;
                        mProjectMember.add(mm);
                        mMemberIds.append(ele.user.id + ",");
                    }

                    if (null != ele.dept) {
                        mm.dept.id = ele.dept.id;
                        mm.dept.name = ele.dept.name;
                        mProjectMember.add(mm);
                        mMemberIds.append(ele.dept.id + ",");
                    }
                }
                mAdapter.notifyDataSetInvalidated();
            }
        });

        lv_project_members.setAdapter(mAdapter);
        Global.setListViewHeightBasedOnChildren(lv_project_members);
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
        projectTransObj.content = content;

        /*取消项目内容，非空判断 2016-4-28*/
        /*if (TextUtils.isEmpty(content)) {
            Toast("项目内容不能为空!");
            return;
        } else {
            projectTransObj.content = content;
        }*/

        projectTransObj.managers = getProjectManager();

        /**
         * 获取post参与人数据
         * 过滤为空的dept或user
         * */
        if (!TextUtils.isEmpty(mMemberIds) && null != mAdapter) {
            projectTransObj.members = mAdapter.GetProjectMembers();
            for (ManagersMembers managersMembers : projectTransObj.members) {
                if (managersMembers.dept != null && null == managersMembers.dept.id) {
                    managersMembers.dept = null;
                }
                if (managersMembers.user != null && null == managersMembers.user.id) {
                    managersMembers.user = null;
                }
            }
        }

        if (mUpdate) {
            UpdateProject(projectTransObj);
        } else {
            CreateProject(projectTransObj);
        }
    }

    /**
     * 项目创建
     */
    void CreateProject(final ProjectTransObj obj) {
        LogUtil.d(" 创建项目传递数据： " + MainApp.gson.toJson(obj));
        showLoading("");
        app.getRestAdapter().create(IProject.class).Create(obj, new RCallback<Project>() {
            @Override
            public void success(final Project project, final Response response) {
                Global.ToastLong("新增项目成功");
                cancelLoading();
                Intent intent = new Intent();
                intent.putExtra("data", project);
                app.finishActivity(ProjectAddActivity.this, MainApp.ENTER_TYPE_LEFT, 0x09, intent);
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                cancelLoading();
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 项目编辑
     */
    void UpdateProject(final ProjectTransObj obj) {
        LogUtil.d(" 编辑项目传递数据: " + MainApp.gson.toJson(obj));
        showLoading("");
        app.getRestAdapter().create(IProject.class).Update(mProject.getId(), obj, new RCallback<Project>() {
            @Override
            public void success(final Project project, final Response response) {
                Global.ToastLong("编辑项目成功");
                cancelLoading();
                Intent intent = new Intent();
                intent.putExtra("data", project);
                app.finishActivity(ProjectAddActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
            }

            @Override
            public void failure(final RetrofitError error) {
                cancelLoading();
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 组装【负责人】的请求参数
     *
     * @return
     */
    ArrayList<HttpProject.ProjectManaer> getProjectManager() {
        if (TextUtils.isEmpty(mManagerIds)) {
            return new ArrayList<>();
        }
        String[] arr = mManagerIds.toString().split(",");
        String[] arrName = mManagerNames.toString().split(",");
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
     * 编辑 组装【参与人】的请求参数
     *
     * @return
     */
    public ArrayList<ManagersMembers> getMenbersEdit() {
        ArrayList<ManagersMembers> newData = new ArrayList<>();
        for (HttpProject.ProjectMember mm : mProject.members) {
            if (null != mm.dept && null != mm.dept.name) {
                ManagersMembers menb = new ManagersMembers();
                menb.dept.name = mm.dept.name;
                menb.dept.id = mm.dept.id;
                menb.dept.xpath = mm.dept.xpath;
                newData.add(menb);
            }

            if (null != mm.user && null != mm.user.name) {
                ManagersMembers menb = new ManagersMembers();
                menb.user.name = mm.user.name;
                menb.user.id = mm.user.id;
                menb.user.avatar = mm.user.avatar;
                newData.add(menb);
            }
        }
        return newData;
    }

    /**
     * 新建 组装【参与人】的请求参数
     *
     * @return
     */
    public ArrayList<ManagersMembers> getMenbersAdd() {
        ArrayList<ManagersMembers> newData = new ArrayList<>();
        if (null != members.depts) {
            for (com.loyo.oa.v2.beans.NewUser newUser : members.depts) {
                ManagersMembers menb = new ManagersMembers();
                menb.dept.name = newUser.getName();
                menb.dept.id = newUser.getId();
                menb.dept.xpath = newUser.getXpath();
                newData.add(menb);
            }
        }

        if (null != members.users) {
            for (com.loyo.oa.v2.beans.NewUser newUser : members.users) {
                ManagersMembers menb = new ManagersMembers();
                menb.user.name = newUser.getName();
                menb.user.id = newUser.getId();
                menb.user.avatar = newUser.getAvatar();
                newData.add(menb);
            }
        }
        return newData;
    }

    public class ProjectTransObj {
        public String title;
        public String content;
        public List<ManagersMembers> members;
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
}
