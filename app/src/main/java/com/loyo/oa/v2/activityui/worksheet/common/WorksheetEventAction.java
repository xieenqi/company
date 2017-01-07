package com.loyo.oa.v2.activityui.worksheet.common;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.loyo.oa.v2.R;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * Created by EthanGong on 16/9/1.
 */
public enum WorksheetEventAction implements Serializable {

    None{
        public int getIcon() {return R.style.worksheet_btn_trans;}
        public boolean visible() { return false;}
        public String getBtnTitle(){return "无";}
    },

    Transfer {
        public int getIcon() { return R.style.worksheet_btn_trans; }
        public boolean visible() { return true;}
        public String getBtnTitle(){ return "转移"; }
    },
    Dispatch {
        public int getIcon() {return R.style.worksheet_btn_assignment;}
        public boolean visible() { return true; }
        public String getBtnTitle(){ return "分派";}
    },
    Redo {
        public int getIcon() { return R.style.worksheet_btn_redo;}
        public boolean visible() { return true;}
        public String getBtnTitle(){ return "重做";}
    },
    Finish {
        public int getIcon() { return R.style.worksheet_btn_complete; }
        public boolean visible() {return true; }
        public String getBtnTitle(){return "完成";}
    };

    private WorksheetEventAction() {

    }

    /**
     * 获取显示图标
     */
    public abstract int getIcon();
    public abstract String getBtnTitle();
    public abstract boolean visible();


    /**
     * gson 序列化和反序列化
     */
    public static class EnumSerializer implements JsonSerializer<WorksheetEventAction>,
            JsonDeserializer<WorksheetEventAction> {

        // 对象转为Json时调用,实现JsonSerializer<WorksheetEventAction>接口
        @Override
        public JsonElement serialize(WorksheetEventAction state, Type arg1,
                                     JsonSerializationContext arg2) {
            return new JsonPrimitive(state.ordinal());
        }

        // json转为对象时调用,实现JsonDeserializer<WorksheetEventAction>接口
        @Override
        public WorksheetEventAction deserialize(JsonElement json, Type typeOfT,
                                                JsonDeserializationContext context) throws JsonParseException {
            WorksheetEventAction[] list = WorksheetEventAction.values();
            for (int i = 0; i < list.length; i++) {
                if (list[i].ordinal() == json.getAsInt()) {
                    return list[i];
                }
            }
            return WorksheetEventAction.None;
        }
    }

}
