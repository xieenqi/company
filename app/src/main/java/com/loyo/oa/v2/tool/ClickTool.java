package com.loyo.oa.v2.tool;

public class ClickTool {
    private static long lastClickTime = 0;

    protected ClickTool() {
        throw new UnsupportedOperationException(); // 防止子类调用
    }

    public static boolean isDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
