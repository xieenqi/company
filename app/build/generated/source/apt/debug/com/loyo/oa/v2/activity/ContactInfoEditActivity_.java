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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import com.loyo.oa.v2.R.id;
import com.loyo.oa.v2.R.layout;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.tool.customview.RoundImageView;
import org.androidannotations.api.SdkVersionHelper;
import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ContactInfoEditActivity_
    extends ContactInfoEditActivity
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
        setContentView(layout.activity_contactinfo_edit);
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

    public static ContactInfoEditActivity_.IntentBuilder_ intent(Context context) {
        return new ContactInfoEditActivity_.IntentBuilder_(context);
    }

    public static ContactInfoEditActivity_.IntentBuilder_ intent(android.app.Fragment fragment) {
        return new ContactInfoEditActivity_.IntentBuilder_(fragment);
    }

    public static ContactInfoEditActivity_.IntentBuilder_ intent(android.support.v4.app.Fragment supportFragment) {
        return new ContactInfoEditActivity_.IntentBuilder_(supportFragment);
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
        iv_submit = ((ImageView) hasViews.findViewById(id.iv_submit));
        et_weixin = ((EditText) hasViews.findViewById(id.et_weixin));
        layout_mobile = ((ViewGroup) hasViews.findViewById(id.layout_mobile));
        layout_birthday = ((ViewGroup) hasViews.findViewById(id.layout_birthday));
        sex_male = ((RadioButton) hasViews.findViewById(id.sex_male));
        tv_mobile = ((TextView) hasViews.findViewById(id.tv_mobile));
        sex_famale = ((RadioButton) hasViews.findViewById(id.sex_famale));
        tv_birthday = ((TextView) hasViews.findViewById(id.tv_birthday));
        layout_set_avartar = ((ViewGroup) hasViews.findViewById(id.layout_set_avartar));
        et_qq = ((TextView) hasViews.findViewById(id.et_qq));
        tv_title = ((TextView) hasViews.findViewById(id.tv_title));
        tv_departments = ((TextView) hasViews.findViewById(id.tv_departments));
        img_title_user = ((RoundImageView) hasViews.findViewById(id.img_title_user));
        tv_age = ((TextView) hasViews.findViewById(id.tv_age));
        layout_back = ((ViewGroup) hasViews.findViewById(id.layout_back));
        tv_positions = ((TextView) hasViews.findViewById(id.tv_positions));
        if (layout_back!= null) {
            layout_back.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    ContactInfoEditActivity_.this.onClick(view);
                }

            }
            );
        }
        if (layout_set_avartar!= null) {
            layout_set_avartar.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    ContactInfoEditActivity_.this.onClick(view);
                }

            }
            );
        }
        if (layout_birthday!= null) {
            layout_birthday.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    ContactInfoEditActivity_.this.onClick(view);
                }

            }
            );
        }
        if (iv_submit!= null) {
            iv_submit.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    ContactInfoEditActivity_.this.onClick(view);
                }

            }
            );
            iv_submit.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    ContactInfoEditActivity_.this.onClick(view);
                }

            }
            );
        }
        if (layout_mobile!= null) {
            layout_mobile.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    ContactInfoEditActivity_.this.onClick(view);
                }

            }
            );
        }
        if (sex_famale!= null) {
            sex_famale.setOnCheckedChangeListener(new OnCheckedChangeListener() {


                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ContactInfoEditActivity_.this.onCheckChanged(buttonView, isChecked);
                }

            }
            );
        }
        if (sex_male!= null) {
            sex_male.setOnCheckedChangeListener(new OnCheckedChangeListener() {


                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ContactInfoEditActivity_.this.onCheckChanged(buttonView, isChecked);
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
        extends ActivityIntentBuilder<ContactInfoEditActivity_.IntentBuilder_>
    {

        private android.app.Fragment fragment_;
        private android.support.v4.app.Fragment fragmentSupport_;

        public IntentBuilder_(Context context) {
            super(context, ContactInfoEditActivity_.class);
        }

        public IntentBuilder_(android.app.Fragment fragment) {
            super(fragment.getActivity(), ContactInfoEditActivity_.class);
            fragment_ = fragment;
        }

        public IntentBuilder_(android.support.v4.app.Fragment fragment) {
            super(fragment.getActivity(), ContactInfoEditActivity_.class);
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

        public ContactInfoEditActivity_.IntentBuilder_ user(User user) {
            return super.extra(USER_EXTRA, ((Serializable) user));
        }

    }

}
