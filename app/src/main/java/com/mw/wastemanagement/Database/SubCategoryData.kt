package com.mw.wastemanagement.Database

data class SubCategoryData(
    val sub_category_id: String,
    val sub_category_name: String
){
    override fun toString(): String {
        return sub_category_name
    }
}