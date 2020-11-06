package com.example.rtproduct

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rtproduct.manager.MQTTConnectionParams
import com.example.rtproduct.manager.MQTTmanager
import com.example.rtproduct.protocols.UIUpdaterInterface
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity(), UIUpdaterInterface {

    override fun resetUIWithConnection(status: Boolean) {
        if (status) {
            updateStatusViewWith("Connected")
        } else {
            updateStatusViewWith("Disconnected")
        }
    }

    override fun updateStatusViewWith(status: String) {
        statusLab1.text = status
    }

    override fun update(message: String) {
    }

    override fun topicUpdate(topic: String) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        resetUIWithConnection(false)

        val context = this

        var topics = arrayOf<String>(
            "/agg_scale/${GlobalClass.plantCode}/${GlobalClass.scaleCode}/metrics",
            "/agg_scale/${GlobalClass.plantCode}/${GlobalClass.scaleCode}/veh/agg/${GlobalClass.truckComp}/${GlobalClass.truckType}/${GlobalClass.TruckNo}/scale_out",
//            "/agg_scale/+/+/veh/agg/${GlobalClass.truckComp}/${GlobalClass.truckType}/${GlobalClass.TruckNo}/status",
            "/vehicle/agg/${GlobalClass.TruckNo}/call_out"
        )

        var connectionParams = MQTTConnectionParams("RTP1", GlobalClass.MqttHost.toString(), topics, "", "")
        GlobalClass.mqttManager = MQTTmanager(connectionParams, applicationContext, this)
        GlobalClass.mqttManager?.connect()

        //Submitting to Global Class and the database
        SunmitBtn.setOnClickListener() {
            if (truckCompET.text != null) {
                GlobalClass.truckComp = truckCompET.text.toString()
            }
            if (truckTypeET.text != null) {
                GlobalClass.truckType = truckTypeET.text.toString()
            }
            if (trucknoET.text != null) {
                GlobalClass.TruckNo = trucknoET.text.toString()
            }
            if (mqttseverET.text != null) {
                GlobalClass.MqttHost = mqttseverET.text.toString()
            }
            if (passwordET.text != null) {
                GlobalClass.DialogPassword = passwordET.text.toString()
            }
            if (GlobalClass.truckComp.toString().isNotEmpty() &&
                GlobalClass.truckType.toString().isNotEmpty() &&
                GlobalClass.TruckNo.toString().isNotEmpty() &&
                GlobalClass.MqttHost.toString().isNotEmpty() &&
                GlobalClass.DialogPassword.toString().isNotEmpty()) {
                val user = User("${GlobalClass.truckComp}", "${GlobalClass.truckType}", "${GlobalClass.TruckNo}", "${GlobalClass.MqttHost}", "${GlobalClass.DialogPassword}")
                val db = DataBaseHandler(context)
                db.insertData(user)
            }else {
                Toast.makeText(context, "Please Fill All Data's", Toast.LENGTH_SHORT).show()
            }
            StatusDone()
        }

        HomeView6.setOnClickListener() {
            StatusDone()
        }
    }

    fun StatusDone() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}