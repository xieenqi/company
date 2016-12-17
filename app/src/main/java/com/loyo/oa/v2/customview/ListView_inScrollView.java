package com.loyo.oa.v2.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ListView_inScrollView extends ListView {

	public ListView_inScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.setOverScrollMode(OVER_SCROLL_NEVER);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
