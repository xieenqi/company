package com.loyo.oa.v2.tool;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.contacts.api.ContactsService;
import com.loyo.oa.v2.contacts.model.ContactsRoleModel;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.model.BaseResponse;

import java.util.List;

/**
 * 选择联系人角色
 * Created by jie on 17/2/8.
 */

public abstract class BaseSingleSelectActivity<T> extends BaseActivity{
    protected TextView tvTitle;
    protected ListView listView;
    protected LoadingLayout loadingLayout;
    protected LinearLayout llBack;
    protected List<T> listData;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_single_select_activity);
        initView();
    }
    protected void initView(){
        tvTitle = (TextView) findViewById(R.id.tv_title);
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        listView= (ListView) findViewById(R.id.lv_list);
    }

    protected abstract void  getData();

    protected  void  fail(){

    }
    protected  void  success(List<T> data){

    }

    class MyAdaper extends BaseAdapter{

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }

}
