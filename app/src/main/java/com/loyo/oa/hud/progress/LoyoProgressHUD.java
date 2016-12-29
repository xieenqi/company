/*
 *    Copyright 2015 Kaopiz Software Co., Ltd.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.loyo.oa.hud.progress;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;

import static com.loyo.oa.hud.progress.LoyoProgressHUD.Style.LOYO_COMMIT;
import static com.loyo.oa.hud.progress.LoyoProgressHUD.Style.NONE;

public class LoyoProgressHUD {


    public static LoyoProgressHUD spinHUD(Context context) {
        return LoyoProgressHUD.create(context)
                .setDimAmount(0.382f)
                .setWindowColor(Color.WHITE)
                .setLabelColor(context.getResources().getColor(R.color.text66))
                .setStyle(Style.LOYO_SPIN);
    }

    public static LoyoProgressHUD commitHUD(Context context) {
        return LoyoProgressHUD.create(context)
                .setDimAmount(0.382f)
                .setLabel("正在提交数据...")
                .setMinSize(150, 100)
                .setWindowColor(Color.WHITE)
                .setLabelColor(context.getResources().getColor(R.color.text66))
                .setStyle(Style.LOYO_COMMIT);
    }

    public enum Style {
        NONE, SPIN_INDETERMINATE, PIE_DETERMINATE, ANNULAR_DETERMINATE, BAR_DETERMINATE, LOYO_SPIN, LOYO_COMMIT
    }

    // To avoid redundant APIs, all HUD functions will be forward to
    // a custom dialog
    private ProgressDialog mProgressDialog;
    private float mDimAmount;
    private int mWindowColor;
    private float mCornerRadius;
    private Context mContext;

    private int mAnimateSpeed;

    private int mMaxProgress;
    private boolean mIsAutoDismiss;

    private @ColorInt int labelColor;
    private @ColorInt int detailColor;

    private Style mStyle;

    public LoyoProgressHUD(Context context) {
        mContext = context;
        labelColor = Color.WHITE;
        detailColor = Color.WHITE;
        mStyle = NONE;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setLabelColor(labelColor);
        mProgressDialog.setDetailColor(detailColor);

        mDimAmount = 0;
        //noinspection deprecation
        mWindowColor = context.getResources().getColor(R.color.kprogresshud_default_color);
        mAnimateSpeed = 1;
        mCornerRadius = 10;
        mIsAutoDismiss = true;

        setStyle(Style.SPIN_INDETERMINATE);
    }

    /**
     * Create a new HUD. Have the same effect as the constructor.
     * For convenient only.
     * @param context Activity context that the HUD bound to
     * @return An unique HUD instance
     */
    public static LoyoProgressHUD create(Context context) {
        return new LoyoProgressHUD(context);
    }

  /**
   * Create a new HUD. specify the HUD style (if you use a custom view, you need {@code LoyoProgressHUD.create(Context context)}).
   *
   * @param context Activity context that the HUD bound to
   * @param style One of the LoyoProgressHUD.Style values
   * @return An unique HUD instance
   */
    public static LoyoProgressHUD create(Context context, Style style) {
        return new LoyoProgressHUD(context).setStyle(style);
    }

    /**
     * Specify the HUD style (not needed if you use a custom view)
     * @param style One of the LoyoProgressHUD.Style values
     * @return Current HUD
     */
    public LoyoProgressHUD setStyle(Style style) {
        View view = null;
        switch (style) {
            case SPIN_INDETERMINATE:
                view = new SpinView(mContext);
                break;
            case PIE_DETERMINATE:
                view = new PieView(mContext);
                break;
            case ANNULAR_DETERMINATE:
                view = new AnnularView(mContext);
                break;
            case BAR_DETERMINATE:
                view = new BarView(mContext);
                break;
            case LOYO_SPIN:
                view = new LoyoSpinView(mContext);
                break;
            case LOYO_COMMIT:
                view = new LoyoCommitView(mContext);
                break;
            // No custom view style here, because view will be added later
        }
        mStyle = style;
        mProgressDialog.setView(view);
        return this;
    }

    /**
     * Specify the dim area around the HUD, like in Dialog
     * @param dimAmount May take value from 0 to 1.
     *                  0 means no dimming, 1 mean darkness
     * @return Current HUD
     */
    public LoyoProgressHUD setDimAmount(float dimAmount) {
        if (dimAmount >= 0 && dimAmount <= 1) {
            mDimAmount = dimAmount;
        }
        return this;
    }

    /**
     * Set HUD size. If not the HUD view will use WRAP_CONTENT instead
     * @param width in dp
     * @param height in dp
     * @return Current HUD
     */
    public LoyoProgressHUD setSize(int width, int height) {
        mProgressDialog.setSize(width, height);
        return this;
    }

    public LoyoProgressHUD setMinSize(int width, int height) {
        mProgressDialog.setMinSize(width, height);
        return this;
    }

    /**
     * Specify the HUD background color
     * @param color ARGB color
     * @return Current HUD
     */
    public LoyoProgressHUD setWindowColor(int color) {
        mWindowColor = color;
        return this;
    }

    /**
     * Specify corner radius of the HUD (default is 10)
     * @param radius Corner radius in dp
     * @return Current HUD
     */
    public LoyoProgressHUD setCornerRadius(float radius) {
        mCornerRadius = radius;
        return this;
    }

    /**
     * Change animate speed relative to default. Only have effect when use with indeterminate style
     * @param scale 1 is default, 2 means double speed, 0.5 means half speed..etc.
     * @return Current HUD
     */
    public LoyoProgressHUD setAnimationSpeed(int scale) {
        mAnimateSpeed = scale;
        return this;
    }

    /**
     * Optional label to be displayed on the HUD
     * @return Current HUD
     */
    public LoyoProgressHUD setLabel(String label) {
        mProgressDialog.setLabel(label);
        return this;
    }

    /**
     * Optional detail description to be displayed on the HUD
     * @return Current HUD
     */
    public LoyoProgressHUD setDetailsLabel(String detailsLabel) {
        mProgressDialog.setDetailsLabel(detailsLabel);
        return this;
    }

    /**
     * Max value for use in one of the determinate styles
     * @return Current HUD
     */
    public LoyoProgressHUD setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
        return this;
    }

    /**
     * Set current progress. Only have effect when use with a determinate style, or a custom
     * view which implements Determinate interface.
     */
    public void setProgress(int progress) {
        mProgressDialog.setProgress(progress);
    }

    /**
     * Provide a custom view to be displayed.
     * @param view Must not be null
     * @return Current HUD
     */
    public LoyoProgressHUD setCustomView(View view) {
        if (view != null) {
            mProgressDialog.setView(view);
        } else {
            throw new RuntimeException("Custom view must not be null!");
        }
        return this;
    }

    /**
     * Specify whether this HUD can be cancelled by using back button (default is false)
     * @return Current HUD
     */
    public LoyoProgressHUD setCancellable(boolean isCancellable) {
        mProgressDialog.setCancelable(isCancellable);
        return this;
    }

    public LoyoProgressHUD setLabelColor(@ColorInt int color) {
        labelColor = color;
        mProgressDialog.setLabelColor(color);
        return this;
    }

    public LoyoProgressHUD setDetailColor(@ColorInt int color) {
        detailColor = color;
        mProgressDialog.setDetailColor(color);
        return this;
    }

    /**
     * Specify whether this HUD closes itself if progress reaches max. Default is true.
     * @return Current HUD
     */
    public LoyoProgressHUD setAutoDismiss(boolean isAutoDismiss) {
        mIsAutoDismiss = isAutoDismiss;
        return this;
    }

    public LoyoProgressHUD show() {
        if (!isShowing()) {
            mProgressDialog.show();
        }
        return this;
    }

    public boolean isShowing() {
        return mProgressDialog != null && mProgressDialog.isShowing();
    }

    public void dismiss() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void dismissWithSuccess() {
        dismissWithSuccess("提交成功");
    }

    public void dismissWithError() {
        dismissWithError("提交失败");
    }

    public void dismissWithSuccess(String msg) {
        if (mStyle == LOYO_COMMIT) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismissWithSuccess(msg);
            }
        }
        else {
            dismiss();
        }
    }

    public void dismissWithError(String msg) {
        if (mStyle == LOYO_COMMIT) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismissWithError(msg);
            }
        }
        else {
            dismiss();
        }
    }

    private class ProgressDialog extends Dialog {

        private Determinate mDeterminateView;
        private Indeterminate mIndeterminateView;
        private CommitIndeterminate mCommitIndeterminate;
        private View mView;

        private @ColorInt int labelColor;
        private @ColorInt int detailColor;

        private TextView mLabelText;
        private TextView mDetailsText;
        private String mLabel;
        private String mDetailsLabel;
        private FrameLayout mCustomViewContainer;
        private BackgroundLayout mBackgroundLayout;
        private int mWidth, mHeight;
        private int mMinWidth, mMinHeight;
		
        public ProgressDialog(Context context) {
            super(context);
            mMinWidth = 100;
            mMinHeight = 100;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.kprogresshud_hud);

            Window window = getWindow();
            window.setBackgroundDrawable(new ColorDrawable(0));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.dimAmount = mDimAmount;
            layoutParams.gravity = Gravity.CENTER;
            window.setAttributes(layoutParams);

            setCancellable(false);
            setCanceledOnTouchOutside(false);

            initViews();
        }

        private void initViews() {

            mBackgroundLayout = (BackgroundLayout) findViewById(R.id.background);
            mBackgroundLayout.setBaseColor(mWindowColor);
            mBackgroundLayout.setCornerRadius(mCornerRadius);
            updateBackgroundSize();

            mCustomViewContainer = (FrameLayout) findViewById(R.id.container);
            addViewToFrame(mView);

            if (mDeterminateView != null) {
                mDeterminateView.setMax(mMaxProgress);
            }
            if (mIndeterminateView != null) {
                mIndeterminateView.setAnimationSpeed(mAnimateSpeed);
            }

            mLabelText = (TextView) findViewById(R.id.label);
            if (mLabel != null) {
                mLabelText.setText(mLabel);
                mLabelText.setVisibility(View.VISIBLE);
            } else {
                mLabelText.setVisibility(View.GONE);
            }
            mDetailsText = (TextView) findViewById(R.id.details_label);
            if (mDetailsLabel != null) {
                mDetailsText.setText(mDetailsLabel);
                mDetailsText.setVisibility(View.VISIBLE);
            } else {
                mDetailsText.setVisibility(View.GONE);
            }
            mLabelText.setTextColor(labelColor);
            mDetailsText.setTextColor(detailColor);
        }

        private void addViewToFrame(View view) {
            if (view == null) return;
            int wrapParam = ViewGroup.LayoutParams.WRAP_CONTENT;
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(wrapParam, wrapParam);
            mCustomViewContainer.addView(view, params);
        }

        private void updateBackgroundSize() {
            ViewGroup.LayoutParams params = mBackgroundLayout.getLayoutParams();
            if (mWidth > 0) {
                params.width = Helper.dpToPixel(mWidth, getContext());
                params.height = Helper.dpToPixel(mHeight, getContext());
            }
            else {
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            }
            mBackgroundLayout.setLayoutParams(params);
            if (mMinWidth > 0) {
                mBackgroundLayout.setMinimumWidth(Helper.dpToPixel(mMinWidth, getContext()));
            }
            if (mMinHeight > 0) {
                mBackgroundLayout.setMinimumHeight(Helper.dpToPixel(mMinHeight, getContext()));
            }

        }

        public void setProgress(int progress) {
            if (mDeterminateView != null) {
                mDeterminateView.setProgress(progress);
                if (mIsAutoDismiss && progress >= mMaxProgress) {
                    dismiss();
                }
            }
        }

        public void setView(View view) {
            if (view != null) {
                if (view instanceof Determinate) {
                    mDeterminateView = (Determinate) view;
                }
                if (view instanceof Indeterminate) {
                    mIndeterminateView = (Indeterminate) view;
                }
                if (view instanceof CommitIndeterminate) {
                    mCommitIndeterminate = (CommitIndeterminate)view;
                }
                mView = view;
                if (isShowing()) {
                    mCustomViewContainer.removeAllViews();
                    addViewToFrame(view);
                }
            }
        }

        public void setLabel(String label) {
            mLabel = label;
            if (mLabelText != null) {
                mLabelText.setTextColor(labelColor);
                if (label != null) {
                    mLabelText.setText(label);
                    mLabelText.setVisibility(View.VISIBLE);
                } else {
                    mLabelText.setVisibility(View.GONE);
                }
            }
        }

        public void setDetailsLabel(String detailsLabel) {
            mDetailsLabel = detailsLabel;
            if (mDetailsText != null) {
                mDetailsText.setTextColor(detailColor);
                if (detailsLabel != null) {
                    mDetailsText.setText(detailsLabel);
                    mDetailsText.setVisibility(View.VISIBLE);
                } else {
                    mDetailsText.setVisibility(View.GONE);
                }
            }
        }

        public void setLabelColor(@ColorInt int color) {
            labelColor = color;
            if (mLabelText != null) {
                mLabelText.setTextColor(color);
            }
        }

        public void setDetailColor(@ColorInt int color) {
            detailColor = color;
            if (mDetailsText != null) {
                mDetailsText.setTextColor(color);
            }
        }

        public void setSize(int width, int height) {
            mWidth = width;
            mHeight = height;
            if (mBackgroundLayout != null) {
                updateBackgroundSize();
            }
        }

        public void setMinSize(int width, int height) {
            mMinWidth = width;
            mMinHeight = height;
            if (mBackgroundLayout != null) {
                updateBackgroundSize();
            }
        }

        public void dismissWithSuccess(String msg) {
            if (mCommitIndeterminate != null) {
                mCommitIndeterminate.loadSuccessState();
                setLabel(msg);
            }
            Helper.dispatchDelayed(new Runnable() {
                @Override
                public void run() {
                    (ProgressDialog.this).dismiss();
                }
            }, 1500);
        }

        public void dismissWithError(String msg) {
            if (mCommitIndeterminate != null) {
                mCommitIndeterminate.loadErrorState();
                setLabel(msg);
            }
            Helper.dispatchDelayed(new Runnable() {
                @Override
                public void run() {
                    (ProgressDialog.this).dismiss();
                }
            }, 1500);
        }
    }
}
