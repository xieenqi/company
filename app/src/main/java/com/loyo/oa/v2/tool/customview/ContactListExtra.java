package com.loyo.oa.v2.tool.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.ContactLeftExtras;
import com.loyo.oa.v2.beans.ExtraData;
import com.loyo.oa.v2.tool.DateTool;
import java.util.ArrayList;

/**
 * 联系人列表 动态字段设置View
 * Created by loyo_dev1 on 16/4/18.
 */
public class ContactListExtra extends LinearLayout {

    private Context mContext;
    private ArrayList<ExtraData> extras = new ArrayList<>();
    private ArrayList<ContactLeftExtras> leftExtrases;

    public ContactListExtra(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        setOrientation(VERTICAL);
    }

    public ContactListExtra(Context context, ArrayList<ExtraData> extras,ArrayList<ContactLeftExtras> leftExtrases, boolean edit, int valueColor, int valueSize) {
        this(context, null, 0);
        this.extras = extras;
        this.leftExtrases = leftExtrases;
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

        for(ContactLeftExtras leftExtras : leftExtrases){
            if(!leftExtras.enabled){
                continue;
            }
            //判断是否为动态字段
            if(!leftExtras.isSystem){
                for(ExtraData extraData : extras){
                    if(leftExtras.label.equals(extraData.getProperties().getLabel())){
                        leftExtras.val = extraData.getVal();
                    }
                }
            }
        }

        for(int i = 0;i<leftExtrases.size();i++){
            if(leftExtrases.get(i).fieldName.length() > 20){
                ContactLeftExtras contactLeftExtras = leftExtrases.get(i);

                View extra = LayoutInflater.from(mContext).inflate(R.layout.item_customer_extra, null, false);
                extra.setEnabled(edit);

                TextView tv_tag = (TextView) extra.findViewById(R.id.tv_tag);
                final EditText tv_content = (EditText) extra.findViewById(R.id.et_content);

                tv_content.setEnabled(edit);
                if (valueSize != 0) {
                    tv_tag.setTextSize(valueSize);
                    tv_content.setTextSize(valueSize);
                }
                tv_content.setTextColor(valueColor);
                if(leftExtrases.get(i).enabled){
                    tv_tag.setText(contactLeftExtras.label);
                }else{
                    continue;
                }

                /**
                 * 说   明: 创建时发送时间戳，获取也是时间戳，但是之前服务器数据存在2015-2-3这种时间格式数据，所以这里判断一下。
                 * 解析格式: yyyy-MM-dd HH:mm
                 * */
                if("long".equals(contactLeftExtras.label)){
                    try{
                        tv_content.setText(DateTool.timet(contactLeftExtras.val, DateTool.DATE_FORMATE_SPLITE_BY_POINT));
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                        tv_content.setText(contactLeftExtras.val);
                    }
                } else {
                    tv_content.setText(contactLeftExtras.val);
                }
                addView(extra);
            }
        }
    }
}
