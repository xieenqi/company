package com.loyo.oa.v2.activityui.wfinstance.bean;

import com.loyo.oa.v2.beans.WfInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by xeq on 16/7/21.
 */
public class WfinstanceUitls {
    /**
     * 组装 【我提交的】审批item数据
     *
     * @param data
     * @return
     */
    public static ArrayList<WflnstanceItemData> convertGroupSubmitData(ArrayList<WflnstanceListItem> data) {
        if (data == null || data.size() == 0) {
            return new ArrayList<>();
        }
        ArrayList<WflnstanceItemData> groupData = new ArrayList<>();

        //1.先排序
        Comparator<WflnstanceListItem> comparator = new Comparator<WflnstanceListItem>() {
            @Override
            public int compare(WflnstanceListItem lhs, WflnstanceListItem rhs) {

                int l = Integer.parseInt("0");
                int r = Integer.parseInt("0");

                if (r - l > 0) {
                    return -1;
                } else if (r - l < 0) {
                    return 1;
                }
                return 0;
            }
        };
        Collections.sort(data, comparator);
        for (WflnstanceListItem ele : data) {
            String order = "";
            switch (ele.status) {
                case WfInstance.STATUS_NEW:
                    order = "待审批";
                    break;
                case WfInstance.STATUS_PROCESSING:
                    order = "审批中";
                    break;
                case WfInstance.STATUS_ABORT:
                    order = "未通过";
                    break;
                case WfInstance.STATUS_FINISHED:
                case WfInstance.STATUS_APPROVED:
                    order = "已通过";
                    break;

            }
            boolean isExist = false;
            for (WflnstanceItemData group : groupData) {
                if (group.orderStr.equals(order)) {
                    isExist = true;
                    group.records.add(ele);
                }
            }

            if (!isExist) {
                WflnstanceItemData data1 = new WflnstanceItemData();
                data1.orderStr = order;
                data1.records.add(ele);
                groupData.add(data1);
            }
        }
        return groupData;
    }

    /**
     * 组装 【我提交的】审批item数据
     *
     * @param data
     * @return
     */
    public static ArrayList<WflnstanceItemData> convertGroupApproveData(ArrayList<WflnstanceListItem> data) {
        if (data == null || data.size() == 0) {
            return new ArrayList<>();
        }
        ArrayList<WflnstanceItemData> groupData = new ArrayList<>();

        //1.先排序
        Comparator<WflnstanceListItem> comparator = new Comparator<WflnstanceListItem>() {
            @Override
            public int compare(WflnstanceListItem lhs, WflnstanceListItem rhs) {

                int l = Integer.parseInt("0");
                int r = Integer.parseInt("0");

                if (r - l > 0) {
                    return -1;
                } else if (r - l < 0) {
                    return 1;
                }
                return 0;
            }
        };
        Collections.sort(data, comparator);
        for (WflnstanceListItem ele : data) {
            String order = "";
            switch (ele.approveStatus) {
                case WfInstance.STATUS_NEW:
                    order = "待我审批的";
                    break;
                case WfInstance.STATUS_PROCESSING:
                    order = "我同意的";
                    break;
                case WfInstance.STATUS_ABORT:
                    order = "我驳回的";
                    break;
            }
            boolean isExist = false;
            for (WflnstanceItemData group : groupData) {
                if (group.orderStr.equals(order)) {
                    isExist = true;
                    group.records.add(ele);
                }
            }

            if (!isExist) {
                WflnstanceItemData data1 = new WflnstanceItemData();
                data1.orderStr = order;
                data1.records.add(ele);
                groupData.add(data1);
            }
        }
        return groupData;
    }
}
