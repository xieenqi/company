package com.loyo.oa.upload;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.loyo.oa.upload.alioss.AliOSSManager;
import com.loyo.oa.upload.view.ImageCell;
import com.loyo.oa.upload.view.ImageUploadGridView;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by EthanGong on 16/10/9.
 */

public class UploadController implements ImageCell.ImageCellCallback{

    private ArrayList<UploadTask> taskList = new ArrayList<UploadTask>();
    private Activity context;
    public UploadImageAdapter adapter;
    private ImageUploadGridView gridView;
    private WeakReference<UploadControllerCallback> observer;

    public int maxSize;


    public UploadController(Activity context, int maxSize){
        this.context = context;
        this.adapter = new UploadImageAdapter(context, taskList, maxSize);
        this.adapter.callback = this;
    }


    public UploadTask uploadFile(String filePath, String UUID)
    {
        UploadTask task  = new UploadTask(filePath, UUID);
        taskList.add(task);
        executeTask(task);
        return task;
    }

    public void addUploadTask(String filePath, String UUID) {
        UploadTask task  = new UploadTask(filePath, UUID);
        taskList.add(task);
    }

    public void addUploadTask(String originPath, String filePath, String UUID) {
        UploadTask task  = new UploadTask(originPath, filePath, UUID);
        taskList.add(task);
    }

    public void removeTaskAt(int index) {
        if (index >=0 && index < taskList.size()) {
            taskList.remove(index);
        }
    }

    public void startUpload() {
        for (int i = 0; i < taskList.size(); i++) {
            UploadTask task = taskList.get(i);
            if (task.status == UploadTask.WAITING) {
                executeTask(task);
            }
        }
    }

    public void retry() {
        for (int i = 0; i < taskList.size(); i++) {
            UploadTask task = taskList.get(i);
            if (task.status == UploadTask.FAILED || task.status == UploadTask.CANCEL) {
                task.status = UploadTask.WAITING;
                task.progress = 1;
            }
            if (task.status == UploadTask.WAITING) {
                executeTask(task);
            }
        }
    }

    public void notifyCompletionIfNeeded() {
        if (isTaskListCompleted()) {
            onAllUploadTasksComplete(taskList);
        }
    }

    public int count() {
        return taskList.size();
    }

    public ArrayList<UploadTask> getTaskList() {
        return taskList;
    }

    public void loadView(ImageUploadGridView view) {
        gridView = view;
        if (gridView != null) {
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    UploadControllerCallback observer = getObserver();
                    if (null != observer) {
                        if (position == taskList.size()) {
                            observer.onAddEvent(UploadController.this);

                        }else {
                            observer.onItemSelected(UploadController.this, position);
                        }
                    }
                }
            });
        }
    }

    public void reloadGridView() {
        adapter.notifyDataSetChanged();
    }

    public boolean hasTask(String filePath, String UUID) {
        boolean result = false;
        UploadTask testTask = new UploadTask(filePath, UUID);
        for (int i = 0; i < taskList.size(); i++) {
            UploadTask task = taskList.get(i);
            if (task.isSameUploading(testTask)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private UploadControllerCallback getObserver() {
        if (observer != null) {
            return observer.get();
        }
        return null;
    }

    public void setObserver(UploadControllerCallback callback) {
        observer = new WeakReference<UploadControllerCallback>(callback);
    }

    private void onUploadTaskProgress(final UploadTask uploadTask,final double progress) {
        uploadTask.status = UploadTask.UPLOADING;
        uploadTask.progress = progress;

        if(gridView == null) {
            return;
        }

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int index = taskList.indexOf(uploadTask);
                if (index == -1) {
                    return;
                }

                int visible = gridView.getFirstVisiblePosition();
                View child = gridView.getChildAt(index - visible);
                if (child != null && child.getClass() == ImageCell.class) {
                    ImageCell cell = (ImageCell)child;
                    int p = (int)(progress*100);
                    if (p >99) {
                        p = 99;
                    }
                    cell.setProgress(p);
                }
            }
        });
    }

    private void onUploadTaskSuccess(final UploadTask uploadTask, final double progress) {
        uploadTask.status = UploadTask.UPLOADED;
        uploadTask.progress = progress;
        ArrayList<UploadTask> list = taskList;
        if (isTaskListCompleted()) {
            onAllUploadTasksComplete(taskList);
        }

        if(gridView == null) {
            return;
        }

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int index = taskList.indexOf(uploadTask);
                if (index == -1) {
                    return;
                }

                int visible = gridView.getFirstVisiblePosition();
                View child = gridView.getChildAt(index - visible);
                if (child != null && child.getClass() == ImageCell.class) {
                    ImageCell cell = (ImageCell)child;
                    int p = (int)(progress*100);
                    cell.setProgress(p);
                }
            }
        });
    }

    private void onUploadTaskFailed(final UploadTask uploadTask, final double progress) {
        uploadTask.status = UploadTask.FAILED;
        uploadTask.progress = progress;
        if (isTaskListCompleted()) {
            onAllUploadTasksComplete(taskList);
        }

        if(gridView == null) {
            return;
        }

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int index = taskList.indexOf(uploadTask);
                if (index == -1) {
                    return;
                }

                int visible = gridView.getFirstVisiblePosition();
                View child = gridView.getChildAt(index - visible);
                if (child != null && child.getClass() == ImageCell.class) {
                    ImageCell cell = (ImageCell)child;
                    int p = (int)(progress*100);
                    cell.setProgress(p);
                }
            }
        });
    }

    private void onAllUploadTasksComplete(final ArrayList<UploadTask> taskList) {
        final UploadControllerCallback observer = getObserver();
        if (null != observer) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    observer.onAllUploadTasksComplete(UploadController.this, taskList);
                }
            });
        }
    }

    public boolean isTaskListCompleted() {
        boolean result = true;
        for (int i = 0; i < taskList.size(); i++) {
            UploadTask task = taskList.get(i);
            if (UploadTask.UPLOADED != task.getStatus() && UploadTask.FAILED != task.getStatus()
                    && UploadTask.CANCEL != task.getStatus()) {
                result = false;
                break;
            }
        }
        return result;
    }

    public int failedTaskCount() {
        int result = 0;
        for (int i = 0; i < taskList.size(); i++) {
            UploadTask task = taskList.get(i);
            if ( UploadTask.FAILED == task.getStatus() || UploadTask.CANCEL == task.getStatus()) {
                result++;
            }
        }
        return result;
    }

    private void executeTask(final UploadTask uploadTask) {
        uploadTask.status = UploadTask.PROCESSING;
        Observable.just(uploadTask)
                .observeOn(Schedulers.io())
                .map(new Func1<UploadTask, UploadTask>() {
                    @Override
                    public UploadTask call(UploadTask task) {
                        process(task);
                        return task;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UploadTask>() {
                    @Override
                    public void call(UploadTask task) {
                        upload(task);
                    }
                });
    }

    private void process(final UploadTask uploadTask) {
        if (uploadTask.filePath != null) {
            return;
        }

        Uri uri = Uri.parse(uploadTask.originPath);
        try {
            File newFile = Global.scal(context, uri);
            if (newFile != null && newFile.length() > 0 && newFile.exists()) {
                uploadTask.setFilePath(newFile.getPath());
                uploadTask.size = newFile.length();
                uploadTask.name = newFile.getName();
            }
            else {
                uploadTask.setFilePath(uploadTask.originPath);
            }
        }
        catch (Exception e) {}
    }

    private void upload(final UploadTask uploadTask) {
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(Config_project.OSS_UPLOAD_BUCKETNAME(),
                uploadTask.getKey(), uploadTask.filePath);

        //异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long l, long l1) {
                onUploadTaskProgress(uploadTask, l*1.0/l1);
            }
        });

        OSSAsyncTask task = AliOSSManager.getInstance().getOss()
                .asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                    @Override
                    public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                        LogUtil.dee("UploadSuccess");

                        LogUtil.dee("ETag" + result.getETag());
                        LogUtil.dee("RequestId" + result.getRequestId());
                        onUploadTaskSuccess(uploadTask, 1.0);

                        Log.v("debug", "UploadSuccess");
                    }

                    @Override
                    public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {

                        Log.v("debug", "UploadFailed");
                        // 本地异常如网络异常等
                        onUploadTaskFailed(uploadTask, -1.0);
                        if (clientExcepion != null) {
                            clientExcepion.printStackTrace();
                        }

                        // 服务异常
                        if (serviceException != null) {
                            LogUtil.dee("ErrorCode" + serviceException.getErrorCode());
                            LogUtil.dee("RequestId" + serviceException.getRequestId());
                            LogUtil.dee("HostId" + serviceException.getHostId());
                            LogUtil.dee("RawMessage" + serviceException.getRawMessage());
                        }
                    }
                });
        uploadTask.status = UploadTask.QUEUED;
        uploadTask.taskRef = new WeakReference<OSSAsyncTask>(task);
    }

    @Override
    public void onRetry(final int index) {
        final UploadControllerCallback observer = getObserver();
        if (null != observer && index >=0 && index < taskList.size()) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    observer.onRetryEvent(UploadController.this, taskList.get(index));
                }
            });
        }
    }
}
