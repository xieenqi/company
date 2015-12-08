package com.loyo.oa.v2.tool.customview;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.BizFormFields;
import com.loyo.oa.v2.tool.ClickTool;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 描述 审批内容节点 审批内容列表
 * 作者 : xnq
 */
public class WfinstanceViewGroup extends LinearLayout {
    private Context context;
    private ArrayList<BizFormFields> lstData;
    private HashMap<String, Object> map_Values;
    private ArrayList<HashMap<String, Object>> submitData = new ArrayList<>();

    private WfinstanceViewGroup(Context c) {
        super(c);
        context = c;
    }

    public WfinstanceViewGroup(Context _context, ArrayList<BizFormFields> lstData, ArrayList<HashMap<String, Object>> data) {
        this(_context);
        setBackgroundColor(getResources().getColor(R.color.white));
        this.lstData = lstData;
        submitData = data;
        setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        setOrientation(LinearLayout.VERTICAL);

        for (int i=0;i<lstData.size();i++){
            LogUtil.d(lstData.get(i).getName()+" ：列表名称 ");
        }
    }

    /**
     * 绑定视图
     *
     * @param index  视图item 数量
     * @param parent 视图父容器
     */
    public void bindView(int index, final ViewGroup parent) {
        if (null == lstData || lstData.isEmpty() || null == submitData || submitData.isEmpty())
            return;
        setId(index);
        map_Values = submitData.get(getId());
        LayoutInflater inflater = LayoutInflater.from(context);
        //加载删除条目
        if (getId() > 0) {
            inflater.inflate(R.layout.item_wfinstance_delete_layout, this, true);
            final TextView title = (TextView) findViewById(R.id.tv_title);
            title.setText("新增内容" + getId());
            findViewById(R.id.layout_delete).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitData.remove(getId());
                    parent.removeView(WfinstanceViewGroup.this);
                    for (int i = 1; i < parent.getChildCount(); i++) {
                        View child = parent.getChildAt(i);
                        int id = child.getId();
                        if (id > getId())
                            id--;
                        child.setId(id);
                        TextView tv = (TextView) child.findViewById(R.id.tv_title);
                        if (null != tv)
                            tv.setText("新增内容" + child.getId());
                    }
                }
            });
        }
        //加载子条目
        for (int i = 0; i < lstData.size(); i++) {
            View view = new View(context);
            view.setBackgroundColor(getResources().getColor(R.color.activity_split));
            view.setLayoutParams(new ViewGroup.LayoutParams(-1, 1));
            addView(view);
            BizFormFields bizFormFields = lstData.get(i);
            View convertView = inflater.inflate(R.layout.item_bizform_string, this, false);
            TextView label = (TextView) convertView.findViewById(R.id.tv_label);
            EditText value = (EditText) convertView.findViewById(R.id.edt_value);
            AlertDialog dialog_follow = null;

            if (bizFormFields != null) {
                if (bizFormFields.isList()) {
                    dialog_follow = initDialog_Wheel_one(value, bizFormFields.getDefaultvalue(), i);
                    value.setOnClickListener(new ValueOnClickListener_list(dialog_follow, i));
                    value.setFocusable(false);
                    value.setFocusableInTouchMode(false);
                    value.setOnFocusChangeListener(null);
                    value.setInputType(InputType.TYPE_CLASS_TEXT);
                } else if ("DateTime".equals(bizFormFields.getDbtype())) {
                    value.setOnClickListener(new ValueOnClickListener_dateTime(value, i));
                    value.setFocusable(false);
                    value.setFocusableInTouchMode(false);
                    value.setOnFocusChangeListener(null);
                    value.setInputType(InputType.TYPE_CLASS_TEXT);
                } else if ("String".equals(bizFormFields.getDbtype())) {
                    value.setFocusableInTouchMode(true);
                    value.setFocusable(true);
                    value.setOnClickListener(null);
                    value.addTextChangedListener(new BizFiedTextWatcher(i));
                    value.requestFocus();
                    value.setInputType(InputType.TYPE_CLASS_TEXT);
                } else if ("Numeric".equals(bizFormFields.getDbtype())) {
                    value.setFocusableInTouchMode(true);
                    value.setFocusable(true);
                    value.setOnClickListener(null);
                    value.addTextChangedListener(new BizFiedTextWatcher(i));
                    value.requestFocus();
                    value.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else if ("Money".equals(bizFormFields.getDbtype())) {
                    value.setFocusableInTouchMode(true);
                    value.setFocusable(true);
                    value.setOnClickListener(null);
                    value.addTextChangedListener(new BizFiedTextWatcher(i));
                    value.requestFocus();
                    value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                }
                if (!bizFormFields.isRequired())
                    value.setHint("");

                value.setText((String) map_Values.get(bizFormFields.getId()));

                label.setText(bizFormFields.getName() + "：");
            }
            addView(convertView);
        }
        parent.addView(this);
    }

    /**
     * 外部获取 内部所输入的数据
     * @return
     */
    public HashMap<String, Object> getInfoData(){
        return map_Values;
    }

    private class BizFiedTextWatcher implements TextWatcher {
        private int position;

        private BizFiedTextWatcher(int position) {
            this.position = position;
        }

        @Override
        public void afterTextChanged(Editable s) {
            LogUtil.d(" 审批内容输出完成   ", "after输入过后TextChanged, s : " + s.toString());
            if (s.toString().length() > 0) {
                map_Values.put(lstData.get(position).getId(), s.toString());
            } else {
                if (map_Values.containsKey(lstData.get(position).getId())
                        && map_Values.get(lstData.get(position).getId()).toString().length() == 1) {
                    map_Values.put(lstData.get(position).getId(), "");
                }
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
    }


    private class ValueOnClickListener_list implements View.OnClickListener {
        AlertDialog dialog_Wheel_one;

        private ValueOnClickListener_list(AlertDialog _dialog, int position) {
            this.dialog_Wheel_one = _dialog;
        }

        @Override
        public void onClick(View v) {
            if (dialog_Wheel_one != null && !dialog_Wheel_one.isShowing()) {
                dialog_Wheel_one.show();
            }
        }
    }

    private class ValueOnClickListener_dateTime implements View.OnClickListener {
        private TextView textView;
        private int position;

        private ValueOnClickListener_dateTime(TextView textView, int position) {
            this.textView = textView;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
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
                    public void onDateTimeChanged(int year, int month, int day, int hour, int min) {

                        String str = year + "-" + String.format("%02d", (month + 1)) + "-"
                                + String.format("%02d", day) + String.format(" %02d", hour) + String.format(":%02d", min);
                        textView.setText(str);
                        map_Values.put(lstData.get(position).getId(), str);

                    }
                });

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

    AlertDialog initDialog_Wheel_one(final TextView textView, String src, int position) {

        String[] str = src.split(",");
        final ArrayList<HashMap<String, String>> lstData1 = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;
        for (int i = 0; i < str.length; i++) {
            map = new HashMap<String, String>();
            map.put("id", lstData.get(position).getId());
            map.put("title", str[i]);
            lstData1.add(map);
        }

        BaseAdapter followAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return lstData1.size();
            }

            @Override
            public Object getItem(int position) {
                return lstData1.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
}
