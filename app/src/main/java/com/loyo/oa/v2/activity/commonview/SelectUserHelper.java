package com.loyo.oa.v2.activity.commonview;

import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by loyocloud on 16/3/29.
 */
public class SelectUserHelper {

    private List<SelectUserBase> mSelectDepartOrUserList = new ArrayList<>();
    private Map<String, Integer> mSelectDepartCounts = new HashMap<>();

    public List<SelectUserBase> getmSelectDepartOrUserList() {
        return mSelectDepartOrUserList;
    }

    public SelectUserBase get(int postition) {
        return mSelectDepartOrUserList.get(postition);
    }

    public void add(SelectUserBase userBase) {
        if (!userBase.isDepart()) {
            pushSelectCounts(userBase.getDepartId(), true);
        } else {
            removeById(userBase.getId());
        }
        mSelectDepartOrUserList.add(0, userBase);
    }

    /**
     * 移除id相等的
     *
     * @param id
     */
    public void removeById(String id) {
        for (int i = mSelectDepartOrUserList.size() - 1; i >= 0; i--) {
            if (!mSelectDepartOrUserList.get(i).isDepart() && mSelectDepartOrUserList.get(i).equalsId(id)) {
                mSelectDepartOrUserList.remove(i);
            }
        }
    }

    /**
     * 用于保存部门中被选中人的数量
     *
     * @param departId
     * @param isAdd
     */
    public void pushSelectCounts(String departId, boolean isAdd) {
        if (mSelectDepartCounts.containsKey(departId)) {
            int count = mSelectDepartCounts.get(departId);
            if (!isAdd && count == 1) {
                mSelectDepartCounts.remove(departId);
            } else {
                mSelectDepartCounts.put(departId, isAdd ? ++count : --count);
            }
        } else {
            if (isAdd) {
                mSelectDepartCounts.put(departId, 1);
            } else {
                LogUtil.d(" 选择数据错误：当前hashmap不存在该departId ");
            }
        }
    }

    /**
     * 用于保存部门中被选中人指定数量
     *
     * @param departId
     * @param count
     */
    public void pushDepartAllCounts(String departId, int count) {
        mSelectDepartCounts.put(departId, count);
    }

    public boolean isDepartAllSelect(int userSize, String departId) {
        int count = mSelectDepartCounts.get(departId);
        return userSize >= count;
    }


    public interface SelectUserBase {

        public static final String NULL_AVATAR = "NULL_AVATAR";

        int getUserCount(); // 获取用户部门人数

        boolean isDepart(); // 是否是部门

        String getId(); // 获取部门或用户id

        String getDepartId(); //获取部门id

        boolean equalsId(String id); //判断id是否相等

        String getAvater(); // 获取头像信息, 部门暂返回NULL_AVATAR
    }
}
