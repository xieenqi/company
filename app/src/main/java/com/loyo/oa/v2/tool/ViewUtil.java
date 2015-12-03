package com.loyo.oa.v2.tool;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

public class ViewUtil {
    private static String TAG = "ViewUtil 视图工具";
    public static final int DATE_NULL = 0;

//    public class OnTouchListener_button_drawableTop implements View.OnTouchListener {
//        int img_resid_bg = DATE_NULL;
//        int img_resid_start = DATE_NULL;
//        int img_resid_top = DATE_NULL;
//        int img_resid_end = DATE_NULL;
//        int img_resid_bottom = DATE_NULL;
//        int img_resid_bg_down = DATE_NULL;
//        int img_resid_start_down = DATE_NULL;
//        int img_resid_top_down = DATE_NULL;
//        int img_resid_end_down = DATE_NULL;
//        int img_resid_bottom_down = DATE_NULL;
//        Drawable img_drawable_start;
//        Drawable img_drawable_top;
//        Drawable img_drawable_end;
//        Drawable img_drawable_bottom;
//        Drawable img_drawable_start_down;
//        Drawable img_drawable_top_down;
//        Drawable img_drawable_end_down;
//        Drawable img_drawable_bottom_down;
//
//        public OnTouchListener_button_drawableTop(Context context,
//                                                  int img_resid_bg, int img_resid_bg_down, int img_resid_start,
//                                                  int img_resid_start_down, int img_resid_top,
//                                                  int img_resid_top_down, int img_resid_end,
//                                                  int img_resid_end_down, int img_resid_bottom,
//                                                  int img_resid_bottom_down) {
//            this.img_resid_bg = img_resid_bg;
//            this.img_resid_start = img_resid_start;
//            this.img_resid_top = img_resid_top;
//            this.img_resid_end = img_resid_end;
//            this.img_resid_bottom = img_resid_bottom;
//            this.img_resid_bg_down = img_resid_bg_down;
//            this.img_resid_start_down = img_resid_start_down;
//            this.img_resid_top_down = img_resid_top_down;
//            this.img_resid_end_down = img_resid_end_down;
//            this.img_resid_bottom_down = img_resid_bottom_down;
//            init(context);
//        }
//
//        private void init(Context context) {
//            Resources resources = context.getResources();
//            img_drawable_start = img_resid_start != DATE_NULL ? resources
//                    .getDrawable(img_resid_start) : null;
//            if (img_drawable_start != null) {
//                img_drawable_start.setBounds(DATE_NULL, DATE_NULL,
//                        img_drawable_start.getMinimumWidth(),
//                        img_drawable_start.getMinimumHeight());
//            }
//
//            img_drawable_top = img_resid_top != DATE_NULL ? resources
//                    .getDrawable(img_resid_top) : null;
//            if (img_drawable_top != null) {
//                img_drawable_top.setBounds(DATE_NULL, DATE_NULL,
//                        img_drawable_top.getMinimumWidth(),
//                        img_drawable_top.getMinimumHeight());
//            }
//
//            img_drawable_end = img_resid_end != DATE_NULL ? resources
//                    .getDrawable(img_resid_end) : null;
//            if (img_drawable_end != null) {
//                img_drawable_end.setBounds(DATE_NULL, DATE_NULL,
//                        img_drawable_end.getMinimumWidth(),
//                        img_drawable_end.getMinimumHeight());
//            }
//
//            img_drawable_bottom = img_resid_bottom != DATE_NULL ? resources
//                    .getDrawable(img_resid_bottom) : null;
//            if (img_drawable_bottom != null) {
//                img_drawable_bottom.setBounds(DATE_NULL, DATE_NULL,
//                        img_drawable_bottom.getMinimumWidth(),
//                        img_drawable_bottom.getMinimumHeight());
//            }
//
//
//            img_drawable_start_down = img_resid_start_down != DATE_NULL ? resources
//                    .getDrawable(img_resid_start_down) : null;
//            if (img_drawable_start_down != null) {
//                img_drawable_start_down.setBounds(DATE_NULL, DATE_NULL,
//                        img_drawable_start_down.getMinimumWidth(),
//                        img_drawable_start_down.getMinimumHeight());
//            }
//
//            img_drawable_top_down = img_resid_top_down != DATE_NULL ? resources
//                    .getDrawable(img_resid_top_down) : null;
//            if (img_drawable_top_down != null) {
//                img_drawable_top_down.setBounds(DATE_NULL, DATE_NULL,
//                        img_drawable_top_down.getMinimumWidth(),
//                        img_drawable_top.getMinimumHeight());
//            }
//
//            img_drawable_end_down = img_resid_end_down != DATE_NULL ? resources
//                    .getDrawable(img_resid_end_down) : null;
//            if (img_drawable_end_down != null) {
//                img_drawable_end_down.setBounds(DATE_NULL, DATE_NULL,
//                        img_drawable_end_down.getMinimumWidth(),
//                        img_drawable_end_down.getMinimumHeight());
//            }
//
//            img_drawable_bottom_down = img_resid_bottom_down != DATE_NULL ? resources
//                    .getDrawable(img_resid_bottom_down) : null;
//            if (img_drawable_bottom_down != null) {
//                img_drawable_bottom_down.setBounds(DATE_NULL, DATE_NULL,
//                        img_drawable_bottom_down.getMinimumWidth(),
//                        img_drawable_bottom_down.getMinimumHeight());
//            }
//
//        }
//
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            Button button = (Button) v;
//
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//
//                button.setCompoundDrawables(img_drawable_start,
//                        img_drawable_top, img_drawable_end, img_drawable_bottom);
//
//                if (img_resid_bg != DATE_NULL) {
//                    v.setBackgroundResource(img_resid_bg);
//                }
//
//            } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                if (img_resid_bg != DATE_NULL) {
//                    v.setBackgroundResource(img_resid_bg);
//                }
//                button.setCompoundDrawables(img_drawable_start_down,
//                        img_drawable_top_down, img_drawable_end_down,
//                        img_drawable_bottom_down);
//            }
//            return false;
//        }
//    }
//
//    public static class OnTouchListener_view_Img implements View.OnTouchListener {
//        private ImageView imageView;
//        private int img1;
//        private int img2;
//
//        public OnTouchListener_view_Img(ImageView imageView, int img1, int img2) {
//            this.imageView = imageView;
//            this.img1 = img1;
//            this.img2 = img2;
//        }
//
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            Log.d(TAG, "onTouch OnTouchListener_view_transparency event.getAction():" + event.getAction());
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                imageView.setImageResource(img2);
//            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
//                imageView.setImageResource(img1);
//            }
//            return false;
//        }
//
//    }

    public static class OnTouchListener_view_transparency implements View.OnTouchListener {

        static OnTouchListener_view_transparency _Instance;

        public static OnTouchListener_view_transparency Instance() {
            if (_Instance == null) {
                _Instance = new OnTouchListener_view_transparency();
            }

            return _Instance;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d(TAG, "onTouch 触摸监听 OnTouchListener_view_transparency event.getAction():" + event.getAction());
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setAlpha(0.25f);
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.setAlpha(1);
            }
            return false;
        }

    }

//    public static class OnTouchListener_view_bg_color implements View.OnTouchListener {
//
//
//        static OnTouchListener_view_bg_color _Instance;
//
//        public static OnTouchListener_view_bg_color Instance() {
//            if (_Instance == null) {
//                _Instance = new OnTouchListener_view_bg_color();
//            }
//
//            return _Instance;
//        }
//
//        int color = DATE_NULL;
//        int color_down = R.color.pressed;
//
//        public OnTouchListener_view_bg_color() {
//        }
//
////        public OnTouchListener_view_bg_color(int color, int color_down) {
////            this.color = color;
////            this.color_down = color_down;
////        }
//
//        @Override
//        public boolean onTouch(final View v, MotionEvent event) {
//            //Log.d(TAG, "onTouch event:"+event);
//            if (color == DATE_NULL) {
//                color = android.R.color.white;
//            }
//
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                v.setBackgroundColor(v.getResources().getColor(color_down));
//                v.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (v == null) return;
//
//                        v.setBackgroundColor(v.getResources().getColor(color));
//                    }
//                }, 300);
//            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
//                v.setBackgroundColor(v.getResources().getColor(color));
//            }
//            return false;
//        }
//
//    }

    public static class OnTouchListener_softInput_hide implements View.OnTouchListener {

        static OnTouchListener_softInput_hide _Instance;

        public static OnTouchListener_softInput_hide Instance() {
            if (_Instance == null) {
                _Instance = new OnTouchListener_softInput_hide();
            }

            return _Instance;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    InputMethodManager imm = (InputMethodManager) v.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    break;
                case MotionEvent.ACTION_UP:

                    break;

            }

            return false;
        }
    }

//    public static void setTextView_textNewline(TextView tv, float offset) {
//        ViewTreeObserver viewTreeObserver = tv.getViewTreeObserver();
//        if (viewTreeObserver.isAlive()) {
//            viewTreeObserver.addOnGlobalLayoutListener(new SetTextView_textNewline(tv, offset));
//        }
//
//    }

//    private static class SetTextView_textNewline implements ViewTreeObserver.OnGlobalLayoutListener {
//        TextView tv;
//        float offset;
//
//        SetTextView_textNewline(TextView tv, float offset) {
//            this.tv = tv;
//            this.offset = offset;
//        }
//
//        @Override
//        public void onGlobalLayout() {
//            tv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//            Log.d(TAG,
//                    "photograph_button.viewTreeObserver.onGlobalLayout. w:"
//                            + tv.getMeasuredWidth() + ";h:"
//                            + tv.getMeasuredHeight());
//            float viewW = tv.getMeasuredWidth();
//            float textW = StringUtil.getTextViewWidth(tv);
//            if ((textW / viewW) > 0) {
//                float textSize = tv.getText().length();
//                float textW_each = textW / textSize;
//                float lastlLineW = textW % viewW;
//                if ((lastlLineW + offset) > viewW) {
//                    float newlineW = lastlLineW + offset - viewW;
//                    int newlineIndex = (int) Math.ceil((double) (newlineW / textW_each));
//                    String oldStrng = tv.getText().toString();
////                    app.logUtil.d("(oldStrng.length()-1-newlineIndex):"+(oldStrng.length()-1-newlineIndex));
////                    app.logUtil.d("oldStrng.length():"+oldStrng.length());
////                    app.logUtil.d("newlineIndex:"+newlineIndex);
////                    String oldStrng_newline= oldStrng.substring((oldStrng.length()-1-newlineIndex));
////
////                    String newString=oldStrng.replace(oldStrng_newline, "\r\n" + oldStrng_newline);
//                    StringBuffer sb = new StringBuffer(oldStrng);
//                    sb.insert((oldStrng.length() - 1 - newlineIndex), "\r\n");
//                    tv.setText(sb.toString());
//                }
//            }
//        }
//    }

    public static void setViewHigh(View view, float scale) {
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new SetViewHigh(view, scale));
        }
    }

    public static class SetViewHigh implements ViewTreeObserver.OnGlobalLayoutListener {
        private View view;
        private float scale;
//        private Boolean isSetMinHeight;

        public SetViewHigh(View view, float scale) {
            this.view = view;
            this.scale = scale;
//            this.isSetMinHeight=false;
        }

        public SetViewHigh(View view, float scale, Boolean isSetMinHeight) {
            this.view = view;
            this.scale = scale;
//            this.isSetMinHeight=isSetMinHeight;
        }

        @Override
        public void onGlobalLayout() {
            view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            Log.d(TAG, "SetViewHigh.onGlobalLayout. w:"
                    + view.getMeasuredWidth() + ";h:"
                    + view.getMeasuredHeight());
            ViewGroup.LayoutParams lp_view = view.getLayoutParams();
            Log.d(TAG, "lp_view.height:" + lp_view.height);
            lp_view.height = (int) ((float) (view.getMeasuredWidth()) * scale);
            Log.d(TAG, "lp_view.height:" + lp_view.height);
            view.setLayoutParams(lp_view);

//            if(isSetMinHeight){
//                ViewGroup view_Parent=(ViewGroup)view.getParent();
//                Log.d(TAG,
//                        "view_Parent.getMeasuredWidth():"
//                                + view_Parent.getMeasuredWidth() + ",view_Parent.getMeasuredHeight():"
//                                + view_Parent.getMeasuredHeight());
////                view_Parent.setMinimumHeight(lp_img.height);
//                ViewGroup.LayoutParams lp_Parent = view_Parent.getLayoutParams();
//                Log.d(TAG, "lp_Parent.height:" + lp_Parent.height);
//                lp_Parent.height = (int) ((float) (view.getMeasuredWidth()) * scale);
//                Log.d(TAG, "lp_Parent.height:" + lp_Parent.height);
//                view_Parent.setLayoutParams(lp_Parent);
//            }

        }
    }
}
