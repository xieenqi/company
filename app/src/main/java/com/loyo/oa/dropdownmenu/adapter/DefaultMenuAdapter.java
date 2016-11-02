package com.loyo.oa.dropdownmenu.adapter;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.loyo.oa.dropdownmenu.callback.OnMenuButtonClick;
import com.loyo.oa.dropdownmenu.callback.OnMenuItemClick;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.OrganizationFilterModel;
import com.loyo.oa.dropdownmenu.filtermenu.view.OrganizationMenuView;
import com.loyo.oa.dropdownmenu.filtermenu.view.TagMenuView;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuListType;
import com.loyo.oa.dropdownmenu.utils.UIUtil;
import com.loyo.oa.dropdownmenu.view.SingleListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/10/31.
 */

public class DefaultMenuAdapter implements MenuAdapter {

    private List<FilterModel> data = new ArrayList<>();
    private Context context;

    private OnMenuModelsSelected callback;

    public void setCallback(OnMenuModelsSelected callback) {
        this.callback = callback;
    }

    public DefaultMenuAdapter(Context context, List<FilterModel> data) {
        this.context = context;
        this.data.addAll(data);
    }

    public void loadData(List<FilterModel> list) {
        this.data.clear();
        data.addAll(list);
    }

    public List<FilterModel> getData() {
        return data;
    }

    @Override
    public int getMenuCount() {
        return data.size();
    }

    @Override
    public String getMenuTitle(int position) {
        return data.get(position).getDefaultTitle();
    }

    @Override
    public int getBottomMargin(int position) {
        return UIUtil.dp(context, 200);
    }

    public int getHeight(int position) {
        FilterModel model = data.get(position);
        if (model.getType() == MenuListType.ORGANIZATION) {
            return UIUtil.dp(context, 200);
        }
        else if (model.getType() == MenuListType.TAG) {
            return UIUtil.dp(context, 350);
        }

        int height = 50 * model.getChildrenCount() + 10;

        if (height > 350) {
            height = 350;
        }
        else if (height <50) {
            height = 50;
        }

        return UIUtil.dp(context, height);
    }

    @Override
    public View getView(final int position, FrameLayout parentContainer) {
        final FilterModel model = data.get(position);
        View result = null;
        switch (model.getType()) {
            case SINGLE_LIST_SINGLE_SEL:
            {
                final SingleListView view = new SingleListView(context);
                view.loadData(model.getChildren());
                view.setCallback(new OnMenuItemClick(){
                    @Override
                    public void onMenuItemClick(int index) {
                        if (DefaultMenuAdapter.this.callback != null) {
                            List<Integer> idxes = new ArrayList<Integer>();
                            idxes.add(index);
                            model.setSelectedIndexes(idxes);
                            DefaultMenuAdapter.this.callback.onMenuModelsSelected(position, model.getSelectedModels(), null);
                        }
                    }
                });
                result = view;
            }
            break;
            case ORGANIZATION:
            {
                final OrganizationMenuView view = new OrganizationMenuView(context);
                view.setFilterModel((OrganizationFilterModel)model);
                view.setCallback(new OnMenuItemClick(){
                    @Override
                    public void onMenuItemClick(int index) {
                        if (DefaultMenuAdapter.this.callback != null) {
                            List<Integer> idxes = new ArrayList<Integer>();
                            idxes.add(index);
                            model.setSelectedIndexes(idxes);
                            DefaultMenuAdapter.this.callback.onMenuModelsSelected(position, view.getSelectedMenuModels(), null);
                        }
                    }
                });
                result = view;
            }
            break;
            case TAG:
            {
                final TagMenuView view = new TagMenuView(context);
                view.setFilterModel(model);
                view.setCallback(new OnMenuButtonClick(){

                    @Override
                    public void onMenuConfirmClick() {
                        DefaultMenuAdapter.this.callback.onMenuModelsSelected(position, view.getSelectedMenuModels(), view.getSelectedTagParams());
                    }

                    @Override
                    public void onMenuResetClick() {
                        DefaultMenuAdapter.this.callback.onMenuModelsSelected(position, view.getSelectedMenuModels(), "");

                    }
                });
                result = view;
            }
            break;

            default:
                break;
        }
        return result;
    }
}
