package com.loyo.oa.hud.toast.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.TypedValue;

import com.loyo.oa.v2.R;

/**
 * Created by Onur.Ciner on 21.12.2016.
 */

public class UtilsLibrary {

    public static int dpToPixels(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static Drawable createButtonBackgroundDrawable(@NonNull Context context, int fillColor) {
        int buttonCornerRadius = dpToPixels(context, 2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TypedValue v = new TypedValue();
            boolean hasAttribute = context.getTheme().resolveAttribute(R.attr.colorControlHighlight, v, true);
            int rippleColor = hasAttribute ? v.data : Color.parseColor("#88CCCCCC");
            return createButtonBackgroundDrawableLollipop(fillColor, rippleColor, buttonCornerRadius);
        }
        return createButtonBackgroundDrawableBase(fillColor, buttonCornerRadius);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Drawable createButtonBackgroundDrawableLollipop(int fillColor, int rippleColor,int cornerRadius) {
        Drawable d = createButtonBackgroundDrawableBase(fillColor, cornerRadius);
        return new RippleDrawable(ColorStateList.valueOf(rippleColor), d, null);
    }

    private static Drawable createButtonBackgroundDrawableBase(int color, int cornerRadius) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.RECTANGLE);
        d.setCornerRadius(cornerRadius);
        d.setColor(color);
        return d;
    }


}