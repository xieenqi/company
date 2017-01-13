/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.loyo.oa.pulltorefresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;

import com.loyo.oa.pulltorefresh.internal.IndicatorLayout;
import com.loyo.oa.v2.R;


public class PullToRefreshRecyclerView2 extends PullToRefreshBase<RecyclerView> {

	private static FrameLayout.LayoutParams convertEmptyViewLayoutParams(ViewGroup.LayoutParams lp) {
		FrameLayout.LayoutParams newLp = null;

		if (null != lp) {
			newLp = new FrameLayout.LayoutParams(lp);

			if (lp instanceof LayoutParams) {
				newLp.gravity = ((LayoutParams) lp).gravity;
			} else {
				newLp.gravity = Gravity.CENTER;
			}
		}

		return newLp;
	}

	private RecyclerView.OnScrollListener mOnScrollListener;
	private OnLastItemVisibleListener mOnLastItemVisibleListener;
	private View mEmptyView;

	private IndicatorLayout mIndicatorIvTop;
	private IndicatorLayout mIndicatorIvBottom;

	private boolean mShowIndicator;
	private boolean mScrollEmptyView = true;

	public PullToRefreshRecyclerView2(Context context) {
		super(context);
		setup();
	}

	public PullToRefreshRecyclerView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup();
	}

	public PullToRefreshRecyclerView2(Context context, Mode mode) {
		super(context, mode);
		setup();

	}

	public PullToRefreshRecyclerView2(Context context, Mode mode, AnimationStyle animStyle) {
		super(context, mode, animStyle);
		setup();
	}

	/**
	 * @return Either {@link Orientation#VERTICAL} or
	 * {@link Orientation#HORIZONTAL} depending on the scroll direction.
	 */
	@Override
	public Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}

	/**
	 * This is implemented by derived classes to return the created View. If you
	 * need to use a custom View (such as a custom ListView), override this
	 * method and return an instance of your custom class.
	 * <p/>
	 * Be sure to set the ID of the view in this method, especially if you're
	 * using a ListActivity or ListFragment.
	 *
	 * @param context Context to create view with
	 * @param attrs   AttributeSet from wrapped class. Means that anything you
	 *                include in the XML layout declaration will be routed to the
	 *                created View
	 * @return New instance of the Refreshable View
	 */
	@Override
	protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
		RecyclerView view = new RecyclerView(context, attrs);
		return view;
	}

	private void setup() {
		mRefreshableView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			public void onScrollStateChanged(RecyclerView recyclerView, int newState){
				if (newState == RecyclerView.SCROLL_STATE_IDLE && null != mOnLastItemVisibleListener) {
					mOnLastItemVisibleListener.onLastItemVisible();
				}

				if (null != mOnScrollListener) {
					mOnScrollListener.onScrollStateChanged(recyclerView, newState);
				}
			}

			public void onScrolled(RecyclerView recyclerView, int dx, int dy){

				// If we're showing the indicator, check positions...
				if (getShowIndicatorInternal()) {
					updateIndicatorViewsVisibility();
				}

				// Finally call OnScrollListener if we have one
				if (null != mOnScrollListener) {
					mOnScrollListener.onScrolled(recyclerView, dx, dy);
				}
			}
		});
	}

	/**
	 * Gets whether an indicator graphic should be displayed when the View is in
	 * a state where a Pull-to-Refresh can happen. An example of this state is
	 * when the Adapter View is scrolled to the top and the mode is set to
	 * {@link Mode#PULL_FROM_START}. The default value is <var>true</var> if
	 * {@link PullToRefreshBase#isPullToRefreshOverScrollEnabled()
	 * isPullToRefreshOverScrollEnabled()} returns false.
	 *
	 * @return true if the indicators will be shown
	 */
	public boolean getShowIndicator() {
		return mShowIndicator;
	}

	/**
	 * Pass-through method for {@link PullToRefreshBase#getRefreshableView()
	 * getRefreshableView()}.
	 * {@link AdapterView#setAdapter(Adapter)}
	 * setAdapter(adapter)}. This is just for convenience!
	 * 
	 * @param adapter - Adapter to set
	 */
	public void setAdapter(RecyclerView.Adapter adapter) {
		mRefreshableView.setAdapter(adapter);
	}

	/**
	 * Sets the Empty View to be used by the Adapter View.
	 * <p/>
	 * We need it handle it ourselves so that we can Pull-to-Refresh when the
	 * Empty View is shown.
	 * <p/>
	 * Please note, you do <strong>not</strong> usually need to call this method
	 * yourself. Calling setEmptyView on the AdapterView will automatically call
	 * this method and set everything up. This includes when the Android
	 * Framework automatically sets the Empty View based on it's ID.
	 * 
	 * @param newEmptyView - Empty View to be used
	 */
	public final void setEmptyView(View newEmptyView) {
//		FrameLayout refreshableViewWrapper = getRefreshableViewWrapper();
//
//		if (null != newEmptyView) {
//			// New view needs to be clickable so that Android recognizes it as a
//			// target for Touch Events
//			newEmptyView.setClickable(true);
//
//			ViewParent newEmptyViewParent = newEmptyView.getParent();
//			if (null != newEmptyViewParent && newEmptyViewParent instanceof ViewGroup) {
//				((ViewGroup) newEmptyViewParent).removeView(newEmptyView);
//			}
//
//			// We need to convert any LayoutParams so that it works in our
//			// FrameLayout
//			FrameLayout.LayoutParams lp = convertEmptyViewLayoutParams(newEmptyView.getLayoutParams());
//			if (null != lp) {
//				refreshableViewWrapper.addView(newEmptyView, lp);
//			} else {
//				refreshableViewWrapper.addView(newEmptyView);
//			}
//		}
//
//		if (mRefreshableView instanceof EmptyViewMethodAccessor) {
//			((EmptyViewMethodAccessor) mRefreshableView).setEmptyViewInternal(newEmptyView);
//		} else {
//			mRefreshableView.setEmptyView(newEmptyView);
//		}
//		mEmptyView = newEmptyView;
	}

	/**
	 * Pass-through method for {@link PullToRefreshBase#getRefreshableView()
	 * getRefreshableView()}.
	 * {@link AdapterView#setOnItemClickListener(OnItemClickListener)
	 * setOnItemClickListener(listener)}. This is just for convenience!
	 * 
	 * @param listener - OnItemClickListener to use
	 */
	public void setOnItemClickListener(OnItemClickListener listener) {
		// mRefreshableView.setOnItemClickListener(listener);
	}

	public final void setOnLastItemVisibleListener(OnLastItemVisibleListener listener) {
		mOnLastItemVisibleListener = listener;
	}

	public final void setOnScrollListener(RecyclerView.OnScrollListener listener) {
		mOnScrollListener = listener;
	}

	public final void setScrollEmptyView(boolean doScroll) {
		mScrollEmptyView = doScroll;
	}

	/**
	 * Sets whether an indicator graphic should be displayed when the View is in
	 * a state where a Pull-to-Refresh can happen. An example of this state is
	 * when the Adapter View is scrolled to the top and the mode is set to
	 * {@link Mode#PULL_FROM_START}
	 * 
	 * @param showIndicator - true if the indicators should be shown.
	 */
	public void setShowIndicator(boolean showIndicator) {
		mShowIndicator = showIndicator;

		if (getShowIndicatorInternal()) {
			// If we're set to Show Indicator, add/update them
			addIndicatorViews();
		} else {
			// If not, then remove then
			removeIndicatorViews();
		}
	}

	;

	@Override
	protected void onPullToRefresh() {
		super.onPullToRefresh();

		if (getShowIndicatorInternal()) {
			switch (getCurrentMode()) {
				case PULL_FROM_END:
					mIndicatorIvBottom.pullToRefresh();
					break;
				case PULL_FROM_START:
					mIndicatorIvTop.pullToRefresh();
					break;
				default:
					// NO-OP
					break;
			}
		}
	}

	protected void onRefreshing(boolean doScroll) {
		super.onRefreshing(doScroll);

		if (getShowIndicatorInternal()) {
			updateIndicatorViewsVisibility();
		}
	}

	@Override
	protected void onReleaseToRefresh() {
		super.onReleaseToRefresh();

		if (getShowIndicatorInternal()) {
			switch (getCurrentMode()) {
				case PULL_FROM_END:
					mIndicatorIvBottom.releaseToRefresh();
					break;
				case PULL_FROM_START:
					mIndicatorIvTop.releaseToRefresh();
					break;
				default:
					// NO-OP
					break;
			}
		}
	}

	@Override
	protected void onReset() {
		super.onReset();

		if (getShowIndicatorInternal()) {
			updateIndicatorViewsVisibility();
		}
	}

	@Override
	protected void handleStyledAttributes(TypedArray a) {
		// Set Show Indicator to the XML value, or default value
		mShowIndicator = a.getBoolean(R.styleable.PullToRefresh_ptrShowIndicator, !isPullToRefreshOverScrollEnabled());
	}

	protected boolean isReadyForPullStart() {
		View view = getRefreshableView().getChildAt(0);

		if (view != null) {
			return view.getTop() >= getRefreshableView().getTop();
		}
		return false;
	}

	protected boolean isReadyForPullEnd() {
//		View view = getRefreshableView().getChildAt(getRefreshableView().getChildCount() - 1);
//		if (null != view) {
//			return getRefreshableView().getBottom() >= view.getBottom();
//		}
// 		return false;
		return !getRefreshableView().canScrollVertically(1);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (null != mEmptyView && !mScrollEmptyView) {
			mEmptyView.scrollTo(-l, -t);
		}
	}

	@Override
	protected void updateUIForMode() {
		super.updateUIForMode();

		// Check Indicator Views consistent with new Mode
		if (getShowIndicatorInternal()) {
			addIndicatorViews();
		} else {
			removeIndicatorViews();
		}
	}

	private void addIndicatorViews() {
		Mode mode = getMode();
		FrameLayout refreshableViewWrapper = getRefreshableViewWrapper();

		if (mode.showHeaderLoadingLayout() && null == mIndicatorIvTop) {
			// If the mode can pull down, and we don't have one set already
			mIndicatorIvTop = new IndicatorLayout(getContext(), Mode.PULL_FROM_START);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.rightMargin = getResources().getDimensionPixelSize(R.dimen.indicator_right_padding);
			params.gravity = Gravity.TOP | Gravity.RIGHT;
			refreshableViewWrapper.addView(mIndicatorIvTop, params);

		} else if (!mode.showHeaderLoadingLayout() && null != mIndicatorIvTop) {
			// If we can't pull down, but have a View then remove it
			refreshableViewWrapper.removeView(mIndicatorIvTop);
			mIndicatorIvTop = null;
		}

		if (mode.showFooterLoadingLayout() && null == mIndicatorIvBottom) {
			// If the mode can pull down, and we don't have one set already
			mIndicatorIvBottom = new IndicatorLayout(getContext(), Mode.PULL_FROM_END);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.rightMargin = getResources().getDimensionPixelSize(R.dimen.indicator_right_padding);
			params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
			refreshableViewWrapper.addView(mIndicatorIvBottom, params);

		} else if (!mode.showFooterLoadingLayout() && null != mIndicatorIvBottom) {
			// If we can't pull down, but have a View then remove it
			refreshableViewWrapper.removeView(mIndicatorIvBottom);
			mIndicatorIvBottom = null;
		}
	}

	private boolean getShowIndicatorInternal() {
		return mShowIndicator && isPullToRefreshEnabled();
	}

	private void removeIndicatorViews() {
		if (null != mIndicatorIvTop) {
			getRefreshableViewWrapper().removeView(mIndicatorIvTop);
			mIndicatorIvTop = null;
		}

		if (null != mIndicatorIvBottom) {
			getRefreshableViewWrapper().removeView(mIndicatorIvBottom);
			mIndicatorIvBottom = null;
		}
	}

	private void updateIndicatorViewsVisibility() {
		if (null != mIndicatorIvTop) {
			if (!isRefreshing() && isReadyForPullStart()) {
				if (!mIndicatorIvTop.isVisible()) {
					mIndicatorIvTop.show();
				}
			} else {
				if (mIndicatorIvTop.isVisible()) {
					mIndicatorIvTop.hide();
				}
			}
		}

		if (null != mIndicatorIvBottom) {
			if (!isRefreshing() && isReadyForPullEnd()) {
				if (!mIndicatorIvBottom.isVisible()) {
					mIndicatorIvBottom.show();
				}
			} else {
				if (mIndicatorIvBottom.isVisible()) {
					mIndicatorIvBottom.hide();
				}
			}
		}
	}
}
