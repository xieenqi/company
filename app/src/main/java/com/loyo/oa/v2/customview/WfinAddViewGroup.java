package com.loyo.oa.v2.customview;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
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
import com.loyo.oa.v2.activityui.wfinstance.bean.BizFormFields;
import com.loyo.oa.v2.tool.ClickTool;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 【审批内容】 动态赋值
 * <p>
 * create by xnq on 2016/02/12
 */
public class WfinAddViewGroup extends LinearLayout {
    private Context context;
    private ArrayList<BizFormFields> lstData;
    private HashMap<String, Object> map_Values;
    private ArrayList<HashMap<String, Object>> submitData = new ArrayList<>();

    private WfinAddViewGroup(Context c) {
        super(c);
        context = c;
    }

    public WfinAddViewGroup(Context _context, ArrayList<BizFormFields> lstData, ArrayList<HashMap<String, Object>> data) {
        this(_context);
        setBackgroundColor(getResources().getColor(R.color.white));
        this.lstData = lstData;
        submitData = data;
        setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < lstData.size(); i++) {
            LogUtil.d(lstData.get(i).getName() + " ：列表名称 ");
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
                    parent.removeView(WfinAddViewGroup.this);
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


        /**
         * 加载子条目，根据后台返回的Type，设置不同的EditText属性
         * */
        for (int i = 0; i < lstData.size(); i++) {
//            View view = new View(context);
//            view.setBackgroundColor(getResources().getColor(R.color.activity_split));
//            view.setLayoutParams(new ViewGroup.LayoutParams(-1, 1));
//            addView(view);
            BizFormFields bizFormFields = lstData.get(i);
            LogUtil.dll("类型TYPE：" + bizFormFields.getDbtype());
            View convertView = inflater.inflate(R.layout.item_bizform_string, this, false);
            TextView label = (TextView) convertView.findViewById(R.id.tv_label);
            EditText value = (EditText) convertView.findViewById(R.id.edt_value);
            AlertDialog dialog_follow = null;

            if (bizFormFields != null) {
                if ("list".equals(bizFormFields.getDbtype())) {//自定义选择类型
                    dialog_follow = initDialog_Wheel_one(value, bizFormFields.getDefaultvalue(), i);
                    value.setOnClickListener(new ValueOnClickListener_list(dialog_follow, i));
                    value.setFocusable(false);
                    value.setFocusableInTouchMode(false);
                    value.setOnFocusChangeListener(null);
                    value.setInputType(InputType.TYPE_CLASS_TEXT);
                } else if ("long".equals(bizFormFields.getDbtype())) {//日期选择类型
                    value.setOnClickListener(new ValueOnClickListener_dateTime(value, i));
                    value.setFocusable(false);
                    value.setFocusableInTouchMode(false);
                    value.setOnFocusChangeListener(null);
                    value.setInputType(InputType.TYPE_CLASS_TEXT);
                } else if ("string".equals(bizFormFields.getDbtype())) {//输入字符类型
                    value.setTag(new String("输入字符类型"));
                    value.setFocusableInTouchMode(true);
                    value.setFocusable(true);
                    value.setOnClickListener(null);
                    value.requestFocus();
                    value.setInputType(InputType.TYPE_CLASS_TEXT);
                    value.addTextChangedListener(new BizFiedTextWatcher(i, value));
                } else if ("int".equals(bizFormFields.getDbtype())) {//输入数字类型
                    value.setTag(new String("输入 数字 类型"));
                    value.setFocusableInTouchMode(true);
                    value.setFocusable(true);
                    value.setOnClickListener(null);
                    value.addTextChangedListener(new BizFiedTextWatcher(i, value));
                    value.requestFocus();
                    value.setInputType(InputType.TYPE_CLASS_NUMBER);
                    value.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                } else if ("double".equals(bizFormFields.getDbtype())) {//货币 输入数字
                    value.setTag(new String("输入 货币 类型"));
                    value.setFocusableInTouchMode(true);
                    value.setFocusable(true);
                    value.setOnClickListener(null);
                    value.addTextChangedListener(new BizFiedTextWatcher(i, value));
                    value.requestFocus();
                    value.setTag("double");
                    value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    value.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                }
                label.setText(bizFormFields.getName());
            }
            if (!bizFormFields.isRequired()) {
                value.setHint("");
                value.setText("" + map_Values.get(bizFormFields.getId()));
            }
            if (bizFormFields.isEnable()) {
                addView(convertView);
            }
        }
        parent.addView(this);
    }

    /**
     * 外部获取 内部所输入的数据
     *
     * @return
     */
    public HashMap<String, Object> getInfoData() {
        return map_Values;
    }

    private class BizFiedTextWatcher implements TextWatcher {
        private int position;
        EditText vv;

        private BizFiedTextWatcher(int position, EditText vv) {
            this.vv = vv;
            this.position = position;
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                if (s.toString().length() > 0) {
                    map_Values.put(lstData.get(position).getId(),
                            !"double".equals(vv.getTag().toString()) ? s.toString() : Double.parseDouble(s.toString()));
                    LogUtil.d(vv.getTag() + "审批输入的内容" + s.toString());
                } else {
                    if (map_Values.containsKey(lstData.get(position).getId())
                            && map_Values.get(lstData.get(position).getId()).toString().length() == 1) {
                        map_Values.put(lstData.get(position).getId(), "");
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void getNuber() {

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

    /*时间选择*/
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
//                final DateTool.DateSetListener_Datetool dateListener = new DateTool.DateSetListener_Datetool(
//                        textView);
//                dateListener.setOnClick_callback(new DateTool.DateSetListener_Datetool.OnClick_Callback() {
//                    @Override
//                    public boolean onClick_onDateSet() {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onClick_onTimeSet() {
//                        map_Values.put(lstData.get(position).getId(), dateListener.strDate + dateListener.strTime);
//                        //                        mainApplication.logUtil.d("map_Values.put(" + (int) mListData.get(position).getId() + "," + dateListener.strDate + dateListener.strTime + ")");
//                        return false;
//                    }
//                });

                DateTimePickDialog dateTimePickDialog = new DateTimePickDialog(context, null);
                dateTimePickDialog.dateTimePicKDialog(new DateTimePickDialog.OnDateTimeChangedListener() {
                    @Override
                    public void onDateTimeChanged(int year, int month, int day, int hour, int min) {

//                        String str = year + "-" + String.format("%02d", (month + 1)) + "-"
//                                + String.format("%02d", day) + String.format(" %02d", hour) + String.format(":%02d", min);
//                        textView.setText(str);
//                        map_Values.put(lstData.get(position).getId(), str);

                        long time= com.loyo.oa.common.utils.DateTool.getStamp(year,month,day,hour,min,0);
                        textView.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(time));
                        map_Values.put(lstData.get(position).getId(), com.loyo.oa.common.utils.DateTool.getDateTimeReal(time));

                    }

                    @Override
                    public void onCancel() {

                    }
                }, true, "取消");
            }
        }
    }

    /*列表选择*/
    public AlertDialog initDialog_Wheel_one(final TextView textView, String src, int position) {
        final AlertDialog dialog;

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
        builder.setView(layout);
        dialog = builder.create();
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
        return dialog;
    }
}
