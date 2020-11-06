package com.example.rtproduct
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        var et_emp_num = findViewById(R.id.et_emp_num) as EditText
        var btn_reset = findViewById(R.id.btn_reset) as Button
        var btn_submit = findViewById(R.id.btn_submit) as Button

        //Set Truck Number from Database
        et_trk_num.setText("Truck# " + GlobalClass.TruckNo)

        //Reset Button to clear entry
        btn_reset.setOnClickListener {
            et_emp_num.setText("")
        }
        versionNumber.text = GlobalClass.version

        btn_submit.setOnClickListener {
            val emp_num = et_emp_num.text
            StatusDone()
            //put login validation here
        }
    }

    fun StatusDone() {
        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
        finish()
    }
}
