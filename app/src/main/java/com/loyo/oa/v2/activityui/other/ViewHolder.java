package com.loyo.oa.v2.activityui.other;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.tool.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ViewHolder {

    SparseArray<View> mViews;
    int mPosition;
    View mConvertView;

    public ViewHolder(Context context, ViewGroup parent, int layoutId, int posision) {
        mPosition = posision;
        mViews = new SparseArray<>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position);
        } else {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.mPosition = position;
            return holder;
        }
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);

            mViews.put(viewId, view);
        }

        return (T) view;
    }

    public int getPosition() {
        return mPosition;
    }

    public View getConvertView() {
        return mConvertView;
    }

    public ViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        if (tv != null) {
            tv.setText(text);
        }
        return this;
    }

    public ViewHolder setImageUri(int viewId, String url, int defaultViewId) {
        ImageView imageView = getView(viewId);

        if (imageView != null) {
            if (!StringUtil.isEmpty(url)) {
                ImageLoader.getInstance().displayImage(url, imageView, MainApp.options_3);
            } else {
                imageView.setImageResource(defaultViewId);
            }
        }
        return this;
    }

    public ViewHolder setImageResId(int viewId, int resId) {
        ImageView imageView = getView(viewId);

        if (imageView != null) {
            imageView.setImageResource(resId);
        }
        return this;
    }
}
