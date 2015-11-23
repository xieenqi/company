package com.loyo.oa.v2.tool.customview.popumenu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.ViewHolder;

/**
 * com.loyo.oa.v2.tool.customview.popumenu
 * 描述 :弹出窗条目
 * 作者 : ykb
 * 时间 : 15/11/4.
 */
public class PopupMenuItem {

    private ImageView menuIcon;
    private TextView menuTitle;
    private ImageView divider;
    private View convertView;

    public PopupMenuItem(Context context) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_popumenu, null, false);
        menuIcon= ViewHolder.get(convertView,R.id.iv_popu_menu_icon);
        menuTitle=ViewHolder.get(convertView,R.id.tv_popu_menu_title);
        divider=ViewHolder.get(convertView,R.id.iv_popu_menu_divider);
    }

    public View getConvertView() {
        return convertView;
    }

    public ImageView getDivider() {
        return divider;
    }

    public void setText(String title){
        menuTitle.setText(title);
    }

    protected TextView getMenuTitle() {
        return menuTitle;
    }

    protected ImageView getMenuIcon() {
        return menuIcon;
    }

    public void setResource(int resId) {
        menuIcon.setImageResource(resId);
    }

    public void setDrawable(Drawable drawable) {
        menuIcon.setImageDrawable(drawable);
    }

    public void setBitmap(Bitmap bitmap) {
        menuIcon.setImageBitmap(bitmap);
    }

    public void setDividerColor(int color) {
        divider.setBackgroundColor(color);
    }

    public void setTextColor(int color) {
        menuTitle.setTextColor(color);
    }

    public void setScalType(ImageView.ScaleType scaleType) {
        menuIcon.setScaleType(scaleType);
    }

    public void setBgResource(int resId) {
        convertView.setBackgroundResource(resId);
    }

    public void setBgDrawable(Drawable drawable) {
        convertView.setBackgroundDrawable(drawable);
    }

    public void setBgColor(int color) {
        convertView.setBackgroundColor(color);
    }

}
