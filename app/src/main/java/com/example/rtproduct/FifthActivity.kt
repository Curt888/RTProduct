package com.example.rtproduct

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextClock
import androidx.appcompat.app.AppCompatActivity
import com.example.rtproduct.manager.MQTTConnectionParams
import com.example.rtproduct.manager.MQTTmanager
import com.example.rtproduct.protocols.UIUpdaterInterface
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.activity_fifth.*
import java.text.SimpleDateFormat
import java.util.*

class FifthActivity : AppCompatActivity(), UIUpdaterInterface {

    var mqttManager: MQTTmanager? = null
    val tct = SimpleDateFormat("MM-dd-yy HH:mm", Locale.getDefault())
    var currentDate: String = tct.format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fifth)

        val txtClock = findViewById<TextClock>(R.id.txtClock)
        txtClock.format12Hour = "MM-dd-yy kk:mm"

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        //MQTT Messaging
        resetUIWithConnection(false)

        var topics = arrayOf<String>(
            "/agg_scale/${GlobalClass.plantCode}/${GlobalClass.scaleCode}/metrics",
            "/agg_scale/${GlobalClass.plantCode}/${GlobalClass.scaleCode}/veh/agg/${GlobalClass.truckComp}/${GlobalClass.truckType}/${GlobalClass.TruckNo}/scale_out",
//            "/agg_scale/+/+/veh/agg/${GlobalClass.truckComp}/${GlobalClass.truckType}/${GlobalClass.TruckNo}/status",
            "/vehicle/agg/${GlobalClass.TruckNo}/call_out"
        )
        var host = "tcp://10.2.203.198:1883"
        var connectionParams = MQTTConnectionParams(
            "RTP1", host, topics, "", ""
        )
        mqttManager = MQTTmanager(connectionParams, applicationContext, this)
        mqttManager?.connect()

        homeView5.setOnClickListener {
            StatusDone()
        }

        //Time Card input
        arvtimeET1.setText(GlobalClass.arriveTime[0])
        arvtimeET2.setText(GlobalClass.arriveTime[1])
        arvtimeET3.setText(GlobalClass.arriveTime[2])
        arvtimeET4.setText(GlobalClass.arriveTime[3])
        arvtimeET5.setText(GlobalClass.arriveTime[4])
        arvtimeET6.setText(GlobalClass.arriveTime[5])
        arvtimeET7.setText(GlobalClass.arriveTime[6])
        arvtimeET8.setText(GlobalClass.arriveTime[7])

        timeStampTV1.text = GlobalClass.timestamp[0]
        timeStampTV2.text = GlobalClass.timestamp[1]
        timeStampTV3.text = GlobalClass.timestamp[2]
        timeStampTV4.text = GlobalClass.timestamp[3]
        timeStampTV5.text = GlobalClass.timestamp[4]
        timeStampTV6.text = GlobalClass.timestamp[5]
        timeStampTV7.text = GlobalClass.timestamp[6]
        timeStampTV8.text = GlobalClass.timestamp[7]

        sourceTV1.text = GlobalClass.source[0]
        sourceTV2.text = GlobalClass.source[1]
        sourceTV3.text = GlobalClass.source[2]
        sourceTV4.text = GlobalClass.source[3]
        sourceTV5.text = GlobalClass.source[4]
        sourceTV6.text = GlobalClass.source[5]
        sourceTV7.text = GlobalClass.source[6]
        sourceTV8.text = GlobalClass.source[7]

        materialTV1.text = GlobalClass.material[0]
        materialTV2.text = GlobalClass.material[1]
        materialTV3.text = GlobalClass.material[2]
        materialTV4.text = GlobalClass.material[3]
        materialTV5.text = GlobalClass.material[4]
        materialTV6.text = GlobalClass.material[5]
        materialTV7.text = GlobalClass.material[6]
        materialTV8.text = GlobalClass.material[7]

        weightTV1.text = GlobalClass.weight[0]
        weightTV2.text = GlobalClass.weight[1]
        weightTV3.text = GlobalClass.weight[2]
        weightTV4.text = GlobalClass.weight[3]
        weightTV5.text = GlobalClass.weight[4]
        weightTV6.text = GlobalClass.weight[5]
        weightTV7.text = GlobalClass.weight[6]
        weightTV8.text = GlobalClass.weight[7]
    }

    override fun resetUIWithConnection(status: Boolean) {
        if (status) {
            updateStatusViewWith("Connected")
        } else {
            updateStatusViewWith("Disconnected")
        }
    }

    override fun updateStatusViewWith(status: String) {
        statusLabl.text = status
    }

    override fun update(message: String) {
        var text = textViewInstructions5.text.toString()
        var newText = """
            $text
            $message
            """
        parseInData(newText)
    }

    override fun topicUpdate(topic: String) {
    }

    data class mqttMsg(val msg: String = "")

    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val adapter: JsonAdapter<mqttMsg> = moshi.adapter(mqttMsg::class.java)

    fun parseInData(inData: String) {
        val message: mqttMsg? = adapter.fromJson(inData)
        if (message != null) {
            if (message.msg == "ready") {
            } else {
                messageBubble5.visibility = View.VISIBLE
                GlobalClass.CallOutMessage5 = message.msg
            }
        }
    }

    fun StatusDone() {
        val intent = Intent(this, SecondActivity::class.java)
        finish()
        startActivity(intent)
    }

    fun arvTime1(view: View) {
        closeKeyBoard()
        if (arvtimeET1.text.isEmpty()) {
            GlobalClass.arriveTime[0] = txtClock.text.toString()
            arvtimeET1.setText(GlobalClass.arriveTime[0])
        }
    }

    fun arvTime2(view: View) {
        closeKeyBoard()
        if (arvtimeET2.text.isEmpty() && timeStampTV1.text.isNotEmpty()) {
            GlobalClass.arriveTime[1] = txtClock.text.toString()
            arvtimeET2.setText(GlobalClass.arriveTime[1])
        }
    }

    fun arvTime3(view: View) {
        closeKeyBoard()
        if (arvtimeET3.text.isEmpty() && timeStampTV2.text.isNotEmpty()) {
            GlobalClass.arriveTime[2] = txtClock.text.toString()
            arvtimeET3.setText(GlobalClass.arriveTime[2])
        }
    }

    fun arvTime4(view: View) {
        closeKeyBoard()
        if (arvtimeET4.text.isEmpty() && timeStampTV3.text.isNotEmpty()) {
            GlobalClass.arriveTime[3] = txtClock.text.toString()
            arvtimeET4.setText(GlobalClass.arriveTime[3])
        }
    }

    fun arvTime5(view: View) {
        closeKeyBoard()
        if (arvtimeET5.text.isEmpty() && timeStampTV4.text.isNotEmpty()) {
            GlobalClass.arriveTime[4] = txtClock.text.toString()
            arvtimeET5.setText(GlobalClass.arriveTime[4])
        }
    }

    fun arvTime6(view: View) {
        closeKeyBoard()
        if (arvtimeET6.text.isEmpty() && timeStampTV5.text.isNotEmpty()) {
            GlobalClass.arriveTime[5] = txtClock.text.toString()
            arvtimeET6.setText(GlobalClass.arriveTime[5])
        }
    }

    fun arvTime7(view: View) {
        closeKeyBoard()
        if (arvtimeET7.text.isEmpty() && timeStampTV6.text.isNotEmpty()) {
            GlobalClass.arriveTime[6] = txtClock.text.toString()
            arvtimeET7.setText(GlobalClass.arriveTime[6])
        }
    }

    fun arvTime8(view: View) {
        closeKeyBoard()
        if (arvtimeET8.text.isEmpty() && timeStampTV7.text.isNotEmpty()) {
            GlobalClass.arriveTime[7] = txtClock.text.toString()
            arvtimeET8.setText(GlobalClass.arriveTime[7])
        }
    }

    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}

