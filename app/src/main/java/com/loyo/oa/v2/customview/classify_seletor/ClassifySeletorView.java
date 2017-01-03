package com.loyo.oa.v2.customview.classify_seletor;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


import com.loyo.oa.v2.R;

import java.util.ArrayList;
import java.util.List;



/**
 * 分类选择器
 * 2种使用的方式
 * 第一种，直接调用setClassifySeletorListener，然后实现加载数据的回调方法，自己管理数据
 * 第二种，使用setup，传入数据和需要使用的回调即可
 * Created by jie on 16/12/29.
 */

public class ClassifySeletorView extends LinearLayout {
    private String TAG = "ClassifySeletorView";
    private Context context;
    private RecyclerView rvTitle;
    private ClassifySeletorListener classifySeletorListener;
    private SlideContainer slideContainer;
    private TitleAdapter titleAdapter;
    private Button btnReset, btnOk;
    private ClassifySeletorItem firstHeadItem;

    //设置是否是单选模式
    private boolean isSingleSelete=false;
    //自动管理数据
    private List<ClassifySeletorItem> listData;
    private SeletorListener seletorListener;

    public ClassifySeletorView(Context context) {
        super(context);
        this.context = context;
    }

    public ClassifySeletorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    /**
     * 自动管理数据
     * @param data 数据
     * @param listener 事件回调
     */
    public void setup(List<ClassifySeletorItem> data, SeletorListener listener) {
        this.listData = data;
        this.seletorListener = listener;
        setClassifySeletorListener(new ClassifySeletorListener() {
            @Override
            public List<ClassifySeletorItem> getData(int level, ClassifySeletorItem item) {
                List<ClassifySeletorItem> d = new ArrayList<>();
                for (ClassifySeletorItem i : listData) {
                    if (i.getParentId().equals(item.getId())) {
                        d.add(i);
                    }
                }
                return d;
            }

            @Override
            public ClassifySeletorItem getFirstData() {
                for (ClassifySeletorItem classifySeletorItem : listData) {
                    if (classifySeletorItem.isRoot) {
                        return classifySeletorItem;
                    }
                }
                return new ClassifySeletorItem("0", "没有分类数据", "1", true, 1, "");
            }

            @Override
            public Boolean isFinal(ClassifySeletorItem item) {
                for (int i = 0; i < listData.size(); i++) {
                    if (listData.get(i).getLevel() == item.getLevel() + 1) {
                        if (listData.get(i).getParentId().equals(item.getId())) {
                            return false;
                        }
                    }
                }
                return true;
            }

            @Override
            public void clickItem(boolean isSelected, ItemAdapter.ItemViewHolder holder, int position, ClassifySeletorItem item) {
                seletorListener.clickItem(isSelected, holder, position, item);
            }

            @Override
            public void clickReset() {
                seletorListener.clickReset();
            }

            @Override
            public void clickOk(List<ClassifySeletorItem> selectItem) {
                seletorListener.clickOk(selectItem);
            }
        });
    }


    //初始化布局
    private void init() {
        LayoutInflater.from(context).inflate(R.layout.customview_cs_main, this, true);
        rvTitle = (RecyclerView) findViewById(R.id.customview_cs_rv_title);
        btnOk = (Button) findViewById(R.id.customview_cs_main_ok);
        btnReset = (Button) findViewById(R.id.customview_cs_main_reset);
        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                classifySeletorListener.clickOk(slideContainer.getSelectItems());
            }
        });
        //重置
        btnReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                titleAdapter.setPage(0);
                slideContainer.setPage(0);
                slideContainer.reset();
                classifySeletorListener.clickReset();
            }
        });
        titleAdapter = new TitleAdapter(context, rvTitle);
        //委托一下，标题被点击
        titleAdapter.setOnItemClickListener(new TitleAdapter.OnItemClickListener() {
            @Override
            public void click(TitleAdapter.TitleViewHolder holder, int position, ClassifySeletorItem item) {
                slideContainer.setPage(position);
            }
        });

        //设置水平标题
        rvTitle.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rvTitle.setAdapter(titleAdapter);

        slideContainer = (SlideContainer) findViewById(R.id.customview_cs_sc_multi);

        slideContainer.setSlideContainListener(new SlideContainer.SlideContainListener() {
            @Override
            public List<ClassifySeletorItem> getData(int itemPosition, ClassifySeletorItem item, int currentPage) {
                if (null == item) {
                    //这里是加载第一个"全部分类"，然后加载第一页的数据
                    if (null == firstHeadItem) {
                        firstHeadItem = classifySeletorListener.getFirstData();
                        titleAdapter.push(firstHeadItem);//让路径添加一个
                    }
                    if (null == firstHeadItem) return null;
                    return classifySeletorListener.getData(currentPage, firstHeadItem);
                } else {
                    //不是点击的导航栏，才添加导航栏路径
                    if (-1 != itemPosition) titleAdapter.push(item);
                    //加载数据
                    return classifySeletorListener.getData(currentPage, item);
                }
            }

            @Override
            public List<ClassifySeletorItem> pageBack(int currentPage, ClassifySeletorItem item) {
                //返回的时候，还要加载数据
                titleAdapter.pop();
                if (null == item) {
                    return classifySeletorListener.getData(2, firstHeadItem);
                } else {
                    return classifySeletorListener.getData(currentPage + 1, item);
                }
            }

            @Override
            public void click(boolean isSelected, ItemAdapter.ItemViewHolder holder, int position, ClassifySeletorItem item) {
                //item的点击事件委托出去
                classifySeletorListener.clickItem(isSelected, holder, position, item);
            }

            @Override
            public Boolean isFinal(ClassifySeletorItem item) {
                //判断某个节点，是不是最终的节点
                return classifySeletorListener.isFinal(item);
            }
        });
    }

    public ClassifySeletorListener getClassifySeletorListener() {
        return classifySeletorListener;
    }

    public void setClassifySeletorListener(ClassifySeletorListener classifySeletorListener) {
        this.classifySeletorListener = classifySeletorListener;
        init();
    }

    public boolean isSingleSelete() {
        return isSingleSelete;
    }

    public void setSingleSelete(boolean singleSelete) {
        isSingleSelete = singleSelete;
        slideContainer.setSingleSelete(singleSelete);
    }

    /**
     * 获取最后选中的状态
     *
     * @return
     */
    public List<ClassifySeletorItem> getSelectItems() {
        return slideContainer.getSelectItems();
    }

    /**
     * 手动管理数据的回调
     */
    public static abstract class ClassifySeletorListener {
        /**
         * 用来加载某一页的数据
         *
         * @param level level 0序
         * @param item  加载的父item，如果是跟，为null
         * @return 返回对应的自元素item
         */
        public abstract List<ClassifySeletorItem> getData(int level, ClassifySeletorItem item);

        /**
         * 获取最开头的初始化数据
         *
         * @return
         */
        public abstract ClassifySeletorItem getFirstData();

        /**
         * 条目被点击了
         *
         * @param isSelected 是否选中
         * @param holder     承载体
         * @param position   位置
         * @param item       内容
         */
        public void clickItem(boolean isSelected, ItemAdapter.ItemViewHolder holder, int position, ClassifySeletorItem item) {
        }

        /**
         * 点击了重置按钮
         */
        public void clickReset() {
        }

        /**
         * 点击了ok按钮
         *
         * @param selectItem 被选中的条目集合
         */
        public void clickOk(List<ClassifySeletorItem> selectItem) {
        }

        /**
         * 判断某个节点是不是最后一级了
         *
         * @param item 节点
         * @return false：不是，true：是
         */
        public abstract Boolean isFinal(ClassifySeletorItem item);
    }

    /**
     * 自动管理数据的回调
     */
    public static abstract class SeletorListener {
        /**
         * 条目被点击了
         *
         * @param isSelected 是否选中
         * @param holder     承载体
         * @param position   位置
         * @param item       内容
         */
        public void clickItem(boolean isSelected, ItemAdapter.ItemViewHolder holder, int position, ClassifySeletorItem item) {
        }

        /**
         * 点击了重置按钮
         */
        public void clickReset() {
        }

        /**
         * 点击了ok按钮
         *
         * @param selectItem 被选中的条目集合
         */
        public void clickOk(List<ClassifySeletorItem> selectItem) {
        }

    }

}
