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
import android.widget.TextView;
import com.loyo.oa.v2.R.id;
import com.loyo.oa.v2.R.layout;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.tool.customview.RoundImageView;
import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ContactInfoActivity_
    extends ContactInfoActivity
    implements HasViews, OnViewChangedListener
{

    private final OnViewChangedNotifier onViewChangedNotifier_ = new OnViewChangedNotifier();
    public final static String USER_EXTRA = "user";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
        setContentView(layout.activity_contactinfo);
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

    public static ContactInfoActivity_.IntentBuilder_ intent(Context context) {
        return new ContactInfoActivity_.IntentBuilder_(context);
    }

    public static ContactInfoActivity_.IntentBuilder_ intent(android.app.Fragment fragment) {
        return new ContactInfoActivity_.IntentBuilder_(fragment);
    }

    public static ContactInfoActivity_.IntentBuilder_ intent(android.support.v4.app.Fragment supportFragment) {
        return new ContactInfoActivity_.IntentBuilder_(supportFragment);
    }

    @Override
    public void onViewChanged(HasViews hasViews) {
        tv_edit = ((TextView) hasViews.findViewById(id.tv_edit));
        tv_sex = ((TextView) hasViews.findViewById(id.tv_sex));
        layout_call = ((ViewGroup) hasViews.findViewById(id.layout_call));
        tv_deptname = ((TextView) hasViews.findViewById(id.tv_deptname));
        layout_action = ((ViewGroup) hasViews.findViewById(id.layout_action));
        tv_phone = ((TextView) hasViews.findViewById(id.tv_phone));
        layout_msg = ((ViewGroup) hasViews.findViewById(id.layout_msg));
        tv_birthday = ((TextView) hasViews.findViewById(id.tv_birthday));
        tv_qq = ((TextView) hasViews.findViewById(id.tv_qq));
        tv_weixin = ((TextView) hasViews.findViewById(id.tv_weixin));
        img_title_user = ((RoundImageView) hasViews.findViewById(id.img_title_user));
        tv_age = ((TextView) hasViews.findViewById(id.tv_age));
        layout_back = ((ViewGroup) hasViews.findViewById(id.layout_back));
        tv_realname = ((TextView) hasViews.findViewById(id.tv_realname));
        if (tv_edit!= null) {
            tv_edit.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    ContactInfoActivity_.this.onClick(view);
                }

            }
            );
        }
        if (layout_back!= null) {
            layout_back.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    ContactInfoActivity_.this.onClick(view);
                }

            }
            );
        }
        if (layout_call!= null) {
            layout_call.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    ContactInfoActivity_.this.onClick(view);
                }

            }
            );
        }
        if (layout_msg!= null) {
            layout_msg.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    ContactInfoActivity_.this.onClick(view);
                }

            }
            );
        }
        initViews();
    }

    private void injectExtras_() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_!= null) {
            if (extras_.containsKey(USER_EXTRA)) {
                user = ((User) extras_.getSerializable(USER_EXTRA));
            }
        }
    }

    @Override
    public void setIntent(Intent newIntent) {
        super.setIntent(newIntent);
        injectExtras_();
    }

    public static class IntentBuilder_
        extends ActivityIntentBuilder<ContactInfoActivity_.IntentBuilder_>
    {

        private android.app.Fragment fragment_;
        private android.support.v4.app.Fragment fragmentSupport_;

        public IntentBuilder_(Context context) {
            super(context, ContactInfoActivity_.class);
        }

        public IntentBuilder_(android.app.Fragment fragment) {
            super(fragment.getActivity(), ContactInfoActivity_.class);
            fragment_ = fragment;
        }

        public IntentBuilder_(android.support.v4.app.Fragment fragment) {
            super(fragment.getActivity(), ContactInfoActivity_.class);
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

        public ContactInfoActivity_.IntentBuilder_ user(User user) {
            return super.extra(USER_EXTRA, ((Serializable) user));
        }

    }

}
