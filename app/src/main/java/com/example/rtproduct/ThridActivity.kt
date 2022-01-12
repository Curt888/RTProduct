package com.example.rtproduct
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.rtproduct.manager.MQTTConnectionParams
import com.example.rtproduct.manager.MQTTmanager
import com.example.rtproduct.protocols.UIUpdaterInterface
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.activity_thrid.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ThridActivity : AppCompatActivity(), UIUpdaterInterface {

    data class scaleOutMsg (val createdTimeUTC: String, val weight: String = "", val material: String = "", val statusCode: Int)
    data class WeightMsg (val createdTimeUTC: String, val weight: String = "")
    data class mqttMsg (val msg: String = "")

    var mqttManager: MQTTmanager? = null
    var Product:String? = null
    var ProductSubmit:String? = null
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm'Z'", Locale.getDefault())
    var currentDateandTime: String = sdf.format(Date())
    val tct = SimpleDateFormat("MM-dd-yy HH:mm", Locale.getDefault())
    var timeCardDate: String = tct.format(Date())
    val df = DecimalFormat("##.##")
    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val weightAdapter: JsonAdapter<WeightMsg> = moshi.adapter(WeightMsg::class.java)
    val scaleOutadapter: JsonAdapter<scaleOutMsg> = moshi.adapter(scaleOutMsg::class.java)
    val msgAdapter : JsonAdapter<mqttMsg> = moshi.adapter(mqttMsg::class.java)

//    val moshiDate = Moshi.Builder().add(Date::class.java, Rfc3339DateJsonAdapter()).build()
//    val Dateadapter:JsonAdapter<Date> = moshi.adapter (Date::class.java)

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
        var text = textViewInstructions.text.toString()
        var newText = """
            $text
            $message
            """
        parseInData(newText)
    }

    override fun topicUpdate(topic: String) {
        parseInTopic(topic)
    }

    //parsing in topic
    fun parseInTopic(topic: String) {
        val input: String = topic
        var result = input.split("/").map { it.trim() }
        if (result.last() == "status") {
            GlobalClass.plantCode = result.get(2)
            GlobalClass.scaleCode = result.get(3)
            GlobalClass.topicMessage = result.last()
            System.err.println("Activity 3")
            result.forEach {
                println(it)}
            System.err.println(GlobalClass.plantCode)
            System.err.println(GlobalClass.scaleCode)
        } else
            GlobalClass.topicMessage = result.last()
    }

    //parsing in messages
    fun parseInData(inData:String) {
        if (GlobalClass.topicMessage == "metrics") {
            val message: WeightMsg? = weightAdapter.fromJson(inData)
            if (message != null) {
                if (message.weight.toFloat() > 5.00) {
                    tvWeight.text = df.format(message.weight.toFloat())
                    GlobalClass.Weight = df.format(message.weight)
                } else if (message.weight.toFloat() < 5.00f) {
                    StatusDone()
                }
            }
        }else if(GlobalClass.topicMessage == "scale_out") {
            val message1: scaleOutMsg? = scaleOutadapter.fromJson(inData)
            if (message1 != null) {
                if (message1.statusCode == 1 && message1.weight.toFloat() > 5) {
                    textViewInstructions.text = "Accepted"
                    tvWeight.text = df.format(message1.weight.toFloat())
                    GlobalClass.Weight = message1.weight
                }else if (message1.statusCode == 2 && message1.weight.toFloat() > 5) {
                    textViewInstructions.text = "RejectedOver"
                    tvWeight.text = df.format(message1.weight.toFloat())
                }else if (message1.statusCode == 3 && message1.weight.toFloat() > 5) {
                    textViewInstructions.text = "RejectedUnder"
                    tvWeight.text = df.format(message1.weight.toFloat())
                }else if (message1.statusCode == 4 && message1.weight.toFloat() > 5) {
                    textViewInstructions.text = "RejectedNetwork"
                    tvWeight.text = df.format(message1.weight.toFloat())
                }
            }
        }else if(GlobalClass.topicMessage == "call_out") {
            val message3: mqttMsg? = msgAdapter.fromJson(inData)
            if(message3 != null) {
                if (message3.msg == "ready") {
                }else  {
                    messageBubble.visibility = View.VISIBLE
                    GlobalClass.CallOutMessage3 = message3.msg
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thrid)

        val textViewInstructions = findViewById<TextView>(R.id.textViewInstructions)


        var topics = arrayOf<String>(
            "/agg_scale/${GlobalClass.plantCode}/${GlobalClass.scaleCode}/metrics",
            "/agg_scale/${GlobalClass.plantCode}/${GlobalClass.scaleCode}/veh/agg/${GlobalClass.truckComp}/${GlobalClass.truckType}/${GlobalClass.TruckNo}/scale_out",
            "/agg_scale/+/+/veh/agg/${GlobalClass.truckComp}/${GlobalClass.truckType}/${GlobalClass.TruckNo}/status",
            "/vehicle/agg/${GlobalClass.TruckNo}/call_out"
        )
        var host = "tcp://10.2.203.198:1883"
        var connectionParams = MQTTConnectionParams("RTP1", host, topics, "", "")
        mqttManager = MQTTmanager(connectionParams, applicationContext, this)
        mqttManager?.connect()

        MessageView.setOnClickListener() {
            StatusDone()
        }

        //selecting a product
        ProductBtn1.setOnCheckedChangeListener { compoundbutton, isChecked ->
            if (isChecked) {
                tvProduct.text = "Sand"
                Product = "9"
                ProductSubmit = "Sand"
                ProductBtn1.setBackgroundResource(R.drawable.button_pressed)
                ProductBtn1.setTextColor(Color.BLACK)
                ProductBtn2.setEnabled(false)
                ProductBtn3.setEnabled(false)
                ProductBtn4.setEnabled(false)
                SubmitBtn.visibility = View.VISIBLE
            } else {
                tvProduct.text = ""
                ProductBtn1.setBackgroundResource(R.drawable.button_normal)
                ProductBtn1.setTextColor(Color.RED)
                ProductBtn2.setEnabled(true)
                ProductBtn3.setEnabled(true)
                ProductBtn4.setEnabled(true)
                SubmitBtn.visibility = View.GONE
            }
        }

        ProductBtn2.setOnCheckedChangeListener { compoundbutton, isChecked ->
            if (isChecked) {
                tvProduct.text = "1 1/2 Rock"
                Product =  "6"
                ProductSubmit = "1 1/2"
                ProductBtn2.setBackgroundResource(R.drawable.button_pressed)
                ProductBtn2.setTextColor(Color.BLACK)
                ProductBtn1.setEnabled(false)
                ProductBtn3.setEnabled(false)
                ProductBtn4.setEnabled(false)
                SubmitBtn.visibility = View.VISIBLE
            } else {
                tvProduct.text = ""
                ProductBtn2.setBackgroundResource(R.drawable.button_normal)
                ProductBtn2.setTextColor(Color.RED)
                ProductBtn1.setEnabled(true)
                ProductBtn3.setEnabled(true)
                ProductBtn4.setEnabled(true)
                SubmitBtn.visibility = View.GONE
            }
        }

        ProductBtn3.setOnCheckedChangeListener { compoundbutton, isChecked ->
            if (isChecked) {
                tvProduct.text = "1 inch Rock"
                Product =  "5"
                ProductSubmit = "1 inch"
                ProductBtn3.setBackgroundResource(R.drawable.button_pressed)
                ProductBtn3.setTextColor(Color.BLACK)
                ProductBtn1.setEnabled(false)
                ProductBtn2.setEnabled(false)
                ProductBtn4.setEnabled(false)
                SubmitBtn.visibility = View.VISIBLE
            } else {
                tvProduct.text = ""
                ProductBtn3.setBackgroundResource(R.drawable.button_normal)
                ProductBtn3.setTextColor(Color.RED)
                ProductBtn1.setEnabled(true)
                ProductBtn2.setEnabled(true)
                ProductBtn4.setEnabled(true)
                SubmitBtn.visibility = View.GONE
            }
        }

        ProductBtn4.setOnCheckedChangeListener { compoundbutton, isChecked ->
            if (isChecked) {
                tvProduct.text = "3/8 Rock"
                Product =  "4"
                ProductSubmit = "3/8"
                ProductBtn4.setBackgroundResource(R.drawable.button_pressed)
                ProductBtn4.setTextColor(Color.BLACK)
                ProductBtn1.setEnabled(false)
                ProductBtn2.setEnabled(false)
                ProductBtn3.setEnabled(false)
                SubmitBtn.visibility = View.VISIBLE
            } else {
                tvProduct.text = ""
                ProductBtn4.setBackgroundResource(R.drawable.button_normal)
                ProductBtn4.setTextColor(Color.RED)
                ProductBtn1.setEnabled(true)
                ProductBtn2.setEnabled(true)
                ProductBtn3.setEnabled(true)
                SubmitBtn.visibility = View.GONE
            }
        }

        //submitting that product
        ProductSubmit
        SubmitBtn.setOnClickListener {
            if (SubmitBtn.isChecked()) {
                SubmitBtn.setTextColor(Color.BLACK)
                SubmitBtn.setBackgroundResource(R.drawable.button_pressed)
            }else {
                SubmitBtn.setTextColor(Color.RED)
                SubmitBtn.setBackgroundResource(R.drawable.button_normal)
            }
            createJsonData()
            ProductBtn1.setEnabled(false)
            ProductBtn2.setEnabled(false)
            ProductBtn3.setEnabled(false)
            ProductBtn4.setEnabled(false)

            val timer = object : CountDownTimer(30000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    SubmitBtn.setEnabled(false)
                    tvProduct.text = "$ProductSubmit is Submitted"
                    if (textViewInstructions.text == "Accepted") {
                        onFinish()
                        cancel()
                    }else if (textViewInstructions.text == "RejectedOver") {
                        onFinish()
                        cancel()
                    }else if (textViewInstructions.text == "RejectedUnder") {
                        onFinish()
                        cancel()
                    }else if (textViewInstructions.text == "RejectedNetwork") {
                        onFinish()
                        cancel()
                    }
                }

                override fun onFinish() {
                    if (textViewInstructions.text == "") {
                        ProductBtn1.setEnabled(true)
                        ProductBtn2.setEnabled(true)
                        ProductBtn3.setEnabled(true)
                        ProductBtn4.setEnabled(true)
                        ProductBtn1.setChecked(false)
                        ProductBtn2.setChecked(false)
                        ProductBtn3.setChecked(false)
                        ProductBtn4.setChecked(false)
                        SubmitBtn.setChecked(false)
                        SubmitBtn.visibility = View.GONE
                        tvProduct.text = "Try Again"
                        SubmitBtn.setTextColor(Color.RED)
                        SubmitBtn.setBackgroundResource(R.drawable.button_normal)

                        val timer1 = object : CountDownTimer(15000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                if (textViewInstructions.getText().toString().isEmpty()) {

                                } else {
                                    cancel()
                                }
                            }
                            override fun onFinish() {
                                StatusDone()
                            }
                        }
                        timer1.start()

                    }else if (textViewInstructions.text == "Accepted") {
                        Accepted()
                        ProductBtn1.setEnabled(true)
                        ProductBtn2.setEnabled(true)
                        ProductBtn3.setEnabled(true)
                        ProductBtn4.setEnabled(true)
                        ProductBtn1.setChecked(false)
                        ProductBtn2.setChecked(false)
                        ProductBtn3.setChecked(false)
                        ProductBtn4.setChecked(false)
                        SubmitBtn.setChecked(false)
                        SubmitBtn.visibility = View.GONE
                        SubmitBtn.setTextColor(Color.RED)
                        SubmitBtn.setBackgroundResource(R.drawable.button_normal)

                        val timer2 = object : CountDownTimer(8000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                            }
                            override fun onFinish() {
                                textViewInstructions.setText("")
                                tvProduct.setText("")

                            }
                        }
                        timer2.start()

                    } else if (textViewInstructions.text == "RejectedUnder") {
                        RejectedUnder()
                        ProductBtn1.setEnabled(true)
                        ProductBtn2.setEnabled(true)
                        ProductBtn3.setEnabled(true)
                        ProductBtn4.setEnabled(true)
                        ProductBtn1.setChecked(false)
                        ProductBtn2.setChecked(false)
                        ProductBtn3.setChecked(false)
                        ProductBtn4.setChecked(false)
                        SubmitBtn.setChecked(false)
                        SubmitBtn.visibility = View.GONE
                        SubmitBtn.setTextColor(Color.RED)
                        SubmitBtn.setBackgroundResource(R.drawable.button_normal)

                        val timer3 = object : CountDownTimer(8000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                            }
                            override fun onFinish() {
                                textViewInstructions.setText("")
                                tvProduct.setText("")
                            }
                        }
                        timer3.start()

                    } else if (textViewInstructions.text == "RejectedOver") {
                        RejectedOver()
                        ProductBtn1.setEnabled(true)
                        ProductBtn2.setEnabled(true)
                        ProductBtn3.setEnabled(true)
                        ProductBtn4.setEnabled(true)
                        ProductBtn1.setChecked(false)
                        ProductBtn2.setChecked(false)
                        ProductBtn3.setChecked(false)
                        ProductBtn4.setChecked(false)
                        SubmitBtn.setChecked(false)
                        SubmitBtn.visibility = View.GONE
                        SubmitBtn.setTextColor(Color.RED)
                        SubmitBtn.setBackgroundResource(R.drawable.button_normal)

                        val timer4 = object : CountDownTimer(8000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                            }
                            override fun onFinish() {
                                textViewInstructions.setText("")
                                tvProduct.setText("")
                            }
                        }
                        timer4.start()
                    } else if (textViewInstructions.text == "RejectedNetwork") {
                        systemDown()
                        ProductBtn1.setEnabled(true)
                        ProductBtn2.setEnabled(true)
                        ProductBtn3.setEnabled(true)
                        ProductBtn4.setEnabled(true)
                        ProductBtn1.setChecked(false)
                        ProductBtn2.setChecked(false)
                        ProductBtn3.setChecked(false)
                        ProductBtn4.setChecked(false)
                        SubmitBtn.setChecked(false)
                        SubmitBtn.visibility = View.GONE
                        SubmitBtn.setTextColor(Color.RED)
                        SubmitBtn.setBackgroundResource(R.drawable.button_normal)

                        val timer5 = object : CountDownTimer(8000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                            }
                            override fun onFinish() {
                                textViewInstructions.setText("")
                                tvProduct.setText("")
                                StatusDone()
                            }
                        }
                        timer5.start()
                    }
                }
            }
            timer.start()
        }
    }

    private fun createJsonData() {
        val ScaleOutMsg = scaleOutMsg("$currentDateandTime", "${GlobalClass.Weight}", "$Product", 0 )
        var mymessage = scaleOutadapter.toJson(ScaleOutMsg)
        mqttManager?.publish("/vehicle/agg/${GlobalClass.truckComp}/${GlobalClass.truckType}/${GlobalClass.TruckNo}/scale_out", "$mymessage")
        System.err.println("Activity 3 Parse")
        System.err.println(GlobalClass.plantCode)
        System.err.println(GlobalClass.scaleCode)
    }

    fun RejectedUnder() {
        val mAlertDialog = AlertDialog.Builder(this@ThridActivity)
        mAlertDialog.setTitle("Rejected!")
        mAlertDialog.setMessage("You are under weight, Please add material")
        mAlertDialog.setNegativeButton("OK") { dialog, id ->
            dialog.dismiss()
            StatusDone()
        }
        mAlertDialog.show()
    }

    fun RejectedOver() {
        val mAlertDialog = AlertDialog.Builder(this@ThridActivity)
        mAlertDialog.setTitle("Rejected!")
        mAlertDialog.setMessage("You are over weight, Please trim")
        mAlertDialog.setNegativeButton("OK") { dialog, id ->
            dialog.dismiss()
            StatusDone()
        }
        mAlertDialog.show()
    }

    fun systemDown() {
        val mAlertDialog = AlertDialog.Builder(this@ThridActivity)
        mAlertDialog.setTitle("Network is down")
        mAlertDialog.setMessage("Network is Down at this time")
        mAlertDialog.setNegativeButton("") { dialog, id ->
            dialog.dismiss()
        }
        mAlertDialog.show()
    }

    fun Accepted() {
        val mAlertDialog = AlertDialog.Builder(this@ThridActivity)
        mAlertDialog.setTitle("Accepted!")
        mAlertDialog.setMessage("Weight: " + tvWeight.text.toString() + ", Material ID #" + Product + ", is Accepted")
        mAlertDialog.setNegativeButton("Ok") { dialog, _ ->
            dialog.dismiss()
            GlobalClass.Retry = "VISIBLE"
            StatusDone()

            //writing data to Time Card
            if (GlobalClass.i == 0) {
                GlobalClass.timestamp[0] = timeCardDate
                GlobalClass.source[0] = GlobalClass.plantCode.toString()
                GlobalClass.material[0] = Product.toString()
                GlobalClass.weight[0] = tvWeight.text.toString()
                GlobalClass.i = GlobalClass.i?.plus(1)
            }else if (GlobalClass.i == 1) {
                GlobalClass.timestamp[1] = timeCardDate
                GlobalClass.source[1] = GlobalClass.plantCode.toString()
                GlobalClass.material[1] = Product.toString()
                GlobalClass.weight[1] = tvWeight.text.toString()
                GlobalClass.i = GlobalClass.i?.plus(1)
            } else if (GlobalClass.i == 2) {
                GlobalClass.timestamp[2] = timeCardDate
                GlobalClass.source[2] = GlobalClass.plantCode.toString()
                GlobalClass.material[2] = Product.toString()
                GlobalClass.weight[2] = tvWeight.text.toString()
                GlobalClass.i = GlobalClass.i?.plus(1)
            } else if (GlobalClass.i == 3) {
                GlobalClass.timestamp[3] = timeCardDate
                GlobalClass.source[3] = GlobalClass.plantCode.toString()
                GlobalClass.material[3] = Product.toString()
                GlobalClass.weight[3] = tvWeight.text.toString()
                        GlobalClass.i = GlobalClass.i?.plus(1)
            } else if (GlobalClass.i == 4) {
                GlobalClass.timestamp[4] = timeCardDate
                GlobalClass.source[4] = GlobalClass.plantCode.toString()
                GlobalClass.material[4] = Product.toString()
                GlobalClass.weight[4] = tvWeight.text.toString()
                GlobalClass.i = GlobalClass.i?.plus(1)
            } else if (GlobalClass.i == 5) {
                GlobalClass.timestamp[5] = timeCardDate
                GlobalClass.source[5] = GlobalClass.plantCode.toString()
                GlobalClass.material.set(5, Product.toString())
                GlobalClass.weight[5] = tvWeight.text.toString()
                GlobalClass.i = GlobalClass.i?.plus(1)
            } else if (GlobalClass.i == 6) {
                GlobalClass.timestamp[6] = timeCardDate
                GlobalClass.source[6] = GlobalClass.plantCode.toString()
                GlobalClass.material[6] = Product.toString()
                GlobalClass.weight[6] = tvWeight.text.toString()
                GlobalClass.i = GlobalClass.i?.plus(1)
            } else if (GlobalClass.i == 7) {
                GlobalClass.timestamp[7] = timeCardDate
                GlobalClass.source[7] = GlobalClass.plantCode.toString()
                GlobalClass.material[7] = Product.toString()
                GlobalClass.weight[7] = tvWeight.text.toString()
                GlobalClass.i = 0
            }
        }
        mAlertDialog.show()
    }

    fun StatusDone() {
        val intent = Intent(this, SecondActivity::class.java)
        finish()
        startActivity(intent)
    }

}





