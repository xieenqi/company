package com.loyo.oa.v2.tool.customview;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.sale.AdapterSaleTeamScreen;
import com.loyo.oa.v2.activity.sale.bean.SaleTeamUser;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 团队销售机会列表 筛选View
 * Created by yyy on 16/5/18.
 */
public class SaleScreenPopupView extends PopupWindow implements View.OnClickListener{

    private View contentView;
    private ListView listView1;
    private ListView listView2;
    private Button confirm;
    private Button cancel;
    private SaleTeamUser saleTeamUser;
    private Context mContext;
    private Handler mHandler;

    private AdapterSaleTeamScreen adapter1;
    private AdapterSaleTeamScreen adapter2;
    private List<SaleTeamUser> depementData;
    private List<SaleTeamUser> userData = new ArrayList<>();
    private ArrayList<User> deptAllUser = new ArrayList<>();

    public SaleScreenPopupView(final Activity context, List<SaleTeamUser> data,Handler handler){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.saleteam_screentag1,null);
        this.depementData = data;
        this.mContext = context;
        this.mHandler = handler;
        initView();

        this.setContentView(contentView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setBackgroundDrawable(new ColorDrawable(0000000000));
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        getFirstDept(0);

        adapter1 = new AdapterSaleTeamScreen(context,depementData,1);
        listView1.setAdapter(adapter1);
        adapter2 = new AdapterSaleTeamScreen(context,userData,2);
        listView2.setAdapter(adapter2);
    }

    public void initView(){
        listView1 = (ListView) contentView.findViewById(R.id.saleteam_screentag1_lv1);
        listView2 = (ListView) contentView.findViewById(R.id.saleteam_screentag1_lv2);
        confirm = (Button) contentView.findViewById(R.id.saleteam_screentag1_confirm);
        cancel = (Button) contentView.findViewById(R.id.saleteam_screentag1_cancel);
        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);

        /*左侧列表*/
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getFirstDept(position);
                adapter2.notifyDataSetChanged();
            }
        });

        /*右侧列表*/
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putSerializable("data",userData.get(position));
                msg.setData(bundle);
                msg.what = 0x01;
                mHandler.sendMessage(msg);
                Toast.makeText(mContext,userData.get(position).getName(),Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    /**
     * 组装获取某部门全体人员
     * */
    public void getFirstDept(int position){
        userData.clear();
        deptAllUser.clear();
        Common.getAllUsersByDeptId(depementData.get(position).getId(),deptAllUser);
        for(User user : deptAllUser){
            saleTeamUser = new SaleTeamUser();
            saleTeamUser.setName(user.getRealname());
            saleTeamUser.setId(user.getId());
            userData.add(saleTeamUser);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //全公司 确定
            case R.id.saleteam_screentag1_confirm:
                break;
            //全公司 全部取消
            case R.id.saleteam_screentag1_cancel:
                break;
            default:
                break;
        }
    }
}
