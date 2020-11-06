package com.example.rtproduct
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextClock
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.rtproduct.manager.MQTTConnectionParams
import com.example.rtproduct.manager.MQTTmanager
import com.example.rtproduct.protocols.UIUpdaterInterface
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.android.synthetic.main.password_dialog.view.*
import java.text.SimpleDateFormat
import java.util.*

class SecondActivity : AppCompatActivity(), UIUpdaterInterface {

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm'Z'", Locale.getDefault())
    val currentDateandTime: String = sdf.format(Date())

    data class mqttMsg (val msg: String = "")
    data class WeightMsg (val createdTimeUTC: String, val weight: String = "")
    data class Textmessage(val createdTimeUTC: String, val TruckNo: String, val msg: String)

    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val msgAdapter: JsonAdapter<mqttMsg> = moshi.adapter(mqttMsg::class.java)
    val Textadapter: JsonAdapter<Textmessage> = moshi.adapter(Textmessage::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val TextTime = findViewById<TextClock>(R.id.TextTime)
        TextTime.format12Hour = "MM-dd-yy kk:mm"

        val Scale_OutBtn = findViewById<Button>(R.id.Scale_OutBtn)
        val ClearBtn = findViewById<Button>(R.id.ClearBtn)
        val TimeCardBtn = findViewById<Button>(R.id.TimeCardBtn)
        val mapBtn = findViewById<Button>(R.id.mapBtn)

        //Displaying the Scale Out button IF scale out happened
        if (GlobalClass.Weight!!.toFloat() < 3.00) {
            Scale_OutBtn.visibility = View.GONE
        } else if (GlobalClass.Retry == "VISIBLE") {
            Scale_OutBtn.visibility = View.VISIBLE
            val timer = object : CountDownTimer(10000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                }

                override fun onFinish() {
                    Scale_OutBtn.visibility = View.GONE
                    GlobalClass.Retry = ""
                }
            }
            timer.start()
        }

        ClearBtn.setOnClickListener() {
            tvCall_Out.setText("")
            GlobalClass.CallOutMessage3 = null
            GlobalClass.CallOutMessage4 = null
            GlobalClass.CallOutMessage5 = null
            spinner.setSelection(0)
        }

        Scale_OutBtn.setOnClickListener() {
            StatusReady()
        }

        mapBtn.setOnClickListener() {
//            MapReady()
        }

        TimeCardBtn.setOnClickListener() {
            Timecard()
        }

        versionTV.text = GlobalClass.version

        //Dialog Box for Setting Page
        SettingsIcon.setOnClickListener() {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.password_dialog, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Password")
            val mAlertDialog = mBuilder.show()
            mDialogView.dialogLoginBtn.setOnClickListener {
                val Dpassword = mDialogView.dialogPasswordET.text.toString()
                if (GlobalClass.DialogPassword == null) {
                    Dpassword == "1234"
                    mAlertDialog.dismiss()
                    settings()
                } else if (Dpassword == GlobalClass.DialogPassword) {
                    mAlertDialog.dismiss()
                    settings()
                } else {
                    mAlertDialog.dismiss()
                }
            }
            mDialogView.dialogCancelBtn.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }

        //Messages from other activities
        if (GlobalClass.CallOutMessage3 == null) {
        } else {
            tvCall_Out.text = GlobalClass.CallOutMessage3
        }

        if (GlobalClass.CallOutMessage4 == null) {
        } else {
            tvCall_Out.text = GlobalClass.CallOutMessage4
        }

        if (GlobalClass.CallOutMessage5 == null) {
        } else {
            tvCall_Out.text = GlobalClass.CallOutMessage5
        }

        //Canned Messages
        val msgArrays = resources.getStringArray(R.array.messageArray)

        val arrayAdapter = ArrayAdapter(this, R.layout.spinner_item, msgArrays)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                GlobalClass.sendMessage = msgArrays[position]
                if (GlobalClass.sendMessage == "Tap Here for Can Messages") {
                } else {
                    createJsonData()
                    val timer = object : CountDownTimer(5000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            tvCall_Out.setText("")
                        }

                        override fun onFinish() {
                            spinner.setSelection(0)

                        }
                    }
                    timer.start()
                }
            }
        }

        //MQTT messaging
        resetUIWithConnection(false)

        var topics = arrayOf<String>(
            "/agg_scale/${GlobalClass.plantCode}/${GlobalClass.scaleCode}/metrics",
            "/agg_scale/${GlobalClass.plantCode}/${GlobalClass.scaleCode}/veh/agg/${GlobalClass.truckComp}/${GlobalClass.truckType}/${GlobalClass.TruckNo}/scale_out",
            "/agg_scale/+/+/veh/agg/${GlobalClass.truckComp}/${GlobalClass.truckType}/${GlobalClass.TruckNo}/status",
            "/vehicle/agg/${GlobalClass.TruckNo}/call_out"
        )

        var connectionParams =
            MQTTConnectionParams("RTP1", GlobalClass.MqttHost.toString(), topics, "", "")
        GlobalClass.mqttManager = MQTTmanager(connectionParams, applicationContext, this)
        GlobalClass.mqttManager?.connect()
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
        var text = tvReady.text.toString()
        var newText = """
            $text
            $message
            """
        parseInData(newText)

        val timer = object : CountDownTimer(1500, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                tvReady.setText("")
            }
        }
        timer.start()
    }

    override fun topicUpdate(topic: String) {
        parseInTopic(topic)
    }

    fun parseInTopic(topic: String) {
        val input: String = topic
        var result = input.split("/").map { it.trim() }
        if (result.last() == "status") {
            GlobalClass.plantCode = result.get(2)
            GlobalClass.scaleCode = result.get(3)
            GlobalClass.topicMessage = result.last()
            System.err.println("Activity 2")
            result.forEach {println(it) }
            System.err.println(GlobalClass.plantCode)
            System.err.println(GlobalClass.scaleCode)
        } else
            GlobalClass.topicMessage = result.last()
    }


    fun parseInData(inData: String) {
        try {
            val message: mqttMsg? = msgAdapter.fromJson(inData)
            if (message != null) {
                if (message.msg == "ready") {
                    StatusReady()
                }else  {
                    tvCall_Out.setText(message.msg)
                }
            }
        } catch (ex: Exception) {
            Log.e("Mqtt", "Message Parsing Error: " + ex.message)
        }
    }

    private fun createJsonData() {
        val TextMessage = Textmessage("$currentDateandTime", "${GlobalClass.TruckNo}", GlobalClass.sendMessage.toString())
        var mymessage = Textadapter.toJson(TextMessage)
        GlobalClass.mqttManager?.publish("/vehicle/agg/${GlobalClass.TruckNo}/call_out", "$mymessage")
    }

    fun StatusReady() {
        val intent = Intent(this, ThridActivity::class.java)
        finish()
        startActivity(intent)
    }

//    fun MapReady() {
//        val intent = Intent(this, ForthActivity::class.java)
//        startActivity(intent)
//        finish()
//    }

    fun Timecard() {
        val intent = Intent(this, FifthActivity::class.java)
        finish()
        startActivity(intent)
    }

    fun settings() {
        val intent = Intent(this, SettingsActivity::class.java)
        finish()
        startActivity(intent)
    }
}
