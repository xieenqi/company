package com.loyo.oa.v2.activity.home.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.home.ActivityMainHome;
import com.loyo.oa.v2.activity.home.cusview.MyViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class AllFragment extends Fragment implements OnPageChangeListener {
    Button b;
    private int select = 0;
    private MyViewPager vPager;
    private Timer timer;
    private ArrayList<ImageView> pointviews = new ArrayList<ImageView>();
    private int[] ints = new int[]{R.drawable.attendance_popview, R.drawable.background_tab, R.drawable.bg_btn_cancel,
            R.drawable.bg_view_red_circle};
    private List<Integer> imgs = new ArrayList<Integer>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (Integer integer : ints) {
            imgs.add(integer);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_all, container, false);
		b = (Button) view.findViewById(R.id.button1);
        // 获取屏幕分辨率
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        int height = 180 * metric.heightPixels / 480;
		vPager = (MyViewPager) view.findViewById(R.id.viewpager);
        ll_point = (LinearLayout) view.findViewById(R.id.adddot);
		RelativeLayout.LayoutParams ll = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, height);
		vPager.setLayoutParams(ll);
        return view;
    }

    private void onInit() {
        //由于换成android-pre-support-v4.jar所以广告还是让他预先加载一页,不然滑过去空白一片在现实图片体验不好
        //如果需要底部tab预先加载直接换成android-support-v4.jar 在把广告vPager.setOffscreenPageLimit(1)这个给不要
		vPager.setOffscreenPageLimit(1);
		vPager.setOnPageChangeListener(this);
		vPager.setAdapter(new MyPagerAdapter());
        initPoint();
        draw_Point(0);
		if (imgs.size() > 1) {
			vPager.setCurrentItem(imgs.size() * 100, false);
			select = 100 * imgs.size();
		} else {
			vPager.setCurrentItem(0, false);
		}
		vPager.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					stopLoop();
					break;
				case MotionEvent.ACTION_UP:
					startLoop();
					break;
				}
				return false;
			}
		});
    }

    private void startLoop() {
        if (timer == null)// 当用户快速滑动的时候，touchup有几率触发两次，导致开启两个定时器，加个判断防止
        {
            timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Message message = handler.obtainMessage();
                    message.what = 2;
                    message.arg1 = select;
                    handler.sendMessage(message);
                    select++;
                }
            }, 3000, 3000);
        }
    }

    private void stopLoop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    MailListFragment f;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        final String name = makeFragmentName(ActivityMainHome.index, 1);
        onInit();
        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                f = (MailListFragment) getFragmentManager()
                        .findFragmentByTag(name);
                // TODO Auto-generated method stub
                if (f != null) {
                    f.onInIt();
                }

            }
        });
        System.out
                .println("~~~~~~~~~~~~~~~~~~~~~~fragment1-->onActivityCreated()");
    }

    private void initPoint() {
        ImageView imageView;
        for (int i = 0; i < 4; i++) {
            imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(R.drawable.asy);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            ll_point.addView(imageView, layoutParams);
            pointviews.add(imageView);
        }
    }

    private void draw_Point(int index) {
        for (int i = 0; i < pointviews.size(); i++) {
            pointviews.get(i).setImageResource(R.drawable.asy);
        }
        if (pointviews.size() > index) {
            pointviews.get(index)
                    .setImageResource(R.drawable.arrow_down);
        }
    }

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return imgs.size();
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return POSITION_NONE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.item_pic_bluetooth, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.pic);
            imageView.setBackgroundDrawable(getResources().getDrawable(
                    imgs.get(position % (imgs.size()))));
            container.addView(view);
            return view;
        }

        // 移除
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int arg0) {
		select = vPager.getCurrentItem();
        draw_Point(arg0 % imgs.size());
    }

    //这个保存fragment名字的方法,这是FragmentPagerAdapter源码中的，有了他就可以在fragment与fragment中通讯传值
    private String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~fragment1-->onDestroy()");
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~fragment1-->onPause()");
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~fragment1-->onResume()");
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~fragment1-->onStart()");
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~fragment1-->onStop()");
    }

    private LinearLayout ll_point;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (msg.what == 2) {
				vPager.setCurrentItem(msg.arg1);
            }
        }
    };
}
