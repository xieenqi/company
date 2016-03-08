package com.loyo.oa.v2.tool.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.loyo.oa.v2.R;

public class MyLetterListView extends View {

    OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    String[] keyword = {"#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    int choose = -1;
    Paint paint = new Paint();
    boolean showBkg = false;
    private float density;

    public static final byte FINGER_ACTION_DOWN = -3;
    public static final byte FINGER_ACTION_MOVE = -2;
    public static final byte FINGER_ACTION_UP = -1;

    public MyLetterListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        density = context.getResources().getDisplayMetrics().density;
    }

    public MyLetterListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyLetterListView(Context context) {
        this(context, null);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showBkg) {
            canvas.drawColor(Color.parseColor("#000000"));
        }

        int height = getHeight();
        int width = getWidth();
        int count = keyword.length;
        if (count < 14) {
            count = 14;
        }
        float singleHeight = height / count;
        for (int i = 0; i < keyword.length; i++) {
            //			paint.setColor(Color.WHITE);	//设置字体的颜色
            //            paint.setColor(getResources().getColor(R.color.gray_));
            paint.setColor(getResources().getColor(R.color.title_bg1));
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            //			paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.navigation_fontsize));//设置字体的大小
            //paint.setTextSize(16 * density);//设置字体的大小
            paint.setTextSize(25);//设置字体的大小
            paint.setAntiAlias(true);
            if (i == choose) {
                paint.setColor(Color.parseColor("#3399ff"));
                paint.setFakeBoldText(true);
            }
            float xPos = width / 2 - paint.measureText(keyword[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(keyword[i], xPos, yPos, paint);
            paint.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * keyword.length);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                showBkg = true;
                if (oldChoose != c && listener != null) {
                    if (c >= 0 && c < keyword.length) {
                        listener.onTouchingLetterChanged(c, keyword[c], FINGER_ACTION_DOWN);
                        choose = c;
                        invalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != c && listener != null) {
                    if (c > 0 && c < keyword.length) {
                        listener.onTouchingLetterChanged(c, keyword[c], FINGER_ACTION_MOVE);
                        choose = c;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                showBkg = false;
                choose = -1;
                listener.onTouchingLetterChanged(-1, null, FINGER_ACTION_UP);
                invalidate();
                break;
        }
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(int selectionIndex, String sectionLetter, int state);
    }

    public void setKeyword(String s) {
        if (s != null) {
            keyword = s.split("");
        }
    }
}

