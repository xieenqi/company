package com.loyo.oa.upload.explosion;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * https://github.com/Xieyupeng520/AZExplosion
 */
public class Utils {
    /**
     * 密度
     */
    public static final float DENSITY = Resources.getSystem().getDisplayMetrics().density;

    public static int dp2px(Context context, int dp) {
        return  (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics()) + 0.5F);
    }
}
