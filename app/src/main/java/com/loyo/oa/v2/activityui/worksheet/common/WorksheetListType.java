package com.loyo.oa.v2.activityui.worksheet.common;

import com.loyo.oa.v2.R;

import java.io.Serializable;

/**
 * Created by EthanGong on 16/8/30.
 * <p/>
 * 工单列表的类型
 */
public enum WorksheetListType implements Serializable {
    /* 我创建的工单 */
    SELF_CREATED {
        public String getTitle() {
            return "我创建的";
        }
        public int getIcon() {
            return R.drawable.icon_my;
        }
    },

    /* 我分派的工单 */
    ASSIGNABLE {
        public String getTitle() {
            return "我分派的";
        }
        public int getIcon() {
            return R.drawable.icon_assignment;
        }
    },

    /* 我负责的工单 */
    RESPONSABLE {
        public String getTitle() {
            return "我负责的";
        }
        public int getIcon() {
            return R.drawable.icon_public;
        }
    },

    /* 团队的工单 */
    TEAM {
        public String getTitle() {
            return "团队工单";
        }
        public int getIcon() {
            return R.drawable.icon_team;
        }
    };


    public abstract String getTitle() ;
    public abstract int getIcon() ;
}
