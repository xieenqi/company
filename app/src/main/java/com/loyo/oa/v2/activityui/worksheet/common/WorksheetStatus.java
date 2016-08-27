package com.loyo.oa.v2.activityui.worksheet.common;

import com.loyo.oa.v2.R;

/**
 * Created by EthanGong on 16/8/27.
 *
 * 工单状态
 * code : 状态对应数字
 * name : 状态显示文本
 * color: 状态显示颜色
 * icon : 状态icon
 *
 */
public enum WorksheetStatus implements GroupKey{

    /** 待分派 */
    WAITASSIGN(1){
        public String getName() { return "待分派"; }
        public int getColor() { return R.color.text66; }
        public int getIcon() { return R.drawable.bg_view_red_circle; }
    },

    /** 进行中 */
    INPROGRESS(2){
        public String getName() { return "进行中"; }
        public int getColor() { return R.color.text66; }
        public int getIcon() { return R.drawable.bg_view_red_circle; }
    },

    /** 待审核 */
    WAITAPPROVE(3){
        public String getName() { return "待审核"; }
        public int getColor() { return R.color.text66; }
        public int getIcon() { return R.drawable.bg_view_red_circle; }
    },

    /** 已完成 */
    FINISHED(4){
        public String getName() { return "已完成"; }
        public int getColor() { return R.color.text66; }
        public int getIcon() { return R.drawable.bg_view_red_circle; }
    },

    /** 意外终止 */
    TEMINATED(5){
        public String getName() { return "意外终止"; }
        public int getColor() { return R.color.text66; }
        public int getIcon() { return R.drawable.bg_view_red_circle; }
    };

    private final int code;
    private WorksheetStatus(int code) {
        this.code = code;
    }


    /** 获取显示内容 */
    public abstract String getName();

    /** 获取显示颜色*/
    public abstract int getColor();

    /** 获取显示图标*/
    public abstract int getIcon();

    /** 获取排序权值 */
    public int compareWeight()
    {
        return this.code;
    }
}