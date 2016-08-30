package com.loyo.oa.v2.activityui.worksheet.common;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.loyo.oa.v2.R;

import java.lang.reflect.Type;

/**
 * Created by EthanGong on 16/8/27.
 *
 * 工单事件状态
 * code : 状态对应数字
 * name : 状态显示文本
 * color: 状态显示颜色
 * icon : 状态icon
 *
 */
public enum WorksheetEventStatus implements GroupKey {

    /** 其他
     *
     * 不应出现的数据
     * 防备服务端返回非 1 - 4 数据
     *
     */
    OTHERS(Integer.MAX_VALUE){
        public String getName() { return "其他"; }
        public int getColor() { return R.color.ws_status_4; }
        public int getIcon() { return R.drawable.icon_ws_status4; }
    },

    /** 待处理 */
    WAITPROCESS(1){
        public String getName() { return "待处理"; }
        public int getColor() { return R.color.ws_status_1; }
        public int getIcon() { return R.drawable.icon_ws_status1; }
    },

    /** 未触发 */
    UNACTIVATED(2){
        public String getName() { return "未触发"; }
        public int getColor() { return R.color.ws_status_2; }
        public int getIcon() { return R.drawable.icon_ws_status2; }
    },

    /** 已处理 */
    FINISHED(3){
        public String getName() { return "已处理"; }
        public int getColor() { return R.color.ws_status_3; }
        public int getIcon() { return R.drawable.icon_ws_status3; }
    },

    /** 意外终止 */
    TEMINATED(4){
        public String getName() { return "意外终止"; }
        public int getColor() { return R.color.ws_status_4; }
        public int getIcon() { return R.drawable.icon_ws_status4; }
    };

    private final int code;
    private WorksheetEventStatus(int code) {
        this.code = code;
    }


    /** 获取显示内容 */
    public abstract String getName();

    /** 获取显示颜色*/
    public abstract int getColor();

    /** 获取显示图标*/
    public abstract int getIcon();

    /** 获取排序权值 */
    public int compareWeight()
    {
        return this.code;
    }


    /** gson 序列化和反序列化 */
    public static class EnumSerializer implements JsonSerializer<WorksheetEventStatus>,
            JsonDeserializer<WorksheetEventStatus> {

        // 对象转为Json时调用,实现JsonSerializer<WorksheetEventStatus>接口
        @Override
        public JsonElement serialize(WorksheetEventStatus state, Type arg1,
                                     JsonSerializationContext arg2) {
            return new JsonPrimitive(state.code);
        }

        // json转为对象时调用,实现JsonDeserializer<WorksheetEventStatus>接口
        @Override
        public WorksheetEventStatus deserialize(JsonElement json, Type typeOfT,
                                                JsonDeserializationContext context) throws JsonParseException {
            WorksheetEventStatus[] list  = WorksheetEventStatus.values();
            for (int i = 0; i < list.length; i++) {
                if (list[i].code == json.getAsInt()) {
                    return list[i];
                }
            }
            return WorksheetEventStatus.OTHERS;
        }

    }
}
