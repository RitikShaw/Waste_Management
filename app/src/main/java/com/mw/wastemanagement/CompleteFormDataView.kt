package com.mw.wastemanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class CompleteFormDataView : AppCompatActivity() {
    lateinit var back: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_form_data_view)
        supportActionBar!!.hide()
        back = findViewById(R.id.back)

        back.setOnClickListener {
            navigateBack()
        }
    }

    override fun onBackPressed() {
        navigateBack()
    }

    private fun navigateBack() {
        var intent = Intent(this, ReportActivity::class.java)
        startActivity(intent)
        overridePendingTransition(
            R.anim.anim_slide_in_left,
            R.anim.anim_slide_in_right
        )
        finish()
    }
}