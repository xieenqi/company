//
// DO NOT EDIT THIS FILE.Generated using AndroidAnnotations 3.3.2.
//  You can create a larger work that contains this file and distribute that work under terms of your choice.
//


package com.loyo.oa.v2.activity;

import java.io.Serializable;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import com.loyo.oa.v2.R.id;
import com.loyo.oa.v2.R.layout;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.tool.customview.PagerSlidingTabStrip;
import org.androidannotations.api.SdkVersionHelper;
import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ProjectInfoActivity_
    extends ProjectInfoActivity
    implements HasViews, OnViewChangedListener
{

    private final OnViewChangedNotifier onViewChangedNotifier_ = new OnViewChangedNotifier();
    public final static String PROJECT_EXTRA = "project";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
        setContentView(layout.activity_project_info);
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        injectExtras_();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        onViewChangedNotifier_.notifyViewChanged(this);
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        onViewChangedNotifier_.notifyViewChanged(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static ProjectInfoActivity_.IntentBuilder_ intent(Context context) {
        return new ProjectInfoActivity_.IntentBuilder_(context);
    }

    public static ProjectInfoActivity_.IntentBuilder_ intent(android.app.Fragment fragment) {
        return new ProjectInfoActivity_.IntentBuilder_(fragment);
    }

    public static ProjectInfoActivity_.IntentBuilder_ intent(android.support.v4.app.Fragment supportFragment) {
        return new ProjectInfoActivity_.IntentBuilder_(supportFragment);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (((SdkVersionHelper.getSdkInt()< 5)&&(keyCode == KeyEvent.KEYCODE_BACK))&&(event.getRepeatCount() == 0)) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onViewChanged(HasViews hasViews) {
        tabs = ((PagerSlidingTabStrip) hasViews.findViewById(id.tabs));
        img_title_left = ((ViewGroup) hasViews.findViewById(id.img_title_left));
        img_project_status = ((ImageView) hasViews.findViewById(id.img_project_status));
        tv_title_1 = ((TextView) hasViews.findViewById(id.tv_title_1));
        tv_project_title = ((TextView) hasViews.findViewById(id.tv_project_title));
        tv_project_extra = ((TextView) hasViews.findViewById(id.tv_project_extra));
        img_title_right = ((ViewGroup) hasViews.findViewById(id.img_title_right));
        layout_project_des = ((ViewGroup) hasViews.findViewById(id.layout_project_des));
        pager = ((ViewPager) hasViews.findViewById(id.pager));
        if (img_title_left!= null) {
            img_title_left.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    ProjectInfoActivity_.this.onClick(view);
                }

            }
            );
        }
        if (img_title_right!= null) {
            img_title_right.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    ProjectInfoActivity_.this.onClick(view);
                }

            }
            );
        }
        if (layout_project_des!= null) {
            layout_project_des.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    ProjectInfoActivity_.this.onClick(view);
                }

            }
            );
        }
        initViews();
    }

    private void injectExtras_() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_!= null) {
            if (extras_.containsKey(PROJECT_EXTRA)) {
                project = ((Project) extras_.getSerializable(PROJECT_EXTRA));
            }
        }
    }

    @Override
    public void setIntent(Intent newIntent) {
        super.setIntent(newIntent);
        injectExtras_();
    }

    public static class IntentBuilder_
        extends ActivityIntentBuilder<ProjectInfoActivity_.IntentBuilder_>
    {

        private android.app.Fragment fragment_;
        private android.support.v4.app.Fragment fragmentSupport_;

        public IntentBuilder_(Context context) {
            super(context, ProjectInfoActivity_.class);
        }

        public IntentBuilder_(android.app.Fragment fragment) {
            super(fragment.getActivity(), ProjectInfoActivity_.class);
            fragment_ = fragment;
        }

        public IntentBuilder_(android.support.v4.app.Fragment fragment) {
            super(fragment.getActivity(), ProjectInfoActivity_.class);
            fragmentSupport_ = fragment;
        }

        @Override
        public void startForResult(int requestCode) {
            if (fragmentSupport_!= null) {
                fragmentSupport_.startActivityForResult(intent, requestCode);
            } else {
                if (fragment_!= null) {
                    if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                        fragment_.startActivityForResult(intent, requestCode, lastOptions);
                    } else {
                        fragment_.startActivityForResult(intent, requestCode);
                    }
                } else {
                    if (context instanceof Activity) {
                        Activity activity = ((Activity) context);
                        ActivityCompat.startActivityForResult(activity, intent, requestCode, lastOptions);
                    } else {
                        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                            context.startActivity(intent, lastOptions);
                        } else {
                            context.startActivity(intent);
                        }
                    }
                }
            }
        }

        public ProjectInfoActivity_.IntentBuilder_ project(Project project) {
            return super.extra(PROJECT_EXTRA, ((Serializable) project));
        }

    }

}
