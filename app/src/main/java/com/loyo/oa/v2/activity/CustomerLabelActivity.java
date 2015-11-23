package com.loyo.oa.v2.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Item_info_Group;
import com.loyo.oa.v2.beans.NewTag;
import com.loyo.oa.v2.beans.Tag;
import com.loyo.oa.v2.beans.TagItem;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import retrofit.client.Response;


@EActivity(R.layout.activity_customer_label)
public class CustomerLabelActivity extends BaseActivity {

    @ViewById ExpandableListView expand_listview_label;

    @ViewById ViewGroup img_title_left;

    @Extra("tagitems") ArrayList<TagItem> mTagItems;

    @Extra("customerId") String mCustomerId;

    ArrayList<Tag> tags = new ArrayList<>();

    @AfterViews
    void init() {
        if (mTagItems == null) {
            mTagItems = new ArrayList<>();
        }

        setTitle("标签");

        expand_listview_label.setAdapter(adapter);
        expand_listview_label.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

        expand_listview_label.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                TagItem tagItem = (TagItem) adapter.getChild(groupPosition, childPosition);
                if (tagItem.isChecked()) {
                    tagItem.setIsChecked(false);
                    for (int i = 0; i < mTagItems.size(); i++) {
                        TagItem cacheItem = mTagItems.get(i);
                        if (TextUtils.equals(cacheItem.getId(), tagItem.getId())) {
                            mTagItems.remove(cacheItem);
                            i--;
                        }
                    }
                } else {
                    Tag tag = (Tag) adapter.getGroup(groupPosition);
                    tagItem.setIsChecked(true);
                    tagItem.setTagId(tag.getId());
                    mTagItems.add(tagItem);
                    for (TagItem item : tag.getItems()) {
                        if (!TextUtils.equals(item.getId(), tagItem.getId())) {
                            item.setIsChecked(false);
                            //修复标签多选bug
                            for (int i = 0; i < mTagItems.size(); i++) {
                                TagItem cacheItem = mTagItems.get(i);
                                if (TextUtils.equals(cacheItem.getId(), item.getId())) {
                                    mTagItems.remove(cacheItem);
                                    i--;
                                }
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                expand();

                return false;
            }
        });

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).GetTags(new RCallback<ArrayList<Tag>>() {
            @Override
            public void success(ArrayList<Tag> _tags, Response response) {
                tags = _tags;
                handler.sendEmptyMessage(0);
            }
        });
    }

    void expand() {
        for (int i = 0; i < tags.size(); i++) {
            expand_listview_label.expandGroup(i, false);
        }
    }

    LabelsHandler handler = new LabelsHandler();

    class LabelsHandler extends BaseHandler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            for (Tag tag : tags) {
                ArrayList<TagItem> items = tag.getItems();
                for (TagItem item : items) {
                    item.setTagId(tag.getId());
                    for (TagItem _item : mTagItems) {
                        if (TextUtils.equals(item.getId(), _item.getId())) {
                            item.setIsChecked(true);
                            _item.setTagId(item.getTagId());
                        }
                    }
                }
            }
            adapter.notifyDataSetChanged();
            expand();
        }
    }

    @Click(R.id.img_title_left)
    void goBack() {
        app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
    }

    @Click(R.id.img_title_right)
    void checkItems() {
        close();
    }

    /**
     * 构建新标签
     * @return
     */
    private ArrayList<NewTag> convertNewTags() {
        if (null == mTagItems || mTagItems.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<NewTag> tags = new ArrayList<>();
        for (int i = 0; i < mTagItems.size(); i++) {
            TagItem item = mTagItems.get(i);
            NewTag tag = new NewTag();
            tag.setItemId(item.getId());
            tag.setItemName(item.getName());
            tag.settId(item.getTagId());

            tags.add(tag);
        }

        return tags;
    }

    void close() {
        Intent intent = new Intent();
        intent.putExtra("data", convertNewTags());
        app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
    }

    BaseExpandableListAdapter adapter = new BaseExpandableListAdapter() {
        @Override
        public int getGroupCount() {
            return tags.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return tags.get(groupPosition).getItems().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return tags.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return tags.get(groupPosition).getItems().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        Item_info_Group item_info_Group;
        Item_info_Child item_info_Child;

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_sign_show_group, null);
                item_info_Group = new Item_info_Group();
                item_info_Group.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(item_info_Group);
            } else {
                item_info_Group = (Item_info_Group) convertView.getTag();
            }

            Tag tag = tags.get(groupPosition);
            if (tag != null && tag.getName() != null) {
                item_info_Group.tv_title.setText(tag.getName());
            }

            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_customer_tag, null);
                item_info_Child = new Item_info_Child();
                item_info_Child.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                item_info_Child.cb = (CheckBox) convertView.findViewById(R.id.cBox);
                convertView.setTag(item_info_Child);
            } else {
                item_info_Child = (Item_info_Child) convertView.getTag();
            }

            TagItem item = (TagItem) getChild(groupPosition, childPosition);
            item_info_Child.tv_title.setText(item.getName());
            item_info_Child.cb.setChecked(item.isChecked());

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    };

    class Item_info_Child {
        TextView tv_title;
        CheckBox cb;
    }
}
