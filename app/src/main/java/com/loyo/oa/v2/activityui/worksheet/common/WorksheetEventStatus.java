package com.loyo.oa.v2.activityui.worksheet.common;

import com.loyo.oa.v2.R;

/**
 * Created by EthanGong on 16/8/27.
 */
public enum WorksheetEventStatus  implements GroupKey {
    /** 待处理 */
    WAITPROCESS(1){
        public String getName() { return "待处理"; }
        public int getColor() { return R.color.text66; }
        public int getIcon() { return R.drawable.bg_view_red_circle; }
    },

    /** 未触发 */
    UNACTIVATED(2){
        public String getName() { return "未触发"; }
        public int getColor() { return R.color.text66; }
        public int getIcon() { return R.drawable.bg_view_red_circle; }
    },

    /** 已处理 */
    FINISHED(3){
        public String getName() { return "已处理"; }
        public int getColor() { return R.color.text66; }
        public int getIcon() { return R.drawable.bg_view_red_circle; }
    },

    /** 意外终止 */
    TEMINATED(4){
        public String getName() { return "意外终止"; }
        public int getColor() { return R.color.text66; }
        public int getIcon() { return R.drawable.bg_view_red_circle; }
    };

    private final int code;
    private WorksheetEventStatus(int code) {
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
