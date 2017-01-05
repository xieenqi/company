package com.loyo.oa.v2.customview.classify_seletor;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
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

    //可以配置的参数
    private float parallax = 0.2f;//视差因子
    private float alpha = 0.4f;//遮罩层初始透明度
    private long animateTime=300;//动画时间，毫秒


    private float startX = 0, startY = 0;//开始点击的坐标，用来处理滑动冲突
    private int level = 0;//当前深度，加载前是0序列，加载以后是1序列
    private FrameLayout frameLayout1, frameLayout2, frameLayoutTop;//用frameLayout套 一下，主要是为了以后增加蒙层
    private RecyclerView recyclerView1, recyclerView2;
    private boolean needExchange = false; //是否需要交换页面
    private SlideContainListener slideContainListener;//获取数据的接口，把数据加载这个委托出去
    private boolean allowClick = true;//是否允许点击，避免动画过程中点击，造成数据混乱
    private Map<String, ClassifySeletorItem> path = new HashMap<>();//用来存储选择的路径
    private Map<Integer,RecycleViewPosition> positionCache=new HashMap<>();//用来缓存滑动的位置，方便恢复
    private View maskView1, maskView2;//遮罩

    private ItemAdapter itemAdapter1, itemAdapter2;

    public ClassifySeletorItem seletedItem;

    public void clearSelected(){
        ItemAdapter.seletedItem=null;
        seletedItem=null;
    }

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
        //添加遮罩
        maskView1 = new View(context);
        maskView2 = new View(context);
        maskView1.setBackgroundColor(Color.BLACK);
        maskView2.setBackgroundColor(Color.BLACK);
        maskView1.setAlpha(0);
        maskView2.setAlpha(0);
        frameLayout1.addView(maskView1);
        frameLayout2.addView(maskView2);

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


    public void setSingleSelete(boolean isSingleSelete) {
        itemAdapter1.setSingleSelete(isSingleSelete);
        itemAdapter2.setSingleSelete(isSingleSelete);
    }

    /**
     * 设置当前是第几页，主要是点击导航的时候，直接切换到某一页,
     * @param position
     * @return 如果被成功处理了点击时间，就返true，否则返回false。避免动画过程中，点击了标题，标题消失，但是页面没有跟上
     */
    public boolean setPage(int position) {
        if (!allowClick) return false;
        //删除多余的路径
        int size = path.size();
        for (int i = position; i < size; i++) {
            path.remove(path.size() - 1);
        }
        //这里逻辑有的复杂，要保证，getData前level是0序列，加载以后level是1序列。就可以了
        int nowPos=level-1;//因为数据加载成功以后，会考虑加载下一页的数据，所以要－1
        level = position;
        getData(-1, path.get(position + ""));
        restorePosition(position,(RecyclerView)((FrameLayout)getChildAt(0)).getChildAt(0));//还原位置
        moveBackPage(nowPos,position);
        return true;
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
            savePosition(level-1);//存加载前的0序列
            getData(position, item);
            //新的页面滑动到第一个
            ((LinearLayoutManager)((RecyclerView)((FrameLayout)getChildAt(0)).getChildAt(0)).getLayoutManager()).scrollToPositionWithOffset(0, 0);
            moveNextPage();
        }
    }

    @Override
    public void clickItem(ClassifySeletorItem item) {
        Log.i(TAG, "clickItem: "+item.getName());
        seletedItem=item;
        if (frameLayout1 == frameLayoutTop) {
            ((RecyclerView)frameLayout2.getChildAt(0)).getAdapter().notifyDataSetChanged();
        } else {
            ((RecyclerView)frameLayout1.getChildAt(0)).getAdapter().notifyDataSetChanged();
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
        if (level <= 1||!allowClick) return false;//最上面一级，不可以再滑动,在动画过程不允许滑动
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //当向右滑动
                if ((event.getX() - startX) > 0) {
                    if (frameLayout1 == frameLayoutTop) {
                        //按照比例手指滑动，移动当前页面
                        frameLayout1.setTranslationX((event.getX() - startX));
                        //造成视差
                        frameLayout2.setTranslationX(-(frameLayout2.getMeasuredWidth() * (parallax) - (event.getX() - startX) * parallax));
                        maskView2.setAlpha(alpha - alpha * (frameLayout1.getTranslationX() / frameLayout1.getMeasuredWidth()));
                    } else {
                        //按照比例手指滑动，移动当前页面
                        frameLayout2.setTranslationX((event.getX() - startX));
                        //造成视差
                        frameLayout1.setTranslationX(-(frameLayout1.getMeasuredWidth() * (parallax) - (event.getX() - startX) * parallax));
                        maskView1.setAlpha(alpha - alpha * (frameLayout2.getTranslationX() / frameLayout2.getMeasuredWidth()));
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
                //滑动超过页面宽度1/5就弹出来显示，否则弹回去
                if (event.getX() - startX > frameLayout1.getMeasuredWidth() / 5) {
                    //出去
                    slideAnimate(true);
                    needExchange = true;
                } else {
                    //恢复
                    slideAnimate(false);
                }
                break;
        }
        return true;
    }

    /**
     * 直接交换页面，也就是交换recycler，让另一个变成上一层无动画
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
        //交换以后，要恢复位置
        frameLayout1.setTranslationX(0f);
        frameLayout2.setTranslationX(0f);
    }

    //下一个页面,动画效果
    private void moveNextPage() {
        changePage();
        final View topView = getChildAt(1);
        final View bottomView = getChildAt(0);
        //前进动画
        ValueAnimator animator = ValueAnimator.ofFloat(topView.getMeasuredWidth(), 0).setDuration(animateTime);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                topView.setTranslationX(value);
                bottomView.setTranslationX(-(frameLayout1.getMeasuredWidth() - value) * parallax);
                ((FrameLayout) bottomView).getChildAt(1).setAlpha(alpha * (1 - (value / bottomView.getMeasuredWidth())));
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
                maskView1.setAlpha(0);
                maskView2.setAlpha(0);
                //加载了下一页，要把上一页的选中状态清理了，避免滑动看到状态的突变
                RecyclerView bottomRv=(RecyclerView)((FrameLayout)getChildAt(0)).getChildAt(0);
                ((ItemAdapter)(bottomRv).getAdapter()).reset();
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

    /**
     * 返回到第position个页面,动画效果
     * 说明，只是为了动画效果，切换的时候不会交换页面，切换完以后，会交换一次页面
     * @param nowPos 当前页面
     * @param backTo 返回到第几页
     */
    private void moveBackPage(int nowPos,int backTo) {
        int moveNum = nowPos-backTo;
        Log.i(TAG, "moveBackPage: nowPos:" + nowPos + ",backTo:" + backTo+",moveNum:"+moveNum);
        if (moveNum <= 0) return;
        final View topView = getChildAt(1);
        final View bottomView = getChildAt(0);
        ValueAnimator animator = ValueAnimator.ofFloat(0, topView.getMeasuredWidth()).setDuration(animateTime / moveNum);
        if (moveNum > 1) animator.setRepeatCount(moveNum - 1);//移动多余1页，就需要重复
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                topView.setTranslationX(value);
                bottomView.setTranslationX((value-topView.getMeasuredWidth()) * parallax);
                //处理遮罩层变化
                if (frameLayout1 == frameLayoutTop) {
                    maskView2.setAlpha(alpha - alpha * (value / frameLayout1.getMeasuredWidth()));
                } else {
                    maskView1.setAlpha(alpha - alpha * (value / frameLayout2.getMeasuredWidth()));
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                allowClick = false;
            }
            @Override
            public void onAnimationEnd(Animator animator) {
                changePage();
                allowClick = true;
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

    /**
     * 滑动以后，恢复或者是后退,动画效果
     *
     * @param isBack 如果是要返回上一页，就是true，恢复原来的状态，就是false
     */
    private void slideAnimate(boolean isBack) {
        ValueAnimator animator;
        if (isBack) {
            //返回页面,顶层出去，下层滑入
            animator = ValueAnimator.ofFloat(frameLayoutTop.getTranslationX(), frameLayoutTop.getMeasuredWidth()).setDuration(animateTime);
        } else {
            //恢复
            animator = ValueAnimator.ofFloat(frameLayoutTop.getTranslationX(), 0).setDuration(animateTime);
        }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
//                view.setTranslationX((Float) valueAnimator.getAnimatedValue());
                //处理遮罩层变化
                if (frameLayout1 == frameLayoutTop) {
                    frameLayout1.setTranslationX(value);
                    frameLayout2.setTranslationX(-(frameLayoutTop.getMeasuredWidth() - value) * parallax);
                    maskView2.setAlpha(alpha * (1-value/frameLayoutTop.getMeasuredWidth()));
                } else {
                    frameLayout2.setTranslationX(value);
                    frameLayout1.setTranslationX(-(frameLayoutTop.getMeasuredWidth() - value) * parallax);
                    maskView1.setAlpha(alpha * (1-value/frameLayoutTop.getMeasuredWidth()));
                }
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
                    level--;//退回来到加载当前页
//                    level--;//加载当页的上一页
//                    getData(-1, path.get((／level+"")));//因为页面复用的原因，上一页，本来还是原来的数据，所以不用加载数据
                    if(slideContainListener!=null)slideContainListener.pageBack(level,path.get(level+""));
                    //更新状态，取消已经选中的按钮
                    changePage();
                    if(level>0){
                        //把再前面一页的数据加载好，避免再次回退，看到不正确的数据
                        getData(-1, path.get((level-2+"")));
                        level--;//加载数据level会自增，这里减回来。
                        if(level>1){
                            restorePosition(level-2,(RecyclerView)((FrameLayout)getChildAt(0)).getChildAt(0));//还原位置
                        }
                    }
                }
                allowClick = true;//动画过程结束，可以点击
                maskView1.setAlpha(0);
                maskView2.setAlpha(0);
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

    /**
     * 还原recycler的位置
     */
    private void restorePosition(int page,RecyclerView rv){
        RecycleViewPosition pos=positionCache.get(page);
        if(null==null)return;
        ((LinearLayoutManager)rv.getLayoutManager()).scrollToPositionWithOffset(pos.lastPosition, pos.lastOffset);
    }

    /**
     * 页面跳转的时候，保存上面的rv的滑动位置
     * @param page
     */
    private void savePosition(int page){
        RecyclerView rv=(RecyclerView)((FrameLayout)getChildAt(1)).getChildAt(0);
        View topView = rv.getLayoutManager().getChildAt(0);          //获取可视的第一个view
        RecycleViewPosition pos=new RecycleViewPosition(rv.getLayoutManager().getPosition(topView), topView.getTop());
        Log.i(TAG, "savePosition: page:"+page+","+pos);
        positionCache.put(page,pos);
    }

    //根据点击的item，获取下一页的新数据
    public void getData(int itemPosition, ClassifySeletorItem item) {
        Log.i(TAG, "getData: itemPosition:"+itemPosition+",level:"+level);
        if (null == slideContainListener) return;
        List<ClassifySeletorItem> data;
        ItemAdapter itemAdapterTop;
        //第一次加载或者点击的获取全部分类
        if (null == item) {
            //获取首页的数据
            data = slideContainListener.getData(itemPosition, item, level);
            if (null == data) return;
            //最开始初始化的时候，rv2在上面
            itemAdapterTop = (ItemAdapter) recyclerView2.getAdapter();

            if (-1 == itemPosition) {
                //点击的导航标题跳转，判断显示在那个recycler上面
                if (frameLayoutTop == frameLayout1) {
                    itemAdapterTop = (ItemAdapter) recyclerView2.getAdapter();
                } else {
                    itemAdapterTop = (ItemAdapter) recyclerView1.getAdapter();
                }
            }
        } else {
            //加载下一页的数据
            data = slideContainListener.getData(itemPosition, item, level);
            //如果不是点击的导航，需要把点击加入到路径，点击的导航，不需要添加
            if (-1 != itemPosition) {
                path.put("" + level, item);
            }
            if (null == data) return;
            //判断当前显示的是哪个recyclerView,加载新的数据到另一个view
            if (frameLayoutTop == frameLayout1) {
                itemAdapterTop = (ItemAdapter) recyclerView2.getAdapter();
            } else {
                itemAdapterTop = (ItemAdapter) recyclerView1.getAdapter();
            }
        }
        itemAdapterTop.setData(data);

        level++;//加载完数据以后，一定要把深度＋1
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
        List<ClassifySeletorItem> data= ((ItemAdapter) ((RecyclerView) frameLayoutTop.getChildAt(0)).getAdapter()).getStatus();
        if(data.size()==0&&seletedItem!=null){
            data.add(seletedItem);
        }
        return data;
    }

    /**
     * 全部取消
     */
    public boolean reset() {
        //滑动过程不可点击
        if(allowClick){
            ((ItemAdapter) ((RecyclerView) frameLayoutTop.getChildAt(0)).getAdapter()).reset();
            return true;
        }
        return false;
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
         * 页面回退的时候，主要是把事件通知出去，做标题的处理
         * @param level 深度 0序，加载首页的时候，是0
         * @param item 加载的时候，父级别的item
         */
        void pageBack(int level, ClassifySeletorItem item);

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

    //用来存放 recycler的滑动位置
    class RecycleViewPosition{
        public int lastPosition;
        public int lastOffset;

        public RecycleViewPosition(int lastPosition, int lastOffset) {
            this.lastPosition = lastPosition;
            this.lastOffset = lastOffset;
        }

        @Override
        public String toString() {
            return "("+lastPosition+","+lastOffset+")";
        }
    }
}
