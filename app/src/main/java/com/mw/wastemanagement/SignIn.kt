package com.mw.wastemanagement

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.mw.vendorsurvey.utils.Constant
import com.github.razir.progressbutton.hideProgress
import com.mw.m_leads.MySingleton
import com.mw.m_leads.model.VolleyErrorParser
import com.mw.utills.SharedPrefs
import com.mw.wastemanagement.utils.SharedPrefsEmail
import org.json.JSONObject

class SignIn : AppCompatActivity() {
    lateinit var signin: TextView
    lateinit var prefs: SharedPrefs
    lateinit var dialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        supportActionBar!!.hide()

        dialog = ProgressDialog(this)
        dialog.setTitle("Please Wait...")
        dialog.setCancelable(true)

        signin = findViewById(R.id.login)
        val email: EditText = findViewById(R.id.email)
        val password: EditText = findViewById(R.id.password)

        prefs = SharedPrefs(this)

        val emailpref = SharedPrefsEmail(this)

        val type = emailpref.email

        //autoLogin
        if(type!!.isEmpty()) {
            // Toast.makeText(this@Login, "Please Login", Toast.LENGTH_SHORT).show()
        }
        else {
            dialog.show()

            val i = Intent(this,Home::class.java)
            startActivity(i)
            finish()

            dialog.dismiss()
        }

        signin.setOnClickListener {

            signin.setBackgroundResource(R.drawable.orangebtn)

            // Add any additional actions you want to perform on click

            // To revert the background to the normal state after a certain delay (e.g., 500 milliseconds)
            Handler().postDelayed({
                signin.setBackgroundResource(R.drawable.mainbutton)
            }, 500)



            if (email.text.toString().trim().isEmpty()) {
                email.error = "Email cannot be empty"
                return@setOnClickListener
            } else if (password.text.toString().trim().isEmpty()) {
                password.error = "Password cannot be empty"
                return@setOnClickListener
            }

            //  dialog.show()

            val jsonObject = JSONObject()

            jsonObject.put("req","login")
            jsonObject.put("username", email.text.toString().trim())
            jsonObject.put("password", password.text.toString().trim())

            Log.e("jsonObject", jsonObject.toString())
            val url = Constant.BASE_URL
            Log.e("url", "---->$url")

            val request = object : StringRequest(
                Method.POST, url, Response.Listener { response ->
                    try {
                        val responseObj = JSONObject(response)
                        if (response.isNotEmpty()) {
                            signin.hideProgress("Login")

                            val status = responseObj.getString("status")
                            val message = responseObj.getString("response")

                            if (status == "success") {

                                val data = responseObj.getJSONObject("data")
                                val token = data.getString("token")
                                val vname = data.getString("name")
                                val prefs = SharedPrefs(this)

                                prefs.token = (token)
                                Log.e("token", "---->$token")

                                emailpref.email = vname

                                successDialog(message)
                                dialog.dismiss()

                            }
                            else {
                                signin.hideProgress("Login")
                                val message = responseObj.getString("response")
                                failureDialog(message)
                                dialog.dismiss()

                            }


                        } else {
                            signin.hideProgress("Login")
                            failureDialog("Something went wrong")
                            dialog.dismiss()

                        }
                    } catch (ex: Exception) {
                        signin.hideProgress("Login")
                        failureDialog("Something went wrong")
                        dialog.dismiss()

                    }
                },
                Response.ErrorListener { error ->
                    signin.hideProgress("Login")
                    val errorMessage = VolleyErrorParser.getVolleyErrorMessage(error)
                    failureDialog(errorMessage)
                    dialog.dismiss()

                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val params = java.util.HashMap<String, String>()
                    params["Content-Type"] = "application/json"
                    return params
                }

                override fun getBody(): ByteArray {
                    return jsonObject.toString().toByteArray()
                }
            }
            MySingleton.getInstance(this).addToRequestQueue(request)


        }
        /*back.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }*/


    }

    private fun successDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        val layoutView = layoutInflater.inflate(R.layout.success_view_layout, null)
        val btnOk = layoutView.findViewById<AppCompatButton>(R.id.btnOk)
        val loginMsg = layoutView.findViewById<TextView>(R.id.tvLoginMsg)
        loginMsg.text = message
        builder.setView(layoutView)
        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btnOk.setOnClickListener {

            btnOk.setBackgroundResource(R.drawable.orangebtn)

            // Add any additional actions you want to perform on click

            // To revert the background to the normal state after a certain delay (e.g., 500 milliseconds)
            Handler().postDelayed({
                btnOk.setBackgroundResource(R.drawable.lightgreenbutton)
            }, 500)

//            val intent = Intent(this,VendorTypes::class.java)
            val intent = Intent(this,Home::class.java)
            startActivity(intent)
            finish()
            alertDialog.dismiss()

        }
    }

    private fun failureDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        val layoutView = layoutInflater.inflate(R.layout.failure_view_layout, null)
        val btnOk = layoutView.findViewById<AppCompatButton>(R.id.btnOk)
        val loginMsg = layoutView.findViewById<TextView>(R.id.tvLoginMsg)
        loginMsg.text = message
        builder.setView(layoutView)
        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btnOk.setOnClickListener {
            alertDialog.dismiss()


        }
    }

    override fun onBackPressed() {
        navigateBack()
    }

    private fun navigateBack() {
        var i = Intent(this, MainActivity::class.java)
        startActivity(i)
        overridePendingTransition(
            R.anim.anim_slide_in_left,
            R.anim.anim_slide_in_right
        )
        finish()
    }
}