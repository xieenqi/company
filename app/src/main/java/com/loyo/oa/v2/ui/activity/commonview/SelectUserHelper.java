package com.loyo.oa.v2.ui.activity.commonview;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.ui.activity.customer.bean.Department;
import com.loyo.oa.v2.ui.activity.other.bean.SelectDepData;
import com.loyo.oa.v2.ui.activity.other.bean.SelectUserData;
import com.loyo.oa.v2.ui.activity.other.bean.User;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.customview.RoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loyocloud on 16/3/29.
 */
public class SelectUserHelper {
//
//    public interface SelectDepartmentCallback extends Serializable {
//        /**
//         * 回调部门被选中
//         *
//         * @param department  被选中的部门
//         * @param oldSelected 部门的上一个状态
//         * @param isUserSelet 是否是单个用户选择触发
//         */
//        void onSelectDepartment(Department department, boolean oldSelected, boolean isUserSelet);
//    }
//
//    public interface SelectUserCallback extends Serializable {
//        /**
//         * 用户选择回调
//         *
//         * @param user //
//         * @param notify
//         */
//        void onSelectUser(User user, boolean notify);
////    }

    public interface SelectUserBase {

        public static final String NULL_AVATAR = "NULL_AVATAR";

        int getUserCount(); // 获取用户部门人数

        boolean isDepart(); // 是否是部门

        String getId(); // 获取部门或用户id

        String getDepartId(); //获取部门id

        boolean equalsId(String id); //判断id是否相等

        String getName();

        String getAvater(); // 获取头像信息, 部门暂返回NULL_AVATAR
    }

    /**
     * 选择的人员的显示
     */
    public static class SelectDataAdapter extends BaseAdapter {

        private final Context mContext;
        private List<SelectUserData> mSelectDatas = new ArrayList();
        private OnDataChangeCallback mDataChangeCallback;

        public SelectDataAdapter(Context context) {
            this.mContext = context;
        }

        public void updataList(List<SelectUserData> datas) {
            if (datas == null)
                datas = new ArrayList<>();
            this.mSelectDatas = datas;
            this.notifyDataSetChanged();
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (mDataChangeCallback != null) {
                mDataChangeCallback.onChange(getCount());
            }
        }

        public void setDataChangeCallback(OnDataChangeCallback mDataChangeCallback) {
            this.mDataChangeCallback = mDataChangeCallback;
        }

        @Override
        public int getCount() {
            return mSelectDatas.size();
        }

        @Override
        public SelectUserData getItem(final int position) {
            return mSelectDatas.get(position);
        }

        @Override
        public long getItemId(final int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_select_user, null);
                holder.head = (RoundImageView) convertView.findViewById(R.id.riv_head);
                holder.name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            SelectUserData base = getItem(position);
            if (base instanceof SelectDepData) {
                holder.name.setVisibility(View.VISIBLE);
                holder.head.setVisibility(View.INVISIBLE);
                String name = base.getName();
                if (TextUtils.isEmpty(name)) {
                    holder.name.setText("暂无");
                } else {
                    if (name.length() == 1 || name.length() == 2) {
                        holder.name.setText(name);
                    } else {
                        String str = name.substring(0, 2);
                        holder.name.setText(str);
                    }
                }
            } else {
                holder.name.setVisibility(View.INVISIBLE);
                holder.head.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(getItem(position).getAvatar(), holder.head);
            }
            //holder.name.setText(selectUserList.get(position).name);
            LogUtil.d("刷新的数九：" + mSelectDatas.size());
            return convertView;
        }

        class Holder {
            RoundImageView head;
            TextView name;
        }
    }

    public static final List<SelectDepData> mSelectDatas = new ArrayList<>(); // 组织架构数据
    public static final List<SelectUserData> mCurrentSelectDatas = new ArrayList<>(); // 多选时选中项列表
    public static final List<SelectDepData> mAllSelectDatas = new ArrayList<>();
    public static final ArrayList<User> useAlllist = new ArrayList<>();

    /**
     * 为选中列表添加数据
     *
     * @param data
     */
    public static boolean addSelectItem(SelectUserData data) {
//        if (!mCurrentSelectDatas.contains(data)) {
//            mCurrentSelectDatas.add(0, data);
//            return true;
//        }
        if (data.getClass() == SelectDepData.class) {
            if (mCurrentSelectDatas.contains(data)) {
                return false;
            } else {
                delAllSelectDep((SelectDepData) data);
            }
        } else {
            for (int i = 0; i < mCurrentSelectDatas.size(); i++) {
                SelectUserData d = mCurrentSelectDatas.get(i);
                if (d.getClass() == SelectDepData.class) {
                    if (((SelectDepData) d).getUsers().contains(data)) {
                        return false;
                    }
                } else {
                    if (d.equals(data)) {
                        return false;
                    }
                }
            }
        }
        mCurrentSelectDatas.add(0, data);
        return true;
    }

    /**
     * 为选中列表移除数据
     *
     * @param data
     */
    public static boolean removeSelectItem(SelectUserData data) {
        return mCurrentSelectDatas.remove(data);
    }

    /**
     * 清空选中列表
     */
    public static void clear() {
        mCurrentSelectDatas.clear();
    }

//    /**
//     * 删除传入部门之前, 所选中的该部门下属用户或子部门...并添加传入的部门
//     *
//     * @param data
//     */
//    public static boolean removeSelectItemByDepAndAdd(SelectDepData data) {
//        return delAllSelectDepAndAdd(data);
//    }

    /**
     * 删除子部门, 子用户数据, 如果列表中没有其对应的父部门, 添加该数据到列表中
     *
     * @param data
     * @return
     */
    public static boolean delAllSelectDepAndAdd(SelectDepData data) {
        addSelectItem(data);
        for (int i = 0; i < mCurrentSelectDatas.size(); i++) {
            SelectUserData userData = mCurrentSelectDatas.get(i);
            if (userData.getClass() == SelectDepData.class) {
                if (userData.getXpath().contains(data.getXpath())
                        && !data.getId().equals(userData.getId())) {
                    mCurrentSelectDatas.remove(userData);
                    i--;
                } else if (data.getXpath().contains(userData.getXpath())
                        && !data.getId().equals(userData.getId())) {
                    mCurrentSelectDatas.remove(data);
                    i--;
                }
            } else {
//                lable:
//                for (int j = 0; j < data.getUsers().size(); j++) {
//                    SelectUserData d = data.getUsers().get(j);
//                    if (TextUtils.isEmpty(d.getId())) {
//                        continue lable;
//                    }
//                    if (d.getId().equals(userData.getId())) {
//                        removeSelectItem(userData);
//                    }
//                }
                if (data.getUsers().contains(userData)) {
                    mCurrentSelectDatas.remove(userData);
                    i--;
                }
            }
        }
        return true;
    }

    /**
     * 移除选中的部门
     *
     * @param data
     * @return
     */
    public static boolean removeSelectItemAllDep(SelectDepData data) {
        return removeSelectItem(data);
    }

    /**
     * 点击单个用户触发操作
     *
     * @param data
     * @return
     */
    public static boolean addSelectUserChangeDep(SelectDepData data) {
        if (data.isSelect()) {
            return delAllSelectDepAndAdd(data);
        } else {
            addDepNoChangeItem(data);
            return true;
        }
    }

    /**
     * 更新部门的选择状态
     *
     * @param data
     */
    public static void addDepNoChangeItem(SelectDepData data) {
        removeSelectItem(data);
        for (int i = 0; i < mSelectDatas.size(); i++) {
            SelectDepData depData = mSelectDatas.get(i);
            if (depData.getXpath().contains(data.getXpath())) {
                if (depData.isSelect()) {
                    addSelectItem(depData);
                } else if (depData.mSelectCount == 0) {
                    delAllSelectDep(depData);
                } else {
                    for (int j = 0; j < depData.getUsers().size(); j++) {
                        if (depData.getUsers().get(j).isSelect()) {
                            addSelectItem(depData.getUsers().get(j));
                        } else {
                            if (removeSelectItem(depData.getUsers().get(j))) {
                                j--;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 仅删除相关的部门和用户不添加
     *
     * @param data
     */
    private static void delAllSelectDep(SelectDepData data) {
        for (int i = 0; i < mCurrentSelectDatas.size(); i++) {
            SelectUserData userData = mCurrentSelectDatas.get(i);
            if (userData instanceof SelectDepData) {
                if (userData.getXpath().contains(data.getXpath())) {
                    mCurrentSelectDatas.remove(userData);
                    i--;
                }
            } else {
                if (data.getUsers().contains(userData)) {
                    mCurrentSelectDatas.remove(userData);
                    i--;
                }
            }
        }
    }

    /**
     * 重构组织架构数据【重新封装】
     */
    public static class SelectThread extends Thread {
        public static final int OK = 0x00001;
        public static final int FAILURE = 0x00000;

        private final List<Department> mDepartmentDatas;
        private final List<SelectDepData> mDepSource = new ArrayList<>();
        private final List<SelectUserData> mUserSource = new ArrayList<>();
        private final Handler mHandler;

        public SelectThread(List<Department> datas, Handler handler) {
            this.mDepartmentDatas = datas;
            this.mHandler = handler;
        }

        @Override
        public void run() {
            mSelectDatas.clear();
            mDepSource.clear();
            mUserSource.clear();
            useAlllist.clear();
            try {
                if (mDepartmentDatas != null) {
                    bindAllUser(mDepartmentDatas.get(0));
                    for (int i = 1; i < mDepartmentDatas.size(); i++) {
                        SelectDepData data = newDepSource(mDepartmentDatas.get(i));
                        mDepSource.add(data);
                    }
                    for (int i = 1; i < mDepSource.size(); i++) {
                        bindUserInfos(mDepSource.get(i));
                    }
                    mSelectDatas.addAll(mDepSource);

                    getAllUsers();
                }
                mHandler.sendEmptyMessage(OK);
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(FAILURE);
            }
        }

        private void getAllUsers() {
            /*全部人员获取*/
            List<User> localCacheUserList = new ArrayList<>();
            for (int i = 0; i < mDepartmentDatas.size(); i++) {
                try {
                    for (int k = 0; k < mDepartmentDatas.get(i).getUsers().size(); k++) {
                        localCacheUserList.add(mDepartmentDatas.get(i).getUsers().get(k));
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            useAlllist.addAll(RemoveSame(localCacheUserList));
        }

        /**
         * 去掉人员重复数据
         */
        private List RemoveSame(final List<User> list) {
            for (int i = 0; i < list.size() - 1; i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    if (list.get(i).getId().equals(list.get(j).getId())) {
                        list.remove(j);
                        j--;
                    }
                }
            }
            return list;
        }

        /**
         * 获取所有用户信息, 默认取第一个部门
         *
         * @param department
         */
        private void bindAllUser(Department department) {
            SelectDepData userData = new SelectDepData();
            userData.setId(department.getId());
            userData.setName(department.getName());
            userData.setAvatar(department.getAvater());
            userData.setXpath(department.getXpath());

            mUserSource.addAll(newDepSourceAll());

            userData.setUsers(mUserSource);
            userData.startCallback(); //为部门中的所有用户添加回调
            mDepSource.add(0, userData);
        }

        /**
         * 更新所有用户的组织架构
         *
         * @param
         */
        private List<SelectUserData> newDepSourceAll() {
            List<SelectUserData> userDatas = new ArrayList<>();
            for (int i = 0; i < mDepartmentDatas.size(); i++) {
                Department dep = mDepartmentDatas.get(i);
                for (int j = 0; j < dep.getUsers().size(); j++) {
                    User user = dep.getUsers().get(j);
                    SelectUserData data = new SelectUserData();
                    data.setAvatar(user.getAvatar());
                    data.setName(user.getRealname());
                    data.setId(user.getId());
                    try {
                        data.setDeptName(user.depts.get(0).getShortDept().getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                        data.setDeptName("暂无");
                    }
                    try {
                        data.setDeptName(user.depts.get(0).getTitle());
                    } catch (Exception e) {
                        e.printStackTrace();
                        data.setDeptName("暂无");
                    }
                    userDatas.add(data);
                }
            }
            removeSameAll(userDatas);
            return userDatas;
        }

        /**
         * 组装部门的数据
         *
         * @param department
         * @return
         */
        private SelectDepData newDepSource(Department department) {
            SelectDepData userData = new SelectDepData();
            userData.setId(department.getId());
            userData.setName(department.getName());
            userData.setAvatar(department.getAvater());
            userData.setXpath(department.getXpath());

            List<SelectUserData> userDatas = new ArrayList<>();
            for (int i = 0; i < department.getUsers().size(); i++) {
                User user = department.getUsers().get(i);
                int index = -1;
                if ((index = getUserIndex(user)) > -1 && index < mUserSource.size()) {
                    userDatas.add(mUserSource.get(index));
                } else {
                    SelectUserData data = new SelectUserData();
                    data.setAvatar(user.getAvatar());
                    data.setName(user.getRealname());
                    data.setId(user.getId());
                    try {
                        data.setDeptName(user.depts.get(0).getShortDept().getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                        data.setDeptName("暂无");
                    }
                    try {
                        data.setDeptName(user.depts.get(0).getTitle());
                    } catch (Exception e) {
                        e.printStackTrace();
                        data.setDeptName("暂无");
                    }
                    userDatas.add(data);
                }
            }
            userData.setUsers(userDatas);
            return userData;
        }

        private int getUserIndex(User user) {
            String id = user.getId();
            if (TextUtils.isEmpty(id)) {
                return -1;
            }
            for (int i = 0; i < mUserSource.size(); i++) {
                if (id.equals(mUserSource.get(i).getId())) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 获取部门用户与其子部门的用户
         *
         * @param depData
         * @return
         */
        private void bindUserInfos(SelectDepData depData) {
            for (SelectDepData d : mDepSource) {
                if (d.getXpath().contains(depData.getXpath())) {
                    depData.getUsers().addAll(d.getUsers());
                }
            }
            removeSame(depData.getUsers());
            depData.startCallback();
        }

        /**
         * 去掉人员重复数据
         */
        private void removeSame(final List<SelectUserData> list) {
            for (int i = 0; i < list.size() - 1; i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    if (list.get(i).getId().equals(list.get(j).getId())) {
                        SelectUserData data = list.remove(j);
                        j--;
                    }
                }
            }
        }

        /**
         * 去掉人员重复数据
         */
        private void removeSameAll(final List<SelectUserData> list) {
            for (int i = 0; i < list.size() - 1; i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    if (list.get(i).getId().equals(list.get(j).getId())) {
                        SelectUserData data = list.remove(j);
                        j--;
                    }
                }
            }
        }
    }

    public interface OnDataChangeCallback {
        void onChange(int count);
    }

}
