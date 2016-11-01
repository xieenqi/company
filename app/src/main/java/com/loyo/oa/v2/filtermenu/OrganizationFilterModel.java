package com.loyo.oa.v2.filtermenu;

import android.support.annotation.NonNull;

import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuListType;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.db.bean.DBUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by EthanGong on 2016/10/31.
 */

public class OrganizationFilterModel extends FilterModel {

    public static DBDepartment selfDepartment() {
        return new SelfDepartment();
    }

    private List<DBDepartment> departments = new ArrayList<>();
    public OrganizationFilterModel(List<DBDepartment> depts, @NonNull String defaultTitle) {
        super(new ArrayList<MenuModel>(), defaultTitle, MenuListType.ORGANIZATION);
        departments.addAll(depts);
        if (depts.size() >0) {
            defaultSelectedIndex = 0;
        }
        else  {
            defaultSelectedIndex = -1;
        }
    }

    public String getDefaultTitle() {
        return defaultTitle;
    }

    public void setDefaultTitle(String defaultTitle) {
        this.defaultTitle = defaultTitle;
    }

    public MenuListType getType() {
        return type;
    }

    public void setSelectedIndexes(List<Integer> indexes) {
        selectedIndexes.clear();
        selectedIndexes.addAll(indexes);
    }

    public List<Integer> getSelectedIndexes() {
        return new ArrayList<>();
    }

    public List<MenuModel> getSelectedModels() {
        List<MenuModel> result = new ArrayList<>();
        return result;
    }

    @Override
    public MenuModel getChildrenAtIndex(int index) {
        if (index < 0 || index >= departments.size()) {
            return null;
        }

        DBDepartment dept = departments.get(index);
        return DepartmentMenuModel.getMenuModel(dept);
    }

    @Override
    public int getChildrenCount() {
        return departments.size();
    }

    static public class DepartmentMenuModel implements com.loyo.oa.dropdownmenu.model.MenuModel {

        private static HashMap<String, DepartmentMenuModel> reuseCache = new HashMap<>();


        public static void clearResueCache() {
            reuseCache.clear();
        }

        public static DepartmentMenuModel getMenuModel(DBDepartment dept) {
            if (dept==null || dept.id == null) {
                return null;
            }

            DepartmentMenuModel result = reuseCache.get(dept.id);

            if (result == null) {
                result = new DepartmentMenuModel(dept);
                reuseCache.put(dept.id, result);
            }

            return result;
        }

        public boolean isDepartment = true;
        public boolean isUser = false;
        public boolean hideDepartment = false;
        public DBDepartment dept;
        public List<DBUser> users = new ArrayList<>();
        private boolean isSelected;
        private DepartmentMenuModel(@NonNull DBDepartment dept) {
            this.dept = dept;
            this.users.addAll(dept.allUsersSortedByPinyin());
            hideDepartment = dept.getClass().equals(SelfDepartment.class);
        }

        @Override
        public String getKey() {
            return dept.xpath;
        }

        @Override
        public String getValue() {
            return dept.name;
        }

        @Override
        public boolean getSelected() {
            return isSelected;
        }

        @Override
        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        @Override
        public List<MenuModel> getChildren() {
            return null;
        }

        @Override
        public MenuModel getChildrenAtIndex(int index) {

            if (hideDepartment) {
                DBUser user = this.users.get(index);
                return UserMenuModel.getMenuModel(user);
            }

            if (index == 0) {
                return this;
            }

            DBUser user = this.users.get(index - 1);
            return UserMenuModel.getMenuModel(user);
        }

        @Override
        public int getChildrenCount() {

            int size = this.users.size();
            if (hideDepartment) {
                return size;
            }

            return size > 0?size+1:0;
        }
    }


    static public class UserMenuModel implements MenuModel {

        private static HashMap<String, UserMenuModel> reuseCache = new HashMap<>();

        public static void clearResueCache() {
            reuseCache.clear();
        }

        public static UserMenuModel getMenuModel(DBUser user) {
            if (user==null || user.id == null) {
                return null;
            }

            UserMenuModel result = reuseCache.get(user.id);

            if (result == null) {
                result = new UserMenuModel(user);
                reuseCache.put(user.id, result);
            }

            return result;
        }

        public boolean isDepartment = false;
        public boolean isUser = true;
        public DBUser user;
        private boolean isSelected;
        private UserMenuModel(@NonNull DBUser user) {
            this.user = user;
        }

        @Override
        public String getKey() {
            return user.id;
        }

        @Override
        public String getValue() {
            return user.name;
        }

        @Override
        public boolean getSelected() {
            return isSelected;
        }

        @Override
        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        @Override
        public List<MenuModel> getChildren() {
            return null;
        }

        @Override
        public MenuModel getChildrenAtIndex(int index) {
            return null;
        }

        @Override
        public int getChildrenCount() {
            return 0;
        }
    }

    static public class SelfDepartment extends DBDepartment {
        public SelfDepartment() {
            this.id = "我";
            this.xpath = "我";
            this.name = "我";
        }

        public List<DBUser> allUsers() {
            List<DBUser> result = new ArrayList<>();
            if (MainApp.user!= null || MainApp.user.id != null) {
                DBUser user = OrganizationManager.shareManager().getUser(MainApp.user.id);
                if (user != null) {
                    result.add(user);
                }
            }
            return result;
        }
    }
}
