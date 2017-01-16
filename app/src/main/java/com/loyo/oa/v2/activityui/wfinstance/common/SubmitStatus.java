package com.loyo.oa.v2.activityui.wfinstance.common;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetStatus;
import com.loyo.oa.v2.common.GroupKey;

import java.lang.reflect.Type;

/**
 * Created by EthanGong on 2016/11/1.
 */

public enum SubmitStatus implements GroupKey {
    ALL(0, "0", "全部状态"){
        public int getColor() {
            return R.color.title_bg1;
        }
        public int getIcon() {
            return R.drawable.bg_view_blue_circle;
        }
    },
    WAIT_APPROVE(1, "1", "待审批"){
        public int getColor() {
            return R.color.isteston;
        }
        public int getIcon() {
            return R.drawable.bg_view_purple_circle;
        }
    },
    APPROVING(2, "2", "审批中"){
        public int getColor() {
            return R.color.title_bg1;
        }
        public int getIcon() {
            return R.drawable.bg_view_blue_circle;
        }
    },
    UNAPPROVED(3, "3", "未通过"){
        public int getColor() {
            return R.color.wfinstance_notagree;
        }
        public int getIcon() {
            return R.drawable.bg_view_red_circle;
        }
    },
    APPROVED(4, "4", "已通过"){
        public int getColor() {
            return R.color.isfinish;
        }
        public int getIcon() {
            return R.drawable.bg_view_green_circle;
        }
    },
    FINISHED(5, "5", "已办结"){
        public int getColor() {
            return R.color.isfinish;
        }
        public int getIcon() {
            return R.drawable.bg_view_green_circle;
        }
    };

    public int code;
    public String key;
    public String value;

    SubmitStatus(int code, String key, String value) {
        this.code = code;
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    /**
     * 获取显示内容
     */
    public String getName() {
        return value;
    }

    /**
     * 获取显示颜色
     */
    public abstract int getColor();

    /**
     * 获取显示图标
     */
    public abstract int getIcon();

    /**
     * 获取排序权值
     */
    public int compareWeight() {
        return this.code;
    }

    /**
     * gson 序列化和反序列化
     */
    public static class EnumSerializer implements JsonSerializer<WorksheetStatus>,
            JsonDeserializer<SubmitStatus> {

        // 对象转为Json时调用,实现JsonSerializer<WorksheetStatus>接口
        @Override
        public JsonElement serialize(WorksheetStatus state, Type arg1,
                                     JsonSerializationContext arg2) {
            return new JsonPrimitive(state.code);
        }

        // json转为对象时调用,实现JsonDeserializer<WorksheetStatus>接口
        @Override
        public SubmitStatus deserialize(JsonElement json, Type typeOfT,
                                           JsonDeserializationContext context) throws JsonParseException {
            SubmitStatus[] list = SubmitStatus.values();
            for (int i = 0; i < list.length; i++) {
                if (list[i].code == json.getAsInt()) {
                    return list[i];
                }
            }
            return SubmitStatus.ALL;
        }
    }

    public static SubmitStatus getStatus(int code) {
        SubmitStatus[] list = SubmitStatus.values();
        for (int i = 0; i < list.length; i++) {
            if (list[i].code == code) {
                return list[i];
            }
        }
        return SubmitStatus.ALL;
    }
}
