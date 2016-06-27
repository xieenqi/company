package com.loyo.oa.v2.ui.customview.DropListMenu;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by pj on 15/10/5.
 */
public interface OnDropItemSelectedListener {
    void onSelected(View listview, int ColumnIndex, SparseArray<DropItem> items);

    void onCancelAll(int ColumnIndex);

}
