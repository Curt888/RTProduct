package com.example.rtproduct

//class ForthActivity : AppCompatActivity(), UIUpdaterInterface {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_forth)

//        resetUIWithConnection(false)
//
//        var topics = arrayOf<String>(
//            "/agg_scale/${GlobalClass.plantCode}/${GlobalClass.scaleCode}/metrics",
//            "/agg_scale/${GlobalClass.plantCode}/${GlobalClass.scaleCode}/veh/agg/${GlobalClass.truckComp}/${GlobalClass.truckType}/${GlobalClass.TruckNo}/scale_out",
//            "/vehicle/agg/${GlobalClass.plantCode}e/{GlobalClass.truckComp}/${GlobalClass.truckType}/${GlobalClass.TruckNo}/scale_out",
//            "/agg_scale/+/+/veh/agg/${GlobalClass.truckComp}/${GlobalClass.truckType}/${GlobalClass.TruckNo}/status",
//            "/vehicle/agg/${GlobalClass.TruckNo}/call_out"
//        )
//
//        var connectionParams = MQTTConnectionParams("RTP1", GlobalClass.MqttHost.toString(), topics, "", "")
//        GlobalClass.mqttManager = MQTTmanager(connectionParams, applicationContext, this)
//        GlobalClass.mqttManager?.connect()
//
//    homeView5.setOnClickListener() {
//        StatusDone()
//    }
//    }
//
//    override fun resetUIWithConnection(status: Boolean) {
//        if (status) {
//            updateStatusViewWith("Connected")
//        } else {
//            updateStatusViewWith("Disconnected")
//        }
//    }
//
//    override fun updateStatusViewWith(status: String) {
//        statusLabl.text = status
//    }

//    override fun update(message: String) {
//        var text = textViewInstructions4.text.toString()
//        var newText = """
//            $text
//            $message
//            """
//        parseInData(newText)
//    }
//
//    override fun topicUpdate(topic: String) {
//        parseInTopic(topic)
//    }
//
//    data class mqttMsg (val msg: String = "")
//
//    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
//    val adapter: JsonAdapter<mqttMsg> = moshi.adapter(mqttMsg::class.java)

//    fun parseInTopic(topic: String) {
//        val input: String = topic
//        var result = input.split("/").map {it.trim() }
//        if (result.last() == "status") {
//            GlobalClass.plantCode = result.get(2)
//            GlobalClass.scaleCode = result.get(3)
//        }
//    }
//
//    fun parseInData(inData: String) {
//        val message: mqttMsg? = adapter.fromJson(inData)
//        if (message != null) {
//            if (message.msg == "ready") {
//            }else {
//                MessageBubble4.visibility = View.VISIBLE
//                GlobalClass.CallOutMessage4 = message.msg
//            }
//        }
//    }

//    fun StatusDone() {
//        val intent = Intent(this, SecondActivity::class.java)
//        startActivity(intent)
//    }
//}