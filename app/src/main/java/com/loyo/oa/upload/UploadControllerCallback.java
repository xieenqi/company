package com.loyo.oa.upload;

import java.util.ArrayList;

/**
 * Created by EthanGong on 16/10/10.
 */

public interface UploadControllerCallback {
    void onRetryEvent(UploadController controller, UploadTask task);
    void onAddEvent(UploadController controller);
    void onItemSelected(UploadController controller, int index);
    void onAllUploadTasksComplete(UploadController controller, ArrayList<UploadTask> taskList);
}
