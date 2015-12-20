package com.loyo.oa.v2.activity;

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

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.tasks.TasksInfoActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.fragment.AttachmentFragment;
import com.loyo.oa.v2.fragment.DiscussionFragment;
import com.loyo.oa.v2.point.IProject;
import com.loyo.oa.v2.tool.BaseChildMainListFragmentX;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.OnLoadSuccessCallback;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.customview.PagerSlidingTabStrip;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Date;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * com.loyo.oa.v2.activity
 * 描述 :项目详情页
 * 作者 : ykb
 * 时间 : 15/9/8.
 */
@EActivity(R.layout.activity_project_info)
public class ProjectInfoActivity extends BaseFragmentActivity implements OnLoadSuccessCallback {
    private String[] TITLES = {"任务", "报告", "审批", "文件", "讨论"};

    @ViewById ViewGroup img_title_left;
    @ViewById ViewGroup img_title_right;
    @ViewById TextView tv_title_1;

    @ViewById PagerSlidingTabStrip tabs;
    @ViewById ViewPager pager;
    @ViewById ViewGroup layout_project_des;
    @ViewById TextView tv_project_title;
    @ViewById TextView tv_project_extra;
    @ViewById ImageView img_project_status;

    @Extra Project project;

    MyPagerAdapter adapter;
    private ArrayList<BaseFragment> fragmentXes = new ArrayList<>();

    private ArrayList<OnProjectChangeCallback> callbacks = new ArrayList<>();

    /**
     * 获取项目
     */
    private void getProject() {
        app.getRestAdapter().create(IProject.class).getProjectById(project.getId(), new RCallback<Project>() {
            @Override
            public void success(Project _project, Response response) {
                _project.totalAttachment=project.totalAttachment;
                _project.totalDiscussion=project.totalDiscussion;
                _project.totalWfinstance=project.totalWfinstance;
                _project.totalWorkReport=project.totalWorkReport;
                _project.totalTask=project.totalTask;

                project = _project;
                img_title_right.setEnabled(true);
                initData();
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                Global.Toast("获取项目失败");
            }
        });
    }

    @AfterViews
    void initViews() {
        setTouchView(-1);
        img_title_right.setEnabled(false);
        tv_title_1.setText("项目详情");
        getProject();
    }

    @Click({R.id.img_title_left, R.id.img_title_right, R.id.layout_project_des})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.img_title_right:
                if (null == project) {
                    Global.Toast("项目为空！");
                    return;
                }
                Intent intent = new Intent(mContext, SelectEditDeleteActivity.class);

                if (project.status == Project.STATUS_PROCESSING) {
                    if (project.isCreator()) {
                        intent.putExtra("delete", true);
                        intent.putExtra("edit", true);
                        intent.putExtra("extra", "结束项目"); //1:进行中
                    } else if (project.isManager()) {
                        intent.putExtra("edit", true);
                    }
                } else {
                    if (project.isCreator()) {
                        intent.putExtra("delete", true);
                        intent.putExtra("extra", "重启项目"); //0:关闭
                    }
                }

                startActivityForResult(intent, TasksInfoActivity.REQUEST_EDIT_DELETE);
                break;
            case R.id.layout_project_des:
                Bundle b = new Bundle();
                b.putSerializable("project", project);
                app.startActivity(this, ProjectDescriptionActivity_.class, MainApp.ENTER_TYPE_BUTTOM, false, b);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("review", project);
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (null == project) {
            return;
        }
        if (adapter == null) {
            int[] sizes = new int[]{project.totalTask, project.totalWorkReport, project.totalWfinstance, project.totalAttachment, project.totalDiscussion};
            for (int i = 0; i < TITLES.length; i++) {
                TITLES[i]+="("+sizes[i]+")";
                app.logUtil.e(" size : "+sizes[i]);
                Bundle b = new Bundle();
                b.putSerializable("project", project);
                BaseFragment fragmentX = null;
                if (i == 0) {
                    b.putInt("type", 2);
                } else if (i == 1) {
                    b.putInt("type", 1);
                } else if (i == 2) {
                    b.putInt("type", 12);
                }
                if (i <= 2) {
                    fragmentX = new BaseChildMainListFragmentX();
                } else if (i == TITLES.length - 1) {
                    fragmentX = new DiscussionFragment();
                } else {
                    fragmentX = new AttachmentFragment();
                }
                fragmentX.setArguments(b);
                fragmentX.setCallback(i, this);
                callbacks.add(fragmentX);
                fragmentXes.add(fragmentX);
            }


            tabs.setTextSize(app.spTopx(18));
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
        tv_project_extra.setText(creator.getRealname() + " " + app.df2.format(new Date(project.getCreatedAt())) + " 发布");

        if (project.status == 1) {
            img_project_status.setImageResource(R.drawable.icon_project_processing);
        } else {
            img_project_status.setImageResource(R.drawable.icon_project_completed);
        }
    }

    @Override
    public void onLoadSuccess(int id, int size) {
        app.logUtil.e("id : " + id + " size : " + size);
        int idexS = TITLES[id].indexOf("(");
        int idexE = TITLES[id].lastIndexOf(")");
        String c = TITLES[id].substring(idexS + 1, idexE);
        if (!TextUtils.equals(c, "" + size)) {
            TITLES[id] = TITLES[id].replace(c, size + "");
        }
        adapter.notifyDataSetChanged();
        tabs.notifyDataSetChanged();
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentXes.isEmpty() ? null : fragmentXes.get(position);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case TasksInfoActivity.REQUEST_SCORE:
            case TasksInfoActivity.REQUEST_EDIT:
                getProject();
                break;
            case TasksInfoActivity.REQUEST_EDIT_DELETE:
                if (data.getBooleanExtra("edit", false)) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("project", project);
                    app.startActivityForResult(this, ProjectAddActivity_.class, MainApp.ENTER_TYPE_RIGHT,
                            TasksInfoActivity.REQUEST_EDIT, bundle);
                } else if (data.getBooleanExtra("delete", false)) {
                    //删除
                    app.getRestAdapter().create(IProject.class).deleteProject(project.getId(), new RCallback<Project>() {
                        @Override
                        public void success(Project o, Response response) {
                            Intent intent = new Intent();
                            intent.putExtra("delete", project);
                            app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_RIGHT, RESULT_OK, intent);
                        }
                        @Override
                        public void failure(RetrofitError error) {
                            HttpErrorCheck.checkError(error);
                        }
                    });
                } else if (data.getBooleanExtra("extra", false)) {
                    //结束任务或重启任务
                    app.getRestAdapter().create(IProject.class).UpdateStatus(project.getId(),project.status == 1 ? 2 : 1, new RCallback<Project>() {
                                @Override
                                public void success(Project o, Response response) {
                                    LogUtil.d(" 结束 和 编辑项目： "+MainApp.gson.toJson(o));
                                    project.status=(project.status == 1 ? 0 : 1);
                                    initViews();
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    HttpErrorCheck.checkError(error);
                                    Global.ToastLong("有任务未结束,不能结束项目!");
                                }
                            });
                }

                break;
        }
    }

    public interface OnProjectChangeCallback {
        void onProjectChange(int status);
    }
}
