package com.loyo.oa.contactpicker.callback;

import com.loyo.oa.contactpicker.model.PickDepartmentModel;
import com.loyo.oa.contactpicker.model.PickUserModel;

/**
 * Created by EthanGong on 2016/10/14.
 */

public interface OnPickUserEvent {
    void onAddUser(PickUserModel model);
    void onDeleteUser(PickUserModel model);
    void onAddAllUsers(PickDepartmentModel model);
    void onDeleteAllUsers(PickDepartmentModel model);
}
