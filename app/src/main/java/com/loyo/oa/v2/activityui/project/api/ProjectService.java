package com.loyo.oa.v2.activityui.project.api;

import com.loyo.oa.v2.activityui.clue.api.IClue;
import com.loyo.oa.v2.activityui.project.HttpProject;
import com.loyo.oa.v2.activityui.project.ProjectAddActivity;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;

import java.util.HashMap;

import rx.Observable;

/**
 * 项目与服务器的接口
 * Created by jie on 16/12/21.
 */

public class ProjectService {

    public static Observable<PaginationX<Project>> getProjects(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL())
                        .create(IProject.class)
                        .getProjects(params)
                        .compose(RetrofitAdapterFactory.<PaginationX<Project>>compatApplySchedulers());
    }


    public static Observable<HttpProject> getProjectById(String id,  String key) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL())
                        .create(IProject.class)
                        .getProjectById(id,key)
                        .compose(RetrofitAdapterFactory.<HttpProject>compatApplySchedulers());
    }

    public static Observable<Project> Update(String id,ProjectAddActivity.ProjectTransObj body) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL())
                        .create(IProject.class)
                        .Update(id,body)
                        .compose(RetrofitAdapterFactory.<Project>compatApplySchedulers());
    }
    //1:工作报告, 2:任务, 12:快捷审批
    public static <T> Observable<PaginationX<T>> getProjectNewSubs(String id, int bizType,  HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL())
                        .create(IProject.class)
                        .<T>getProjectNewSubs(id,bizType,map)
                        .compose(RetrofitAdapterFactory.<PaginationX<T>>compatApplySchedulers());
    }

    public static Observable<Project> Create(ProjectAddActivity.ProjectTransObj body) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL())
                        .create(IProject.class)
                        .Create(body)
                        .compose(RetrofitAdapterFactory.<Project>compatApplySchedulers());
    }


    public static Observable<Project> deleteProject(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL())
                        .create(IProject.class)
                        .deleteProject(id)
                        .compose(RetrofitAdapterFactory.<Project>compatApplySchedulers());
    }

    public static Observable<Project> UpdateStatus(String id,int status) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL())
                        .create(IProject.class)
                        .UpdateStatus(id,status)
                        .compose(RetrofitAdapterFactory.<Project>compatApplySchedulers());
    }
}
