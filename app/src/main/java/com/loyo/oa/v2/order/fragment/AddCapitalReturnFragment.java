package com.loyo.oa.v2.order.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.contactpicker.ContactPickerActivity;
import com.loyo.oa.contactpicker.model.event.ContactPickedEvent;
import com.loyo.oa.contactpicker.model.result.StaffMemberCollection;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.OrderAttachmentActivity;
import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.beans.OrganizationalMember;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.compat.Compat;
import com.loyo.oa.v2.customview.DateTimePickDialog;
import com.loyo.oa.v2.customview.PaymentPopView;
import com.loyo.oa.v2.order.activity.ActivityFragmentsStackManager;
import com.loyo.oa.v2.order.adapter.AddCapitalReturnAdapter;
import com.loyo.oa.v2.order.cell.OrderAddBaseCell;
import com.loyo.oa.v2.order.common.CapitalReturnValidator;
import com.loyo.oa.v2.order.common.PaymentMethod;
import com.loyo.oa.v2.order.model.CapitalReturn;
import com.loyo.oa.v2.tool.StringUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.app.Activity.RESULT_OK;


/**
 * Created by EthanGong on 2017/2/28.
 */

public class AddCapitalReturnFragment extends BaseStackFragment
        implements OrderAddBaseCell.ActionListener, OrderAddBaseCell.CapitalReturnListener {

    public interface AddCapitalReturnCallback {
        void onAddCapitalReturn(ArrayList<EstimateAdd> data, boolean initEmpty);
        void onBack(boolean initEmpty);
    }

    public AddCapitalReturnCallback callback;
    public String dealMoney = "0";

    View view;
    ActivityFragmentsStackManager manager;
    AddCapitalReturnAdapter adapter;
    String uuid = StringUtil.getUUID();
    int currentPickIndex = -1;

    @BindView(R.id.img_title_left)  ViewGroup backButton;
    @BindView(R.id.img_title_right) ViewGroup rightButton;
    @BindView(R.id.tv_title_1) TextView titleView;
    @BindView(R.id.recycle_view) RecyclerView recyclerView;


    @BindView(R.id.tv_rate_payment) TextView tv_rate_payment;
    @BindView(R.id.tv_dealprice) TextView tv_dealprice ; //成交金额
    @BindView(R.id.tv_totalprice) TextView tv_totalprice; //已回款
    @BindView(R.id.tv_aleryprice)TextView tv_aleryprice; //开票总金额
    @BindView(R.id.tv_faileprice) TextView tv_faileprice;  //未回款

    @OnClick(R.id.img_title_left) void onBack() {

        hideKeyboard();
        ArrayList<CapitalReturn> list = adapter.data;
        boolean hasUnsavedData = false;
        for (CapitalReturn capitalReturn : list) {
            if (capitalReturn.hasChanged()) {
                    hasUnsavedData = true;
                    break;
            }
        }
        if (hasUnsavedData) {
            sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                }
            }, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                    manager.pop();
                }
            }, "提示", "是否放弃编辑？确定后信息将不会保存。");
        }
        else {
            if (callback != null) {
                callback.onBack(list.size() == 0);
            }
            this.manager.pop();
        }
    }

    @OnClick(R.id.img_title_right) void onCommit() {
        hideKeyboard();
        ArrayList<CapitalReturn> list = adapter.data;
        ArrayList<EstimateAdd> commitData = new ArrayList<>();
        boolean canCommit = true;
        int i = 1;
        for (CapitalReturn capitalReturn : list) {
            canCommit = CapitalReturnValidator.validateAndToast(capitalReturn, "回款记录"+i);
            if (canCommit) {
                commitData.add(capitalReturn.toEstimateAdd());
            }
            else {
                break;
            }
            i++;
        }
        if (!canCommit) {
            return;
        }
        if (callback != null) {
            callback.onAddCapitalReturn(commitData, list.size() == 0);
        }
        this.manager.pop();
    }

    public AddCapitalReturnFragment(ActivityFragmentsStackManager manager, boolean initEmpty) {
        this.manager = manager;
        adapter = new AddCapitalReturnAdapter(this, this, initEmpty);
//        adapter.callback = new AddCapitalReturnAdapter.ListChangeCallback() {
//            @Override
//            public void onListChange(long totalMoney, long totalBilling) {
//                tv_dealprice.setText("￥"+totalMoney);
//                tv_aleryprice.setText("￥"+totalBilling);
//            }
//        };
    }

    private AddCapitalReturnFragment() {
        adapter = new AddCapitalReturnAdapter(this, this, false);
    }

    public void setData(ArrayList<EstimateAdd> data) {
        if (data == null) {
            return;
        }
        ArrayList<CapitalReturn> list = new ArrayList<>();
        for (EstimateAdd estimateAdd : data) {
            list.add(new CapitalReturn(estimateAdd));
        }
        adapter.setData(list);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_add_capital_return, container, false);
            ButterKnife.bind(this, view);
            setup();
        }
        return view;
    }

    private void setup() {
        titleView.setText("新增回款");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.fireListChange();
        tv_dealprice.setText("￥"+dealMoney);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            //附件回调
            case ExtraAndResult.MSG_WHAT_HIDEDIALOG:
                CapitalReturn capitalReturn =  adapter.data.get(currentPickIndex);
                String uuid = data.getStringExtra("uuid");
                int attachmentSize = data.getIntExtra("size", 0);
                capitalReturn.attachmentUUId = uuid;
                capitalReturn.attachmentCount = attachmentSize;
                if (attachmentSize == 0) {
                    capitalReturn.attachmentUUId = null;
                }
                adapter.notifyItemChanged(currentPickIndex);
                break;

        }
    }

    /**
     * 选人回调
     */
    @Subscribe
    public void onContactPicked(ContactPickedEvent event) {

        if (FinalVariables.PICK_RESPONSIBLE_USER_REQUEST.equals(event.request)
                &&
                uuid.equals(event.session)) {

            StaffMemberCollection collection = event.data;
            OrganizationalMember user = Compat.convertStaffCollectionToNewUser(collection);
            if (user == null) {
                return;
            }
            EstimateAdd.PayeeUser payee = new EstimateAdd.PayeeUser();
            payee.id = user.getId();
            payee.name = user.getName();
            payee.avatar = user.getAvatar();
            CapitalReturn capitalReturn =  adapter.data.get(currentPickIndex);
            capitalReturn.payeeUser = payee;
            adapter.notifyItemChanged(currentPickIndex);
        }
    }

    @Override
    public boolean onBackPressed() {
        onBack();
        return true;
    }

    /**
     * OrderAddBaseCell.ActionListener
     */

    @Override
    public void onDeleteAtIndex(final int index) {
        CapitalReturn capitalReturn = adapter.data.get(index);
        if (capitalReturn.isEmpty()) {
            adapter.data.remove(index);
            adapter.notifyDataSetChanged();
            return;
        }
        sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        }, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
                adapter.data.remove(index);
                adapter.notifyDataSetChanged();
            }
        }, "提示", "你确定要删除“回款记录"+ (index+1) +"”？");
    }

    @Override
    public void toast(String msg) {
        Toast(msg);
    }

    @Override
    public void onAdd() {
        adapter.data.add(new CapitalReturn());
        adapter.notifyDataSetChanged();
    }

    /**
     * OrderAddBaseCell.CapitalReturnListener
     */
    @Override
    public void onFundingDateForIndex(int index) {
        currentPickIndex = index;
        CapitalReturn capitalReturn = adapter.data.get(index);

        DateTimePickDialog dateTimePickDialog = new DateTimePickDialog(getActivity(), null, true);
        dateTimePickDialog.initTimeStamp =
                capitalReturn.receivedAt > 0 ?
                        capitalReturn.receivedAt*1000 :new Date().getTime();
        dateTimePickDialog.endTimeStamp = new Date().getTime();
        dateTimePickDialog.dateTimePicKDialog(new DateTimePickDialog.OnDateTimeChangedListener() {
            @Override
            public void onDateTimeChanged(final int year, final int month, final int day, final int hour, final int min) {
                long time= DateTool.getStamp(year, month, day, hour, min, 0);
                CapitalReturn capitalReturn =  adapter.data.get(currentPickIndex);
                capitalReturn.receivedAt = time;
                adapter.notifyItemChanged(currentPickIndex);
            }

            @Override
            public void onCancel() {

            }

        }, true, "取消");
    }

    @Override
    public void onPayeeForIndex(int index) {
        currentPickIndex = index;
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContactPickerActivity.SINGLE_SELECTION_KEY, true);
        bundle.putSerializable(ContactPickerActivity.REQUEST_KEY, FinalVariables.PICK_RESPONSIBLE_USER_REQUEST);
        bundle.putSerializable(ContactPickerActivity.SESSION_KEY, uuid);
        Intent intent = new Intent();
        intent.setClass(getActivity(), ContactPickerActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onPaymentForIndex(int index) {
        currentPickIndex = index;
        ArrayList<String> data = PaymentMethod.getNames();
        final PaymentPopView popView = new PaymentPopView(getActivity(), data, "付款方式");
        popView.show();
        popView.setCanceledOnTouchOutside(true);
        popView.setCallback(new PaymentPopView.VaiueCallback() {
            @Override
            public void setValue(String value, int index) {
                CapitalReturn capitalReturn =  adapter.data.get(currentPickIndex);
                capitalReturn.payeeMethod = PaymentMethod.getMethodAt(index-1);
                adapter.notifyItemChanged(currentPickIndex);
            }
        });
    }

    @Override
    public void onAttachmentForIndex(int index) {
        currentPickIndex = index;
        CapitalReturn capitalReturn =  adapter.data.get(currentPickIndex);
        String uuid = capitalReturn.attachmentUUId;
        if (uuid == null) {
            uuid = StringUtil.getUUID();
        }
        Bundle mBundle = new Bundle();
        mBundle.putInt("bizType", 26);
        mBundle.putString("uuid", uuid);
        Intent intent = new Intent(getActivity(), OrderAttachmentActivity.class);
        intent.putExtras(mBundle);
        startActivityForResult(intent, ExtraAndResult.MSG_WHAT_HIDEDIALOG, null);
    }

}
