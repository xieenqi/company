package com.loyo.oa.v2.activityui.commonview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;

import com.loyo.oa.v2.R;

/**
 * Created by xeq on 16/12/1.
 */

public class LoadingView {

    public static View getLoadingView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.loading_page, null);
        ((AnimationDrawable) view.findViewById(R.id.iv_loading).getBackground()).start();
        return view;
    }
}
