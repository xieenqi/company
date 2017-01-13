package com.loyo.oa.common.type

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer

import java.lang.reflect.Type

/**
 * Created by xeq on 17/1/12.
 */

enum class RecycleType private constructor(private val type: Int) {
    NONE(0) {
        override val text: String
            get() = "--"
    },
    MANUAL(1) {
        override val text: String
            get() = "手动丢公海"
    },
    AUTOMATIC(2) {
        override val text: String
            get() = "自动丢公海"
    };

    fun getmValue(): Int {
        return type
    }

    abstract val text: String

    class RecycleTypeSerializer : JsonSerializer<RecycleType>, JsonDeserializer<RecycleType> {

        override fun serialize(src: RecycleType, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.type)
        }

        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): RecycleType {
            val list = RecycleType.values()
            for (i in list.indices) {
                if (list[i].type == json.asInt) {
                    return list[i]
                }
            }
            return RecycleType.NONE
        }
    }


}