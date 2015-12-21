package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.ProjectMember;
import com.loyo.oa.v2.tool.CommonAdapter.CommonAdapter;
import com.loyo.oa.v2.tool.CommonAdapter.ViewHolder;

import java.util.ArrayList;

public class ProjectMemberListViewAdapter extends CommonAdapter<ProjectMember> {

    ArrayList<ProjectMember> mProjectMembers;
    ProjectMemberAction mAction;

    public ArrayList<ProjectMember> GetProjectMembers() {
        return mProjectMembers;
    }

    public void SetAction(ProjectMemberAction action) {
        mAction = action;
    }

    public ProjectMemberListViewAdapter(Context context, ArrayList<ProjectMember> projectMembers) {
        super(context, projectMembers, R.layout.item_listview_project_members);
        mProjectMembers = projectMembers;
    }

    @Override
    public void convert(final ViewHolder holder, final ProjectMember projectMember) {
        holder.setText(R.id.tv_member, projectMember.user.getRealname());

        ImageView view = holder.getView(R.id.img_project_member_delete);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAction != null) {
                    mProjectMembers.remove(projectMember);
                    mAction.DeleteMember();
                }
            }
        });

        Switch sw = holder.getView(R.id.switch_member);
        sw.setChecked(projectMember.canreadall);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ProjectMember p = mProjectMembers.get(holder.getPosition());
                if (p != null){
                    p.canreadall=isChecked;
                }
            }
        });
    }

    public interface ProjectMemberAction {
        void DeleteMember();
    }
}
