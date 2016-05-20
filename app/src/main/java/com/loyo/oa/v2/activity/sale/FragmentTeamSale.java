package com.loyo.oa.v2.activity.sale;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.sale.adapter.AdapterSaleTeam;
import com.loyo.oa.v2.activity.sale.bean.SaleMyList;
import com.loyo.oa.v2.activity.sale.bean.SaleTeamScreen;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ISale;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.customview.SaleCommPopupView;
import com.loyo.oa.v2.tool.customview.SaleScreenPopupView;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【团队销售机会列表】
 * Created by yyy on 16/5/17.
 */
public class FragmentTeamSale extends BaseFragment implements View.OnClickListener,PullToRefreshListView.OnRefreshListener2 {

    /**
     * 部门筛选回调
     */
    public final static int SALETEAM_SCREEN_TAG1 = 0X01;

    /**
     * 销售阶段回调
     */
    public final static int SALETEAM_SCREEN_TAG2 = 0X02;

    /**
     * 排序回调
     */
    public final static int SALETEAM_SCREEN_TAG3 = 0X03;

    private View mView;
    private Button btn_add;
    private Context mContext;
    private SaleTeamScreen saleTeamScreen;
    private ViewStub emptyView;
    private LinearLayout screen1;
    private LinearLayout screen2;
    private LinearLayout screen3;
    private LinearLayout saleteam_topview;
    private TextView saleteam_screen1_commy;
    private ImageView tagImage1;
    private ImageView tagImage2;
    private ImageView tagImage3;
    private AdapterSaleTeam adapterSaleTeam;
    private PullToRefreshListView listView;
    private SaleCommPopupView saleCommPopupView;
    private WindowManager.LayoutParams params;
    private SaleScreenPopupView saleScreenPopupView;
    private SaleMyList mSaleMyList;

    private List<Department> mDeptSource;  //部门和用户集合
    private List<Department> newDeptSource = new ArrayList<>();//我的部门
    private List<SaleTeamScreen> data = new ArrayList<>();

    private int requestPage = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case SALETEAM_SCREEN_TAG1:
                    saleTeamScreen = (SaleTeamScreen) msg.getData().getSerializable("data");
                    saleteam_screen1_commy.setText(saleTeamScreen.getName());
                    break;

                case SALETEAM_SCREEN_TAG2:
                    Toast(msg.getData().getString("data"));
                    break;

                case SALETEAM_SCREEN_TAG3:
                    Toast(msg.getData().getString("data"));
                    break;

            }

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_team_sale, null);
        }
        initView(mView);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void initView(View view) {

        mContext = getActivity();
        params = getActivity().getWindow().getAttributes();
        listView = (PullToRefreshListView) view.findViewById(R.id.saleteam_list);
        screen1 = (LinearLayout) view.findViewById(R.id.saleteam_screen1);
        screen2 = (LinearLayout) view.findViewById(R.id.saleteam_screen2);
        screen3 = (LinearLayout) view.findViewById(R.id.saleteam_screen3);
        emptyView = (ViewStub) view.findViewById(R.id.vs_nodata);
        saleteam_topview = (LinearLayout) view.findViewById(R.id.saleteam_topview);
        saleteam_screen1_commy = (TextView) view.findViewById(R.id.saleteam_screen1_commy);
        tagImage1 = (ImageView) view.findViewById(R.id.saleteam_screen1_iv1);
        tagImage2 = (ImageView) view.findViewById(R.id.saleteam_screen1_iv2);
        tagImage3 = (ImageView) view.findViewById(R.id.saleteam_screen1_iv3);
        btn_add = (Button) view.findViewById(R.id.btn_add);

        btn_add.setOnTouchListener(Global.GetTouch());
        btn_add.setOnClickListener(this);
        screen1.setOnClickListener(this);
        screen2.setOnClickListener(this);
        screen3.setOnClickListener(this);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        listView.setEmptyView(emptyView);

        mDeptSource = Common.getLstDepartment();
        deptSort();
        saleScreenPopupView = new SaleScreenPopupView(getActivity(), data, mHandler);

        /**
         * 列表监听
         * */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                app.startActivity(getActivity(), ActivitySaleDetails.class, MainApp.ENTER_TYPE_RIGHT, false, null);
            }
        });

    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        requestPage = 1;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        requestPage++;
        getData();
    }

    /**
     * 获取 我的机会列表
     */
    public void getData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", requestPage);
        params.put("pageSize", 1);
        params.put("stageId", "");
        params.put("type", "");

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISale.class).getSaleMyList(params, new RCallback<SaleMyList>() {
            @Override
            public void success(SaleMyList saleMyLists, Response response) {
                HttpErrorCheck.checkResponse("客户列表", response);
                if (null == saleMyLists.records || saleMyLists.records.size() == 0) {
                    Toast("没有更多数据了!");
                    listView.onRefreshComplete();
                    return;
                } else {
                    mSaleMyList = saleMyLists;
                }
                bindData();
                listView.onRefreshComplete();
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                listView.onRefreshComplete();
            }
        });
    }

    public void bindData(){
        if(null == adapterSaleTeam){
            adapterSaleTeam = new AdapterSaleTeam(getActivity(),mSaleMyList.records);
            listView.setAdapter(adapterSaleTeam);
        }else{
            adapterSaleTeam.notifyDataSetChanged();
        }
    }


    /**
     * PopupWindow关闭 恢复背景正常颜色
     * */
    private void closePopupWindow(ImageView view)
    {
        params = getActivity().getWindow().getAttributes();
        params.alpha = 1f;
        getActivity().getWindow().setAttributes(params);
        view.setBackgroundResource(R.drawable.arrow_down);
    }

    /**
     * PopupWindow打开，背景变暗
     * */
    private void openPopWindow(ImageView view){
        params = getActivity().getWindow().getAttributes();
        params.alpha=0.95f;
        getActivity().getWindow().setAttributes(params);
        view.setBackgroundResource(R.drawable.arrow_up);
    }

    /**
     * 组装部门格式
     * */
    private void setUser(){
        data.clear();
        for(Department department: newDeptSource){
            saleTeamScreen = new SaleTeamScreen();
            saleTeamScreen.setId(department.getId());
            saleTeamScreen.setName(department.getName());
            data.add(saleTeamScreen);
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

            //新建机会
            case R.id.btn_add:
                MainApp.getMainApp().startActivityForResult(mActivity, ActivityAddMySale.class,
                        MainApp.ENTER_TYPE_RIGHT, FinalVariables.REQUEST_CREATE_LEGWORK, null);
                break;


            //全公司筛选
            case R.id.saleteam_screen1:

                    saleScreenPopupView.showAsDropDown(screen1);
                    openPopWindow(tagImage1);
                    saleScreenPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            closePopupWindow(tagImage1);
                        }
                    });

                break;
            //销售阶段
            case R.id.saleteam_screen2:
                saleCommPopupView = new SaleCommPopupView(getActivity(),mHandler,1);
                saleCommPopupView.showAsDropDown(screen2);
                openPopWindow(tagImage2);
                saleCommPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        closePopupWindow(tagImage2);
                    }
                });

                break;
            //排序
            case R.id.saleteam_screen3:
                saleCommPopupView = new SaleCommPopupView(getActivity(),mHandler,2);
                saleCommPopupView.showAsDropDown(screen3);
                openPopWindow(tagImage3);
                saleCommPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        closePopupWindow(tagImage3);
                    }
                });

                break;

            default:
                break;
        }
    }
}
