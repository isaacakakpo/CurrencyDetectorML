package com.example.ceditect.response

import com.example.ceditect.models.Rates

data class ExchangerateResponse (
    val disclaimer:String,
    val license:String,
    val timestamp: String,
    val base:String,
    val rates: Rates

)