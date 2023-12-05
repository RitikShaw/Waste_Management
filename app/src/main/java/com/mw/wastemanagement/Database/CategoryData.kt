package com.mw.wastemanagement.Database

data class CategoryData(
    val category_id: String,
    val category_name: String
){
    override fun toString(): String {
        return category_name
    }
}