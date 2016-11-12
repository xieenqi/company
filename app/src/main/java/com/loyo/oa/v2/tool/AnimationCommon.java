package com.loyo.oa.v2.tool;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by loyo_dev1 on 16/11/11.
 */

public class AnimationCommon {


    /*fromXDelta
      toXDelta
      fromYDelta
      toYDelta*/

    /**
     * 右侧滑入
     * */
    public static Animation inFromRightAnimation(int time) {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(time);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    /**
     * 左侧滑入
     * */
    public static Animation inFromLeftAnimation(int time) {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(time);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }

    /**
     * 下方滑入
     * */
    public static Animation inFromBottomAnimation(int time) {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(time);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }
}
