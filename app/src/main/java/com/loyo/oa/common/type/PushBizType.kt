package com.loyo.oa.common.type

import com.google.gson.*
import java.lang.reflect.Type

/** 推送的biztype
 * Created by xeq on 17/1/6.
 */
enum class PushBizType constructor(public val type: Int) {
    PUSH_TASKS(1);

    class PushBizTypeSerializer : JsonSerializer<PushBizType>, JsonDeserializer<PushBizType> {

        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): PushBizType {
            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            val list = PushBizType.values();
            for (x in list.indices) {
                if (list[x].type == json!!.asInt) {
                    return list[x]
                }
            }


        }

        override fun serialize(src: PushBizType?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            return JsonPrimitive(src!!.type)
        }
    }
}