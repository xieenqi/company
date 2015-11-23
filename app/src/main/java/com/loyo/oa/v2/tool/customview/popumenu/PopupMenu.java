package com.loyo.oa.v2.tool.customview.popumenu;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.tool.popumenu
 * 描述 :弹出菜单
 * 作者 : ykb
 * 时间 : 15/11/4.
 */
public class PopupMenu {

    public interface OnPopupMenuItemClickListener {

        void onPopupMenuItemClick(int position, PopupMenuItem item);

        void onPopupMenuItemLongClick(int position, PopupMenuItem item);
    }

    public interface OnPopupMenuDismissListener {
        void onPopupMenuDismiss();
    }

    private Context mContext;
    private PopupWindow popupWindow;
    private int mMenuItemSize;
    private OnPopupMenuItemClickListener listener;
    private OnPopupMenuDismissListener dismissListener;
    private ArrayList<PopupMenuItem> menuItems = new ArrayList<>();
    private int backGroundResId;

    public PopupMenu(Context context) {
        mContext = context;
    }

    public void setDismissListener(OnPopupMenuDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    public void setBackGroundResId(int backGroundResId) {
        this.backGroundResId = backGroundResId;
    }

    public void setListener(OnPopupMenuItemClickListener listener) {
        this.listener = listener;
    }

    public void setMenuItemSize(int size) {
        mMenuItemSize = size;
    }

    public void setMenuItems(ArrayList<PopupMenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public void showAt(View view) {
        if (null == popupWindow) {
            View container = LayoutInflater.from(mContext).inflate(R.layout.layout_popu_menu, null, false);
            container.setBackgroundResource(backGroundResId);

            ListView lvPopupMenu = (ListView) container.findViewById(R.id.lv_popu_menu);
            final PopupMenuAdapter adapter = new PopupMenuAdapter(menuItems);
            lvPopupMenu.setAdapter(adapter);


            popupWindow = new PopupWindow(container, MainApp.getMainApp().getResources().getDisplayMetrics().widthPixels / 2, -2, true);
            popupWindow.setAnimationStyle(R.style.PopupAnimation);
            popupWindow.setBackgroundDrawable(new BitmapDrawable(mContext.getResources()));// 响应键盘三个主键的必须步骤
            popupWindow.showAsDropDown(view);

            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (null != dismissListener) {
                        dismissListener.onPopupMenuDismiss();
                    }
                }
            });

            lvPopupMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (null != listener) {
                        listener.onPopupMenuItemClick(i, adapter.getItem(i));
                    }
                    popupWindow.dismiss();
                }
            });
            lvPopupMenu.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (null != listener) {
                        listener.onPopupMenuItemLongClick(i, adapter.getItem(i));
                    }
                    popupWindow.dismiss();
                    return false;
                }
            });
        } else {
            popupWindow.showAsDropDown(view);
        }

    }
}
