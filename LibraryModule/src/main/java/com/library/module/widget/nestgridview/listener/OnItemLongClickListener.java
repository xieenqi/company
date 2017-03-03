package com.library.module.widget.nestgridview.listener;

import android.view.View;
import android.view.ViewGroup;

/**
 * 介绍：ViewGroup里 点击事件监听器
 */
public interface OnItemLongClickListener {
    boolean onItemLongClick(ViewGroup parent, View itemView, int position);
}