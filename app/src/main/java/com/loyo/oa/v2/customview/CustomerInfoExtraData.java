package com.loyo.oa.v2.customview;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.common.utils.DensityUtil;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.ExtraData;
import com.loyo.oa.v2.activityui.customer.model.ExtraProperties;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.ClickTool;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.customview
 * 描述 : 客户联系人详情 动态字段
 * 作者 : ykb
 * 时间 : 15/10/7.
 */
public class CustomerInfoExtraData extends LinearLayout {

    private Context mContext;
    private ArrayList<ExtraData> extras = new ArrayList<>();

    public CustomerInfoExtraData(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        setOrientation(VERTICAL);
    }

    public CustomerInfoExtraData(Context context, ArrayList<ExtraData> extras,
                                 boolean edit, int valueColor, int valueSize) {
        this(context, null, 0);
        this.extras = extras;
        bindView(edit, valueColor, valueSize);
    }


    public ArrayList<ExtraData> getExtras() {
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
            ExtraData customerExtra = extras.get(i);
            final ExtraProperties properties = customerExtra.getProperties();
            if (null == properties) {
                continue;
            }
            if (!properties.isEnabled()) {
                continue;
            }

            final View extra = LayoutInflater.from(mContext).inflate(R.layout.item_customer_addextra, null, false);

            extra.setEnabled(edit);

            TextView tv_tag = (TextView) extra.findViewById(R.id.tv_tag);
            final EditText tv_content = (EditText) extra.findViewById(R.id.et_content);
            ImageView ivArrow = (ImageView) extra.findViewById(R.id.img_right_arrow);

            tv_content.setEnabled(edit);
            if (valueSize != 0) {
                tv_tag.setTextSize(valueSize);
                tv_content.setTextSize(valueSize);
            }
            //tv_content.setTextColor(valueColor);
            tv_tag.setText(properties.getLabel());
            if(!TextUtils.isEmpty(customerExtra.getVal())){
                tv_content.setText(customerExtra.getVal());
            }
            /**
             * 说   明: 创建时发送时间戳，获取也是时间戳，但是之前服务器数据存在2015-2-3这种时间格式数据，所以这里判断一下。
             * 解析格式: yyyy-MM-dd HH:mm
             * */
            if ("long".equals(properties.getType())) {
                if (properties.isEnabled()) {

                }
                try {
                    if (!TextUtils.isEmpty(customerExtra.getVal()))
                        tv_content.setText(DateTool.getDateTimeFriendly(Long.parseLong(customerExtra.getVal())));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    tv_content.setText(customerExtra.getVal());
                }
            } else {
                tv_content.setText(customerExtra.getVal());
            }

            addView(extra);
            if (properties.isList()) {
                LogUtil.dll("islist");
                LogUtil.dll("islist enable:" + properties.isEnabled());
                AlertDialog dialog_follow = initDialog_Wheel_one(tv_content, customerExtra);
                extra.setOnTouchListener(Global.GetTouch());
                extra.setOnClickListener(new ValueOnClickListener_list(dialog_follow, i));
//                tv_content.setFocusable(false);
//                tv_content.setFocusableInTouchMode(false);
//                tv_content.setOnFocusChangeListener(null);
                tv_content.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        extra.performClick();
                    }
                });
                tv_content.setFocusable(false);
                tv_content.setInputType(InputType.TYPE_CLASS_TEXT);
                if (properties.isRequired()) {
                    tv_content.setHint("必填");
                }
            } else if ("long".equals(properties.getType())) {
                LogUtil.dll("时间");
                LogUtil.dll("long enable:" + properties.isEnabled());
                extra.setOnTouchListener(Global.GetTouch());
                extra.setOnClickListener(new ValueOnClickListener_dateTime(tv_content, customerExtra));
//                tv_content.setFocusable(false);
//                tv_content.setFocusableInTouchMode(false);
//                tv_content.setOnFocusChangeListener(null);
                tv_content.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        extra.performClick();
                    }
                });
                tv_content.setInputType(InputType.TYPE_CLASS_TEXT);
                if (properties.isRequired()) {
                    tv_content.setHint("必填");
                }
            } else if ("string".equals(properties.getType())) {
                LogUtil.dll("string");
                LogUtil.dll("string enable:" + properties.isEnabled());
                ivArrow.setVisibility(View.INVISIBLE);
//                tv_content.setFocusableInTouchMode(true);
//                tv_content.setFocusable(true);
//                tv_content.setOnClickListener(null);
                tv_content.addTextChangedListener(new BizFiedTextWatcher(customerExtra));
//                tv_content.requestFocus();
                tv_content.setInputType(InputType.TYPE_CLASS_TEXT);
                if (properties.isRequired()) {
                    tv_content.setHint("必填");
                }
            } else if ("int".equals(properties.getType())) {
                LogUtil.dll("int");
                LogUtil.dll("int enable:" + properties.isEnabled());
                ivArrow.setVisibility(View.INVISIBLE);
//                tv_content.setFocusableInTouchMode(true);
//                tv_content.setFocusable(true);
//                tv_content.setOnClickListener(null);
                tv_content.addTextChangedListener(new BizFiedTextWatcher(customerExtra));
//                tv_content.requestFocus();
                tv_content.setInputType(InputType.TYPE_CLASS_NUMBER);
                if (properties.isRequired()) {
                    tv_content.setHint("必填");
                }
            } else if ("double".equals(properties.getType())) {
                LogUtil.dll("double");
                LogUtil.dll("double enable:" + properties.isEnabled());
                ivArrow.setVisibility(View.INVISIBLE);
//                tv_content.setFocusableInTouchMode(true);
//                tv_content.setFocusable(true);
//                tv_content.setOnClickListener(null);
                tv_content.addTextChangedListener(new BizFiedTextWatcher(customerExtra));
//                tv_content.requestFocus();
                tv_content.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                if (properties.isRequired()) {
                    tv_content.setHint("必填");
                }
            }
            //必须放在最后，覆盖前面的
            if(edit){
                tv_content.setTextColor(getResources().getColor(R.color.text66));
            }else{
                tv_content.setTextColor(getResources().getColor(R.color.text99));
                tv_content.setPadding(tv_content.getPaddingLeft(),tv_content.getPaddingTop(), DensityUtil.dp2px(mContext,14),tv_content.getPaddingBottom());
                ivArrow.setVisibility(View.GONE);
            }
        }
    }

    private class BizFiedTextWatcher implements TextWatcher {
        private ExtraData extra;

        private BizFiedTextWatcher(ExtraData extra) {
            this.extra = extra;
        }

        @Override
        public void afterTextChanged(Editable s) {
            extra.setVal(s.toString());
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

    /*动态字段，时间选择监听*/
    class ValueOnClickListener_dateTime implements OnClickListener {
        private TextView textView;
        private ExtraData extra;

        private ValueOnClickListener_dateTime(TextView textView, ExtraData extra) {
            this.textView = textView;
            this.extra = extra;
        }

        @Override
        public void onClick(View v) {
            if (!ClickTool.isDoubleClick()) {
//                final DateTool.DateSetListener_Datetool dateListener = new DateTool.DateSetListener_Datetool(textView);
//                dateListener.setOnClick_callback(new DateTool.DateSetListener_Datetool.OnClick_Callback() {
//                    @Override
//                    public boolean onClick_onDateSet() {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onClick_onTimeSet() {
//                        return false;
//                    }
//                });

                DateTimePickDialog dateTimePickDialog = new DateTimePickDialog(mContext, null);
                dateTimePickDialog.dateTimePicKDialog(new DateTimePickDialog.OnDateTimeChangedListener() {
                    @Override
                    public void onDateTimeChanged(int year, int month, int day, int hour, int min) {

//                        String str = year + "." + String.format("%02d", (month + 1)) + "." + String.format("%02d", day) + String.format(" %02d", hour) + String.format(":%02d", min);
//                        textView.setText(str);
//                        extra.setVal(str);
                        long time = com.loyo.oa.common.utils.DateTool.getStamp(year, month, day, hour, min, 0);
                        String str = DateTool.getDateTimeFriendly(time);
                        extra.setVal(str);
                        textView.setText(str);
                    }

                    @Override
                    public void onCancel() {

                    }
                }, true, "取消");
            }
        }
    }

    AlertDialog initDialog_Wheel_one(final TextView textView, final ExtraData extra) {

        final ArrayList<String> str = extra.getProperties().getDefVal();
        BaseAdapter followAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return null == str ? 0 : str.size();
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

                return convertView;
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View layout = LayoutInflater.from(mContext).inflate(R.layout.dialog_products_select, null);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        ListView listView_follow = (ListView) layout.findViewById(R.id.listView);
        listView_follow.setAdapter(followAdapter);
        listView_follow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                textView.setText(str.get(position));
                extra.setVal(str.get(position));
                dialog.dismiss();
            }
        });

        return dialog;

    }
}
