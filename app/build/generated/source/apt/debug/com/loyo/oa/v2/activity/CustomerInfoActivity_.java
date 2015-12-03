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
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.loyo.oa.v2.R.id;
import com.loyo.oa.v2.R.layout;
import com.loyo.oa.v2.beans.Customer;
import org.androidannotations.api.SdkVersionHelper;
import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class CustomerInfoActivity_
    extends CustomerInfoActivity
    implements HasViews, OnViewChangedListener
{

    private final OnViewChangedNotifier onViewChangedNotifier_ = new OnViewChangedNotifier();
    public final static String M_CUSTOMER_EXTRA = "Customer";
    public final static String M_CUSTOMER_ID_EXTRA = "CustomerId";
    private Handler handler_ = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
        setContentView(layout.activity_customer_info);
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

    public static CustomerInfoActivity_.IntentBuilder_ intent(Context context) {
        return new CustomerInfoActivity_.IntentBuilder_(context);
    }

    public static CustomerInfoActivity_.IntentBuilder_ intent(android.app.Fragment fragment) {
        return new CustomerInfoActivity_.IntentBuilder_(fragment);
    }

    public static CustomerInfoActivity_.IntentBuilder_ intent(android.support.v4.app.Fragment supportFragment) {
        return new CustomerInfoActivity_.IntentBuilder_(supportFragment);
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
        tv_industry = ((TextView) hasViews.findViewById(id.tv_industry));
        img_del_join_users = ((ImageView) hasViews.findViewById(id.img_del_join_users));
        layout_customer_join_users = ((ViewGroup) hasViews.findViewById(id.layout_customer_join_users));
        tv_address = ((EditText) hasViews.findViewById(id.tv_address));
        img_title_right = ((ViewGroup) hasViews.findViewById(id.img_title_right));
        tv_customer_join_users = ((TextView) hasViews.findViewById(id.tv_customer_join_users));
        tv_customer_creator = ((TextView) hasViews.findViewById(id.tv_customer_creator));
        layout_customer_responser = ((ViewGroup) hasViews.findViewById(id.layout_customer_responser));
        tv_labels = ((TextView) hasViews.findViewById(id.tv_labels));
        tv_district = ((TextView) hasViews.findViewById(id.tv_district));
        layout_customer_industry = ((ViewGroup) hasViews.findViewById(id.layout_customer_industry));
        img_title_left = ((ViewGroup) hasViews.findViewById(id.img_title_left));
        tv_customer_responser = ((TextView) hasViews.findViewById(id.tv_customer_responser));
        layout_customer_label = ((ViewGroup) hasViews.findViewById(id.layout_customer_label));
        edt_customer_memo = ((EditText) hasViews.findViewById(id.edt_customer_memo));
        container = ((LinearLayout) hasViews.findViewById(id.layout_customer_extra_info));
        img_refresh_address = ((ImageView) hasViews.findViewById(id.img_refresh_address));
        tv_customer_name = ((EditText) hasViews.findViewById(id.tv_customer_name));
        tv_customer_create_at = ((TextView) hasViews.findViewById(id.tv_customer_create_at));
        img_go_where = ((ImageView) hasViews.findViewById(id.img_go_where));
        layout_customer_district = ((ViewGroup) hasViews.findViewById(id.layout_customer_district));
        if (img_title_left!= null) {
            img_title_left.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    CustomerInfoActivity_.this.onClick(view);
                }

            }
            );
        }
        if (img_title_right!= null) {
            img_title_right.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    CustomerInfoActivity_.this.onClick(view);
                }

            }
            );
        }
        if (layout_customer_label!= null) {
            layout_customer_label.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    CustomerInfoActivity_.this.onClick(view);
                }

            }
            );
        }
        if (img_refresh_address!= null) {
            img_refresh_address.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    CustomerInfoActivity_.this.onClick(view);
                }

            }
            );
        }
        if (img_go_where!= null) {
            img_go_where.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    CustomerInfoActivity_.this.onClick(view);
                }

            }
            );
        }
        if (img_del_join_users!= null) {
            img_del_join_users.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    CustomerInfoActivity_.this.onClick(view);
                }

            }
            );
        }
        if (layout_customer_responser!= null) {
            layout_customer_responser.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    CustomerInfoActivity_.this.onClick(view);
                }

            }
            );
        }
        if (layout_customer_join_users!= null) {
            layout_customer_join_users.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    CustomerInfoActivity_.this.onClick(view);
                }

            }
            );
        }
        if (layout_customer_district!= null) {
            layout_customer_district.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    CustomerInfoActivity_.this.onClick(view);
                }

            }
            );
        }
        if (layout_customer_industry!= null) {
            layout_customer_industry.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    CustomerInfoActivity_.this.onClick(view);
                }

            }
            );
        }
        initUI();
    }

    private void injectExtras_() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_!= null) {
            if (extras_.containsKey(M_CUSTOMER_EXTRA)) {
                mCustomer = ((Customer) extras_.getSerializable(M_CUSTOMER_EXTRA));
            }
            if (extras_.containsKey(M_CUSTOMER_ID_EXTRA)) {
                mCustomerId = extras_.getString(M_CUSTOMER_ID_EXTRA);
            }
        }
    }

    @Override
    public void setIntent(Intent newIntent) {
        super.setIntent(newIntent);
        injectExtras_();
    }

    @Override
    public void setTag() {
        handler_.post(new Runnable() {


            @Override
            public void run() {
                CustomerInfoActivity_.super.setTag();
            }

        }
        );
    }

    public static class IntentBuilder_
        extends ActivityIntentBuilder<CustomerInfoActivity_.IntentBuilder_>
    {

        private android.app.Fragment fragment_;
        private android.support.v4.app.Fragment fragmentSupport_;

        public IntentBuilder_(Context context) {
            super(context, CustomerInfoActivity_.class);
        }

        public IntentBuilder_(android.app.Fragment fragment) {
            super(fragment.getActivity(), CustomerInfoActivity_.class);
            fragment_ = fragment;
        }

        public IntentBuilder_(android.support.v4.app.Fragment fragment) {
            super(fragment.getActivity(), CustomerInfoActivity_.class);
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

        public CustomerInfoActivity_.IntentBuilder_ mCustomer(Customer mCustomer) {
            return super.extra(M_CUSTOMER_EXTRA, ((Serializable) mCustomer));
        }

        public CustomerInfoActivity_.IntentBuilder_ mCustomerId(String mCustomerId) {
            return super.extra(M_CUSTOMER_ID_EXTRA, mCustomerId);
        }

    }

}