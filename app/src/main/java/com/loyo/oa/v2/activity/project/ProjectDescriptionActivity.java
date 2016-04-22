package com.loyo.oa.v2.activity.project;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Date;

/**
 * com.loyo.oa.v2.activity
 * 描述 :项目介绍页
 * 作者 : ykb
 * 时间 : 15/9/9.
 */
@EActivity(R.layout.activity_project_description)
public class ProjectDescriptionActivity extends BaseActivity {

    @ViewById
    TextView tv_managers;
    @ViewById
    TextView tv_members;

    @ViewById
    TextView tv_title;
    @ViewById
    TextView tv_extra;
    @ViewById
    TextView tv_content;

    @ViewById
    TextView tv_title_1;
    @ViewById
    ViewGroup img_title_left;

    @Extra
    HttpProject project;

    @AfterViews
    void initViews() {
        img_title_left.setOnTouchListener(Global.GetTouch());
        tv_title_1.setText("项目介绍");
        if (null == project) {
            return;
        }
        tv_extra.setText(project.creator.getRealname() + " " + MainApp.getMainApp().df2.format(new Date(project.getCreatedAt())) + " 发布");
        tv_title.setText(project.title);
        tv_content.setText(project.content);
        ArrayList<HttpProject.ProjectManaer> responsers = project.managers;
        ArrayList<HttpProject.ProjectMember> members = project.members;
        if (null != responsers && !responsers.isEmpty()) {
            StringBuilder managers = new StringBuilder();
            for (int i = 0; i < responsers.size(); i++) {
                User u = responsers.get(i).user;
                if (null == u) {
                    continue;
                }
                managers.append(responsers.get(i).user.getRealname());
                if (i != responsers.size() - 1) {
                    managers.append(",");
                }
            }
            tv_managers.setText(managers.toString());
        }

        /**
         * 组装参与人String展示数据
         * */
        if (null != members && !members.isEmpty()) {
            StringBuilder subMembers = new StringBuilder();

            for (int i = 0; i < members.size(); i++) {
                if (null != members.get(i).user && null != members.get(i).user.name) {
                    subMembers.append(members.get(i).user.name + ",");
                }

                if (null != members.get(i).dept && null != members.get(i).dept.name) {
                    subMembers.append(members.get(i).dept.name + ",");
                }
            }
            tv_members.setText(subMembers.toString());
        }
    }

    @Click(R.id.img_title_left)
    void onClick(final View v) {
        app.finishActivity(this, MainApp.ENTER_TYPE_TOP, 0, null);
    }

}
