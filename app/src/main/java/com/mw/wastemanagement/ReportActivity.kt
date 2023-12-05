package com.mw.wastemanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request.Method
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.mw.m_leads.MySingleton
import com.mw.m_leads.model.VolleyErrorParser
import com.mw.utills.SharedPrefs
import com.mw.vendorsurvey.utils.Constant
import com.mw.wastemanagement.Database.ReportData
import com.mw.wastemanagement.adapter.ReportAdapter
import org.json.JSONArray

class ReportActivity : AppCompatActivity() {
    lateinit var back: ImageView
    lateinit var rvReport : RecyclerView
    private lateinit var reportAdapter : ReportAdapter
    private lateinit var reportData : List<ReportData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        supportActionBar!!.hide()

        back = findViewById(R.id.back)
        rvReport = findViewById(R.id.rvReport)

        reportData = mutableListOf()
        reportAdapter = ReportAdapter(
            onItemClick = {
                startActivity(Intent(this,CompleteFormDataView::class.java))
            }
        )
        rvReport.layoutManager = LinearLayoutManager(this)
        rvReport.adapter = reportAdapter
        val token = SharedPrefs(this).token
        getReportData(token)

        back.setOnClickListener {
            navigateBack()
        }
    }

    private fun getReportData(token: String) {
        val url = Constant.BASE_URL + "?req=list_survey&token=$token"
        val request = StringRequest(Method.GET,url,
            { response ->
                try {
                    if (response!=null){
                        reportData = listOf(
                            *Gson().fromJson(JSONArray(response).toString(),Array<ReportData>::class.java)
                        )
                        reportAdapter.diffUtil.submitList(reportData)
                        reportAdapter.notifyDataSetChanged()
                    }
                } catch (e:Exception){
                    e.printStackTrace()
                }
            }, {
            val error = VolleyErrorParser.getVolleyErrorMessage(it)
            Toast.makeText(this,error,Toast.LENGTH_SHORT).show()
        })
        MySingleton.getInstance(this).addToRequestQueue(request)


    }

    override fun onBackPressed() {
        navigateBack()
    }

    private fun navigateBack() {
        var intent = Intent(this, Home::class.java)
        startActivity(intent)
        overridePendingTransition(
            R.anim.anim_slide_in_left,
            R.anim.anim_slide_in_right
        )
        finish()
    }
}