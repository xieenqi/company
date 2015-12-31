package com.loyo.oa.v2.activity.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * com.loyo.oa.v2.activity
 * 描述 :子任务负责人选择页面
 * 作者 : ykb
 * 时间 : 15/7/21.
 */
@EActivity(R.layout.layout_customer_select)
public class ChildTaskResponserSelectActivity extends BaseActivity
{
    @Extra("users")
    ArrayList<NewUser> mUsers;
    private UserListAdapter adapter;
    @ViewById
    ListView usersListeView;
    @ViewById
    ViewGroup img_title_left;

    @Click(R.id.img_title_left)
    void onClick()
    {
        MainApp.getMainApp().finishActivity(this,MainApp.ENTER_TYPE_LEFT,RESULT_CANCELED,null);
    }

    @AfterViews
    void initUi()
    {
        img_title_left.setOnTouchListener(Global.GetTouch());
        ((TextView)findViewById(R.id.tv_title_1)).setText("选择");
        if(null==mUsers)
            mUsers=new ArrayList<NewUser>();
        adapter=new UserListAdapter();
        usersListeView.setAdapter(adapter);

        usersListeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                UserListAdapter.Item_info item_info = (UserListAdapter.Item_info) view.getTag();
                //在每次获取点击的item时将对于的checkbox状态改变，同时修改map的值。
                item_info.cBox.toggle();
                Intent data = new Intent();
                data.putExtra("user", (NewUser) adapter.getItem(i));
                app.finishActivity(ChildTaskResponserSelectActivity.this, MainApp.ENTER_TYPE_LEFT, Activity.RESULT_OK, data);
            }
        });

        usersListeView.setItemsCanFocus(false);
        usersListeView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    public class UserListAdapter extends BaseAdapter
    {

        LayoutInflater layoutInflater;

        HashMap<Integer, Boolean> isSelected =new  HashMap<Integer, Boolean>();

        private Item_info item_info;
        public int isSelected_radio_child = -1;

        public UserListAdapter() {
            layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //这儿定义isSelected这个map是记录每个listitem的状态，初始状态全部为false。
            for (int i = 0; i < mUsers.size(); i++) {
                isSelected.put(i, false);
            }
        }

        @Override
        public int getCount() {
            return mUsers.size();
        }

        @Override
        public Object getItem(int i)
        {
            return mUsers.isEmpty()?null:mUsers.get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_usergroup_child, null);
                item_info = new Item_info();
                item_info.img = (ImageView) convertView.findViewById(R.id.img);
                item_info.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                item_info.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                item_info.cBox = (CheckBox) convertView.findViewById(R.id.cb);
                convertView.setTag(item_info);
            } else {
                item_info = (Item_info) convertView.getTag();
            }

            NewUser user = mUsers.get(position);
            item_info.tv_title.setText(user.getRealname());
//            item_info.tv_content.setText(user.getDepartmentsName());
            item_info.cBox.setChecked(isSelected.get(position));

            return convertView;
        }

        public class Item_info {
            ImageView img;
            TextView tv_title;
            TextView tv_content;
            public CheckBox cBox;
        }
    }
}
