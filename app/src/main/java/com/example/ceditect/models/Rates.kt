package com.example.ceditect.models

import androidx.room.Entity
import androidx.room.PrimaryKey

const val CURRENT_RATES_ID = 0


@Entity
data class Rates(
    val GHS:String,
    val CAD:String,
    val EUR:String,
    val NGN:String,
    val XOF:String,
    val ZAR:String,
    val AED:String,
    val CNY:String





    ){
    @PrimaryKey(autoGenerate = false)
    var uid:Int = CURRENT_RATES_ID

}