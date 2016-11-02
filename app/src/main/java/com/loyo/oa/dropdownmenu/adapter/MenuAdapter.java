package com.loyo.oa.dropdownmenu.adapter;

import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by EthanGong on 2016/10/31.
 */

public interface MenuAdapter {
    int getMenuCount();

    String getMenuTitle(int position);

    int getBottomMargin(int position);
    int getHeight(int position);

    View getView(int position, FrameLayout parentContainer);
}
