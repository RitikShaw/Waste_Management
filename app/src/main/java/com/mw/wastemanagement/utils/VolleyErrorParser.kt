package com.mw.m_leads.model

import com.android.volley.*

object VolleyErrorParser {

    fun getVolleyErrorMessage(error: VolleyError): String {

        if (error is TimeoutError) {
            return "Timeout occur for this function, please try after sometime"
        }
        if (error is NoConnectionError) {
            return "No connection available in your device,please check your internet connection";
        }
        if (error is NetworkError) {
            return "No network available in your device,please check your internet connection"
        }
        if (error is ServerError) {
            return "Sorry,server error occur, please try after sometime"
        }
        return "Something went wrong, please try after sometime"
    }
}