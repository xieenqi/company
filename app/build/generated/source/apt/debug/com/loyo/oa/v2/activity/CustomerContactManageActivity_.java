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
import android.widget.LinearLayout;
import android.widget.TextView;
import com.loyo.oa.v2.R.id;
import com.loyo.oa.v2.R.layout;
import com.loyo.oa.v2.beans.Customer;
import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class CustomerContactManageActivity_
    extends CustomerContactManageActivity
    implements HasViews, OnViewChangedListener
{

    private final OnViewChangedNotifier onViewChangedNotifier_ = new OnViewChangedNotifier();
    public final static String M_CUSTOMER_EXTRA = "Customer";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
        setContentView(layout.activity_customer_contact_manage);
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

    public static CustomerContactManageActivity_.IntentBuilder_ intent(Context context) {
        return new CustomerContactManageActivity_.IntentBuilder_(context);
    }

    public static CustomerContactManageActivity_.IntentBuilder_ intent(android.app.Fragment fragment) {
        return new CustomerContactManageActivity_.IntentBuilder_(fragment);
    }

    public static CustomerContactManageActivity_.IntentBuilder_ intent(android.support.v4.app.Fragment supportFragment) {
        return new CustomerContactManageActivity_.IntentBuilder_(supportFragment);
    }

    @Override
    public void onViewChanged(HasViews hasViews) {
        layout_back = ((ViewGroup) hasViews.findViewById(id.layout_back));
        tv_title = ((TextView) hasViews.findViewById(id.tv_title));
        layout_add = ((ViewGroup) hasViews.findViewById(id.layout_add));
        layout_container = ((LinearLayout) hasViews.findViewById(id.layout_container));
        if (layout_back!= null) {
            layout_back.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    CustomerContactManageActivity_.this.back();
                }

            }
            );
        }
        if (layout_add!= null) {
            layout_add.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    CustomerContactManageActivity_.this.addNewContact();
                }

            }
            );
        }
        initViews();
    }

    private void injectExtras_() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_!= null) {
            if (extras_.containsKey(M_CUSTOMER_EXTRA)) {
                mCustomer = ((Customer) extras_.getSerializable(M_CUSTOMER_EXTRA));
            }
        }
    }

    @Override
    public void setIntent(Intent newIntent) {
        super.setIntent(newIntent);
        injectExtras_();
    }

    public static class IntentBuilder_
        extends ActivityIntentBuilder<CustomerContactManageActivity_.IntentBuilder_>
    {

        private android.app.Fragment fragment_;
        private android.support.v4.app.Fragment fragmentSupport_;

        public IntentBuilder_(Context context) {
            super(context, CustomerContactManageActivity_.class);
        }

        public IntentBuilder_(android.app.Fragment fragment) {
            super(fragment.getActivity(), CustomerContactManageActivity_.class);
            fragment_ = fragment;
        }

        public IntentBuilder_(android.support.v4.app.Fragment fragment) {
            super(fragment.getActivity(), CustomerContactManageActivity_.class);
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

        public CustomerContactManageActivity_.IntentBuilder_ mCustomer(Customer mCustomer) {
            return super.extra(M_CUSTOMER_EXTRA, ((Serializable) mCustomer));
        }

    }

}
