package com.loyo.oa.v2.activity.home.cusview;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.home.NewMainActivity;
import com.loyo.oa.v2.common.Global;


public class MoreWindow extends PopupWindow implements OnClickListener{

    private String TAG = MoreWindow.class.getSimpleName();
    private Activity mContext;
    private int mWidth;
    private int mHeight;
    private int statusBarHeight ;
    private Bitmap mBitmap= null;
    private Bitmap overlay = null;
    private Handler mHandler;

    private LinearLayout view;
    private Button  closeBtn;
    private TextView btn1;
    private TextView btn2;
    private TextView btn3;
    private TextView btn4;
    private TextView btn5;
    private TextView btn6;

    public MoreWindow(Activity context,Handler handler) {
        mContext = context;
        mHandler = handler;
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
        int height =  mBitmap.getHeight();

        overlay = Bitmap.createBitmap((int) (width / scaleFactor),(int) (height / scaleFactor),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(mBitmap, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        return overlay;
    }

    private Animation showAnimation1(final View view,int fromY ,int toY) {
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

    public void initBtnUi(View v){
        btn1 = (TextView) v.findViewById(R.id.more_window_local);
        btn2 = (TextView) v.findViewById(R.id.more_window_online);
        btn3 = (TextView) v.findViewById(R.id.more_window_delete);
        btn4 = (TextView) v.findViewById(R.id.more_window_collect);
        btn5 = (TextView) v.findViewById(R.id.more_window_auto);
        btn6 = (TextView) v.findViewById(R.id.more_window_external);
        closeBtn = (Button)v.findViewById(R.id.center_music_window_close);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        closeBtn.setOnClickListener(this);

        btn1.setOnTouchListener(Global.GetTouch());
        btn2.setOnTouchListener(Global.GetTouch());
        btn3.setOnTouchListener(Global.GetTouch());
        btn4.setOnTouchListener(Global.GetTouch());
        btn5.setOnTouchListener(Global.GetTouch());
        btn6.setOnTouchListener(Global.GetTouch());
        closeBtn.setOnTouchListener(Global.GetTouch());
    }

    public void showMoreWindow(View anchor) {
        view = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.center_music_more_window, null);
        setContentView(view);
        initBtnUi(view);
        showAnimation(view);
        setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), blur()));
        setOutsideTouchable(true);
        setFocusable(true);
        showAtLocation(anchor, Gravity.BOTTOM, 0, statusBarHeight);
    }

    private void showAnimation(ViewGroup layout){
        for(int i=0;i<layout.getChildCount();i++){
            final View child = layout.getChildAt(i);
            if(child.getId() == R.id.center_music_window_close){
                continue;
            }
            child.setOnClickListener(this);
            child.setVisibility(View.INVISIBLE);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    child.setVisibility(View.VISIBLE);
                    ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 600, 0);
                    fadeAnim.setDuration(300);
                    KickBackAnimator kickAnimator = new KickBackAnimator();
                    kickAnimator.setDuration(150);
                    fadeAnim.setEvaluator(kickAnimator);
                    fadeAnim.start();
                }
            }, i * 50);
        }
    }

    private void closeAnimation(ViewGroup layout){
        for(int i=0;i<layout.getChildCount();i++){
            final View child = layout.getChildAt(i);
            if(child.getId() == R.id.center_music_window_close){
                continue;
            }
            child.setOnClickListener(this);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    child.setVisibility(View.VISIBLE);
                    ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 0, 600);
                    fadeAnim.setDuration(200);
                    KickBackAnimator kickAnimator = new KickBackAnimator();
                    kickAnimator.setDuration(100);
                    fadeAnim.setEvaluator(kickAnimator);
                    fadeAnim.start();
                    fadeAnim.addListener(new AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            child.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            // TODO Auto-generated method stub

                        }
                    });
                }
            }, (layout.getChildCount()-i-1) * 30);

            if(child.getId() == R.id.more_window_local){
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }, (layout.getChildCount()-i) * 30 + 80);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //关闭
            case R.id.center_music_window_close:
                if (isShowing()) {
                    dismiss();
                    //closeAnimation(view);
                }
                break;
            //新建任务
            case R.id.more_window_local:
                mHandler.sendEmptyMessage(NewMainActivity.TASKS_ADD);
                dismiss();
                break;
            //申请审批
            case R.id.more_window_online:
                mHandler.sendEmptyMessage(NewMainActivity.WFIN_ADD);
                dismiss();
                break;
            //提交报告
            case R.id.more_window_delete:
                mHandler.sendEmptyMessage(NewMainActivity.WORK_ADD);
                dismiss();
                break;
            //新建客户
            case R.id.more_window_collect:
                mHandler.sendEmptyMessage(NewMainActivity.TASKS_ADD_CUSTOMER);
                dismiss();
                break;
            //考勤打卡
            case R.id.more_window_auto:
                mHandler.sendEmptyMessage(NewMainActivity.ATTENT_ADD);
                dismiss();
                break;
            //拜访签到
            case R.id.more_window_external:
                mHandler.sendEmptyMessage(NewMainActivity.SIGNIN_ADD);
                dismiss();
                break;

            default:
                break;
        }
    }

    public void destroy() {
        if (null != overlay) {
            overlay.recycle();
            overlay = null;
            System.gc();
        }
        if (null != mBitmap) {
            mBitmap.recycle();
            mBitmap = null;
            System.gc();
        }
    }

}