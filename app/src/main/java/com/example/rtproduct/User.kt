package com.example.rtproduct

class User {
    var truckComp : String = ""
    var truckType : String = ""
    var TruckNo : String = ""
    var MqttHost : String = ""
    var DialogPassword : String = ""

    constructor(truckComp: String, truckType: String, TruckNo: String, MqttHost: String, DialogPassword: String){
        this.truckComp = truckComp
        this.truckType = truckType
        this.TruckNo = TruckNo
        this.MqttHost = MqttHost
        this.DialogPassword = DialogPassword
    }
}