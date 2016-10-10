package com.loyo.oa.upload.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.loyo.cpb.CircularProgressButton;
import com.loyo.oa.v2.R;

/**
 * Created by EthanGong on 16/9/21.
 */

public class ImageCell extends LinearLayout {

    public ImageCell(Context context) {
        super(context);
    }

    public ImageView imageView;
    public View imageMask;
    private CircularProgressButton button;

    public static ImageCell instance(Context context) {
        LinearLayout content = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.upload_image_cell, null);
        ImageCell cell = new ImageCell(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        cell.addView(content, params);
        cell.setupWithView(content);

        return cell;
    }

    public void setProgress(int progress) {
        if (progress < 0) {
            button.setProgress(-1);
            imageMask.setVisibility(INVISIBLE);
            button.setVisibility(VISIBLE);
        }
        else {
            button.setProgress(progress);
            imageMask.setVisibility(INVISIBLE);
            button.setVisibility(VISIBLE);
        }
        if (progress == 100) {
            imageMask.setVisibility(INVISIBLE);
            button.setVisibility(GONE);
        }
    }

    private void setupWithView(LinearLayout layout) {
        imageView = (ImageView) layout.findViewById(R.id.image_view);
        imageMask = layout.findViewById(R.id.image_mask);
        button = (CircularProgressButton) layout.findViewById(R.id.circularButton2);
        // button.setIndeterminateProgressMode(true);
        // button.setOnClickListener(null);
        button.setClickable(false);
        this.setProgress(1);
        button.setVisibility(GONE);
    }
}
