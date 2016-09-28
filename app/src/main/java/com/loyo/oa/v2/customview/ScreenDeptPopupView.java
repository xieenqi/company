package com.loyo.oa.v2.customview;

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

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.bean.Role;
import com.loyo.oa.v2.activityui.other.bean.User;
import com.loyo.oa.v2.activityui.sale.adapter.AdapterSaleTeamScreen1;
import com.loyo.oa.v2.activityui.sale.adapter.AdapterSaleTeamScreen2;
import com.loyo.oa.v2.activityui.sale.bean.SaleTeamScreen;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBUser;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 【客户和机会】公司人员筛选标签
 * Created by yyy on 16/5/18.
 */
public class ScreenDeptPopupView extends PopupWindow implements View.OnClickListener {

    private View contentView;
    private ListView listView1;
    private ListView listView2;
    private Button confirm;
    private Button cancel;
    private SaleTeamScreen saleTeamScreen;
    private Context mContext;
    private Handler mHandler;
    private Message msg;
    private Bundle bundle;
    private boolean isKind;

    private AdapterSaleTeamScreen1 adapter1;
    private AdapterSaleTeamScreen2 adapter2;
    private List<SaleTeamScreen> depementData;
    private List<SaleTeamScreen> userData = new ArrayList<>();

    private int deptPosition = 0;

    public ScreenDeptPopupView(final Activity context, List<SaleTeamScreen> data, Handler handler) {
        LayoutInflater inflater = LayoutInflater.from(context);
        contentView = inflater.inflate(R.layout.saleteam_screentag1, null, false);
        this.depementData = data;
        this.mContext = context;
        this.mHandler = handler;
        initView();

        this.setContentView(contentView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setBackgroundDrawable(new ColorDrawable(0000000000));//AndroidRuntime
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        if (data.size() > 0)
            getFirstDept(0);

        adapter1 = new AdapterSaleTeamScreen1(context, depementData, 1);
        listView1.setAdapter(adapter1);
        adapter2 = new AdapterSaleTeamScreen2(context, userData, 2);
        listView2.setAdapter(adapter2);
//        adapter2.refshData(0);
    }

    public void initView() {
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
                deptPosition = position;
                getFirstDept(position);
                adapter1.selectPosition(position);
                adapter1.notifyDataSetChanged();
                adapter2.refshData(position);
            }
        });

        /*右侧列表*/
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter2.selectPosition(position);
                adapter2.notifyDataSetChanged();
                resultData(position);
            }
        });
        contentView.findViewById(R.id.shade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * 设置回调数据
     */
    public void resultData(int position) {
        msg = new Message();
        bundle = new Bundle();
        //设置全体人员 名字
        if (position == 0) {
            //如果数据权限为自己，则isKind: false请求数据传id true请求数据传xPath
            if (MainApp.user.role.getDataRange() == Role.SELF) {
                isKind = false;
            } else {
                isKind = true;
                userData.get(position).setName(depementData.get(deptPosition).getName());
            }
        } else {
            isKind = false;
        }
        bundle.putSerializable("data", userData.get(position));
        bundle.putBoolean("kind", isKind);
        msg.setData(bundle);
        msg.what = 0x01;
        mHandler.sendMessage(msg);
        LogUtil.dee("name:" + userData.get(position).getName() + "  id:" + userData.get(position).getId() + "  xPath:" + userData.get(position).getxPath());
        dismiss();
    }

    /**
     * 组装获取某部门全体人员
     */
    public void getFirstDept(int position) {
        userData.clear();
        List<DBUser> deptAllUser = new ArrayList<DBUser>();
        if (depementData.size() > 0) {
            String deptId = depementData.get(position).getId();
            deptAllUser.addAll(OrganizationManager.shareManager().entireUsersOfDepartment(deptId));
        }

        for (int i = 0; i < deptAllUser.size(); i++) {
            saleTeamScreen = new SaleTeamScreen();
            saleTeamScreen.setName(deptAllUser.get(i).name);
            saleTeamScreen.setId(deptAllUser.get(i).id);
            userData.add(saleTeamScreen);
        }
        saleTeamScreen = new SaleTeamScreen();
        saleTeamScreen.setName("全部人员");
        saleTeamScreen.setId(depementData.get(position).getId());

        saleTeamScreen.setxPath(depementData.get(position).getxPath());
        userData.add(0, saleTeamScreen);
    }

    /*暂时弃用*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
