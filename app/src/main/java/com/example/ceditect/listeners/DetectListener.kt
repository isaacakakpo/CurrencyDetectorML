package com.example.ceditect.listeners

interface DetectListener {
    fun onStarted(value:String)
    fun onSuccess(labelValue:String,confiddence:String)
    fun onFailure(error:String)
    fun onNoInternet(error:String)
    fun onModelDownloadComplete(successMessage:String)
}