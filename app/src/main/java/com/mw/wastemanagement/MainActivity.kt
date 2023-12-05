package com.mw.wastemanagement

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private var pressedTime: Long = 0
    var v = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()

        val img1: ImageView = findViewById(R.id.SplashImage)
        val login: TextView = findViewById(R.id.login)
        val textview1: TextView = findViewById(R.id.tv)
        val textView2: TextView = findViewById(R.id.tv2)
        val imgview: ImageView = findViewById(R.id.imageView3)

        img1.setTranslationY(800f)
        textview1.setTranslationY(800f)
        textView2.setTranslationY(800f)
        login.setTranslationY(800f)
        imgview.setTranslationY(800f)

        img1.setAlpha(v)
        textview1.setAlpha(v)
        textView2.setAlpha(v)
        login.setAlpha(v)
        imgview.setAlpha(v)

        img1.animate().translationY(0f).alpha(1f).setDuration(1000).setStartDelay(300).start()
        textview1.animate().translationY(0f).alpha(1f).setDuration(1000).setStartDelay(300).start()
        textView2.animate().translationY(0f).alpha(1f).setDuration(1000).setStartDelay(300).start()
        login.animate().translationY(0f).alpha(1f).setDuration(1000).setStartDelay(300).start()
        imgview.animate().translationY(0f).alpha(1f).setDuration(1000).setStartDelay(300).start()

        login.setOnClickListener {
            login.setBackgroundResource(R.drawable.orangebtn)

            // Add any additional actions you want to perform on click

            // To revert the background to the normal state after a certain delay (e.g., 500 milliseconds)
            Handler().postDelayed({
                login.setBackgroundResource(R.drawable.lightgreenbutton)
            }, 500)

            var i = Intent(this, SignIn::class.java)
            startActivity(i)
            finish()
        }
    }

    override fun onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(baseContext, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
        pressedTime = System.currentTimeMillis()
    }
}