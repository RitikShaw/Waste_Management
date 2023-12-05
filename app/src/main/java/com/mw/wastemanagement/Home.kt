package com.mw.wastemanagement

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Log.e
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.mw.m_leads.MySingleton
import com.mw.m_leads.model.VolleyErrorParser
import com.mw.utills.SharedPrefs
import com.mw.vendorsurvey.utils.Constant
import com.mw.wastemanagement.Database.CategoryData
import com.mw.wastemanagement.Database.SubCategoryData
import com.mw.wastemanagement.utils.SharedPrefsEmail
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date

class Home : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    lateinit var back: ImageView
    lateinit var start: TextView
    lateinit var prefs: SharedPrefs
    lateinit var emailpref: SharedPrefsEmail
    lateinit var categorysp: Spinner
    lateinit var subcatsp: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar!!.hide()

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        back = findViewById(R.id.back)
        start = findViewById(R.id.start)
        var dated: TextView = findViewById(R.id.dated)
        var user: TextView = findViewById(R.id.user)
        categorysp = findViewById(R.id.categorysp)
        subcatsp = findViewById(R.id.subcatsp)
        var others: EditText = findViewById(R.id.others)

        prefs = SharedPrefs(this)
        emailpref = SharedPrefsEmail(this)

        val simpleDateFormat = SimpleDateFormat("dd/MMM/yyyy") // You can change the date format as per your requirement
        val currentDate = simpleDateFormat.format(Date())
        dated.text = currentDate
        getcategory()
        user.setText("Hello User")

        /*val categorylist = resources.getStringArray(R.array.category)
        val categoryadapter = ArrayAdapter(this,R.layout.spinner_list,categorylist)
        categorysp.adapter = categoryadapter*/

        categorysp.onItemSelectedListener = object : AdapterView. OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                /*when(position){
                    0->{
                        subcatsp.visibility = View.GONE
                    }
                    1->{
                        subcatsp.visibility = View.GONE
                    }
                    2->{
                        val subcatlist = resources.getStringArray(R.array.commercialarray)
                        subcatsp.visibility = View.VISIBLE
                        val adapter = ArrayAdapter(this@Home,R.layout.spinner_list,subcatlist)
                        subcatsp.adapter = adapter
                    }
                    3->{
                        val subcatlist = resources.getStringArray(R.array.institutionalarray)
                        subcatsp.visibility = View.VISIBLE
                        val adapter = ArrayAdapter(this@Home,R.layout.spinner_list,subcatlist)
                        subcatsp.adapter = adapter

                    }
                    4->{
                        subcatsp.visibility = View.GONE
                    }
                }*/
                val categoryId = categorysp.selectedItem as CategoryData
                if (categoryId.category_id!="0") getSubCategory(categoryId) else {
                    subcatsp.visibility = View.GONE
                    subcatsp.adapter = null
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        subcatsp.onItemSelectedListener = object : AdapterView. OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                if(subcatsp.selectedItem.toString()=="Others"){

                    others.visibility = View.VISIBLE
                }

                else{
                    others.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


        back.setOnClickListener {
            navigateBack()
        }

        start.setOnClickListener {
            try{
                if (categorysp.selectedItem==null){
                    Toast.makeText(this, "Please wait for category", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if(categorysp.selectedItemPosition == 0){
                    Toast.makeText(this, "Please Select Category", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (subcatsp.visibility==View.VISIBLE && subcatsp.selectedItem==null){
                    Toast.makeText(this, "Please wait for sub category", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if(subcatsp.visibility==View.VISIBLE && subcatsp.selectedItemPosition==0){
                    Toast.makeText(this, "Please Select Sub-category", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if(others.visibility==View.VISIBLE && others.text.toString().trim().isEmpty()){
                    Toast.makeText(this, "Please specify other categories", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                SharedPrefs(this).category = (categorysp.selectedItem as CategoryData).category_id
                if (subcatsp.visibility==View.VISIBLE){
                    SharedPrefs(this).sub_category = (subcatsp.selectedItem as SubCategoryData).sub_category_id
                    if (others.visibility==View.VISIBLE){
                        SharedPrefs(this).others = (others.text.toString().trim())
                    }
                }
            }
            catch (e:Exception){
                e.printStackTrace()
            }
            when (categorysp.selectedItem.toString()) {
                "Household" -> {
                    start.setBackgroundResource(R.drawable.orangebtn)

                    // Add any additional actions you want to perform on click

                    // To revert the background to the normal state after a certain delay (e.g., 500 milliseconds)
                    Handler().postDelayed({
                        start.setBackgroundResource(R.drawable.lightgreenbutton)
                    }, 500)

                    var i = Intent(this,HouseHold::class.java)
                    startActivity(i)
                    finish()
                }
                "Institutional" -> {
                    start.setBackgroundResource(R.drawable.orangebtn)

                    // Add any additional actions you want to perform on click

                    // To revert the background to the normal state after a certain delay (e.g., 500 milliseconds)
                    Handler().postDelayed({
                        start.setBackgroundResource(R.drawable.lightgreenbutton)
                    }, 500)

                    var i = Intent(this,Institutional::class.java)
                    startActivity(i)
                    finish()
                }
                "Commercial" -> {
                    start.setBackgroundResource(R.drawable.orangebtn)

                    // Add any additional actions you want to perform on click

                    // To revert the background to the normal state after a certain delay (e.g., 500 milliseconds)
                    Handler().postDelayed({
                        start.setBackgroundResource(R.drawable.lightgreenbutton)
                    }, 500)

                    var i = Intent(this,Commercial::class.java)
                    startActivity(i)
                    finish()
                }
                "Bio Medical" -> {
                    start.setBackgroundResource(R.drawable.orangebtn)

                    // Add any additional actions you want to perform on click

                    // To revert the background to the normal state after a certain delay (e.g., 500 milliseconds)
                    Handler().postDelayed({
                        start.setBackgroundResource(R.drawable.lightgreenbutton)
                    }, 500)
                    var i = Intent(this,BioMedical::class.java)
                    startActivity(i)
                    finish()
                }
            }
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home ->
                    false

//                R.id.searchvendor -> {
//                   // startActivity(Intent(applicationContext, Search::class.java))
//                    overridePendingTransition(0, 0)
//                    finish()
//                    true
//                }
//
                R.id.profile -> {
                    startActivity(Intent(applicationContext, Profile::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.logout -> {

                    overridePendingTransition(0, 0)
                    showLogoutLayout("Are you sure you want to Logout?")
                    true
                }
                R.id.viewdata -> {
                    startActivity(Intent(applicationContext,ReportActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                else -> false
            }
        }

    }

    private fun getSubCategory(categoryId: CategoryData) {
        val subCategoryData = ArrayList<SubCategoryData>()
        subCategoryData.add(SubCategoryData("0","Choose Option"))
        val jObj = JSONObject()
        jObj.put("req","sub_category")
        jObj.put("category_id",categoryId.category_id)
        e("jObj",jObj.toString())
        val request = object : StringRequest(Method.POST,Constant.BASE_URL,object : Response.Listener<String?> {
            override fun onResponse(response: String?) {
                try {
                    if (!response.isNullOrEmpty()){
                        e("response",response)
                        val responseObj = JSONObject(response)
                        if (responseObj.has("sub_categories")){
                            val subCategory = responseObj.getJSONArray("sub_categories")
                            val data = listOf(*Gson().fromJson(subCategory.toString(),Array<SubCategoryData>::class.java))
                            if (data.isNotEmpty()){
                                subCategoryData.addAll(data)
                                val adapter = ArrayAdapter(this@Home,R.layout.spinner_list,subCategoryData)
                                subcatsp.adapter = adapter
                                subcatsp.visibility = View.VISIBLE
                            }
                            else{
                                subcatsp.visibility = View.GONE
                                subcatsp.adapter = null
                            }
                        }

                    }
                }
                catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }, Response.ErrorListener {
            val error = VolleyErrorParser.getVolleyErrorMessage(it)
            Toast.makeText(this,error,Toast.LENGTH_SHORT).show()
        } ){
            override fun getHeaders(): MutableMap<String, String> {
                return super.getHeaders()
            }

            override fun getBody(): ByteArray {
                return jObj.toString().toByteArray()
            }
        }
        MySingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun getcategory() {
        val categoryData = ArrayList<CategoryData>()
        categoryData.add(CategoryData("0","Choose Category"))
        val jObj = JSONObject().put("req","category")
        e("jObj",jObj.toString())
        val request = object : StringRequest(Method.POST,Constant.BASE_URL,object : Response.Listener<String?> {
            override fun onResponse(response: String?) {
                try {
                    if (!response.isNullOrEmpty()){
                        e("response",response)
                        val responseObj = JSONObject(response)
                        val categoryArr = responseObj.getJSONArray("categories")
                        val data = listOf(*Gson().fromJson(categoryArr.toString(),Array<CategoryData>::class.java))
                        if (data.isNotEmpty()){
                            categoryData.addAll(data)
                            val categoryAdapter = ArrayAdapter(this@Home,
                                R.layout.spinner_list,categoryData)
                            categoryAdapter.setDropDownViewResource(R.layout.spinner_list)
                            categorysp.adapter = categoryAdapter
                        }
                    }
                }
                catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }, Response.ErrorListener {
            val error = VolleyErrorParser.getVolleyErrorMessage(it)
            Toast.makeText(this,error,Toast.LENGTH_SHORT).show()
        } ){
            override fun getHeaders(): MutableMap<String, String> {
                return super.getHeaders()
            }

            override fun getBody(): ByteArray {
                return jObj.toString().toByteArray()
            }
        }
        MySingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun showLogoutLayout(message: String) {
        val builder = AlertDialog.Builder(this)
        val layoutView = layoutInflater.inflate(R.layout.success_view_layout2, null)
        val btnOk = layoutView.findViewById<AppCompatButton>(R.id.btnOk)
        val loginMsg = layoutView.findViewById<TextView>(R.id.tvLoginMsg)
        val header = layoutView.findViewById<TextView>(R.id.heading)
        header.text = "Logout"
        loginMsg.text = message
        builder.setView(layoutView)
        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        /*btnOk.setOnClickListener {

            btnOk.setBackgroundResource(R.drawable.orangebtn)

            // Add any additional actions you want to perform on click

            // To revert the background to the normal state after a certain delay (e.g., 500 milliseconds)
            Handler().postDelayed({
                btnOk.setBackgroundResource(R.drawable.lightgreenbutton)
            }, 500)

            successDialog("Logged out Successfully!")
            prefs.email = ""

        }*/
        btnOk.setOnClickListener {

            val jsonObject = JSONObject()

            jsonObject.put("req","logout")
            jsonObject.put("token", prefs.token)

            Log.e("jsonObject", jsonObject.toString())

            val url = Constant.BASE_URL
            Log.e("url", "---->$url")

            val request = object : StringRequest(
                Method.POST, url, Response.Listener { response ->
                    try {
                        val responseObj = JSONObject(response)
                        if (response.isNotEmpty()) {

                            val status = responseObj.getString("status")
                            val message = responseObj.getString("response")

                            if (status == "success") {

                                successDialog(message)
                                emailpref.email = ""
                                prefs.token = ""
                                alertDialog.dismiss()

                            }
                            else if(message=="Invalid token!"){
                                successDialog("Logged Out Successfully")
                                emailpref.email = ""
                                prefs.token = ""
                                alertDialog.dismiss()
                            }
                            else  {

                                val message = responseObj.getString("response")
                                Toast.makeText(this, "$message", Toast.LENGTH_SHORT).show()

                            }


                        } else {

                            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()

                        }
                    } catch (ex: Exception) {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()

                    }
                },
                Response.ErrorListener { error ->
                    val errorMessage = VolleyErrorParser.getVolleyErrorMessage(error)
                    Toast.makeText(this, "$errorMessage", Toast.LENGTH_SHORT).show()

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

            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

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