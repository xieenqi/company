package com.loyo.oa.v2.task.api;

import com.loyo.oa.v2.activityui.tasks.bean.TaskTpl;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.beans.TaskCheckPoint;
import com.loyo.oa.v2.beans.TaskRecord;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;

import java.util.HashMap;

import rx.Observable;

/**
 * Created by EthanGong on 2016/12/22.
 */

public class TaskService {

    public static
    Observable<TaskCheckPoint> deleteChildTask(String id, String checkPointId) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(ICheckPoint.class)
                        .deleteChildTask(id, checkPointId)
                        .compose(RetrofitAdapterFactory.<TaskCheckPoint>compatApplySchedulers());
    }

    public static
    Observable<TaskCheckPoint> createChildTask(String id, HashMap<String, Object> body) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(ICheckPoint.class)
                        .createChildTask(id, body)
                        .compose(RetrofitAdapterFactory.<TaskCheckPoint>compatApplySchedulers());
    }

    public static
    Observable<TaskCheckPoint> updateChildTask(String id, String checkPointId, HashMap<String, Object> body) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(ICheckPoint.class)
                        .updateChildTask(id, checkPointId, body)
                        .compose(RetrofitAdapterFactory.<TaskCheckPoint>compatApplySchedulers());
    }

    public static
    Observable<TaskCheckPoint> updateChildTaskInfo(String id, String checkPointId, HashMap<String, Object> body) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(ICheckPoint.class)
                        .updateChildTaskInfo(id, checkPointId, body)
                        .compose(RetrofitAdapterFactory.<TaskCheckPoint>compatApplySchedulers());
    }

    public static
    Observable<TaskCheckPoint> deleteChildTaskInfo(String id, String checkPointId) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(ICheckPoint.class)
                        .deleteChildTaskInfo(id, checkPointId)
                        .compose(RetrofitAdapterFactory.<TaskCheckPoint>compatApplySchedulers());
    }

    public static
    Observable<PaginationX<TaskRecord>> getListData(HashMap<String, Object> body) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(ITask.class)
                        .getListData(body)
                        .compose(RetrofitAdapterFactory.<PaginationX<TaskRecord>>compatApplySchedulers());
    }

    public static
    Observable<PaginationX<TaskRecord>> getTasksData(HashMap<String, Object> body) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(ITask.class)
                        .getTasksData(body)
                        .compose(RetrofitAdapterFactory.<PaginationX<TaskRecord>>compatApplySchedulers());
    }

    public static
    Observable<TaskTpl> getTpl(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(ITask.class)
                        .getTpl(id)
                        .compose(RetrofitAdapterFactory.<TaskTpl>compatApplySchedulers());
    }

    public static
    Observable<TaskTpl> deleteTpl(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(ITask.class)
                        .deleteTpl(id)
                        .compose(RetrofitAdapterFactory.<TaskTpl>compatApplySchedulers());
    }

    public static
    Observable<Task> create(HashMap<String, Object> body) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(ITask.class)
                        .create(body)
                        .compose(RetrofitAdapterFactory.<Task>compatApplySchedulers());
    }

    public static
    Observable<Task> updateTask(String Id, HashMap<String, Object> body) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(ITask.class)
                        .updateTask(Id, body)
                        .compose(RetrofitAdapterFactory.<Task>compatApplySchedulers());
    }

    public static
    Observable<Task> updatesTask(String id, String cid, HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(ITask.class)
                        .updatesTask(id, cid, map)
                        .compose(RetrofitAdapterFactory.<Task>compatApplySchedulers());
    }

    public static
    Observable<Task> getTask(String Id, String key) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(ITask.class)
                        .getTask(Id, key)
                        .compose(RetrofitAdapterFactory.<Task>compatApplySchedulers());
    }

    public static
    Observable<Task> commitTask(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(ITask.class)
                        .commitTask(id)
                        .compose(RetrofitAdapterFactory.<Task>compatApplySchedulers());
    }

    public static
    Observable<Task> updateJoiner(String Id, HashMap<String, Object> body) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(ITask.class)
                        .updateJioner(Id, body)
                        .compose(RetrofitAdapterFactory.<Task>compatApplySchedulers());
    }

    public static
    Observable<Task> deleteTask(String Id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(ITask.class)
                        .deleteTask(Id)
                        .compose(RetrofitAdapterFactory.<Task>compatApplySchedulers());
    }

    public static
    Observable<Task> verifyTask(String id, HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL())
                        .create(ITask.class)
                        .verifyTask(id, map)
                        .compose(RetrofitAdapterFactory.<Task>compatApplySchedulers());
    }
}
