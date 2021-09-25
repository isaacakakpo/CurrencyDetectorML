package com.example.ceditect.listeners

import com.example.ceditect.models.Rates

interface RatesAcqiredListener {
    fun onStarted()
    fun onSuccess(quotes: Rates, succesful:Boolean, terms:String, privacy:String, timestamp: String, source:String)
    fun onFailure(error:String)
}