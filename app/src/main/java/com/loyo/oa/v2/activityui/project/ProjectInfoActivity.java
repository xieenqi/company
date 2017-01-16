package com.loyo.oa.v2.activityui.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.activityui.project.api.ProjectService;
import com.loyo.oa.v2.activityui.project.fragment.AttachmentFragment;
import com.loyo.oa.v2.activityui.project.fragment.DiscussionFragment;
import com.loyo.oa.v2.activityui.project.fragment.TaskReoprtWfinstanceFragment;
import com.loyo.oa.v2.activityui.tasks.TasksInfoActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.TaskRecord;
import com.loyo.oa.v2.beans.WfInstanceRecord;
import com.loyo.oa.v2.beans.WorkReportRecord;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.customview.PagerSlidingTabStrip;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.OnLoadSuccessCallback;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.activity
 * 描述 :【项目详情页】
 * 作者 : ykb
 * 时间 : 15/9/8.
 */
@EActivity(R.layout.activity_project_info_new)
public class ProjectInfoActivity extends BaseFragmentActivity implements OnLoadSuccessCallback {
    private String[] TITLES = {"任务", "报告", "审批", "文件", "讨论"};

    @ViewById
    ViewGroup img_title_left;
    @ViewById
    ViewGroup img_title_right;
    @ViewById
    TextView tv_title_1;
    @ViewById
    PagerSlidingTabStrip tabs;
    @ViewById
    ViewPager pager;
    @ViewById
    ViewGroup layout_project_des;
    @ViewById
    TextView tv_project_title;
    @ViewById
    TextView tv_project_extra;
    @ViewById
    TextView tv_status;
    @ViewById
    LoadingLayout ll_loading;

    @Extra("projectId")
    String projectId;
    HttpProject project;
    @Extra(ExtraAndResult.EXTRA_TYPE)
    String keyType;
    @Extra(ExtraAndResult.IS_UPDATE)
    boolean isUpdate;//是否需要刷新列表

    MyPagerAdapter adapter;
    private ArrayList<BaseFragment> fragmentXes = new ArrayList<>();
    private ArrayList<OnProjectChangeCallback> callbacks = new ArrayList<>();
    BaseFragment fragmentX = null;
    private boolean isEdit, isStop, isDelete, isRestart, isEditMember;

    @AfterViews
    void initViews() {
        setTouchView(-1);
        img_title_right.setEnabled(false);
        tv_title_1.setText("项目详情");
        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                getProject();
            }
        });
        getProject();
    }

    /**
     * 获取项目 详细数据
     */
    private void getProject() {

        ProjectService.getProjectById(projectId,keyType).subscribe(new DefaultLoyoSubscriber<HttpProject>(ll_loading) {
            @Override
            public void onNext(HttpProject _project) {
                project = _project;
                img_title_right.setEnabled(true);
                initData(project);
            }
        });

    }

    @Click({R.id.img_title_left, R.id.img_title_right, R.id.layout_project_des})
    void onClick(final View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.img_title_right:
                if (null == project) {
                    Global.Toast("项目为空！");
                    return;
                }
                isEdit = project.status == 1 && project.isCreator();
                isDelete = isStop = project.status == 1 && project.isCreator();
                isRestart = project.status == 2 && project.isCreator();
                //负责人和创建人相同 就取大的那个(创建人)
                isEditMember = project.status == 1 && project.isManager() && !project.isCreator();

                functionButton();
                break;
            case R.id.layout_project_des:
                Bundle b = new Bundle();
                b.putSerializable("project", project);
                app.startActivity(this, ProjectDescriptionActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, b);
                break;
            default:
                break;
        }
    }

    /**
     * 参见 project_perminssion 图片
     * 右上角菜单
     */
    private void functionButton() {
        ActionSheetDialog dialog = new ActionSheetDialog(ProjectInfoActivity.this).builder();
        if (isEdit)
            dialog.addSheetItem("编辑", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    editProject();
                }
            });
        if (isStop)
            dialog.addSheetItem("结束项目", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    restartProject();
                }
            });
        if (isDelete)
            dialog.addSheetItem("删除", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    deleteProject();
                }
            });
        if (isRestart)
            dialog.addSheetItem("重启项目", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    restartProject();
                }
            });
        if (isEditMember)
            dialog.addSheetItem("修改参与人", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    editProject();
                }
            });
        dialog.show();
    }

    /**创建人编辑全部 负者人修改参与人
     * 之前编辑项目权限在 ProjectAddActivity操作 此次没有调整
     */
    private void editProject() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("mUpdate", true);
        bundle.putSerializable(ExtraAndResult.EXTRA_OBJ, project);
        app.startActivityForResult(ProjectInfoActivity.this, ProjectAddActivity_.class, MainApp.ENTER_TYPE_RIGHT,
                TasksInfoActivity.REQUEST_EDIT, bundle);
        isUpdate = true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (null != project) {
            project.viewed = true;
            intent.putExtra("review", project);
        }
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, isUpdate ? 0x09 : RESULT_OK, intent);
    }

    /**
     * 初始化数据(绑定页面 任务 报告 审批...)
     */
    private void initData(final HttpProject project) {
        if (null == project) {
            return;
        }
        if (adapter == null) {
            int[] sizes = new int[]{project.archiveData.task, project.archiveData.workreport,
                    project.archiveData.approval, project.archiveData.attachment, project.archiveData.discuss};
            for (int i = 0; i < TITLES.length; i++) {
                TITLES[i] += "(" + sizes[i] + ")";
                Bundle bundle = new Bundle();
                bundle.putSerializable("project", project);

                if (i == 0) {
                    bundle.putInt("type", 2);
                } else if (i == 1) {
                    bundle.putInt("type", 1);
                } else if (i == 2) {
                    bundle.putInt("type", 12);
                }

                if(0==i){
                    fragmentX = new TaskReoprtWfinstanceFragment<TaskRecord>();//任务
                }else if(1==i){
                    fragmentX = new TaskReoprtWfinstanceFragment<WorkReportRecord>();//报告
                }else if (2==i) {
                    fragmentX = new TaskReoprtWfinstanceFragment<WfInstanceRecord>();//审批
                } else if (i == TITLES.length - 1) {
                    fragmentX = new DiscussionFragment();//讨论
                } else {
                    fragmentX = new AttachmentFragment();//文件
                }
                fragmentX.setArguments(bundle);
                fragmentX.setCallback(i, this);
                callbacks.add(fragmentX);
                fragmentXes.add(fragmentX);
            }
            tabs.setTextSize(app.spTopx(13));//tab文字大小
            tabs.setDividerColor(getResources().getColor(R.color.white));//间隔条的颜色
            adapter = new MyPagerAdapter(getSupportFragmentManager());
            pager.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
            if (!callbacks.isEmpty()) {
                for (OnProjectChangeCallback changeCallback : callbacks) {
                    changeCallback.onProjectChange(project.status);
                }
            }
        }
        int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        tabs.setViewPager(pager);

        User creator = project.creator;
        tv_project_title.setText(project.title);
        tv_project_extra.setText("负责人：" + managersPersion(project.managers));

        if (project.status == 1) {
            tv_status.setBackgroundResource(R.drawable.common_lable_purple);
            tv_status.setText("进行中");
        } else {
            tv_status.setBackgroundResource(R.drawable.common_lable_green);
            tv_status.setText("已结束");

        }
        //是否显示三个点的功能键
        if (!project.isCreator() && !project.isManager()) {
            img_title_right.setVisibility(View.GONE);
        } else if (project.status == 2) {
            if (project.isCreator()) {
                img_title_right.setVisibility(View.VISIBLE);
            } else {
                img_title_right.setVisibility(View.GONE);
            }
        }
        ll_loading.setStatus(LoadingLayout.Success);
    }

    /**
     * 负责人的名字
     *
     * @param managers
     * @return
     */
    private String managersPersion(ArrayList<HttpProject.ProjectManaer> managers) {
        String manaderName = "";
        for (HttpProject.ProjectManaer ele : managers) {
            manaderName += ele.user.name + ",";
        }
        return manaderName.substring(0, manaderName.length() - 1);
    }

    /**
     * 【数据加载成功】的 回调
     *
     * @param id
     * @param size
     */
    @Override
    public void onLoadSuccess(final int id, final int size) {
        int idexS = TITLES[id].indexOf("(");
        int idexE = TITLES[id].lastIndexOf(")");
        String c = TITLES[id].substring(idexS + 1, idexE);
        if (!TextUtils.equals(c, "" + size)) {
            TITLES[id] = TITLES[id].replace(c, size + "");
        }
        adapter.notifyDataSetChanged();
        tabs.notifyDataSetChanged();
    }
    /**
     * 项目删除
     */
    public void deleteProject() {

        ProjectService.deleteProject(project.getId()).subscribe(new DefaultLoyoSubscriber<Project>() {
            @Override
            public void onNext(Project project) {
                Intent intent = new Intent();
                intent.putExtra("delete", project);
                app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_RIGHT, 0x09, intent);
            }
        });

    }

    /**
     * 项目重启/删除
     */
    public void restartProject() {
        showLoading2("");
        ProjectService.UpdateStatus(project.getId(),project.status == 1 ? 2 : 1)
                .subscribe(new DefaultLoyoSubscriber<Project>(hud) {
            @Override
            public void onNext(Project project) {
                project.status = (project.status == 1 ? 0 : 1);
                getProject();
                isUpdate = true;
            }
        });
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(final FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(final int position) {
            return fragmentXes.isEmpty() ? null : fragmentXes.get(position);
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case TasksInfoActivity.REQUEST_SCORE:
                //选择编辑回调
            case TasksInfoActivity.REQUEST_EDIT:
                getProject();
                break;

        }
    }

    public interface OnProjectChangeCallback {
        void onProjectChange(int status);
    }
}
