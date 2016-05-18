package com.loyo.oa.v2.activity.sale;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.sale.bean.SaleTeamUser;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.customview.SaleScreenPopupView;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;
import java.util.ArrayList;
import java.util.List;

/**
 * 【团队销售机会列表】
 * Created by yyy on 16/5/17.
 */
public class FragmentTeamSale extends BaseFragment implements View.OnClickListener{

    private Context mContext;
    private View mView;
    private AdapterSaleTeam adapterSaleTeam;

    private SaleScreenPopupView saleScreenPopupView;
    private WindowManager.LayoutParams params;
    private SaleTeamUser saleTeamUser;

    private PullToRefreshListView lv_list;
    private LinearLayout screen1;
    private LinearLayout screen2;
    private LinearLayout screen3;
    private LinearLayout saleteam_topview;
    private TextView saleteam_screen1_commy;
    private ImageView tagImage1;

    private String selectId;
    private String selectName;

    private List<Department> mDeptSource;  //部门和用户集合
    private List<Department> newDeptSource = new ArrayList<>();//我的部门
    private List<SaleTeamUser> data = new ArrayList<>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            if(msg.what == 0x01){
                saleTeamUser = (SaleTeamUser) msg.getData().getSerializable("data");
                saleteam_screen1_commy.setText(saleTeamUser.getName());
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_team_sale, null);
        }
        init(mView);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void init(View view){

        mContext = getActivity();
        params = getActivity().getWindow().getAttributes();
        lv_list = (PullToRefreshListView)view.findViewById(R.id.saleteam_list);
        screen1 = (LinearLayout)view.findViewById(R.id.saleteam_screen1);
        screen2 = (LinearLayout)view.findViewById(R.id.saleteam_screen2);
        screen3 = (LinearLayout)view.findViewById(R.id.saleteam_screen3);
        saleteam_topview = (LinearLayout)view.findViewById(R.id.saleteam_topview);
        saleteam_screen1_commy = (TextView)view.findViewById(R.id.saleteam_screen1_commy);
        tagImage1 = (ImageView)view.findViewById(R.id.saleteam_screen1_iv1);

        screen1.setOnClickListener(this);
        screen2.setOnClickListener(this);
        screen3.setOnClickListener(this);

        mDeptSource = Common.getLstDepartment();
        deptSort();
        adapterSaleTeam = new AdapterSaleTeam(mContext);
        lv_list.setAdapter(adapterSaleTeam);

    }

    /**
     * PopupWindow关闭 恢复背景正常颜色
     * */
    private void closePopupWindow()
    {
        params = getActivity().getWindow().getAttributes();
        params.alpha = 1f;
        getActivity().getWindow().setAttributes(params);
        tagImage1.setBackgroundResource(R.drawable.arrow_down);
    }

    /**
     * 组装部门格式
     * */
    private void setUser(){
        data.clear();
        for(Department department: newDeptSource){
            saleTeamUser = new SaleTeamUser();
            saleTeamUser.setId(department.getId());
            saleTeamUser.setName(department.getName());
            data.add(saleTeamUser);
        }
    }

    /**
     * 过滤出我的部门
     * */
    private void deptSort() {
        newDeptSource.clear();
        User user = MainApp.user;
        for(Department department : mDeptSource){
            for(int i = 0;i<user.getDepts().size();i++){
                if(department.getId().contains(user.getDepts().get(i).getShortDept().getId())){
                    newDeptSource.add(department);
                }
            }
        }
        setUser();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.saleteam_screen1:

                    saleScreenPopupView = new SaleScreenPopupView(getActivity(),data,mHandler);
                    saleScreenPopupView.showAsDropDown(screen1);

                    /*弹出Popup 背景变暗*/
                    params = getActivity().getWindow().getAttributes();
                    params.alpha=0.9f;
                    getActivity().getWindow().setAttributes(params);
                    tagImage1.setBackgroundResource(R.drawable.arrow_up);

                    saleScreenPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            closePopupWindow();
                        }
                    });

                break;

            case R.id.saleteam_screen2:

                break;

            case R.id.saleteam_screen3:

                break;
        }
    }
}
