package com.example.rtproduct

import android.app.Application
import com.example.rtproduct.manager.MQTTmanager

class GlobalClass() : Application() {
    companion object {
        var mqttManager: MQTTmanager? = null
        var version: String? = "22"
        var MqttHost: String? = null
        var truckComp: String? = null
        var truckType: String? = null
        var TruckNo: String? = null
        var plantCode: String? = null
        var scaleCode: String? = null
        var DialogPassword: String? = null
        var topicMessage: String? = null
        var CallOutMessage3: String? = null
        var CallOutMessage4: String? = null
        var CallOutMessage5: String? = null
        var Retry:String? = null
        var Weight:String? = "5.00"
        var sendMessage: String? = null
        var i : Int? = 0

        var arriveTime = arrayListOf<String>("","","","","","","","")
        var timestamp = arrayListOf <String> ("","","","","","","","")
        var source = arrayListOf<String>("","","","","","","","")
        var material = arrayListOf <String> ("","","","","","","","")
        var weight = arrayListOf<String> ("","","","","","","","")
        var destination = arrayListOf<String> ("","","","","","","","")

    }

    override fun onCreate() {
        super.onCreate()
        val context = this
        val db = DataBaseHandler(context)
        val data = db.readData()
        for (i in 0 until data.size) {
            truckComp = data[i].truckComp
            truckType = data[i].truckType
            TruckNo = data[i].TruckNo
            MqttHost = data[i].MqttHost
            DialogPassword = data[i].DialogPassword
        }
    }
}
