package com.loyo.oa.upload.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.loyo.cpb.CircularProgressButton;
import com.loyo.oa.v2.R;

/**
 * Created by EthanGong on 16/9/21.
 */

public class ImageCell extends RecyclerView.ViewHolder  {

    public ImageView imageView;
    public View imageMask;
    private CircularProgressButton button;
    public int index;
    public ImageCellCallback callback;

    private ImageCell(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.image_view);
        imageMask = itemView.findViewById(R.id.image_mask);
        button = (CircularProgressButton) itemView.findViewById(R.id.circularButton2);
        // button.setIndeterminateProgressMode(true);
        // button.setOnClickListener(null);
        button.setClickable(true);
        this.setProgress(1);
        button.setVisibility(View.GONE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback!=null) {
                    callback.onItemClickAtIndex(index);
                }
            }
        });
    }

    public static ImageCell instance(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upload_image_cell, parent, false);
        return new ImageCell(itemView);
    }


    public void setProgress(int progress) {

        if (progress < 0) {
            button.setProgress(-1);
            imageMask.setVisibility(View.INVISIBLE);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback!=null) {
                        callback.onRetry(index);
                    }
                }
            });
        }
        else {
            button.setProgress(progress);
            imageMask.setVisibility(View.INVISIBLE);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(null);
        }
        if (progress == 0) {
            imageMask.setVisibility(View.INVISIBLE);
            button.setVisibility(View.GONE);
            button.setOnClickListener(null);
        }
        if (progress == 100) {
            imageMask.setVisibility(View.INVISIBLE);
            button.setVisibility(View.GONE);
            button.setOnClickListener(null);
        }
    }

    public interface ImageCellCallback{
        void onRetry(int index);
        void onItemClickAtIndex(int index);
    }
}
