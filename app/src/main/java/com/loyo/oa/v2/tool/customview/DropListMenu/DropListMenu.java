package com.loyo.oa.v2.tool.customview.DropListMenu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class DropListMenu extends LinearLayout {
    // Menu 展开的ListView 的 adapter
    private List<DropListAdapter> mMenuAdapters = new ArrayList<>();

    // Menu 数据源
    private List<DropItem> mMenuItems = new ArrayList<>();

    //菜单 上的文字
    private List<TextView> mTvMenuTitles = new ArrayList<>();
    //菜单 的背景布局
    private List<RelativeLayout> mRlMenuBacks = new ArrayList<>();
    //菜单 的箭头
    private List<ImageView> mIvMenuArrow = new ArrayList<>();

    private Context mContext;

    private List<PopupWindow> mPopupWindows = new ArrayList<>();
    private List<ListView> mMenuLists = new ArrayList<>();
    private List<ListView> mSubMenuLists = new ArrayList<>();
    private List<Button> mButtonConfirm = new ArrayList<>();

    //已经选择DropItem
    private List<SparseArray<DropItem>> mSelectedItem = new ArrayList<>();

    // 监听器
    private OnDropItemSelectedListener mMenuSelectedListener;

    // 主Menu的个数
    private int mMenuCount;
    // Menu 展开的list 显示数量
    private int mShowCount;
    //选中行数
    private int mRowSelected = 0;
    //选中列数
    private int mColumnSelected = 0;
    //Menu的字体颜色
    private int mMenuTitleTextColor;
    //Menu的字体大小
    private int mMenuTitleTextSize;
    //Menu的按下的字体颜色
    private int mMenuPressedTitleTextColor;
    //Menu的按下背景
    private int mMenuPressedBackColor;
    //Menu的背景
    private int mMenuBackColor;
    //Menu list 的字体大小
    private int mMenuListTextSize;
    //Menu list 的字体颜色
    private int mMenuListTextColor;
    //是否显示选中的对勾
    private boolean mShowCheck;
    //是否现实列表的分割线
    private boolean mShowDivider;
    //列表的背景
    private int mMenuListBackColor;
    //列表的按下效果
    private int mMenuListSelectorRes;
    //箭头距离
    private int mArrowMarginTitle;
    //对勾的图片资源
    private int mCheckIcon;
    //向上的箭头图片资源
    private int mUpArrow;
    //向下的箭头图片资源
    private int mDownArrow;

    private boolean mDrawable = false;

    private String[] mDefaultMenuTitle;


    public DropListMenu(Context mContext) {
        super(mContext);
        init(mContext);
    }

    public DropListMenu(Context mContext, AttributeSet attrs) {
        super(mContext, attrs);
        init(mContext);
    }

    private void init(Context mContext) {
        this.mContext = mContext;

        mMenuCount = 2;
        mShowCount = 5;
        mMenuTitleTextColor = getResources().getColor(R.color.default_menu_press_text);
        mMenuPressedBackColor = getResources().getColor(R.color.default_menu_press_back);
        mMenuPressedTitleTextColor = getResources().getColor(R.color.default_menu_press_text);
        mMenuBackColor = getResources().getColor(R.color.default_menu_back);
        mMenuListBackColor = getResources().getColor(R.color.white);
        mMenuListSelectorRes = R.color.lightgrey;
        mMenuTitleTextSize = 18;
        mArrowMarginTitle = 10;
        mShowCheck = true;
        mShowDivider = true;
        //mCheckIcon = R.drawable.ico_make;
        mUpArrow = R.drawable.arrow_up;
        mDownArrow = R.drawable.arrow_down;

    }

    void initPopopWindows() {
        LogUtil.d("左侧的Item有:" + mMenuCount);
        mMenuCount = mMenuItems.size();

        if (mMenuAdapters.size() == 0) {
            for (int i = 0; i < mMenuCount; i++) {
                DropListAdapter adapter = new DropListAdapter(mContext, mMenuItems.get(i).getSubDropItem());
                mMenuAdapters.add(adapter);
            }
        }

        for (int index = 0; index < mMenuCount; index++) {
            final DropItem menuItem = mMenuItems.get(index);
            View viewPopWindow = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_droplist_menu, null);
            final PopupWindow popupWindow = new PopupWindow(viewPopWindow, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    for (int i = 0; i < mMenuCount; i++) {
                        mIvMenuArrow.get(i).setImageResource(mDownArrow);
                        mRlMenuBacks.get(i).setBackgroundColor(mMenuBackColor);
                        //mTvMenuTitles.get(i).setTextColor(mMenuTitleTextColor);
                    }
                }
            });

            final ViewGroup layoutConfirm = (ViewGroup) viewPopWindow.findViewById(R.id.layout_confirm);
            if (layoutConfirm != null) {
                layoutConfirm.setVisibility(menuItem.getSelectType() == DropItem.NORMAL || menuItem.getSelectType() == DropItem.GROUP_SINGLE_DISMISS ? View.GONE : View.VISIBLE);
            }

            /**
             * 客户管理，客户标签筛选Menu
             */
            final ListView menuList = (ListView) viewPopWindow.findViewById(R.id.lv_menu);
            final ListView subMenuList = (ListView) viewPopWindow.findViewById(R.id.lv_menu_sub);
            subMenuList.setBackgroundColor(getResources().getColor(R.color.white));
            if (!mShowDivider) {
                menuList.setDivider(null);
            }

            /**确定*/
            Button buttonConfirm = (Button) viewPopWindow.findViewById(R.id.btn_confirm);
            buttonConfirm.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    if (mMenuSelectedListener != null) {
                        mMenuSelectedListener.onSelected(v, mColumnSelected, getSelectedItems());
                    }
                }
            });

            /**取消*/
            Button buttonCancel = (Button) viewPopWindow.findViewById(R.id.btn_cancel);
            buttonCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedItem.get(mColumnSelected).clear();
                    syncConfirmButton();
                    popupWindow.dismiss();
                    if (mMenuSelectedListener != null) {
                        mMenuSelectedListener.onCancelAll(mColumnSelected);
                    }

                }
            });

            Global.SetTouchView(buttonConfirm, buttonCancel);
            menuList.setBackgroundColor(mMenuListBackColor);
            //menuList.setSelector(mMenuListSelectorRes);

            /**
             * 客户标签筛选，列表监听
             * 一级列表
             * */
            menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mRowSelected = position;
                    /*有自内容时执行*/
                    if (mMenuSelectedListener != null && menuItem.getSubDropItem().get(mRowSelected).hasSub()) {
                        DropItem selectedItem = getSelectedItems().get(mRowSelected);
                        final DropListAdapter adapter = new DropListAdapter(mContext, menuItem.getSubDropItem().get(mRowSelected).getSubDropItem(), selectedItem);
                        //二级列表
                        getSubMenuList().setAdapter(adapter);
                        getSubMenuList().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (menuItem.getSelectType() == DropItem.NORMAL || menuItem.getSelectType() == DropItem.GROUP_SINGLE_DISMISS) {
                                    mIvMenuArrow.get(mColumnSelected).setImageResource(mDownArrow);
                                    mMenuAdapters.get(mColumnSelected).setSelectIndex(mRowSelected);

                                    if (mMenuSelectedListener != null) {
                                        mMenuSelectedListener.onSelected(view, mColumnSelected, getSelectedItems());
                                    }
                                } else if (menuItem.getSelectType() == DropItem.GROUP_SINGLE) {
                                    DropItem selectedItem = getSelectedItems().get(mRowSelected);
                                    DropItem currentItem = menuItem.getSubDropItem().get(mRowSelected).getSubDropItem().get(position);

                                    if (selectedItem == null) {
                                        getSelectedItems().put(mRowSelected, currentItem);
                                        adapter.setSelectIndex(position);
                                    } else {
                                        getSelectedItems().remove(mRowSelected);
                                        if (!selectedItem.equals(currentItem)) {
                                            getSelectedItems().put(mRowSelected, currentItem);
                                            adapter.setSelectIndex(position);
                                        } else {
                                            adapter.setSelectIndex(-1);
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                    syncConfirmButton();
                                }
                            }
                        });
                        /*当一级列表没有自内容时执行*/
                    } else {
                        LogUtil.dee("选中第" + mColumnSelected + "列" + " 第" + mRowSelected + "行");
                        //默认选择状态下
                        popupWindow.dismiss();
                        mIvMenuArrow.get(mColumnSelected).setImageResource(mDownArrow);
                        mMenuAdapters.get(mColumnSelected).setSelectIndex(mRowSelected);

                        if (mMenuSelectedListener != null) {
                            DropItem selectRowItem = menuItem.getSubDropItem().get(mRowSelected);
                            getSelectedItems().clear();
                            getSelectedItems().put(mColumnSelected, selectRowItem);
                            if (mMenuSelectedListener != null) {
                                mMenuSelectedListener.onSelected(view, mColumnSelected, getSelectedItems());
                            }
                        }
                    }
                }
            });

            RelativeLayout shadow = (RelativeLayout) viewPopWindow.findViewById(R.id.rl_menu_shadow);
            shadow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });

            mMenuLists.add(menuList);
            mSubMenuLists.add(subMenuList);
            mPopupWindows.add(popupWindow);
            mButtonConfirm.add(buttonConfirm);
            mSelectedItem.add(new SparseArray<DropItem>());
        }
    }

    void syncConfirmButton() {
        int size = getSelectedItems().size();
        getConfirmButton().setText("确定" + (size > 0 ? "(" + size + ")" : ""));
    }

    // 设置 Menu的item
    public void setmMenuItems(ArrayList<DropItem> menuItems) {
        mMenuItems = menuItems;
        mDrawable = true;

        initPopopWindows();
        invalidate();
    }

    public void setShowDivider(boolean mShowDivider) {
        this.mShowDivider = mShowDivider;
    }

    public void setmMenuListBackColor(int menuListBackColor) {
        mMenuListBackColor = menuListBackColor;
    }

    public void setmMenuListSelectorRes(int menuListSelectorRes) {
        mMenuListSelectorRes = menuListSelectorRes;
    }

    public void setmArrowMarginTitle(int arrowMarginTitle) {
        mArrowMarginTitle = arrowMarginTitle;
    }

    public void setmMenuPressedTitleTextColor(int menuPressedTitleTextColor) {
        mMenuPressedTitleTextColor = menuPressedTitleTextColor;
    }

    public void setDefaultMenuTitle(String[] mDefaultMenuTitle) {
        this.mDefaultMenuTitle = mDefaultMenuTitle;
    }

    // 设置 show 数量
    public void setmShowCount(int showCount) {
        mShowCount = showCount;
    }

    // 设置 Menu的字体颜色
    public void setmMenuTitleTextColor(int menuTitleTextColor) {
        mMenuTitleTextColor = menuTitleTextColor;
    }

    // 设置 Menu的字体大小
    public void setmMenuTitleTextSize(int menuTitleTextSize) {
        mMenuTitleTextSize = menuTitleTextSize;
    }

    //设置Menu的背景色
    public void setmMenuBackColor(int menuBackColor) {
        mMenuBackColor = menuBackColor;
    }

    //设置Menu的按下背景色
    public void setmMenuPressedBackColor(int menuPressedBackColor) {
        mMenuPressedBackColor = menuPressedBackColor;
    }

    //    //设置Menu list的字体颜色
    //    public void setmMenuListTextColor(int menuListTextColor) {
    //        mMenuListTextColor = menuListTextColor;
    //        for (int i = 0; i < mMenuAdapters.size(); i++) {
    //            mMenuAdapters.get(i).setTextColor(mMenuListTextColor);
    //        }
    //    }
    //
    //    //设置Menu list的字体大小
    //    public void setmMenuListTextSize(int menuListTextSize) {
    //        mMenuListTextSize = menuListTextSize;
    //        for (int i = 0; i < mMenuAdapters.size(); i++) {
    //            mMenuAdapters.get(i).setTextSize(menuListTextSize);
    //        }
    //    }

    //设置是否显示对勾
    public void setShowCheck(boolean mShowCheck) {
        this.mShowCheck = mShowCheck;
    }

    //设置对勾的icon
    public void setmCheckIcon(int checkIcon) {
        mCheckIcon = checkIcon;
    }

    public void setmUpArrow(int upArrow) {
        mUpArrow = upArrow;
    }

    public void setmDownArrow(int downArrow) {
        mDownArrow = downArrow;
    }

    public void setMenuSelectedListener(OnDropItemSelectedListener menuSelectedListener) {
        mMenuSelectedListener = menuSelectedListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawable) {
            int width = getWidth();

            for (int i = 0; i < mMenuCount; i++) {
                final RelativeLayout v = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.menu_item, null, false);
                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width / mMenuCount, LinearLayout.LayoutParams.WRAP_CONTENT);
                v.setLayoutParams(parms);
                TextView tv = (TextView) v.findViewById(R.id.tv_menu_title);
                tv.setTextColor(mMenuTitleTextColor);
                tv.setTextSize(mMenuTitleTextSize);
                if (mDefaultMenuTitle == null || mDefaultMenuTitle.length == 0) {
                    tv.setText(mMenuItems.get(i).getName());
                } else {
                    tv.setText(mDefaultMenuTitle[i]);
                }
                this.addView(v, i);
                mTvMenuTitles.add(tv);

                RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.rl_menu_head);
                rl.setBackgroundColor(mMenuBackColor);
                mRlMenuBacks.add(rl);

                ImageView iv = (ImageView) v.findViewById(R.id.iv_menu_arrow);
                mIvMenuArrow.add(iv);
                mIvMenuArrow.get(i).setImageResource(mDownArrow);

                final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) iv.getLayoutParams();
                params.leftMargin = mArrowMarginTitle;
                iv.setLayoutParams(params);

                final int index = i;
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mColumnSelected = index;

                        DropListAdapter adapter = mMenuAdapters.get(mColumnSelected);

                        getMenuList().setAdapter(adapter);
                        View childView = adapter.getView(0, null, getMenuList());
                        childView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                        if (mMenuItems.get(mColumnSelected).hasSubExtend()) {
                            //如果有二级菜单,高度必须定死.
                            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(0, childView.getMeasuredHeight() * mShowCount, 1);
                            getMenuList().setLayoutParams(parms);

                            DropItem subItem = mMenuItems.get(mColumnSelected).getSubDropItem().get(0);
                            if (subItem.hasSubExtend()) {
                                //回显
                                DropItem selectedItem = getSelectedItems().get(0);

                                DropListAdapter subAdapter = new DropListAdapter(mContext, subItem.getSubDropItem(), selectedItem);
                                getSubMenuList().setAdapter(subAdapter);
                            } else {
                                DropListAdapter subAdapter = new DropListAdapter(mContext, null);
                                getSubMenuList().setAdapter(subAdapter);
                            }

                        } else {
                            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            getMenuList().setLayoutParams(parms);
                        }

                        if (!mShowDivider) {
                            getMenuList().setDivider(null);
                        }

                        getMenuList().setBackgroundColor(mMenuListBackColor);
                        getMenuList().setSelector(mMenuListSelectorRes);
                        getSubMenuList().setBackgroundColor(getResources().getColor(R.color.white));
                        // mTvMenuTitles.get(index).setTextColor(mMenuPressedTitleTextColor);
                        mRlMenuBacks.get(index).setBackgroundColor(mMenuPressedBackColor);
                        mIvMenuArrow.get(index).setImageResource(mUpArrow);
                        getPopupWindow().showAsDropDown(v);
                    }
                });
            }
            mDrawable = false;
        }
    }

    PopupWindow getPopupWindow() {
        return mPopupWindows.get(mColumnSelected);
    }

    ListView getMenuList() {
        return mMenuLists.get(mColumnSelected);
    }

    ListView getSubMenuList() {
        return mSubMenuLists.get(mColumnSelected);
    }

    Button getConfirmButton() {
        return mButtonConfirm.get(mColumnSelected);
    }

    SparseArray<DropItem> getSelectedItems() {
        return mSelectedItem.get(mColumnSelected);
    }
}
