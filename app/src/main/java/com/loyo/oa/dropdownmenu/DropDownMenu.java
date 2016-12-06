package com.loyo.oa.dropdownmenu;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.loyo.oa.dropdownmenu.adapter.MenuAdapter;
import com.loyo.oa.dropdownmenu.utils.SimpleAnimationListener;
import com.loyo.oa.dropdownmenu.utils.UIUtil;
import com.loyo.oa.dropdownmenu.view.MenuTabBar;
import com.loyo.oa.v2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/10/28.
 */

public class DropDownMenu extends RelativeLayout implements View.OnClickListener, MenuTabBar.OnTabClickListener {

    public MenuTabBar headerTabBar;
    private FrameLayout frameLayoutContainer;
    private View mask;

    private View currentView;
    private View lastView;

    private Animation dismissAnimation;
    private Animation occurAnimation;
    private Animation alphaDismissAnimation;
    private Animation alphaOccurAnimation;

    private Animation leftLeave;
    private Animation rightLeave;
    private Animation leftEnter;
    private Animation rightEnter;

    private MenuAdapter menuAdapter;

    public DropDownMenu(Context context) {
        this(context, null);
    }

    public DropDownMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DropDownMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.WHITE);
    }

    public void setMenuAdapter(MenuAdapter menuAdapter) {
        this.menuAdapter = menuAdapter;
        initMenuTitles();
        initMenuViews();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setContentView(findViewById(R.id.filter_content_view));
        if (this.menuAdapter != null) {
            initMenuTitles();
            initMenuViews();
        }
    }

    public void setContentView(View contentView) {
        removeAllViews();

        /* 1.顶部筛选条 */
        headerTabBar = new MenuTabBar(getContext());
        headerTabBar.setId(R.id.drop_menu_header);
        addView(headerTabBar, -1, UIUtil.dp(getContext(), 45));

        LayoutParams params = new LayoutParams(-1, -1);
        params.addRule(BELOW, R.id.drop_menu_header);

        /* 2.添加contentView, 内容界面 */
        addView(contentView, params);

        /* 3.添加展开页面, 装载筛选器list */
        frameLayoutContainer = new FrameLayout(getContext());
        frameLayoutContainer.setBackgroundColor(Color.TRANSPARENT);
        addView(frameLayoutContainer, params);
        frameLayoutContainer.setVisibility(GONE);

        initListener();
        initAnimation();
    }


    private void initMenuTitles() {
        int count = menuAdapter.getMenuCount();
        List<String> titles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            titles.add(menuAdapter.getMenuTitle(i));
        }
        headerTabBar.setTitles(titles);
    }

    private void initMenuViews() {
        mask = new View(getContext());
        mask.setBackgroundColor(getResources().getColor(R.color.black_drop_menu_bg));
        frameLayoutContainer.addView(mask);

        int count = menuAdapter.getMenuCount();
        for (int position = 0; position < count; ++position) {
            setPositionView(position, findViewAtPosition(position), menuAdapter.getHeight(position));
        }
    }



    public View findViewAtPosition(int position) {
        View view = frameLayoutContainer.getChildAt(position + 1);
        if (view == null) {
            view = menuAdapter.getView(position, frameLayoutContainer);
        }
        return view;
    }

    private void setPositionView(int position, View view, int bottomMargin) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, -2);
        // params.bottomMargin = bottomMargin;//添加距离底部高度
        params.height = bottomMargin;
        frameLayoutContainer.addView(view, position + 1, params);
        view.setVisibility(GONE);
    }


    public boolean isShowing() {
        return frameLayoutContainer.isShown();
    }

    public boolean isClosed() {
        return !isShowing();
    }

    public void close() {
        if (isClosed()) {
            return;
        }

        mask.startAnimation(alphaDismissAnimation);
        headerTabBar.resetTabAtPosition(headerTabBar.getCurrentIndicatorPosition());

        if (currentView != null) {
            currentView.startAnimation(dismissAnimation);
        }
    }


    public void setPositionIndicatorText(int position, String text) {
        // headerTabBar.setPositionText(position, text);
    }

    public void setCurrentIndicatorText(String text) {
        headerTabBar.setCurrentText(text);
    }


    private void initListener() {
        frameLayoutContainer.setOnClickListener(this);
        headerTabBar.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (isShowing()) {
            close();
        }
    }

    public void onItemClick(View v, int position, boolean open) {
        if (open) {
            close();
        } else {


            currentView = frameLayoutContainer.getChildAt(position);

            if (currentView == null) {
                return;
            }


            frameLayoutContainer.getChildAt(headerTabBar.getLastIndicatorPosition()).setVisibility(View.GONE);
            frameLayoutContainer.getChildAt(position).setVisibility(View.VISIBLE);


            if (isClosed()) {
                frameLayoutContainer.setVisibility(VISIBLE);
                frameLayoutContainer.startAnimation(alphaOccurAnimation);

                //可移出去,进行每次展出
                currentView.startAnimation(occurAnimation);
            }
        }
    }


    private void initAnimation() {
        occurAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.top_in);

        SimpleAnimationListener listener = new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                frameLayoutContainer.setVisibility(GONE);
            }
        };

        SimpleAnimationListener listener2 = new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                lastView.setVisibility(GONE);
            }
        };

        dismissAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.top_out);
        dismissAnimation.setAnimationListener(listener);


        alphaDismissAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_to_zero);
        alphaDismissAnimation.setDuration(300);
        alphaDismissAnimation.setAnimationListener(listener);

        alphaOccurAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_to_one);
        alphaOccurAnimation.setDuration(300);

        leftLeave = AnimationUtils.loadAnimation(getContext(), R.anim.leave_to_left);
        leftLeave.setAnimationListener(listener2);
        leftEnter = AnimationUtils.loadAnimation(getContext(), R.anim.enter_from_left);
        leftEnter.setAnimationListener(listener2);
        rightLeave = AnimationUtils.loadAnimation(getContext(), R.anim.leave_to_right);
        rightLeave.setAnimationListener(listener2);
        rightEnter = AnimationUtils.loadAnimation(getContext(), R.anim.enter_from_right);
        rightEnter.setAnimationListener(listener2);
    }

    @Override
    public void onTabClick(View v, int position, boolean isOpen) {
        if (isOpen) {
            close();
        }
        else {

//            if (position == headerTabBar.getLastIndicatorPosition()) {
//            }
//            else {
//                int last = headerTabBar.getLastIndicatorPosition();
//                View lastView = frameLayoutContainer.getChildAt(last+1);
//                this.lastView = lastView;
//                View currentView = frameLayoutContainer.getChildAt(position+1);
//                currentView.setVisibility(VISIBLE);
//                if (last > position) {
//                    lastView.startAnimation(rightLeave);
//                    currentView.startAnimation(leftEnter);
//                }
//                else {
//                    lastView.startAnimation(leftLeave);
//                    currentView.startAnimation(rightEnter);
//                }
//            }

            position = position+1;
            currentView = frameLayoutContainer.getChildAt(position);
            if (currentView == null) {
                return;
            }
            int last = headerTabBar.getLastIndicatorPosition();
            if (last >= 0) {
                View child = frameLayoutContainer.getChildAt(last+1);
                if (child != null) {
                    child.setVisibility(View.GONE);
                }
            }
            View child = frameLayoutContainer.getChildAt(position);
            child.setVisibility(View.VISIBLE);

            FrameLayout.LayoutParams linearParams = (FrameLayout.LayoutParams) child.getLayoutParams();
//            linearParams.height = 275;
//            child.setLayoutParams(linearParams);

            if (isClosed()) {
                frameLayoutContainer.setVisibility(VISIBLE);
                mask.startAnimation(alphaOccurAnimation);

                //可移出去,进行每次展出
                currentView.startAnimation(occurAnimation);
            }
        }
    }
}
