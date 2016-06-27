package com.loyo.oa.v2.ui.activity.sale.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.SalestagesRadioListViewAdapter;
import com.loyo.oa.v2.beans.SaleStage;

import java.util.ArrayList;


@SuppressLint("ValidFragment")
public class SaleStageDialogFragment extends DialogFragment {

    private View view_SaleStageDialogFragment;
    private ArrayList<SaleStage> lstData_SaleStage;
    private SalestagesRadioListViewAdapter salestagesRadioListViewAdapter;
    public TextView textView;
    public View reasonView;
    public SaleStage lSaleStageID_select;

    public SaleStageDialogFragment() {

    }

    public SaleStageDialogFragment(ArrayList<SaleStage> lstData_SaleStage, TextView textView,View reasonView) {
        this.lstData_SaleStage = lstData_SaleStage;
        this.textView = textView;
        this.reasonView=reasonView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (salestagesRadioListViewAdapter == null) {
            salestagesRadioListViewAdapter = new SalestagesRadioListViewAdapter(getActivity(), lstData_SaleStage);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // 获得视图
        view_SaleStageDialogFragment = inflater.inflate(R.layout.dialog_salestage_select, container, false);
        ListView listView_SaleStage = (ListView) view_SaleStageDialogFragment.findViewById(R.id.listView);
        listView_SaleStage.setAdapter(salestagesRadioListViewAdapter);
        listView_SaleStage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                salestagesRadioListViewAdapter.notifyDataSetChanged();
                salestagesRadioListViewAdapter.isSelected = id;
                lSaleStageID_select = lstData_SaleStage.get((int) id);
                textView.setText(lstData_SaleStage.get((int) id).getName());
                reasonView.setVisibility(lSaleStageID_select.getProb()==0?View.VISIBLE:View.GONE);
                dismiss();
            }
        });

        return view_SaleStageDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }

}
