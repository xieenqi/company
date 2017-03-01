package com.loyo.oa.v2.order.common;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by EthanGong on 2017/3/1.
 */

public enum PaymentMethod {


    CASH(1, "现金"),         // 现金
    CHECK(2, "支票"),        // 支票
    TRANSFER(3, "银行转账"),  // 银行转账
    OTHERS(4, "其它");       // 其它

    private final int code;
    private final String name;
    PaymentMethod(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return this.code;
    }
    public String getName() {
        return this.name;
    }

    /**
     * gson 序列化和反序列化
     */
    public static class EnumSerializer implements JsonSerializer<PaymentMethod>,
            JsonDeserializer<PaymentMethod> {

        // 对象转为Json时调用,实现JsonSerializer<WorksheetStatus>接口
        @Override
        public JsonElement serialize(PaymentMethod method, Type arg1,
                                     JsonSerializationContext arg2) {
            return new JsonPrimitive(method.code);
        }

        // json转为对象时调用,实现JsonDeserializer<WorksheetStatus>接口
        @Override
        public PaymentMethod deserialize(JsonElement json, Type typeOfT,
                                           JsonDeserializationContext context) throws JsonParseException {
            PaymentMethod[] list = PaymentMethod.values();
            for (int i = 0; i < list.length; i++) {
                if (list[i].code == json.getAsInt()) {
                    return list[i];
                }
            }
            return PaymentMethod.OTHERS;
        }
    }

    public static PaymentMethod getPaymentMethod(int code) {
        PaymentMethod[] list = PaymentMethod.values();
        for (int i = 0; i < list.length; i++) {
            if (list[i].code == code) {
                return list[i];
            }
        }
        return PaymentMethod.OTHERS;
    }

    public static ArrayList<String> getNames() {
        ArrayList<String> data = new ArrayList<>();
        PaymentMethod[] list = PaymentMethod.values();
        for (PaymentMethod method : list) {
            data.add(method.getName());
        }
        return data;
    }

    public static PaymentMethod getMethodAt(int index) {
        PaymentMethod[] list = PaymentMethod.values();
        if (index >= list.length || index < 0 ) {
            return OTHERS;
        }
        return list[index];
    }
}
