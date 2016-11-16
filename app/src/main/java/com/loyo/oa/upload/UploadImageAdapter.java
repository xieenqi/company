package com.loyo.oa.upload;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.loyo.oa.upload.view.ImageCell;
import com.loyo.oa.v2.R;

import java.util.ArrayList;

/**
 * Created by EthanGong on 16/10/10.
 */

public class UploadImageAdapter extends RecyclerView.Adapter<ImageCell> implements ImageCell.ImageCellCallback {

    private Context mContext;
    private ArrayList<UploadTask> taskList;
    private int maxSize;
    public ImageCell.ImageCellCallback callback;

    public UploadImageAdapter(Context c, ArrayList<UploadTask> taskList, int maxSize) {
        mContext = c;
        this.taskList = taskList;
        this.maxSize = maxSize;
        setHasStableIds(true);
    }

    @Override
    public ImageCell onCreateViewHolder(ViewGroup parent, int viewType) {
        return ImageCell.instance(parent);
    }

    @Override
    public void onBindViewHolder(ImageCell cell, int position) {

        if( position ==  taskList.size()) {
            cell.imageView.setImageResource(R.drawable.icon_add_file);
            cell.callback = this;
            cell.index = position;
            return ;
        }


        cell.index = position;
        cell.callback = this;
        UploadTask task = taskList.get(position);
        int p = (int)(task.progress*100);
        cell.setProgress(p);
        Glide.with(mContext)
                .load(taskList.get(position).getValidatePath())
                .centerCrop()
                .dontAnimate()
                .override(200, 200)
                .placeholder(R.drawable.default_error)
                .error(R.drawable.default_error)
                .into(cell.imageView);
    }

    @Override
    public void onViewRecycled(ImageCell holder) {
        holder.itemView.setVisibility(View.VISIBLE);
        super.onViewRecycled(holder);
    }
    @Override
    public long getItemId(int i) {
        if( i ==  taskList.size()) {
            return "add".hashCode();
        }
        return taskList.get(i).getValidatePath().hashCode();
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (maxSize > 0  && taskList.size() >= maxSize) {
            return taskList.size();
        }
        return taskList.size() + 1;
    }

    @Override
    public void onRetry(int index) {
        if (callback != null) {
            callback.onRetry(index);
        }
    }

    @Override
    public void onItemClickAtIndex(int index) {
        if (callback != null) {
            callback.onItemClickAtIndex(index);
        }
    }
}