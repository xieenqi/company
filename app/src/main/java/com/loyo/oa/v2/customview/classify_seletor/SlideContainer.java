package com.loyo.oa.v2.customview.classify_seletor;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 主要用来处理下面显示的滑动和数据切换
 * 使用的是在fragment里面放来2个recyclerView滑动切换
 * Created by jie on 16/12/29.
 */

public class SlideContainer extends FrameLayout implements ItemAdapter.OnItemClickListener {
    private String TAG = "SlideContainer";
    private float startX = 0, startY = 0;//开始点击的坐标，用来处理滑动冲突
    private int level = 0;//当前的页码
    private float parallax = 0.2f;//视差因子
    private FrameLayout frameLayout1, frameLayout2, frameLayoutTop;//用frameLayout套 一下，主要是为了以后增加蒙层
    private RecyclerView recyclerView1, recyclerView2;
    private boolean needExchange = false; //是否需要交换页面
    private SlideContainListener slideContainListener;//获取数据的接口，把数据加载这个委托出去
    private boolean allowClick = true;//是否允许点击，避免动画过程中点击，造成数据混乱
    private Map<String, ClassifySeletorItem> path = new HashMap<>();//用来存储选择的路径


    private  ItemAdapter itemAdapter1, itemAdapter2;
    public SlideContainer(Context context) {
        super(context);
        throw new UnsupportedOperationException("不支持java代码实例化，T_T");
    }



    public SlideContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        frameLayout1 = new FrameLayout(context);
        recyclerView1 = new RecyclerView(context);
        frameLayout1.addView(recyclerView1);
        frameLayout2 = new FrameLayout(context);
        recyclerView2 = new RecyclerView(context);
        frameLayout2.addView(recyclerView2);
        addView(frameLayout1);
        addView(frameLayout2);

        recyclerView1.setLayoutManager(new LinearLayoutManager(context));
        recyclerView2.setLayoutManager(new LinearLayoutManager(context));
        frameLayoutTop = frameLayout2;//最开始，显示在上层的是fragnment2
        //必须设置背景颜色，不然透明会看起来页面错乱
        frameLayout1.setBackgroundColor(Color.WHITE);
        frameLayout2.setBackgroundColor(Color.WHITE);

        //设置适配器,数字序号对应：fragment1->recyclerVIew1->itemAdapter1
        itemAdapter1 = new ItemAdapter(context);
        itemAdapter1.setOnItemClickListener(this);
        recyclerView1.setAdapter(itemAdapter1);
         itemAdapter2 = new ItemAdapter(context);
        itemAdapter2.setOnItemClickListener(this);
        recyclerView2.setAdapter(itemAdapter2);

        recyclerView1.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        recyclerView2.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
    }


    public void setSingleSelete(boolean isSingleSelete){
        itemAdapter1.setSingleSelete(isSingleSelete);
        itemAdapter2.setSingleSelete(isSingleSelete);
    }
    /**
     * 设置当前是第几页，主要是点击导航的时候，直接切换到某一页,
     *
     * @param position
     */
    public void setPage(int position) {
        if (!allowClick) return;

        //删除多余的路径
        int size=path.size();;
        for (int i = position; i <size; i++) {
            path.remove(path.size() - 1);
        }
        level = position;
        getData(-1, path.get(level + ""));
    }

    @Override
    public void clickItemToLoadData(ItemAdapter.ItemViewHolder holder, int position, ClassifySeletorItem item) {
        if (!allowClick) return;
        //根据点击的item，加载下一页的数据
        if (null == item.getFinal()) {
            //如果不知道节点是不是最后一级,调用构造，去判断
            item.setFinal(slideContainListener.isFinal(item));
        }
        if (!item.getFinal()) {
            //不是最后一级，就加载新的一页的数据
            getData(position, item);
        }
    }

    @Override
    public Boolean isFinal(ClassifySeletorItem item) {
        return slideContainListener.isFinal(item);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        //处理滑动冲突
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                return false;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getX() - startX) - Math.abs(event.getY() - startY) > 10) {//10是 为了防止有的人肾虚，手抖，设置10个像素容差，避免点击不灵
                    return true;
                }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (level <= 1) return false;//最上面一级，不可以再滑动
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //当向右滑动
                if ((event.getX() - startX) > 0) {
                    if (frameLayout1 == frameLayoutTop) {
                        //按照比例手指滑动，移动当前页面
                        frameLayout1.setTranslationX((event.getX() - startX));
                        //造成视差
                        frameLayout2.setTranslationX(-(frameLayout2.getMeasuredWidth() * (parallax) - (event.getX() - startX) * parallax));
                    } else {
                        //按照比例手指滑动，移动当前页面
                        frameLayout2.setTranslationX((event.getX() - startX));
                        //造成视差
                        frameLayout1.setTranslationX(-(frameLayout1.getMeasuredWidth() * (parallax) - (event.getX() - startX) * parallax));
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //滑动超过页面宽度1/5就弹出来显示，否则弹回去
                if (event.getX() - startX > frameLayout1.getMeasuredWidth() / 5) {
                    //出去
                    if (frameLayout1 == frameLayoutTop) {
                        animate(frameLayout1.getMeasuredWidth(), frameLayout1);
                        animate(0, frameLayout2);
                    } else {
                        animate(frameLayout2.getMeasuredWidth(), frameLayout2);
                        animate(0, frameLayout1);
                    }
                    needExchange = true;
                } else {
                    //恢复
                    if (frameLayout1 == frameLayoutTop) {
                        animate(0, frameLayout1);
                        animate(-frameLayout2.getMeasuredWidth() * (parallax), frameLayout2);
                    } else {
                        animate(0, frameLayout2);
                        animate(-frameLayout2.getMeasuredWidth() * (parallax), frameLayout1);
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 直接交换页面，无动画
     */
    private void changePage() {
        final View view = getChildAt(0);
        removeView(view);
        addView(view);
        if (frameLayout1 == frameLayoutTop) {
            frameLayoutTop = frameLayout2;
        } else {
            frameLayoutTop = frameLayout1;
        }
    }

    //前进的动画和页面交换
    private void moveNextPage() {
        changePage();
        final View view = getChildAt(1);
        //前进动画
        ValueAnimator animator = ValueAnimator.ofFloat(view.getMeasuredWidth(), 0).setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setTranslationX((Float) valueAnimator.getAnimatedValue());
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                allowClick = false;//动画过程，禁止点击
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                allowClick = true;//动画过程结束，可以点击
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        animator.start();
    }

    //回退或者是恢复的动画
    private void animate(float end, final View view) {
        ValueAnimator animator = ValueAnimator.ofFloat(view.getTranslationX(), end).setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setTranslationX((Float) valueAnimator.getAnimatedValue());
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                allowClick = false;//动画过程，禁止点击
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //如果需要交换页面，需要在动画结束以后
                if (needExchange) {
                    needExchange = false;
                    level--;//把＋1的减回来
                    if (null != slideContainListener) {
                        //返回的时候，重新加载上一页的数据
                        List<ClassifySeletorItem> getDataListenerData = slideContainListener.pageBack(level, path.get((level - 1) + ""));//表示上一页
                        ItemAdapter itemAdapter;
                        if (null != getDataListenerData) {
                            //判断当前显示的是哪个recyclerView,加载新的数据到另一个view
                            if (frameLayoutTop == frameLayout1) {
                                itemAdapter = (ItemAdapter) recyclerView2.getAdapter();
                            } else {
                                itemAdapter = (ItemAdapter) recyclerView1.getAdapter();
                            }
                            itemAdapter.setData(getDataListenerData);
                        }
                    }
                    //出去
                    if (frameLayout1 == frameLayoutTop) {
                        removeView(frameLayout1);
                        addView(frameLayout1, 0);
                        frameLayoutTop = frameLayout2;
                    } else {
                        removeView(frameLayout2);
                        addView(frameLayout2, 0);
                        frameLayoutTop = frameLayout1;
                    }
                }
                allowClick = true;//动画过程结束，可以点击

            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        animator.start();
    }

    //根据点击的item，获取下一页的新数据
    public void getData(int itemPosition, ClassifySeletorItem item) {
        if (null == slideContainListener) return;
        List<ClassifySeletorItem> data;
        ItemAdapter itemAdapter;
        //第一次加载或者点击的获取全部分类
        if (null == item || (-1 == itemPosition && null == item)) {
            //获取首页的数据
            data = slideContainListener.getData(itemPosition, item, level);
            if (null == data) return;
            itemAdapter = (ItemAdapter) recyclerView2.getAdapter();
            level++;
            if ((-1 == itemPosition && null == item)) {
                //判断显示在那个recycler上面
                if (frameLayoutTop == frameLayout1) {
                    itemAdapter = (ItemAdapter) recyclerView2.getAdapter();
                } else {
                    itemAdapter = (ItemAdapter) recyclerView1.getAdapter();
                }
                changePage();
            }
        } else {
            //如果不是点击的导航，需要把点击加入到路径，点击的导航，不需要添加
            if (-1 != itemPosition) path.put("" + level, item);
            //加载下一页的数据
            data = slideContainListener.getData(itemPosition, item, level);
            if (null == data) return;
            //判断当前显示的是哪个recyclerView,加载新的数据到另一个view
            if (frameLayoutTop == frameLayout1) {
                itemAdapter = (ItemAdapter) recyclerView2.getAdapter();
            } else {
                itemAdapter = (ItemAdapter) recyclerView1.getAdapter();
            }
            level++;
            if (-1 != itemPosition) {
                moveNextPage();//页面动画
            } else {
                changePage();
            }
        }
        itemAdapter.setData(data);
    }

    public SlideContainListener getSlideContainListener() {
        return slideContainListener;
    }

    public void setSlideContainListener(SlideContainListener slideContainListener) {
        this.slideContainListener = slideContainListener;
        //获取第一页的初始化数据
        getData(0, null);
    }

    /**
     * 获取最后选中的状态
     *
     * @return
     */
    public List<ClassifySeletorItem> getSelectItems() {
        return ((ItemAdapter) ((RecyclerView) frameLayoutTop.getChildAt(0)).getAdapter()).getStatus();
    }

    /**
     * 全部取消
     */
    public void reset() {
        ((ItemAdapter) ((RecyclerView) frameLayoutTop.getChildAt(0)).getAdapter()).reset();
    }


    public interface SlideContainListener {
        /**
         * 获取当前需要显示的数据,注意，初始化加载第一页的数据的时候，item为null
         *
         * @param itemPosition -1表示点击的导航跳转的
         * @param item
         * @return 返回下一页要现实的数据
         */
        List<ClassifySeletorItem> getData(int itemPosition, ClassifySeletorItem item, int currentPage);

        /**
         * 当页面回退的时候
         *
         * @param currentPage 返回以后，页面的深度
         */
        List<ClassifySeletorItem> pageBack(int currentPage, ClassifySeletorItem item);

        /**
         * 当没有子元素的时候，点击的回调
         *
         * @param holder
         * @param position
         * @param item
         */
        void click(boolean isSelected, ItemAdapter.ItemViewHolder holder, int position, ClassifySeletorItem item);

        /**
         * 判断某个节点是不是最后一级了
         *
         * @param item 节点
         * @return false：不是，true：是
         */
        Boolean isFinal(ClassifySeletorItem item);
    }
}
