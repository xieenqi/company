package com.loyo.oa.v2.tool.customview;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Contact;
import com.loyo.oa.v2.beans.ContactLeftExtras;
import com.loyo.oa.v2.beans.ExtraData;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.ClickTool;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * com.loyo.oa.v2.tool.customview
 * 描述 : 新增客户联系人 动态字段 【销售机会的动态字段也在用】
 * 作者 : ykb
 * 时间 : 15/10/7.
 */
public class ContactAddforExtraData extends LinearLayout {

    private AlertDialog dialog;
    private Context mContext;
    private ArrayList<ContactLeftExtras> extras = new ArrayList<>();
    private Contact mContact;
    private String birthStr;
    private int age;

    public ContactAddforExtraData(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        setOrientation(VERTICAL);
    }

    public ContactAddforExtraData(Context context, Contact mContact, ArrayList<ContactLeftExtras> extras, boolean edit, int valueColor, int valueSize) {
        this(context, null, 0);
        this.extras = extras;
        this.mContact = mContact;
        bindView(edit, valueColor, valueSize);

        LogUtil.dee("新增联系人 动态字段Contact:" + MainApp.gson.toJson(mContact));
        LogUtil.dee("新增联系人 动态字段ContactExtras:" + MainApp.gson.toJson(extras));
    }

    public ArrayList<ContactLeftExtras> getExtras() {
        return extras;
    }

    /**
     * 绑定数据
     *
     * @param edit
     * @param valueColor
     * @param valueSize
     */
    private void bindView(boolean edit, int valueColor, int valueSize) {
        if (null == extras || extras.isEmpty()) {
            return;
        }

        for (int i = 0; i < extras.size(); i++) {
            ContactLeftExtras customerExtra = extras.get(i);
            if (null == customerExtra) {
                continue;
            }
            if (!customerExtra.enabled) {
                continue;
            }

            final View extra = LayoutInflater.from(mContext).inflate(R.layout.item_customer_extra, null, false);
            extra.setEnabled(edit);

            TextView tv_tag = (TextView) extra.findViewById(R.id.tv_tag);
            final EditText tv_content = (EditText) extra.findViewById(R.id.et_content);

            tv_content.setEnabled(edit);
            if (valueSize != 0) {
                tv_tag.setTextSize(valueSize);
                tv_content.setTextSize(valueSize);
            }
            tv_content.setTextColor(valueColor);
            tv_tag.setText(customerExtra.label);

            /**
             * 编辑联系人，数据赋值
             * */
            if (null != mContact) {
                if (customerExtra.fieldName.equals("name")) {
                    tv_content.setText(mContact.getName());
                } else if (customerExtra.fieldName.equals("wiretel")) {
                    tv_content.setText(mContact.getWiretel());
                } else if (customerExtra.fieldName.equals("tel")) {
                    tv_content.setText(mContact.getTel());
                } else if (customerExtra.fieldName.equals("birth")) {
                    tv_content.setText(mContact.getBirthStr());
                } else if (customerExtra.fieldName.equals("wx")) {
                    tv_content.setText(mContact.getWx());
                } else if (customerExtra.fieldName.equals("qq")) {
                    tv_content.setText(mContact.getQq());
                } else if (customerExtra.fieldName.equals("email")) {
                    tv_content.setText(mContact.getEmail());
                } else if (customerExtra.fieldName.equals("memo")) {
                    tv_content.setText(mContact.getMemo());
                } else if (customerExtra.fieldName.equals("dept_name")) {
                    tv_content.setText(mContact.getDeptName());
                } else if (!customerExtra.isSystem) {
                    for (ExtraData extraData : mContact.getExtDatas()) {
                        if (customerExtra.label.equals(extraData.getProperties().getLabel())) {
                            tv_content.setText(extraData.getVal());
                            customerExtra.val = extraData.getVal();
                        }
                    }
                }
            }

            if (customerExtra.isList) {//改过
                tv_content.setEnabled(true);
            }

            addView(extra);
            if (customerExtra.isList) {
                LogUtil.dll("islist");
                LogUtil.dll("islist enable:" + customerExtra.enabled);
                AlertDialog dialog_follow = initDialog_Wheel_one(tv_content, customerExtra);
                extra.setOnTouchListener(Global.GetTouch());
                extra.setOnClickListener(new ValueOnClickListener_list(dialog_follow, i));
                tv_content.setFocusable(false);
                tv_content.setFocusableInTouchMode(false);
                tv_content.setOnFocusChangeListener(null);
                tv_content.setInputType(InputType.TYPE_CLASS_TEXT);
                tv_content.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        extra.performClick();
                    }
                });
                if (customerExtra.required) {
                    tv_content.setHint("必填");
                }

            } else if ("birth".equals(customerExtra.fieldName)) {
                LogUtil.dll("生日");
                LogUtil.dll("long enable:" + customerExtra.enabled);
                extra.setOnTouchListener(Global.GetTouch());
                extra.setOnClickListener(new ValueOnClickListener_dateTime(tv_content, customerExtra, true));
                tv_content.setFocusable(false);
                tv_content.setFocusableInTouchMode(false);
                tv_content.setOnFocusChangeListener(null);
                tv_content.setInputType(InputType.TYPE_CLASS_TEXT);
                tv_content.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        extra.performClick();
                    }
                });
                if (customerExtra.required) {
                    tv_content.setHint("必填");
                }
            } else if ("long".equals(customerExtra.type)) {
                LogUtil.dll("时间");
                LogUtil.dll("long enable:" + customerExtra.enabled);
                extra.setOnTouchListener(Global.GetTouch());
                extra.setOnClickListener(new ValueOnClickListener_dateTime(tv_content, customerExtra, false));
                tv_content.setFocusable(false);
                tv_content.setFocusableInTouchMode(false);
                tv_content.setOnFocusChangeListener(null);
                tv_content.setInputType(InputType.TYPE_CLASS_TEXT);
                tv_content.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        extra.performClick();
                    }
                });
                if (customerExtra.required) {
                    tv_content.setHint("必填");
                }
            } else if ("string".equals(customerExtra.type)) {
                LogUtil.dll("string");
                LogUtil.dll("string enable:" + customerExtra.enabled);
                extra.findViewById(R.id.img_right_arrow).setVisibility(View.INVISIBLE);
                tv_content.setFocusableInTouchMode(true);
                tv_content.setFocusable(true);
                tv_content.setOnClickListener(null);
                tv_content.addTextChangedListener(new BizFiedTextWatcher(customerExtra));
                tv_content.requestFocus();
                tv_content.setInputType(InputType.TYPE_CLASS_TEXT);
                if (customerExtra.required) {
                    tv_content.setHint("必填");
                }
            } else if ("int".equals(customerExtra.type)) {
                LogUtil.dll("int");
                LogUtil.dll("int enable:" + customerExtra.enabled);
                extra.findViewById(R.id.img_right_arrow).setVisibility(View.INVISIBLE);
                tv_content.setFocusableInTouchMode(true);
                tv_content.setFocusable(true);
                tv_content.setOnClickListener(null);
                tv_content.addTextChangedListener(new BizFiedTextWatcher(customerExtra));
                tv_content.requestFocus();
                tv_content.setInputType(InputType.TYPE_CLASS_NUMBER);
                if (customerExtra.required) {
                    tv_content.setHint("必填");
                }
            } else if ("double".equals(customerExtra.type)) {
                LogUtil.dll("double");
                LogUtil.dll("double enable:" + customerExtra.enabled);
                extra.findViewById(R.id.img_right_arrow).setVisibility(View.INVISIBLE);
                tv_content.setFocusableInTouchMode(true);
                tv_content.setFocusable(true);
                tv_content.setOnClickListener(null);
                tv_content.addTextChangedListener(new BizFiedTextWatcher(customerExtra));
                tv_content.requestFocus();
                tv_content.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                if (customerExtra.required) {
                    tv_content.setHint("必填");
                }
            }
        }
    }


    /**
     * 功 能: 生日选择器
     */
    public void pickDate(final ContactLeftExtras extra, final TextView textView) {
        final Calendar cal = Calendar.getInstance();
        final DatePickerDialog mDialog = new DatePickerDialog(mContext, null,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        //手动设置按钮
        mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatePicker datePicker = mDialog.getDatePicker();
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();

                age = Utils.getAge(year + "");
                if (age > 0) {
                    birthStr = year + "." + String.format("%02d", (month + 1)) + "." + String.format("%02d", day);
                    extra.val = birthStr;
                    textView.setText(birthStr);
                } else {
                    Toast.makeText(mContext, "出生日期不能是未来时间，请重新设置", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //取消按钮，如果不需要直接不设置即可
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDialog.dismiss();
            }
        });

        mDialog.show();
    }

    private class BizFiedTextWatcher implements TextWatcher {
        private ContactLeftExtras extra;

        private BizFiedTextWatcher(ContactLeftExtras extra) {
            this.extra = extra;
        }

        @Override
        public void afterTextChanged(Editable s) {
            extra.val = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
    }

    class ValueOnClickListener_list implements OnClickListener {
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

    /**
     * 动态字段，时间选择监听
     */
    class ValueOnClickListener_dateTime implements OnClickListener {
        private TextView textView;
        private ContactLeftExtras extra;
        private boolean isBrith;

        private ValueOnClickListener_dateTime(TextView textView, ContactLeftExtras extra, boolean isBrith) {
            this.textView = textView;
            this.extra = extra;
            this.isBrith = isBrith;
        }

        @Override
        public void onClick(View v) {
            if (!ClickTool.isDoubleClick()) {
                final DateTool.DateSetListener_Datetool dateListener = new DateTool.DateSetListener_Datetool(textView);
                dateListener.setOnClick_callback(new DateTool.DateSetListener_Datetool.OnClick_Callback() {
                    @Override
                    public boolean onClick_onDateSet() {
                        return false;
                    }

                    @Override
                    public boolean onClick_onTimeSet() {
                        return false;
                    }
                });

                if (isBrith) {
                    pickDate(extra, textView);
                } else {
                    DateTimePickDialog dateTimePickDialog = new DateTimePickDialog(mContext, null);
                    dateTimePickDialog.dateTimePicKDialog(new DateTimePickDialog.OnDateTimeChangedListener() {
                        @Override
                        public void onDateTimeChanged(int year, int month, int day, int hour, int min) {
                            String str = year + "." + String.format("%02d", (month + 1)) + "." + String.format("%02d", day) + String.format(" %02d", hour) + String.format(":%02d", min);
                            textView.setText(str);
                            String times = DateTool.getDataOne(str, DateTool.DATE_FORMATE_SPLITE_BY_POINT);
                            extra.val = str;
                        }

                        @Override
                        public void onCancel() {

                        }
                    }, true, "取消");
                }
            }
        }
    }


    AlertDialog initDialog_Wheel_one(final TextView textView, final ContactLeftExtras extra) {
        final ArrayList<String> str = extra.defVal;
        BaseAdapter followAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return str.size();
            }

            @Override
            public Object getItem(int position) {
                return str.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_listview_product_select, parent, false);
                }

                TextView tv = (TextView) convertView.findViewById(R.id.tv);
                tv.setText(str.get(position));
                extra.val = str.get(position);

                return convertView;
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View layout = LayoutInflater.from(mContext).inflate(R.layout.dialog_products_select, null);
        ListView listView_follow = (ListView) layout.findViewById(R.id.listView);
        listView_follow.setAdapter(followAdapter);
        listView_follow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                textView.setText(str.get(position));
                extra.val = str.get(position);
                dialog.dismiss();
            }
        });
        builder.setView(layout);
        dialog = builder.create();
        return dialog;

    }
}
