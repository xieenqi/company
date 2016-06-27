package com.loyo.oa.v2.ui.activity.wfinstance.adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.BizFormFields;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.ClickTool;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.customview.DateTimePickDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BizFormFieldsListViewAdapter extends BaseAdapter {

    ArrayList<BizFormFields> lstData;
    public HashMap<String, Object> map_Values;

    Context context;
    HashMap<Integer, Item_info> views = new HashMap<>();

    public BizFormFieldsListViewAdapter(final Context _context, final ArrayList<BizFormFields> lstData) {
        this.lstData = lstData;
        this.context = _context;
        map_Values = new HashMap<String, Object>();
    }

    public ArrayList<BizFormFields> getLstData() {
        return lstData;
    }

    @Override
    public int getCount() {
        return lstData.size();
    }

    @Override
    public Object getItem(final int position) {
        return lstData.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        //mainTasksInfoActivity.logger.d( "getView() position:" + position);
        Item_info item_info;
        BizFormFields bizFormFields = lstData.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_bizform_string, null);

            item_info = new Item_info();
            item_info.tv_label = (TextView) convertView.findViewById(R.id.tv_label);
            item_info.value = (EditText) convertView.findViewById(R.id.edt_value);

            convertView.setTag(item_info);
        } else {
            item_info = (Item_info) convertView.getTag();
        }
        views.put(position, item_info);

        if (bizFormFields != null) {
            if (bizFormFields.isList()) {
                item_info.dialog_follow = initDialog_Wheel_one(item_info.value, bizFormFields.getDefaultvalue(), position);

                item_info.value.setOnClickListener(new ValueOnClickListener_list(item_info.dialog_follow, position));
                item_info.value.setFocusable(false);
                item_info.value.setFocusableInTouchMode(false);
                item_info.value.setOnFocusChangeListener(null);
                item_info.value.setInputType(InputType.TYPE_CLASS_TEXT);
//                item_info.value.setHint(R.string.app_please_select);
            } else if ("DateTime".equals(bizFormFields.getDbtype())) {
                item_info.value.setOnClickListener(new ValueOnClickListener_dateTime(item_info.value, position));
                item_info.value.setFocusable(false);
                item_info.value.setFocusableInTouchMode(false);
                item_info.value.setOnFocusChangeListener(null);
                item_info.value.setInputType(InputType.TYPE_CLASS_TEXT);
//                item_info.value.setHint(R.string.app_please_select);
            } else if ("String".equals(bizFormFields.getDbtype())) {
                item_info.value.setFocusableInTouchMode(true);
                item_info.value.setFocusable(true);
                item_info.value.setOnClickListener(null);
                item_info.value.addTextChangedListener(new BizFiedTextWatcher(position));
                item_info.value.requestFocus();
                item_info.value.setInputType(InputType.TYPE_CLASS_TEXT);
//                item_info.value.setHint(R.string.app_please_input);
            } else if ("Numeric".equals(bizFormFields.getDbtype())) {
                item_info.value.setFocusableInTouchMode(true);
                item_info.value.setFocusable(true);
                item_info.value.setOnClickListener(null);
                item_info.value.addTextChangedListener(new BizFiedTextWatcher(position));
                item_info.value.requestFocus();
                item_info.value.setInputType(InputType.TYPE_CLASS_NUMBER);
//                item_info.value.setHint(R.string.app_please_input);
            } else if ("Money".equals(bizFormFields.getDbtype())) {
                item_info.value.setFocusableInTouchMode(true);
                item_info.value.setFocusable(true);
                item_info.value.setOnClickListener(null);
                item_info.value.addTextChangedListener(new BizFiedTextWatcher(position));
                item_info.value.requestFocus();
                item_info.value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//                item_info.value.setHint(R.string.app_please_input);
            }
            if (!bizFormFields.isRequired())
                item_info.value.setHint("");
//            if(!map_Values.keySet().contains((int)bizFormFields.getBizformId()))
//            if(map_Values.isEmpty())
//                map_Values.put((int)bizFormFields.getId(),"");

            item_info.value.setText((String) map_Values.get(bizFormFields.getId()));

            item_info.tv_label.setText(bizFormFields.getName() + "：");
        }

        return convertView;
    }

    public void setEmpty() {
        //新增后，清空内容

        try {
            Iterator<Map.Entry<Integer, Item_info>> iter = views.entrySet().iterator();

            while (iter.hasNext()) {
                iter.next().getValue().value.setText("");
            }

            views.get(0).value.requestFocus();

            map_Values.clear();
        } catch (Exception ex) {
            Global.ProcException(ex);
        }
    }

    private class BizFiedTextWatcher implements TextWatcher {
        private int position;

        private BizFiedTextWatcher(final int position) {
            this.position = position;
        }

        @Override
        public void afterTextChanged(final Editable s) {
            Log.e(getClass().getSimpleName(), "afterTextChanged, s : " + s.toString());
            if (s.toString().length() > 0) {
                map_Values.put(lstData.get(position).getId(), s.toString());
            } else {
                if (map_Values.containsKey(lstData.get(position).getId())
                        && map_Values.get(lstData.get(position).getId()).toString().length() == 1) {
//                    map_Values.put((int) lstData.get(position).getId(), "");
                }
            }
        }

        @Override
        public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {

        }

        @Override
        public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

        }
    }

    private class ValueOnClickListener_list implements View.OnClickListener {
        AlertDialog dialog_Wheel_one;

        private ValueOnClickListener_list(final AlertDialog _dialog, final int position) {
            this.dialog_Wheel_one = _dialog;
        }

        @Override
        public void onClick(final View v) {
            if (dialog_Wheel_one != null && !dialog_Wheel_one.isShowing()) {
                dialog_Wheel_one.show();
            }
        }
    }

    private class ValueOnClickListener_dateTime implements View.OnClickListener {
        private TextView textView;
        private int position;

        private ValueOnClickListener_dateTime(final TextView textView, final int position) {
            this.textView = textView;
            this.position = position;
        }

        @Override
        public void onClick(final View v) {
            if (!ClickTool.isDoubleClick()) {
                DateTool.calendar = Calendar.getInstance();
                final DateTool.DateSetListener_Datetool dateListener = new DateTool.DateSetListener_Datetool(
                        textView);
                dateListener.setOnClick_callback(new DateTool.DateSetListener_Datetool.OnClick_Callback() {
                    @Override
                    public boolean onClick_onDateSet() {
                        return false;
                    }

                    @Override
                    public boolean onClick_onTimeSet() {
                        map_Values.put(lstData.get(position).getId(), dateListener.strDate + dateListener.strTime);
//                        mainApplication.logUtil.d("map_Values.put(" + (int) mListData.get(position).getId() + "," + dateListener.strDate + dateListener.strTime + ")");
                        return false;
                    }
                });

                DateTimePickDialog dateTimePickDialog = new DateTimePickDialog(context, null);
                dateTimePickDialog.dateTimePicKDialog(new DateTimePickDialog.OnDateTimeChangedListener() {
                    @Override
                    public void onDateTimeChanged(final int year, final int month, final int day, final int hour, final int min) {

                        String str = year + "-" + String.format("%02d", (month + 1)) + "-"
                                + String.format("%02d", day) + String.format(" %02d", hour) + String.format(":%02d", min);
                        textView.setText(str);
                        map_Values.put(lstData.get(position).getId(), str);

                    }

                    @Override
                    public void onCancel() {

                    }
                },false,"取消");

//                DatePickerDialog datePickerDialog = new DatePickerDialog(
//                        textView.getContext(), dateListener,
//                        DateTool.calendar.get(Calendar.YEAR),
//                        DateTool.calendar.get(Calendar.MONTH),
//                        DateTool.calendar.get(Calendar.DAY_OF_MONTH));
//                datePickerDialog.show();
            }
        }
    }

    AlertDialog dialog;

    AlertDialog initDialog_Wheel_one(final TextView textView, final String src, final int position) {

        String[] str = src.split(",");
        final ArrayList<HashMap<String, String>> lstData1 = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;
        for (int i = 0; i < str.length; i++) {
            map = new HashMap<>();
            map.put("id", lstData.get(position).getId() + "");
            map.put("title", str[i]);
            lstData1.add(map);
        }

        BaseAdapter followAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return lstData1.size();
            }

            @Override
            public Object getItem(final int position) {
                return lstData1.get(position);
            }

            @Override
            public long getItemId(final int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, final ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_listview_product_select, parent, false);
                }

                TextView tv = (TextView) convertView.findViewById(R.id.tv);
                tv.setText(lstData1.get(position).get("title"));

                return convertView;
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_products_select, null);
        ListView listView_follow = (ListView) layout.findViewById(R.id.listView);
        listView_follow.setAdapter(followAdapter);
        listView_follow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                map_Values.put(lstData1.get(position).get("id"),
                        lstData1.get(position).get("title"));

                textView.setText(lstData1.get(position).get("title"));
                dialog.dismiss();
            }
        });
        builder.setView(layout);
        dialog = builder.create();
        return dialog;

    }

    class Item_info {
        AlertDialog dialog_follow;
        TextView tv_label;
        EditText value;
    }
}
