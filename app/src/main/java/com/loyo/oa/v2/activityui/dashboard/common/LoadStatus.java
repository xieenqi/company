package com.loyo.oa.v2.activityui.dashboard.common;

import android.view.View;

/**
 * Created by yyy on 16/12/28.
 */

public enum LoadStatus {

    LOAD{
        @Override
        public int getLoadView() {
            return View.VISIBLE;
        }

        @Override
        public int getRestView() {
            return View.GONE;
        }

        @Override
        public int getLayoutView() {
            return View.VISIBLE;
        }

        @Override
        public int getModelView() {
            return View.GONE;
        }
    },
    SUCCESS{
        @Override
        public int getLoadView() {
            return View.GONE;
        }

        @Override
        public int getRestView() {
            return View.GONE;
        }

        @Override
        public int getLayoutView() {
            return View.GONE;
        }

        @Override
        public int getModelView() {
            return View.VISIBLE;
        }
    },
    ERROR{
        @Override
        public int getLoadView() {
            return View.GONE;
        }

        @Override
        public int getRestView() {
            return View.VISIBLE;
        }

        @Override
        public int getLayoutView() {
            return View.VISIBLE;
        }

        @Override
        public int getModelView() {
            return View.GONE;
        }
    };

    public abstract int getLoadView();
    public abstract int getRestView();
    public abstract int getLayoutView();
    public abstract int getModelView();


}
