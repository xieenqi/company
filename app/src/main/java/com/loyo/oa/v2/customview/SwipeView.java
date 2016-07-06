package com.loyo.oa.v2.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

import com.loyo.oa.v2.R;

/**
 * com.loyo.oa.v2.customview
 * 描述 :侧滑视图
 * 作者 : ykb
 * 时间 : 15/9/18.
 */
public class SwipeView extends HorizontalScrollView {
    private String TAG = getClass().getSimpleName();
    /**
     * 滑动偏移量
     */
    private static final int BASE_SLIDE_BLOCK = 6;

    /**
     * 屏幕像素密度
     */
    private float density;

    /**
     * 是否已加载过一次layout，这里onLayout中的初始化只需加载一次
     */
    private boolean loadOnce;

    /**
     * 左侧布局对象。
     */
    private View leftLayout;

    /**
     * 右侧布局对象。
     */
    private View rightLayout;
    /**
     * 菜单宽度
     */
    private int menuWidth;
    /**
     * 滑动开始时的x坐标
     */
    private float downX;
    /**
     * 菜单状态
     */
    private boolean isMenuOpen;


    public SwipeView(Context context) {
        this(context, null);
    }

    public SwipeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        density = getResources().getDisplayMetrics().density;
        setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
    }

    /**
     * 初始化数据
     */
    public void onLayoutInit() {
        // 获取左侧布局对象
        leftLayout = findViewById(R.id.front);
        MarginLayoutParams rightLayoutParams = (MarginLayoutParams) leftLayout.getLayoutParams();
        rightLayoutParams.width = getResources().getDisplayMetrics().widthPixels;
        leftLayout.setLayoutParams(rightLayoutParams);
        // 获取右侧布局对象
        rightLayout = findViewById(R.id.back);
        menuWidth = rightLayout.getLayoutParams().width;
        scrollTo(0, 0);
        loadOnce = true;
    }


    /**
     * 在onLayout中重新设定左侧布局和右侧布局的参数。
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && !loadOnce) {
            onLayoutInit();
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        float scale = l * 1.0f / menuWidth;
//        ViewHelper.setTranslationX(rightLayout, menuWidth * scale);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP) {

            int scrollX = (int) (ev.getRawX() - downX);
            int slideWidth = menuWidth / BASE_SLIDE_BLOCK;
            Log.e(TAG, "scrollx : " + scrollX + " downX : " + downX + " upX " + ev.getRawX() + " width/6 : " + slideWidth + " menuwidth : " + menuWidth);
            if (scrollX == 0 || scrollX == menuWidth) {
                return false;
            }
            int slideX = Math.abs(scrollX);
            //右滑
            if (scrollX > 0) {
                if (slideX >= slideWidth) {
                    smoothScrollTo(0, 0);
                    isMenuOpen = true;
                } else {
                    smoothScrollTo(menuWidth, 0);
                    isMenuOpen = false;
                }
            }
            //左滑
            else if (scrollX < 0) {
                if (slideX >= slideWidth) {
                    smoothScrollTo(menuWidth, 0);
                    isMenuOpen = false;
                } else {
                    smoothScrollTo(menuWidth, 0);
                    isMenuOpen = true;
                }
            }
            //未滑动
            else {
                if (!isMenuOpen) {
                    smoothScrollTo(0, 0);
                } else {
                    smoothScrollTo(menuWidth, 0);
                }
            }
            downX = 0;
            return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (downX == 0) {
                downX = ev.getRawX();
            }
        }

        return super.onTouchEvent(ev);
    }

    /**
     * 关闭菜单
     */
    public void closeMenu(){
        if(!isMenuOpen){
            return;
        }
        isMenuOpen=false;
        smoothScrollTo(0,0);
    }

    /**
     * 打开菜单
     */
    public void openMenu(){
        if(isMenuOpen){
            return;
        }
        isMenuOpen=true;
        smoothScrollTo(menuWidth,0);
    }

    /**
     * 切换菜单显示状态
     */
    public void toggle(){
        if(isMenuOpen){
            closeMenu();
        }else {
            openMenu();
        }
    }
}
