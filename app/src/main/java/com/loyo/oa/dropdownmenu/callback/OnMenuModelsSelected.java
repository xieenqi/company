package com.loyo.oa.dropdownmenu.callback;

import com.loyo.oa.dropdownmenu.model.MenuModel;

import java.util.List;

/**
 * Created by EthanGong on 2016/10/31.
 */

public interface OnMenuModelsSelected {
    void onMenuModelsSelected(int menuIndex, List<MenuModel> selectedModels, Object userInfo);
}
