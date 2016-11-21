package com.loyo.oa.pulltorefresh.Slide;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class SlideListView extends ListView {

    private static final String TAG = "SlideListView";

    private SlideView mFocusedSlideView;

    public SlideListView(Context context) {
        super(context);
    }

    public SlideListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlideListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void shrinkListItem(int position) {
        View item = getChildAt(position);

        if (item != null) {
            try {
                ((SlideView) item).shrink();
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN: {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int position = pointToPosition(x, y);
            Log.e(TAG, "x=" + x);
            Log.e(TAG, "y=" + y);
            if (position != INVALID_POSITION) {
//                MessageItem data = (MessageItem) getItemAtPosition(position);
//                mFocusedSlideView = data.slideView;
                mFocusedSlideView=(SlideView)getAdapter().getItem(position);
//                Log.e(TAG, "mFocusedSlideView=" + mFocusedSlideView);
            }
        }
        default:
            break;
        }

        if (mFocusedSlideView != null) {
        	mFocusedSlideView.onRequireTouchEvent(event);
        }

        return super.onTouchEvent(event);
    }

}
