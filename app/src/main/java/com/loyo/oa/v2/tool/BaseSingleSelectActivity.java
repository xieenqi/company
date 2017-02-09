package com.loyo.oa.v2.tool;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.network.model.LoyoError;

import java.io.Serializable;
import java.util.List;

/**
 * 单选条目的基类，比客户状态，用户标签等。注意，要复写item的model的toString（）方法。
 * Created by jie on 17/2/8.
 */

public abstract class BaseSingleSelectActivity<T extends Serializable> extends BaseActivity{
    protected TextView tvTitle;
    protected ListView listView;
    protected LoadingLayout loadingLayout;
    protected LinearLayout llBack;
    protected List<T> listData;
    protected int selectPosition=-1;//选中的条目，默认为－1，没有选中的
    protected MyAdaper adaper;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_single_select_activity);
        initView();
    }
    protected void initView(){
        tvTitle = (TextView) findViewById(R.id.tv_title);
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        listView= (ListView) findViewById(R.id.lv_list);
        loadingLayout= (LoadingLayout) findViewById(R.id.ll_loading);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //改变选择的条目
                selectPosition=position;
                adaper.notifyDataSetChanged();
                //返回数据
                Intent intent=new Intent();
                intent.putExtra("data", listData.get(position));
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        loadingLayout.setStatus(LoadingLayout.Loading);
        loadingLayout.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                getData();
            }
        });
        tvTitle.setText(getPageTitle());
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getData();
    }

    /**
     *加载数据
     */
    protected abstract void  getData();

    /**
     * 返回当前页面的标题
     * @return
     */
    protected abstract String  getPageTitle();

    /**
     * 判断默认选中项目
     * @param item
     * @return 返回true，就默认选中这一项
     */
    protected boolean isDefault(T item){
        return false;
    }


    /**
     * 请求失败的时候
     *
     * @param e
     */
    protected void fail(Throwable e) {
        LoyoErrorChecker.checkLoyoError(e,  LoyoErrorChecker.LOADING_LAYOUT , loadingLayout);
    }
    protected  void  success(List<T> data){
        loadingLayout.setStatus(LoadingLayout.Success);
        listData=data;
        adaper=new MyAdaper();
        listView.setAdapter(adaper);
    }

    class MyAdaper extends BaseAdapter{
        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(null==convertView){
                convertView = getLayoutInflater().inflate(R.layout.base_single_select_item, null);
                viewHolder=new ViewHolder(convertView);
            }else{
                viewHolder= (ViewHolder) convertView.getTag();
            }
            //注意，这里直接调用的model的toString()，所以，在实体里要复写这个方法；
            viewHolder.textView.setText(getItem(position).toString());
            if(selectPosition==position){//点击选中了的
                viewHolder.imageView.setVisibility(View.VISIBLE);
            }else if(selectPosition==-1&&isDefault((T) getItem(position))){
                //默认选中了的
                viewHolder.imageView.setVisibility(View.VISIBLE);
            }else{
                viewHolder.imageView.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
        class ViewHolder{
            public View view;
            public TextView textView;
            public ImageView imageView;
            public ViewHolder(View view) {
                this.view = view;
                textView = (TextView) view.findViewById(R.id.tv_name);
                imageView= (ImageView) view.findViewById(R.id.iv_img);
                view.setTag(this);
            }
        }
    }

}
