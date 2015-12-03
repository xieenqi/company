package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * com.loyo.oa.v2.beans
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/9/10.
 */
public class PagingGroupData_<T extends BaseBeans> implements Serializable {
    private String orderStr;
    private ArrayList<T> records;

    public PagingGroupData_() {
        records = new ArrayList<T>();
    }

    public String getOrderStr() {
        return orderStr;
    }

    public void setOrderStr(String orderStr) {
        this.orderStr = orderStr;
    }

    public ArrayList<T> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<T> records) {
        this.records = records;
    }

    public static <T extends BaseBeans> ArrayList<PagingGroupData_<T>> convertGroupData(ArrayList<T> records) {

        if (records == null || records.size() == 0) {
            return new ArrayList<>();
        }

        ArrayList<PagingGroupData_<T>> groupData = new ArrayList<>();
        //1.先排序
        Comparator<T> comparator = new Comparator<T>() {
            @Override
            public int compare(T lhs, T rhs) {

                int l = Integer.parseInt(lhs.getOrderStr2());
                int r = Integer.parseInt(rhs.getOrderStr2());

                if (r - l > 0) {
                    return -1;
                } else if (r - l < 0) {
                    return 1;
                }
                return 0;
            }
        };
        Collections.sort(records, comparator);

        for (T item : records) {
            String order = "";
            if (item instanceof Project) {
                Project p = (Project) item;
                switch (p.getStatus()) {
                    case 1:
                        order = "个项目进行中";
                        break;
                    case 0:
                        order = "个项目已结束";
                        break;
                }

            } else if (item instanceof Task) {
                Task task = (Task) item;
                switch (task.getStatus()) {
                    case 1:
                        order = "个任务进行中";
                        break;
                    case 2:
                        order = "个任务待审核";
                        break;
                    case 3:
                        order = "个任务已完成";
                        break;
                }
            } else if (item instanceof WfInstance) {
                WfInstance wfInstance = (WfInstance) item;
                switch (wfInstance.getStatus()) {
                    case WfInstance.STATUS_NEW:
                        order = "个待审批";
                        break;
                    case WfInstance.STATUS_PROCESSING:
                        order = "个审批中";
                        break;
                    case WfInstance.STATUS_ABORT:
                        order = "个审批未通过";
                        break;
                    case WfInstance.STATUS_APPROVED:
                        order = "个审批已通过";
                        break;
                    case WfInstance.STATUS_FINISHED:
                        order = "个审批已办结";
                        break;
                }

            } else if (item instanceof WorkReport) {
                WorkReport workReport = (WorkReport) item;
                if (workReport.isReviewed()) {
                    order = "个报告已点评";
                } else {
                    order = "个报告待点评";
                }
            }

            boolean isExist = false;
            for (PagingGroupData_ group : groupData) {
                if (group.getOrderStr().equals(order)) {
                    isExist = true;
                    group.getRecords().add(item);
                }
            }

            if (!isExist) {
                PagingGroupData_ data = new PagingGroupData_();
                data.setOrderStr(order);
                data.getRecords().add(item);
                groupData.add(data);
            }
        }
//        if (!groupData.isEmpty()) {
//            //1.先排序
//            Comparator<T> comparator1 = new Comparator<T>() {
//                @Override
//                public int compare(T lhs, T rhs) {
//
//                    long l = DateTool.getDateToTimestamp(lhs.getOrderStr(), app.df_api_get);
//                    long r = DateTool.getDateToTimestamp(rhs.getOrderStr(), app.df_api_get);
//
//                    if (r - l > 0) {
//                        return -1;
//                    } else if (r - l < 0) {
//                        return 1;
//                    }
//                    return 0;
//                }
//            };
//            for (int i = 0; i < groupData.size(); i++) {
//                if (null != groupData.get(i).getRecords() && !groupData.get(i).getRecords().isEmpty()) {
//                    Collections.sort(groupData.get(i).getRecords(), comparator1);
//                }
//            }
//
//        }

        return groupData;
    }
}
