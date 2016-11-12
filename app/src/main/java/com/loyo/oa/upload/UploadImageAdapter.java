package com.loyo.oa.upload;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.loyo.oa.upload.view.ImageAddCell;
import com.loyo.oa.upload.view.ImageCell;
import com.loyo.oa.v2.R;

import java.util.ArrayList;

/**
 * Created by EthanGong on 16/10/10.
 */

public class UploadImageAdapter extends BaseAdapter implements ImageCell.ImageCellCallback {

    private Context mContext;
    private ArrayList<UploadTask> taskList;
    private int maxSize;
    public ImageCell.ImageCellCallback callback;

    public UploadImageAdapter(Context c, ArrayList<UploadTask> taskList, int maxSize) {
        mContext = c;
        this.taskList = taskList;
        this.maxSize = maxSize;
    }


    @Override
    public int getCount() {
        if (maxSize > 0  && taskList.size() >= maxSize) {
            return taskList.size();
        }
        return taskList.size() + 1;
    }

    @Override
    public Object getItem(int i) {
        if( i ==  taskList.size()) {
            return "";
        }

        return taskList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup group) {


        if (i == taskList.size()) {
            ImageAddCell addCell;

            if (convertView == null || convertView.getClass() != ImageAddCell.class) {
                addCell = new ImageAddCell(mContext);

            } else {
                addCell = (ImageAddCell) convertView;
            }

            return addCell;
        }

        ImageCell cell;
        if (convertView == null || convertView.getClass() != ImageCell.class) {
            cell = ImageCell.instance(mContext);
        } else {
            cell = (ImageCell) convertView;
        }
        cell.index = i;
        cell.callback = this;

        Glide.with(mContext)
                .load(taskList.get(i).getValidatePath())
                .centerCrop()
                .dontAnimate()
                .override(100, 100)
                .placeholder(R.drawable.default_error)
                .error(R.drawable.default_error)
                .into(cell.imageView);
        return cell;
    }

    @Override
    public void onRetry(int index) {
        if (callback != null) {
            callback.onRetry(index);
        }
    }
}