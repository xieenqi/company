package com.loyo.oa.v2.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 【自适应ListView】 用于ScrollList/ListView中再嵌套ListView
 * Created by yyy on 16/8/26.
 */
public class CustomerListView extends ListView {

        public CustomerListView(Context context) {
            super(context);
        }
        public CustomerListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
        public CustomerListView(Context context, AttributeSet attrs,
                                     int defStyle) {
            super(context, attrs, defStyle);
        }
        @Override
        /**
         * 重写该方法，达到使ListView适应ScrollView的效果
         */
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                    MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        }

}
