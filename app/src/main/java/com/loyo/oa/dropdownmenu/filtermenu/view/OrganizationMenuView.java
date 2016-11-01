package com.loyo.oa.dropdownmenu.filtermenu.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.loyo.oa.dropdownmenu.callback.OnMenuItemClick;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.R;
import com.loyo.oa.dropdownmenu.filtermenu.OrganizationDepartmentAdpater;
import com.loyo.oa.dropdownmenu.filtermenu.OrganizationFilterModel;
import com.loyo.oa.dropdownmenu.filtermenu.OrganizationUserAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/10/31.
 */

public class OrganizationMenuView extends LinearLayout {

    private RecyclerView deptView;
    private RecyclerView userView;

    private OrganizationDepartmentAdpater deptAdapter;
    private OrganizationUserAdapter userAdapter;

    private OrganizationFilterModel filterModel;

    private OnMenuItemClick callback;

    public OrganizationMenuView(Context context) {
        super(context);
        init(context);
    }

    public OrganizationMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.WHITE);
        deptAdapter = new OrganizationDepartmentAdpater();
        userAdapter = new OrganizationUserAdapter();
        inflate(context, R.layout.layout_org_menu_view, this);
        deptView = (RecyclerView)findViewById(R.id.department_view);
        deptView.setBackgroundColor(0xfff4f4f4);
        deptView.setLayoutManager(new LinearLayoutManager(getContext()));
        userView = (RecyclerView)findViewById(R.id.user_view);
        userView.setLayoutManager(new LinearLayoutManager(getContext()));

        deptView.setAdapter(deptAdapter);
        userView.setAdapter(userAdapter);

        deptAdapter.setCallback(new OnMenuItemClick() {
            @Override
            public void onMenuItemClick(int index) {
                userAdapter.loadModel(filterModel.getChildrenAtIndex(index));
            }
        });

        userAdapter.setCallback(new OnMenuItemClick() {
            @Override
            public void onMenuItemClick(int index) {
                if (callback != null) {
                    callback.onMenuItemClick(index);
                }
            }
        });
    }

    public void setFilterModel(OrganizationFilterModel filterModel) {
        this.filterModel = filterModel;
        deptAdapter.setFilterModel(filterModel);
        if (filterModel.getChildrenCount() > 0) {
            userAdapter.loadModel(filterModel.getChildrenAtIndex(0));
        }
    }

    public void setCallback(OnMenuItemClick callback) {
        this.callback = callback;
    }

    public List<MenuModel> getSelectedMenuModels() {
        int deptIndex = deptAdapter.selectedIndex;
        int userIndex = userAdapter.selectedIndex;
        MenuModel model = null;
        if (filterModel.getChildrenAtIndex(deptIndex) != null) {
            model = filterModel.getChildrenAtIndex(deptIndex);
            model = model.getChildrenAtIndex(userIndex);
        }

        List<MenuModel> result = new ArrayList<MenuModel>();
        if (model != null) {
            result.add(model);
        }
        return result;
    }

}
