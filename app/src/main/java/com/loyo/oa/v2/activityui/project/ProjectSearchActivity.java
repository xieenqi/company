package com.loyo.oa.v2.activityui.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.activityui.project.api.ProjectService;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseSearchActivity;
import com.loyo.oa.v2.tool.Utils;

import java.util.HashMap;

import retrofit.Callback;

/**
 * com.loyo.oa.v2.activity
 * 描述 :项目搜索
 * 作者 : ykb
 * 时间 : 15/10/14.
 */
public class ProjectSearchActivity extends BaseSearchActivity<Project> {
    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = getIntent().getExtras();
    }

    @Override
    public void getData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", strSearch);
        //项目 默认搜索全部 选择项目只能是进行中 全部(0) 进行(1) 完成(2)
        params.put("status", mBundle.getInt(ExtraAndResult.EXTRA_STATUS, -1) == -1 ? 0 : mBundle.getInt(ExtraAndResult.EXTRA_STATUS, -1));
        params.put("type", 0);
        params.put("endAt", System.currentTimeMillis() / 1000);
        params.put("startAt", DateTool.getDateStamp("2014-01-01") / 1000);
        params.put("pageIndex", paginationX.getShouldLoadPageIndex());
        params.put("pageSize", paginationX.getPageSize());

        ProjectService.getProjects(params).subscribe(new DefaultLoyoSubscriber<PaginationX<Project>>(ll_loading) {
            @Override
            public void onNext(PaginationX<Project> projectPaginationX) {
                ProjectSearchActivity.this.success(projectPaginationX);
            }

            @Override
            public void onError(Throwable e) {
                ProjectSearchActivity.this.fail(e);
            }
        });

    }

    @Override
    public void onListItemClick(View view, int position) {
        Intent mIntent = new Intent(getApplicationContext(), ProjectInfoActivity_.class);
        mIntent.putExtra("projectId", paginationX.getRecords().get(position).getId());
        startActivity(mIntent);
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
