package com.example.rtproduct

import android.app.Application

class GlobalClass() : Application() {
    companion object {

        var version: String? = "22"
        var MqttHost: String? = "tcp://10.2.203.198:1883"
        var truckComp: String? = "10"
        var truckType: String? = "1"
        var TruckNo: String? = "777"
        var plantCode: String? = "50"
        var scaleCode: String? = "01"
        var DialogPassword: String? = null
        var topicMessage: String? = null
        var CallOutMessage3: String? = null
        var CallOutMessage4: String? = null
        var CallOutMessage5: String? = null
        var Retry:String? = null
        var Weight:String? = "39.80"
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
