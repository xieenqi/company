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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import com.loyo.oa.v2.R.id;
import com.loyo.oa.v2.R.layout;
import com.loyo.oa.v2.beans.Task;
import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class TasksInfoScoreActivity_
    extends TasksInfoScoreActivity
    implements HasViews, OnViewChangedListener
{

    private final OnViewChangedNotifier onViewChangedNotifier_ = new OnViewChangedNotifier();
    public final static String M_TASK_EXTRA = "mTask";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
        setContentView(layout.activity_tasks_info_score);
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

    public static TasksInfoScoreActivity_.IntentBuilder_ intent(Context context) {
        return new TasksInfoScoreActivity_.IntentBuilder_(context);
    }

    public static TasksInfoScoreActivity_.IntentBuilder_ intent(android.app.Fragment fragment) {
        return new TasksInfoScoreActivity_.IntentBuilder_(fragment);
    }

    public static TasksInfoScoreActivity_.IntentBuilder_ intent(android.support.v4.app.Fragment supportFragment) {
        return new TasksInfoScoreActivity_.IntentBuilder_(supportFragment);
    }

    @Override
    public void onViewChanged(HasViews hasViews) {
        btn_task_agree = ((Button) hasViews.findViewById(id.btn_task_agree));
        edt_content = ((EditText) hasViews.findViewById(id.edt_content));
        ratingBar_Task = ((RatingBar) hasViews.findViewById(id.ratingBar_Task));
        btn_task_notagree = ((Button) hasViews.findViewById(id.btn_task_notagree));
        img_title_left = ((ViewGroup) hasViews.findViewById(id.img_title_left));
        if (img_title_left!= null) {
            img_title_left.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    TasksInfoScoreActivity_.this.onClick(view);
                }

            }
            );
        }
        if (btn_task_agree!= null) {
            btn_task_agree.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    TasksInfoScoreActivity_.this.onClick(view);
                }

            }
            );
        }
        if (btn_task_notagree!= null) {
            btn_task_notagree.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    TasksInfoScoreActivity_.this.onClick(view);
                }

            }
            );
        }
        init();
    }

    private void injectExtras_() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_!= null) {
            if (extras_.containsKey(M_TASK_EXTRA)) {
                mTask = ((Task) extras_.getSerializable(M_TASK_EXTRA));
            }
        }
    }

    @Override
    public void setIntent(Intent newIntent) {
        super.setIntent(newIntent);
        injectExtras_();
    }

    public static class IntentBuilder_
        extends ActivityIntentBuilder<TasksInfoScoreActivity_.IntentBuilder_>
    {

        private android.app.Fragment fragment_;
        private android.support.v4.app.Fragment fragmentSupport_;

        public IntentBuilder_(Context context) {
            super(context, TasksInfoScoreActivity_.class);
        }

        public IntentBuilder_(android.app.Fragment fragment) {
            super(fragment.getActivity(), TasksInfoScoreActivity_.class);
            fragment_ = fragment;
        }

        public IntentBuilder_(android.support.v4.app.Fragment fragment) {
            super(fragment.getActivity(), TasksInfoScoreActivity_.class);
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

        public TasksInfoScoreActivity_.IntentBuilder_ mTask(Task mTask) {
            return super.extra(M_TASK_EXTRA, ((Serializable) mTask));
        }

    }

}