package com.loyo.oa.v2.activityui.worksheet.common;

import com.loyo.oa.v2.R;

/**
 * Created by EthanGong on 16/8/27.
 */
public enum WorksheetStatus implements GroupKey{

    /** 待分派 */
    WAITASSIGN(1){
        public String getName() { return "待分派"; }
        public int getColor() { return R.color.text66; }
    },

    /** 进行中 */
    INPROGRESS(2){
        public String getName() { return "进行中"; }
        public int getColor() { return R.color.text66; }
    },

    /** 待审核 */
    WAITAPPROVE(3){
        public String getName() { return "待审核"; }
        public int getColor() { return R.color.text66; }
    },

    /** 已完成 */
    FINISHED(4){
        public String getName() { return "已完成"; }
        public int getColor() { return R.color.text66; }
    },

    /** 意外终止 */
    TEMINATED(5){
        public String getName() { return "意外终止"; }
        public int getColor() { return R.color.text66; }
    };

    private final int code;
    private WorksheetStatus(int code) {
        this.code = code;
    }


    /** 获取显示内容 */
    public abstract String getName();

    /** 获取显示颜色*/
    public abstract int getColor();

    /** 获取排序权值 */
    public int compareWeight()
    {
        return this.code;
    }
}