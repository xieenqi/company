package com.loyo.oa.v2.activityui.project.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.project.ProjectAddActivity;
import com.loyo.oa.v2.activityui.other.CommonAdapter;
import com.loyo.oa.v2.activityui.other.ViewHolder;

import java.util.ArrayList;

public class ProjectMemberListViewAdapter extends CommonAdapter<ProjectAddActivity.ManagersMembers> {

    ArrayList<ProjectAddActivity.ManagersMembers> mProjectMembers;
    ProjectMemberAction mAction;

    public ArrayList<ProjectAddActivity.ManagersMembers> GetProjectMembers() {
        return mProjectMembers;
    }

    public void SetAction(final ProjectMemberAction action) {
        mAction = action;
    }

    public ProjectMemberListViewAdapter(final Context context, final ArrayList<ProjectAddActivity.ManagersMembers> projectMembers) {
        super(context, projectMembers, R.layout.item_listview_project_members);
        mProjectMembers = projectMembers;
    }

    @Override
    public void convert(final ViewHolder holder, final ProjectAddActivity.ManagersMembers projectMember) {

        if (null != projectMember.user.name) {
            holder.setText(R.id.tv_member, projectMember.user.name);
        } else if (null != projectMember.dept.name) {
            holder.setText(R.id.tv_member, projectMember.dept.name);
        }

        final ImageView view = holder.getView(R.id.img_project_member_delete);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mAction != null) {
                    mProjectMembers.remove(projectMember);
                    mAction.DeleteMember();
                }
            }
        });

        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.performClick();
            }
        });
        /*默认设置参与人可看全部*/
        ProjectAddActivity.ManagersMembers p = mProjectMembers.get(holder.getPosition());
        p.canReadAll = true;

        /*隐藏参与人权限，弃用*/
        Switch sw = holder.getView(R.id.switch_member);
        sw.setChecked(projectMember.canReadAll);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                ProjectAddActivity.ManagersMembers p = mProjectMembers.get(holder.getPosition());
                if (p != null) {
                    p.canReadAll = isChecked;
                }
            }
        });
    }

    public interface ProjectMemberAction {
        void DeleteMember();
    }
}
