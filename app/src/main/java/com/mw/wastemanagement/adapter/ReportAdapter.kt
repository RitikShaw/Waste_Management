package com.mw.wastemanagement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mw.wastemanagement.Database.ReportData
import com.mw.wastemanagement.R

class ReportAdapter(
    val onItemClick : ((ReportData)->Unit)
) : RecyclerView.Adapter<ReportAdapter.ViewHolder>() {
    private val diffCalback = object : DiffUtil.ItemCallback<ReportData>(){
        override fun areItemsTheSame(oldItem: ReportData, newItem: ReportData): Boolean {
            return oldItem.survey_id == newItem.survey_id
        }

        override fun areContentsTheSame(oldItem: ReportData, newItem: ReportData): Boolean {
            return oldItem == newItem
        }
    }

    val diffUtil = AsyncListDiffer(this,diffCalback)
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val surveyId : TextView = itemView.findViewById(R.id.surveyId)
        val tvCategory : TextView = itemView.findViewById(R.id.tvCategory)
        val tvSubCategory : TextView = itemView.findViewById(R.id.tvSubCategory)
        val tvSubCategoryOthers : TextView = itemView.findViewById(R.id.tvSubCategoryOthers)
        val tvLocality : TextView = itemView.findViewById(R.id.tvLocality)
        val tvOwner : TextView = itemView.findViewById(R.id.tvOwner)
        val imgLink : ImageView = itemView.findViewById(R.id.imgLink)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_survey_list_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = diffUtil.currentList[position]
        holder.surveyId.text = data.survey_id
        holder.tvCategory.text = data.category
        holder.tvSubCategory.text = data.sub_category
        holder.tvSubCategoryOthers.text = data.sub_category_other
        holder.tvLocality.text = data.locality
        holder.tvOwner.text = data.respondent_owner_name
        holder.imgLink.setOnClickListener {
           // onItemClick.invoke(data)
        }
    }
}