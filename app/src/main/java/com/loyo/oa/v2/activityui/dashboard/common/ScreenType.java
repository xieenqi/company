package com.loyo.oa.v2.activityui.dashboard.common;

/**
 * Created by yyy on 16/12/29.
 */

public enum ScreenType {

    FOLLOWUP{
        @Override
        public String[] screenTitle() {
            return new String[]{"今天", "昨天", "本周", "上周", "本月", "上月", "取消"};
        }

        @Override
        public int type() {
            return 0;
        }
    },
    STOCK{
        @Override
        public String[] screenTitle() {
            return new String[]{"今天", "昨天", "本周", "上周", "本月", "上月", "取消"};
        }

        @Override
        public int type() {
            return 1;
        }
    },
    MONEY{
        @Override
        public String[] screenTitle() {
            return new String[]{"本月", "上月", "本季度", "上季度", "本年", "去年", "取消"};
        }

        @Override
        public int type() {
            return 2;
        }

    };

    public abstract String[] screenTitle();
    public abstract int type();


}
