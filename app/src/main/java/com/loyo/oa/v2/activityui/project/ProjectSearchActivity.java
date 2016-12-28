package com.loyo.oa.v2.activityui.project;

import android.content.Intent;

import com.loyo.oa.v2.activityui.project.api.ProjectService;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseSearchActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.HashMap;

import retrofit.Callback;

/**
 * com.loyo.oa.v2.activity
 * 描述 :项目搜索
 * 作者 : ykb
 * 时间 : 15/10/14.
 */
public class ProjectSearchActivity extends BaseSearchActivity<Project> {

    @Override
    protected void openDetail(final int position) {
        Intent intent = new Intent();
        intent.setClass(mContext, ProjectInfoActivity_.class);
        intent.putExtra("project", adapter.getItem(position));
        startActivity(intent);
    }

    @Override
    public void getData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", strSearch);
        //项目 默认搜索全部 选择项目只能是进行中 全部(0) 进行(1) 完成(2)
        params.put("status", mBundle.getInt(ExtraAndResult.EXTRA_STATUS, -1) == -1 ? 0 : mBundle.getInt(ExtraAndResult.EXTRA_STATUS, -1));
        params.put("type", 0);
        params.put("endAt", System.currentTimeMillis() / 1000);
//        params.put("startAt", DateTool.getDateToTimestamp("2014-01-01", app.df5) / 1000);
        params.put("startAt", com.loyo.oa.common.utils.DateTool.getDateStamp("2014-01-01")/ 1000);
        params.put("pageIndex", paginationX.getPageIndex());
        params.put("pageSize", isTopAdd ? lstData.size() >= 20 ? lstData.size() : 20 : 20);

//        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IProject.class).getProjects(params, this);

        ProjectService.getProjects(params).subscribe(new DefaultLoyoSubscriber<PaginationX<Project>>(ll_loading) {
            @Override
            public void onNext(PaginationX<Project> projectPaginationX) {
                ((Callback)ProjectSearchActivity.this).success(projectPaginationX,null);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ((Callback)ProjectSearchActivity.this).failure(null);
            }
        });

    }
}
