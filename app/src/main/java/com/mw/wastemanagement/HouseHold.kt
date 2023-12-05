package com.mw.wastemanagement

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Radio
import android.util.Log
import android.util.Log.e
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.gson.Gson
import com.mw.m_leads.MySingleton
import com.mw.m_leads.model.VolleyErrorParser
import com.mw.utills.SharedPrefs
import com.mw.vendorsurvey.utils.Constant
import com.mw.wastemanagement.Database.ImageData
import com.mw.wastemanagement.Database.SubCategoryData
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HouseHold : AppCompatActivity() {
    private val cameraPermissionCode = 100
    private lateinit var selectedImageBitmap: Bitmap
    lateinit var back: ImageView
    lateinit var submit: TextView
    lateinit var uploadbtn: TextView
    private val imageContainer: LinearLayout by lazy { findViewById(R.id.imageContainer) }
    private var imageViews = mutableListOf<ImageView>()
    private lateinit var imageSignatureList : ArrayList<String>
    private lateinit var imageData: ArrayList<ImageData>

    private lateinit var scrollView : ScrollView



    private val takePicturesLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val file = createImageFile()
                val type = getFileExtension(file.path)
                saveImageToFile(result.data?.extras?.get("data") as Bitmap, file)
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                addImageToContainer(imageBitmap)
                val encodedImage = convertBitmapToBase64(imageBitmap)
                imageData.add(ImageData(encodedImage, type))
            }
        }

    private val pickPicturesLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                val imageStream = contentResolver.openInputStream(selectedImageUri!!)

                val mime = MimeTypeMap.getSingleton()
                val type = mime.getExtensionFromMimeType(contentResolver.getType(selectedImageUri)).toString()
                imageSignatureList.add(type)
                val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
                addImageToContainer(imageBitmap)
                val encodedImage = convertBitmapToBase64(imageBitmap)
                imageData.add(ImageData(encodedImage, type))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_household)
        supportActionBar!!.hide()

        imageSignatureList = ArrayList()
        imageData = ArrayList()

        submit = findViewById(R.id.submit)
        back = findViewById(R.id.back)
        scrollView = findViewById(R.id.scrollView)
        val wasteGrp: RadioGroup = findViewById(R.id.wastebinsgroup)
        val yeswasteLayout: LinearLayout = findViewById(R.id.yeswastelayout)
        val sewagedisposalgrp: RadioGroup= findViewById(R.id.sewagedisposalgrp)
        val othersewagedisposalLayout: LinearLayout = findViewById(R.id.othersewagedisposalLayout)
        /*val sanitaryToiletGrp: RadioGroup = findViewById(R.id.sanitarytoiletsgrp)
        val sanitaryToiletLayout: LinearLayout = findViewById(R.id.sanitarytoiletsLayout)*/
        uploadbtn = findViewById(R.id.uploadpicture)
        var garbageTruckGrp: RadioGroup = findViewById(R.id.garbagetruckgrp)
        var lessthanaweekLayout: LinearLayout = findViewById(R.id.lessthanaweekLayout)
        var stoppingpointgrp: RadioGroup = findViewById(R.id.stoppingpointgrp)
        var stoppingpointLayout: LinearLayout = findViewById(R.id.stoppingpointLayout)
        var septictankfullgrp: RadioGroup = findViewById(R.id.setictankfullgrp)
        var yesseptictankfullLayout: LinearLayout = findViewById(R.id.yesseptictankfullLayout)
        var etpmethod: RadioGroup = findViewById(R.id.etpmethodgrp)
        var yesetpmethodknowLayout: LinearLayout = findViewById(R.id.yesetpmethodknowlayout)
        var etpinstalledgrp: RadioGroup = findViewById(R.id.etpinstalledgrp)
        var etpmethodtypeLayout: LinearLayout = findViewById(R.id.etpmethodtypeLayout)
        val etpmethodspecify: EditText = findViewById(R.id.etpmethodspecify)
        val usedwaterdisposalgrp: RadioGroup = findViewById(R.id.usedwaterdisposal)
        val waterbodiesLayout: LinearLayout = findViewById(R.id.waterbodiesLayout)
        val waterbodiesoutfall: EditText = findViewById(R.id.waterbodiesoutfalls)
        val solidwastedisposalgrp: RadioGroup = findViewById(R.id.solidwastedisposalGroup)
        val otherssolidwastedisposalLayout: LinearLayout = findViewById(R.id.otherssolidwastelayout)

        val locality : EditText = findViewById(R.id.locality)
        val name : EditText = findViewById(R.id.name)
        val holdingno : EditText = findViewById(R.id.holdingno)
        val householdsno : EditText = findViewById(R.id.householdsno)
        val familymember : EditText = findViewById(R.id.familymember)
        val otherssolidwastedisposal : EditText = findViewById(R.id.otherssolidwastedisposal)
        //val wastebinsno : EditText = findViewById(R.id.wastebinsno)
        val wastebinsno : RadioGroup = findViewById(R.id.wastebinsno)
        val remarks : EditText = findViewById(R.id.remarks)
        val solidwastemgmtgrp : RadioGroup = findViewById(R.id.solidwastemgmtgrp)
        val rgSeggregateWaste : RadioGroup = findViewById(R.id.rgSeggregateWaste)
        val lessthanaweek : EditText = findViewById(R.id.lessthanaweek)
        //val toilet : EditText = findViewById(R.id.toilet)
        val reasongrp : RadioGroup = findViewById(R.id.reasongrp)
        //val drainagesystemgrp : RadioGroup = findViewById(R.id.drainagesystemgrp)
        val othersewagedisposal : EditText = findViewById(R.id.othersewagedisposal)
        val etStopReasonOther : EditText = findViewById(R.id.etStopReasonOther)
        val etUploadImgDifficulty : EditText = findViewById(R.id.etUploadImgDifficulty)
        val setictankfullissuegrp : RadioGroup = findViewById(R.id.setictankfullissuegrp)
        val rgTrapSolidWaste : RadioGroup = findViewById(R.id.rgTrapSolidWaste)
        val houseGroup : RadioGroup = findViewById(R.id.houseGroup)
        val setictankgrp : RadioGroup = findViewById(R.id.setictankgrp)
        val rbOthers : RadioButton = findViewById(R.id.rbOthers)
        val stoppingpointOtherLayout : LinearLayout = findViewById(R.id.stoppingpointOtherLayout)

        wasteGrp.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = findViewById<RadioButton>(checkedId)
            if (checkedRadioButton.text.toString() == "Yes") {
               yeswasteLayout.visibility = View.VISIBLE
            }
            else{
                yeswasteLayout.visibility = View.GONE
            }
        }

        solidwastedisposalgrp.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = findViewById<RadioButton>(checkedId)
            if (checkedRadioButton.text.toString() == "Others") {
                otherssolidwastedisposalLayout.visibility = View.VISIBLE
            }
            else{
                otherssolidwastedisposalLayout.visibility = View.GONE
            }
        }

        sewagedisposalgrp.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = findViewById<RadioButton>(checkedId)
            if (checkedRadioButton.text.toString() == "Others") {
                othersewagedisposalLayout.visibility = View.VISIBLE
            }
            else{
                othersewagedisposalLayout.visibility = View.GONE
            }
        }


        /*sanitaryToiletGrp.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = findViewById<RadioButton>(checkedId)
            if (checkedRadioButton.text.toString() == "No") {
                sanitaryToiletLayout.visibility = View.VISIBLE
            }
            else{
                sanitaryToiletLayout.visibility = View.GONE
            }
        }*/

        uploadbtn.setOnClickListener {
            if (imageData.size<4){
                if (checkCameraPermission()) {
                    showPictureDialog()
                } else {
                    requestCameraPermission()
                }
            }
            else{
                Toast.makeText(this,"Can only add upto 4 images ",Toast.LENGTH_SHORT).show()
            }

            imageContainer.visibility = View.VISIBLE
        }

        garbageTruckGrp.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = findViewById<RadioButton>(checkedId)
            if (checkedRadioButton.text.toString() == "Less than once a week") {
                lessthanaweekLayout.visibility = View.VISIBLE
            }
            else{
                lessthanaweekLayout.visibility = View.GONE
            }
        }

        stoppingpointgrp.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = findViewById<RadioButton>(checkedId)
            if (checkedRadioButton.text.toString() == "No") {
                stoppingpointLayout.visibility = View.VISIBLE
            }
            else{
                stoppingpointLayout.visibility = View.GONE
            }
        }

        septictankfullgrp.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = findViewById<RadioButton>(checkedId)
            if (checkedRadioButton.text.toString() == "Yes") {
                yesseptictankfullLayout.visibility = View.VISIBLE
            }
            else{
                yesseptictankfullLayout.visibility = View.GONE
            }
        }


        etpmethod.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = findViewById<RadioButton>(checkedId)
            if (checkedRadioButton.text.toString() == "Yes") {
                yesetpmethodknowLayout.visibility = View.VISIBLE
            }
            else{
                yesetpmethodknowLayout.visibility = View.GONE
                etpmethodtypeLayout.visibility = View.GONE
              //  etpinstalledgrp.clearCheck()
                etpmethodspecify.setText("")
            }

        }

        etpinstalledgrp.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = findViewById<RadioButton>(checkedId)
            if (checkedRadioButton.text.toString() == "Yes") {
                etpmethodtypeLayout.visibility = View.VISIBLE
            }
            else{
                etpmethodtypeLayout.visibility = View.GONE
                etpmethodspecify.setText("")
            }
        }

        usedwaterdisposalgrp.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = findViewById<RadioButton>(checkedId)
            if (checkedRadioButton.text.toString() == "Others") {
                waterbodiesLayout.visibility = View.VISIBLE
            }
            else{
                waterbodiesLayout.visibility = View.GONE
                waterbodiesoutfall.setText("")
            }
        }
        reasongrp.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = findViewById<RadioButton>(checkedId)
            if (checkedRadioButton.text.toString() == "Others") {
                stoppingpointOtherLayout.visibility = View.VISIBLE
            }
            else{
                stoppingpointOtherLayout.visibility = View.GONE
                etStopReasonOther.setText("")
            }
        }

        back.setOnClickListener {
            navigateBack()
        }


        submit.setOnClickListener {

            if (locality.text.toString().trim().isEmpty()) {
                locality.requestFocus()
                locality.error = "Locality cannot be empty"
                return@setOnClickListener
            } else if (name.text.toString().trim().isEmpty()) {
                name.requestFocus()
                name.error = "Name cannot be empty"
                return@setOnClickListener
            }
            else if (houseGroup.checkedRadioButtonId==-1) {
                Toast.makeText(this,"Choose Owner/Tenant type",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if (holdingno.text.toString().trim().isEmpty()) {
                holdingno.requestFocus()
                holdingno.error = "Holding no. cannot be empty"
                return@setOnClickListener
            }
            else if (householdsno.text.toString().trim().isEmpty()) {
                householdsno.requestFocus()
                householdsno.error = "Household no. cannot be empty"
                return@setOnClickListener
            }
            else if (familymember.text.toString().trim().isEmpty()) {
                familymember.requestFocus()
                familymember.error = "Family members cannot be empty"
                return@setOnClickListener
            }
            else if (solidwastedisposalgrp.checkedRadioButtonId==-1) {
                Toast.makeText(this,"Choose how do you dispose solid waste",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if (rbOthers.isChecked && otherssolidwastedisposal.text.trim().isEmpty()) {
                otherssolidwastedisposal.requestFocus()
                otherssolidwastedisposal.error = "Specify how do you dispose solid waste"
                return@setOnClickListener
            }
            else if (solidwastemgmtgrp.checkedRadioButtonId==-1) {
                Toast.makeText(this,"Chose whether you are aware of the solid waste management rules 2016",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if (wasteGrp.checkedRadioButtonId==-1) {
                Toast.makeText(this,"Choose whether you have waste/storage bins in your house",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if (findViewById<RadioButton>(wasteGrp.checkedRadioButtonId).text.toString().trim()=="Yes"
                && wastebinsno.checkedRadioButtonId==-1) {
                Toast.makeText(this,"Choose how many waste/storage bins present in your house",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if (rgSeggregateWaste.checkedRadioButtonId==-1) {
                Toast.makeText(this,"Choose whether you segregate the waste or not",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if (garbageTruckGrp.checkedRadioButtonId==-1) {
                Toast.makeText(this,"Choose how many times in a week does garbage truck collect waste",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if (findViewById<RadioButton>(garbageTruckGrp.checkedRadioButtonId).text.toString().trim()=="Less than once a week"
                && lessthanaweek.text.toString().trim().isEmpty()) {
                lessthanaweek.requestFocus()
                Toast.makeText(this,"Specify how many times does garbage truck collect waste",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (stoppingpointgrp.checkedRadioButtonId==-1) {
                Toast.makeText(this,"Choose if the stopping point of the garbage collection truck is easily accessible",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if (findViewById<RadioButton>(stoppingpointgrp.checkedRadioButtonId).text.toString().trim()=="No"
                && reasongrp.checkedRadioButtonId==-1) {
                Toast.makeText(this,"Choose reason for stopping point of garbage truck not being accessible",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if (findViewById<RadioButton>(reasongrp.checkedRadioButtonId).text.toString().trim()=="Others"
                && etStopReasonOther.text.toString().trim().isEmpty()) {
                Toast.makeText(this,"Specify reason for stopping point of garbage truck not being accessible",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            /*else if (sanitaryToiletGrp.checkedRadioButtonId==-1) {
                Toast.makeText(this,"Choose if you have provision for sanitary toilets in the shopping premise/ building",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if (findViewById<RadioButton>(sanitaryToiletGrp.checkedRadioButtonId).text.toString().trim()=="No"
                && toilet.text.toString().trim().isEmpty()) {
                toilet.requestFocus()
                Toast.makeText(this,"Specify reason for no provision of sanitary toilets",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if (drainagesystemgrp.checkedRadioButtonId==-1) {
                Toast.makeText(this,"Choose if there a proper drainage system in the vicinity of your shop",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }*/
            else if (sewagedisposalgrp.checkedRadioButtonId==-1) {
                Toast.makeText(this,"Choose how you dispose sewage from your toilets",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if (findViewById<RadioButton>(sewagedisposalgrp.checkedRadioButtonId).text.toString().trim()=="Others"
                && othersewagedisposal.text.toString().isEmpty()) {
                othersewagedisposal.requestFocus()
                Toast.makeText(this,"Specify how you dispose sewage from your toilets",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if (setictankgrp.checkedRadioButtonId==-1) {
                Toast.makeText(this,"Choose condition of the septic tank",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if (septictankfullgrp.checkedRadioButtonId==-1) {
                Toast.makeText(this,"Choose if there is any issue in cleaning the septic tanks when full",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if (findViewById<RadioButton>(septictankfullgrp.checkedRadioButtonId).text.toString().trim()=="Yes"
                && setictankfullissuegrp.checkedRadioButtonId==-1) {
                Toast.makeText(this,"Choose issue in cleaning the septic tanks",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if (etpmethod.checkedRadioButtonId==-1) {
                Toast.makeText(this,"Choose if you are aware of the effluent treatment tank system",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if (findViewById<RadioButton>(etpmethod.checkedRadioButtonId).text.toString().trim()=="Yes"
                && etpinstalledgrp.checkedRadioButtonId==-1) {
                Toast.makeText(this,"Choose whether you installed it or not",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if (usedwaterdisposalgrp.checkedRadioButtonId==-1) {
                Toast.makeText(this,"Choose how you dispose used water coming out from your household",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if (findViewById<RadioButton>(usedwaterdisposalgrp.checkedRadioButtonId).text.toString().trim()=="Others"
                && waterbodiesoutfall.text.toString().isEmpty()) {
                waterbodiesoutfall.requestFocus()
                Toast.makeText(this,"Specify how you dispose used water",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if (rgTrapSolidWaste.checkedRadioButtonId==-1) {
                Toast.makeText(this,"Choose if you practice trapping of solid waste before you discharge the used water from your household",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            submit.setBackgroundResource(R.drawable.lightgreenbutton)
            Handler().postDelayed({
                submit.setBackgroundResource(R.drawable.orangebtn)
            }, 500)
            submit.showProgress()
            val category = SharedPrefs(this).category
            val subCategory = SharedPrefs(this).sub_category
            val others = SharedPrefs(this).others
            val jsonObject = JSONObject().apply {
                put("req", "add_survey")
                put("token", SharedPrefs(this@HouseHold).token)
                put("category", category)
                put("sub_category", subCategory)
                put("sub_category_other", others)
                put("locality", locality.text.toString())
                put("holding_respondent_name", name.text.toString())
                put("holding_owner_tenant", findViewById<RadioButton>(houseGroup.checkedRadioButtonId).text.toString())
                put("holding_number", holdingno.text.toString())
                put("holding_household_number", householdsno.text.toString().trim())
                put("holding_household_num_of_member", familymember.text.toString().trim())
                put("commercial_owner_name", "")
                put("institution_name", "")
                put("institution_respondent_name", "")
                put("institution_respondent_rank", "")
                put("institution_strength", "")
                put("bio_establishment_name", "")
                put("bio_respondent_name", "")
                put("bio_respondent_rank", "")
                put("bio_bed_number", "")
                put("dispose_solid_waste", findViewById<RadioButton>(solidwastedisposalgrp.checkedRadioButtonId).text.toString())
                if (rbOthers.isChecked){
                    put("dispose_solid_waste_other", otherssolidwastedisposal.text.toString().trim())
                }
                else{
                    put("dispose_solid_waste_other", "")
                }
                put("aware_swm_bmw_rules", findViewById<RadioButton>(solidwastemgmtgrp.checkedRadioButtonId).text.toString())
                put("abide_swm_bmw_rules", "")
                put("have_waste_storage_bin", findViewById<RadioButton>(wasteGrp.checkedRadioButtonId).text.toString())
                if (findViewById<RadioButton>(wasteGrp.checkedRadioButtonId).text.toString()=="Yes"){
                    put("how_many_bin", findViewById<RadioButton>(wastebinsno.checkedRadioButtonId).text.toString().trim())
                }
                else{
                    put("how_many_bin", "")
                }
                put("dy_segregate_waste", findViewById<RadioButton>(rgSeggregateWaste.checkedRadioButtonId).text.toString())
                put("time_sw_collection_truck", findViewById<RadioButton>(garbageTruckGrp.checkedRadioButtonId).text.toString())
                if (findViewById<RadioButton>(garbageTruckGrp.checkedRadioButtonId).text.toString()=="Less than once a week"){
                    put("specify_sw_collection_less_week", lessthanaweek.text.toString().trim())
                }
                else{
                    put("specify_sw_collection_less_week", "")
                }
                put("stop_point_truck_easily_accessible", findViewById<RadioButton>(stoppingpointgrp.checkedRadioButtonId).text.toString())
                if (findViewById<RadioButton>(stoppingpointgrp.checkedRadioButtonId).text.toString()=="No"){
                    put("if_not_accessible_reason", findViewById<RadioButton>(reasongrp.checkedRadioButtonId).text.toString())
                    if (findViewById<RadioButton>(reasongrp.checkedRadioButtonId).text.toString()=="Others"){
                        put("if_not_accessible_reason_other", etStopReasonOther.text.toString().trim())
                    }
                    else{
                        put("if_not_accessible_reason_other", "")
                    }
                }
                else{
                    put("if_not_accessible_reason", "")
                    put("if_not_accessible_reason_other", "")
                }

                put("provision_for_sanitary_toilet", "")
                put("where_dy_use_toilet", "")
                /*if (findViewById<RadioButton>(sanitaryToiletGrp.checkedRadioButtonId).text.toString()=="No"){
                    put("where_dy_use_toilet", toilet.text.toString().trim())
                }
                else{
                    put("where_dy_use_toilet", "")
                }*/
                //put("is_proper_drainage_system", findViewById<RadioButton>(drainagesystemgrp.checkedRadioButtonId).text.toString())
                put("is_proper_drainage_system", "")
                put("hdy_dispose_sewage", findViewById<RadioButton>(sewagedisposalgrp.checkedRadioButtonId).text.toString())
                if (findViewById<RadioButton>(sewagedisposalgrp.checkedRadioButtonId).text.toString()=="Others"){
                    put("specify_dispose_sewage_other", othersewagedisposal.text.toString().trim())
                }
                else{
                    put("specify_dispose_sewage_other", othersewagedisposal.text.toString().trim())
                }
                put("septic_tank_contition", findViewById<RadioButton>(setictankgrp.checkedRadioButtonId).text.toString())
                when(imageData.size){
                    0->{
                        put("septic_tank_image_0", "")
                        put("septic_tank_image_0_ext", "")
                        put("septic_tank_image_1", "")
                        put("septic_tank_image_1_ext", "")
                        put("septic_tank_image_2", "")
                        put("septic_tank_image_2_ext", "")
                        put("septic_tank_image_3", "")
                        put("septic_tank_image_3_ext", "")
                    }
                    1->{
                        put("septic_tank_image_0", imageData[0].encodedData)
                        put("septic_tank_image_0_ext", imageData[0].signatureData)
                        put("septic_tank_image_1", "")
                        put("septic_tank_image_1_ext", "")
                        put("septic_tank_image_2", "")
                        put("septic_tank_image_2_ext", "")
                        put("septic_tank_image_3", "")
                        put("septic_tank_image_3_ext", "")
                    }
                    2->{
                        put("septic_tank_image_0", imageData[0].encodedData)
                        put("septic_tank_image_0_ext", imageData[0].signatureData)
                        put("septic_tank_image_1", imageData[1].encodedData)
                        put("septic_tank_image_1_ext", imageData[1].signatureData)
                        put("septic_tank_image_2", "")
                        put("septic_tank_image_2_ext", "")
                        put("septic_tank_image_3", "")
                        put("septic_tank_image_3_ext", "")
                    }
                    3->{
                        put("septic_tank_image_0", imageData[0].encodedData)
                        put("septic_tank_image_0_ext", imageData[0].signatureData)
                        put("septic_tank_image_1", imageData[1].encodedData)
                        put("septic_tank_image_1_ext", imageData[1].signatureData)
                        put("septic_tank_image_2", imageData[2].encodedData)
                        put("septic_tank_image_2_ext", imageData[2].signatureData)
                        put("septic_tank_image_3", "")
                        put("septic_tank_image_3_ext", "")
                    }
                    4 -> {
                        put("septic_tank_image_0", imageData[0].encodedData)
                        put("septic_tank_image_0_ext", imageData[0].signatureData)
                        put("septic_tank_image_1", imageData[1].encodedData)
                        put("septic_tank_image_1_ext", imageData[1].signatureData)
                        put("septic_tank_image_2", imageData[2].encodedData)
                        put("septic_tank_image_2_ext", imageData[2].signatureData)
                        put("septic_tank_image_3", imageData[3].encodedData)
                        put("septic_tank_image_3_ext", imageData[3].signatureData)
                    }
                }
                put("specify_image_difficulty", etUploadImgDifficulty.text.toString().trim())
                put("tank_full_issue_cleaning", findViewById<RadioButton>(septictankfullgrp.checkedRadioButtonId).text.toString())
                if (findViewById<RadioButton>(septictankfullgrp.checkedRadioButtonId).text.toString()=="Yes"){
                    put("type_of_issue", findViewById<RadioButton>(setictankfullissuegrp.checkedRadioButtonId).text.toString())
                }
                else{
                    put("type_of_issue", "")
                }
                put("aware_of_ett_system", findViewById<RadioButton>(etpmethod.checkedRadioButtonId).text.toString())
                if (findViewById<RadioButton>(etpmethod.checkedRadioButtonId).text.toString()=="Yes"){
                    put("hy_installed_ett_system", findViewById<RadioButton>(etpinstalledgrp.checkedRadioButtonId).text.toString())
                    if (findViewById<RadioButton>(etpinstalledgrp.checkedRadioButtonId).text.toString()=="Yes"){
                        put("type_of_methodology", etpmethodspecify.text.toString().trim())
                    }
                    else{
                        put("type_of_methodology", "")
                    }
                }
                else{
                    put("hy_installed_ett_system", "")
                    put("type_of_methodology", "")
                }
                put("hdy_dispose_used_water", findViewById<RadioButton>(usedwaterdisposalgrp.checkedRadioButtonId).text.toString())
                if (findViewById<RadioButton>(usedwaterdisposalgrp.checkedRadioButtonId).text.toString()=="Others"){
                    put("specify_dispose_used_water_other", waterbodiesoutfall.text.toString().trim())
                }
                else{
                    put("specify_dispose_used_water_other", "")
                }
                put("dy_practice_trapping_sw", findViewById<RadioButton>(rgTrapSolidWaste.checkedRadioButtonId).text.toString())
                put("remarks", remarks.text.toString().trim())
                put("latitude", "")
                put("longitude", "")
            }
            e("saveObj",jsonObject.toString())
            saveData(jsonObject)

            // Add any additional actions you want to perform on click

            // To revert the background to the normal state after a certain delay (e.g., 500 milliseconds)

            //successDialog("Survey submitted")
        }

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("PNG_${timeStamp}_", ".png", storageDir)
    }

    private fun saveImageToFile(bitmap: Bitmap, file: File) {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
    }

    private fun getFileExtension(filePath: String?): String {
        if (filePath == null || filePath.isEmpty()) return ""

        val lastDot = filePath.lastIndexOf(".")
        return if (lastDot >= 0) {
            filePath.substring(lastDot + 1)
        } else {
            ""
        }
    }

    private fun saveData(jsonObject: JSONObject) {
        val request = object : StringRequest(Method.POST, Constant.BASE_URL,object : Response.Listener<String?> {
            override fun onResponse(response: String?) {
                try {
                    if (!response.isNullOrEmpty()){
                        Log.e("response", response)
                        val responseObj = JSONObject(response)
                        val message = responseObj.getString("response")
                        val survey_no = responseObj.getString("survey_no")
                        successDialog("$message \n Survey No:$survey_no")
                        submit.hideProgress()
                    }
                }
                catch (e:Exception){
                    submit.hideProgress()
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
                return jsonObject.toString().toByteArray()
            }
        }
        MySingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            cameraPermissionCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showPictureDialog()
            } else {
                // Handle permission denied
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                // You may display a message or take appropriate action
            }
        }
    }


    private fun showPictureDialog() {
        // Implement your own logic or use a library for choosing between camera and gallery
        // For simplicity, we'll use the camera for demonstration purposes
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val pickPictureIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Add Photo!")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> takePicturesLauncher.launch(takePictureIntent)
                1 -> pickPicturesLauncher.launch(pickPictureIntent)
                // Handle Cancel if needed
            }
        }
        builder.show()
    }

    private fun addImageToContainer(bitmap: Bitmap) {
        // Set the desired dimensions for the ImageView
        val targetWidth = resources.getDimensionPixelSize(R.dimen.image_width)
        val targetHeight = resources.getDimensionPixelSize(R.dimen.image_height)

        // Scale the bitmap to fit the desired dimensions
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false)

        // Dynamically add ImageView to the LinearLayout with horizontal orientation
        val imageView = ImageView(this)
        imageView.setImageBitmap(scaledBitmap)
        imageView.layoutParams = LinearLayout.LayoutParams(
            targetWidth,
            targetHeight
        ).apply {
            marginEnd = 16 // Add margin between images
        }
        imageContainer.addView(imageView)
        imageViews.add(imageView)

        // Set click listener for each ImageView for deletion or editing
        imageView.setOnClickListener {
            removeImageFromContainer(imageView)
        }
    }

    private fun removeImageFromContainer(imageView: ImageView) {
        imageContainer.removeView(imageView)
        imageViews.remove(imageView)
    }


    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
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

            Handler().postDelayed({
                btnOk.setBackgroundResource(R.drawable.lightgreenbutton)
            }, 500)

            val intent = Intent(this, Home::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()

        }
    }


    override fun onBackPressed() {
        navigateBack()
    }

    private fun navigateBack() {
        var i = Intent(this, Home::class.java)
        startActivity(i)
        overridePendingTransition(
            R.anim.anim_slide_in_left,
            R.anim.anim_slide_in_right
        )
        finish()

    }
}