package com.loyo.oa.v2.tool.customview.dragSortListView;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.MainActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.tool.Utils;

/**
 * Simple implementation of the FloatViewManager class. Uses list
 * items as they appear in the ListView to create the floating View.
 */
public class SimpleFloatViewManager implements DragSortListView.FloatViewManager {

    private Bitmap mFloatBitmap;

    private ImageView mImageView;

    private int mFloatBGColor = Color.BLACK;

    private DragSortListView mListView;

    public SimpleFloatViewManager(DragSortListView lv) {
        mListView = lv;
    }

    public void setBackgroundColor(int color) {
        mFloatBGColor = color;
    }

    /**
     * This simple implementation creates a Bitmap copy of the
     * list item currently shown at ListView <code>position</code>.
     */
    @Override
    public View onCreateFloatView(int position) {
        // Guaranteed that this will not be null? I think so. Nope, got
        // a NullPointerException once...
        MainApp.getMainApp().logUtil.e("position : "+position+" childrenCount : "+mListView.getChildCount());
        View v = mListView.getChildAt(position + mListView.getHeaderViewsCount() - mListView.getFirstVisiblePosition());
        MainApp.getMainApp().logUtil.e("HeaderViewsCount : "+mListView.getHeaderViewsCount()+" FirstVisiblePosition : "+mListView.getFirstVisiblePosition());
        if (v == null) {
            return null;
        }
        v.setPressed(false);
        //解决创建item悬浮视图时显示错位的bug
        DragSortListView.AdapterWrapper wrapper=(DragSortListView.AdapterWrapper)mListView.getAdapter();
        MainActivity.ClickItemAdapter adapter=(MainActivity.ClickItemAdapter)wrapper.mAdapter;
        MainActivity.ClickItem item=adapter.getItem(position);
        ImageView img_item = (ImageView) v.findViewById(R.id.img_item);
        TextView tv_item = (TextView) v.findViewById(R.id.tv_item);
        img_item.setImageResource(item.imageViewRes);
        tv_item.setText(item.title);
        // Create a copy of the drawing cache so that it does not get
        // recycled by the framework when the list tries to clean up memory
//        v.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//        v.setDrawingCacheEnabled(true);
//        mFloatBitmap = Bitmap.createBitmap(v.getDrawingCache());
//        v.setDrawingCacheEnabled(false);
        mFloatBitmap = Utils.loadBitmapFromView(v);

        if (mImageView == null) {
            mImageView = new ImageView(mListView.getContext());
        }
        mImageView.setBackgroundColor(mFloatBGColor);
        mImageView.setPadding(0, 0, 0, 0);
        mImageView.setImageBitmap(mFloatBitmap);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(v.getWidth(), v.getHeight()));

        return mImageView;
    }



    /**
     * This does nothing
     */
    @Override
    public void onDragFloatView(View floatView, Point position, Point touch) {
        // do nothing
    }

    /**
     * Removes the Bitmap from the ImageView created in
     * onCreateFloatView() and tells the system to recycle it.
     */
    @Override
    public void onDestroyFloatView(View floatView) {
        ((ImageView) floatView).setImageDrawable(null);

        mFloatBitmap.recycle();
        mFloatBitmap = null;
    }

}

