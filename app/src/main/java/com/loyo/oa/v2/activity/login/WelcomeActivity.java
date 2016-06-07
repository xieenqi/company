package com.loyo.oa.v2.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.MainActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.tool.SharedUtil;

/**
 * 启动
 * Created xnq 16/1/18.
 */
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.welcome_pager);
        SharedUtil.putBoolean(getApplicationContext(), ExtraAndResult.WELCOM_KEY, true);
        viewPager.setAdapter(new PagerAdapter() {

            private static final int COUNT = 4;

            private final int[] IMAGE_RES = new int[]{
                    R.drawable.welcome_01,
                    R.drawable.welcome_02,
                    R.drawable.welcome_03,
                    R.drawable.welcome_04
            };

            @Override
            public int getCount() {
                return IMAGE_RES.length;
            }

            @Override
            public boolean isViewFromObject(final View view, final Object o) {
                return view == o;
            }

            @Override
            public void destroyItem(final ViewGroup container, final int position, final Object object) {
                container.removeView((View) object);
            }

            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_welcome, container, false);
                container.addView(view);
                ImageView imageView = (ImageView) view.findViewById(R.id.welcomeImage);
                imageView.setImageResource(IMAGE_RES[position]);
                ImageView buttonOk = (ImageView) view.findViewById(R.id.welcomeOkButton);
                if (position == getCount() - 1) {
                    buttonOk.setVisibility(View.VISIBLE);
                    buttonOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            Intent intent = new Intent();
                            intent.setClass(WelcomeActivity.this,
                                    TextUtils.isEmpty(MainApp.getToken()) ? LoginActivity.class : MainActivity_.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
                return view;
            }
        });
//        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.welcomePagerIndicator);
//        indicator.setViewPager(viewPager);

    }

}
