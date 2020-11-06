package com.example.rtproduct

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

//class moshiAdapter(function: () -> FromJson) {
//    @FromJson
//    fun fromJson(reader: JsonReader): Asset? {
//        var version: String? = null
//        var TruckNo: String? = null
//        var MqttHost: String? = null
//        var plantCode: String? = null
//        var scaleCode: String? = null
//        var DialogPassword: String? = null
//        reader.beginObject()
//        while (reader.hasNext()) {
//            when (reader.nextName()) {
//                "version" -> version = reader.nextString()
//                "TruckNo" -> TruckNo = reader.nextString()
//                "MqttHost0" -> MqttHost = reader.nextString()
//                "plantCode" -> plantCode = reader.nextString()
//                "scaleCode" -> scaleCode = reader.nextString()
//                "DialogPassword" -> DialogPassword = reader.nextString()
//                else -> reader.skipValue()
//            }
//        }
//        reader.endObject()
//        return Asset(version!!, TruckNo!!, MqttHost!!, plantCode!!, scaleCode!!, DialogPassword!!)
//    }
//    @ToJson
//        fun toJson(writer: JsonWriter, value:Asset?) {
//        TODO("Implement")
//    }
//
//}