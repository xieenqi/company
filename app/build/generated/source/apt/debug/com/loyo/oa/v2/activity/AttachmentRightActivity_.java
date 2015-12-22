//
// DO NOT EDIT THIS FILE.Generated using AndroidAnnotations 3.3.2.
//  You can create a larger work that contains this file and distribute that work under terms of your choice.
//


package com.loyo.oa.v2.activity;

import java.io.Serializable;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import com.loyo.oa.v2.R.id;
import com.loyo.oa.v2.R.layout;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.User;
import org.androidannotations.api.SdkVersionHelper;
import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class AttachmentRightActivity_
    extends AttachmentRightActivity
    implements HasViews, OnViewChangedListener
{

    private final OnViewChangedNotifier onViewChangedNotifier_ = new OnViewChangedNotifier();
    public final static String USERS_EXTRA = "users";
    public final static String M_ATTACHMENT_EXTRA = "data";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
        setContentView(layout.activity_attachment_right_setting);
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

    public static AttachmentRightActivity_.IntentBuilder_ intent(Context context) {
        return new AttachmentRightActivity_.IntentBuilder_(context);
    }

    public static AttachmentRightActivity_.IntentBuilder_ intent(android.app.Fragment fragment) {
        return new AttachmentRightActivity_.IntentBuilder_(fragment);
    }

    public static AttachmentRightActivity_.IntentBuilder_ intent(android.support.v4.app.Fragment supportFragment) {
        return new AttachmentRightActivity_.IntentBuilder_(supportFragment);
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
        rv_user = ((RecyclerView) hasViews.findViewById(id.rv_user));
        cb1 = ((CheckBox) hasViews.findViewById(id.cb1));
        layout_type1 = ((ViewGroup) hasViews.findViewById(id.layout_type1));
        {
            View view = hasViews.findViewById(id.img_title_left);
            if (view!= null) {
                view.setOnClickListener(new OnClickListener() {


                    @Override
                    public void onClick(View view) {
                        AttachmentRightActivity_.this.click();
                    }

                }
                );
            }
        }
        if (layout_type1 != null) {
            layout_type1 .setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    AttachmentRightActivity_.this.toggleCbAll();
                }

            }
            );
        }
        init();
    }

    @SuppressWarnings("unchecked")
    private void injectExtras_() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_!= null) {
            if (extras_.containsKey(USERS_EXTRA)) {
                users = ((ArrayList<User> ) extras_.getSerializable(USERS_EXTRA));
            }
            if (extras_.containsKey(M_ATTACHMENT_EXTRA)) {
                mAttachment = ((Attachment) extras_.getSerializable(M_ATTACHMENT_EXTRA));
            }
        }
    }

    @Override
    public void setIntent(Intent newIntent) {
        super.setIntent(newIntent);
        injectExtras_();
    }

    public static class IntentBuilder_
        extends ActivityIntentBuilder<AttachmentRightActivity_.IntentBuilder_>
    {

        private android.app.Fragment fragment_;
        private android.support.v4.app.Fragment fragmentSupport_;

        public IntentBuilder_(Context context) {
            super(context, AttachmentRightActivity_.class);
        }

        public IntentBuilder_(android.app.Fragment fragment) {
            super(fragment.getActivity(), AttachmentRightActivity_.class);
            fragment_ = fragment;
        }

        public IntentBuilder_(android.support.v4.app.Fragment fragment) {
            super(fragment.getActivity(), AttachmentRightActivity_.class);
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

        public AttachmentRightActivity_.IntentBuilder_ users(ArrayList<User> users) {
            return super.extra(USERS_EXTRA, ((Serializable) users));
        }

        public AttachmentRightActivity_.IntentBuilder_ mAttachment(Attachment mAttachment) {
            return super.extra(M_ATTACHMENT_EXTRA, ((Serializable) mAttachment));
        }

    }

}
