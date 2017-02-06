package com.loyo.oa.v2.activityui.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.activityui.project.api.ProjectService;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseSearchActivity;

import java.util.HashMap;

/**
 * com.loyo.oa.v2.activity
 * 描述 : 选择项目，这个主要是提供给其他模块调用，选择项目，eg，任务，审批的关联项目调用
 */
public class ProjectSearchOrPickerActivity extends BaseSearchActivity<Project> {

    public static final String EXTRA_PICKER_ID = "projectId";
    public static final String EXTRA_STATUS = "extra_status";

    private boolean jumpNewPage = false;
    private Class<?> cls;
    private boolean canBeEmpty = false;
    private int status=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent=getIntent();
        if (null != intent) {
            canBeEmpty = intent.getBooleanExtra(EXTRA_CAN_BE_EMPTY, false);
            jumpNewPage = intent.getBooleanExtra(EXTRA_JUMP_NEW_PAGE, false);
            cls = (Class<?>) intent.getSerializableExtra(EXTRA_JUMP_PAGE_CLASS);
            status = intent.getIntExtra(EXTRA_STATUS, 0);

        }
        super.onCreate(savedInstanceState);
        ll_loading.setStatus(LoadingLayout.Success);

    }

    @Override
    public void getData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", strSearch);
        //项目 默认搜索全部 选择项目只能是进行中 全部(0) 进行(1) 完成(2)
        params.put("status", status);
        params.put("type", 0);
        params.put("endAt", System.currentTimeMillis() / 1000);
        params.put("startAt", DateTool.getDateStamp("2014-01-01") / 1000);
        params.put("pageIndex", paginationX.getShouldLoadPageIndex());
        params.put("pageSize", paginationX.getPageSize());

        subscriber =ProjectService.getProjects(params).subscribe(new DefaultLoyoSubscriber<PaginationX<Project>>(ll_loading) {
            @Override
            public void onNext(PaginationX<Project> projectPaginationX) {
                ProjectSearchOrPickerActivity.this.success(projectPaginationX);
            }

            @Override
            public void onError(Throwable e) {
                ProjectSearchOrPickerActivity.this.fail(e);
            }
        });

    }



    @Override
    public boolean isShowHeadView() {
        return canBeEmpty;
    }

    @Override
    public void onListItemClick(View view, int position) {
        if (jumpNewPage) {
            Bundle b = new Bundle();
            b.putInt(ExtraAndResult.DYNAMIC_ADD_ACTION, ExtraAndResult.DYNAMIC_ADD_CUSTOMER);
            b.putSerializable(Project.class.getName(), paginationX.getRecords().get(position));
            b.putString(EXTRA_PICKER_ID, paginationX.getRecords().get(position).getId());
            MainApp.getMainApp().startActivity(this, cls, MainApp.ENTER_TYPE_RIGHT, false, b);
        } else {
            Intent intent = new Intent();
            intent.putExtra("data", paginationX.getRecords().get(position));
            app.finishActivity(ProjectSearchOrPickerActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
        }
    }

    @Override
    public void bindData(CommonSearchAdapter.SearchViewHolder viewHolder, Project data) {
        try {
            viewHolder.time.setText("提交时间: " + DateTool.getDateTimeFriendly(data.getCreatedAt() / 1000));
        } catch (Exception e) {
            Global.ProcException(e);
        }
        viewHolder.content.setText(data.content);
        viewHolder.ack.setVisibility(View.GONE);
        viewHolder.title.setText(data.title);
    }

}
