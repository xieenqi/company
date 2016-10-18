package com.loyo.oa.upload.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.loyo.oa.v2.R;

/**
 * Created by EthanGong on 16/9/22.
 */

public class ImageAddCell extends LinearLayout {

    private ImageView imageView;

    public ImageAddCell(Context context) {
        super(context);
        initView();
    }

    public ImageAddCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ImageAddCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        imageView = new SquareImageView(this.getContext());
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.addView(imageView, params);
        imageView.setImageResource(R.drawable.icon_add_file);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
    }
}
