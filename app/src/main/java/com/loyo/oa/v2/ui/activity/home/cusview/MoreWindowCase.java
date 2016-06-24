package com.loyo.oa.v2.ui.activity.home.cusview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.ui.activity.home.adapter.AdapterMoreWindow;
import com.loyo.oa.v2.ui.activity.home.bean.MoreWindowItem;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;

import java.util.ArrayList;

/**
 * 【主页菜单】 快捷创建页
 */
public class MoreWindowCase extends PopupWindow {

    private Activity mContext;
    private Bitmap mBitmap = null;
    private Bitmap overlay = null;
    private Handler mHandler;
    private RelativeLayout view;
    private Button closeBtn;
    private ArrayList<MoreWindowItem> data;
    private AdapterMoreWindow mAdapter;
    private GridView gridView;

    private int mWidth;
    private int mHeight;
    private int statusBarHeight;

    public MoreWindowCase(Activity context, Handler handler, ArrayList<MoreWindowItem> data) {
        if (null == data) {
            data = new ArrayList<>();
        }
        this.data = data;
        mContext = context;
        mHandler = handler;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    public void init() {
        Rect frame = new Rect();
        mContext.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        statusBarHeight = frame.top;
        DisplayMetrics metrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);
        mWidth = metrics.widthPixels;
        mHeight = metrics.heightPixels;

        setWidth(mWidth);
        setHeight(mHeight);
    }

    private Bitmap blur() {
        if (null != overlay) {
            return overlay;
        }
        long startMs = System.currentTimeMillis();

        View view = mContext.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        mBitmap = view.getDrawingCache();

        float scaleFactor = 8;
        float radius = 10;
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();

        overlay = Bitmap.createBitmap((int) (width / scaleFactor), (int) (height / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(mBitmap, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        return overlay;
    }

    private Animation showAnimation1(final View view, int fromY, int toY) {
        AnimationSet set = new AnimationSet(true);
        TranslateAnimation go = new TranslateAnimation(0, 0, fromY, toY);
        go.setDuration(300);
        TranslateAnimation go1 = new TranslateAnimation(0, 0, -10, 2);
        go1.setDuration(100);
        go1.setStartOffset(250);
        set.addAnimation(go1);
        set.addAnimation(go);

        set.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }

        });
        return set;
    }

    public void initBtnUi(View v) {
        closeBtn = (Button) v.findViewById(R.id.case_music_window_close);
        closeBtn.setOnTouchListener(Global.GetTouch());
        gridView = (GridView) v.findViewById(R.id.case_gridview);

        if (null == mAdapter) {
            mAdapter = new AdapterMoreWindow(data, mContext, mHandler);
        } else {
            mAdapter.notifyDataSetChanged();
        }

        gridView.setAdapter(mAdapter);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));

        //关闭
        closeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing()) {
                    dismiss();
                }
            }
        });

        //列表监听
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (data.get(position).name) {
                    case "新建任务":
                        mHandler.sendEmptyMessage(BaseActivity.TASKS_ADD);
                        dismiss();
                        break;

                    case "申请审批":
                        mHandler.sendEmptyMessage(BaseActivity.WFIN_ADD);
                        dismiss();
                        break;

                    case "提交报告":
                        mHandler.sendEmptyMessage(BaseActivity.WORK_ADD);
                        dismiss();
                        break;

                    case "新建客户":
                        mHandler.sendEmptyMessage(BaseActivity.TASKS_ADD_CUSTOMER);
                        dismiss();
                        break;

                    case "写跟进":
                        mHandler.sendEmptyMessage(BaseActivity.FOLLOW_ADD);
                        dismiss();
                        break;

                    case "新建机会":
                        mHandler.sendEmptyMessage(BaseActivity.SALE_ADD);
                        dismiss();
                        break;

                    case "考勤打卡":
                        mHandler.sendEmptyMessage(BaseActivity.ATTENT_ADD);
                        dismiss();
                        break;

                    case "拜访签到":
                        mHandler.sendEmptyMessage(BaseActivity.SIGNIN_ADD);
                        dismiss();
                        break;
                }
            }
        });
    }

    public void showMoreWindow(View anchor) {
        view = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.case_music_more_window, null);
        setContentView(view);
        initBtnUi(view);
        showAnimation();
        setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), blur()));
        setOutsideTouchable(true);
        setFocusable(true);
        showAtLocation(anchor, Gravity.BOTTOM, 0, statusBarHeight);
    }

    /**
     * 弹出动画
     */
    private void showAnimation() {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ValueAnimator fadeAnim = ObjectAnimator.ofFloat(gridView, "translationY", 600, 0);
                fadeAnim.setDuration(300);
                KickBackAnimator kickAnimator = new KickBackAnimator();
                kickAnimator.setDuration(150);
                fadeAnim.setEvaluator(kickAnimator);
                fadeAnim.start();
            }
        }, 50);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ValueAnimator fadeAnim = ObjectAnimator.ofFloat(closeBtn, "translationY", 300, 0);
                fadeAnim.setDuration(300);
                KickBackAnimator kickAnimator = new KickBackAnimator();
                kickAnimator.setDuration(150);
                fadeAnim.setEvaluator(kickAnimator);
                fadeAnim.start();
            }
        }, 100);
    }
}