package com.example.rtproduct

import com.squareup.moshi.JsonClass
import java.io.Serializable
@JsonClass(generateAdapter = true)
data class Asset(var version: String,
                 var TruckNo: String,
                 var MqttHost: String,
                 var plantCode: String,
                 var scaleCode: String,
                 var DialogPassword: String):Serializable {
}
