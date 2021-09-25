package com.example.ceditect.ui.currencyvalue

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.ceditect.R
import com.example.ceditect.models.AppDatabase
import com.example.ceditect.models.Rates
import com.example.ceditect.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_exchange_rates.*
import kotlinx.coroutines.launch
import kotlin.math.round

class ExchangeRatesActivity : BaseActivity() {


    var eur:Double?= null
    var can:Double?= null
    var nig:Double?= null
    var fran:Double ?= null
    var ghs:Double?= null
    var zar:Double?= null
    var aed:Double?= null
    var cny:Double?= null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exchange_rates)

        launch{

            val storedRates:List<Rates> = AppDatabase(this@ExchangeRatesActivity).getRatesDao().loadAllRates()

            storedRates.forEach {
                ghs = it.GHS.toDouble().round(2)
                can = it.CAD.toDouble().round(2)
                nig = it.NGN.toDouble().round(2)
                fran = it.XOF.toDouble().round(2)
                eur = it.EUR.toDouble().round(2)
                zar = it.ZAR.toDouble().round(2)
                aed  = it.AED.toDouble().round(2)
                cny = it.CNY.toDouble().round(2)


                value_usd.text = ghs.toString()
                value_cad.text = can.toString()
                value_ngr.text = nig.toString()
                value_cfa.text = fran.toString()
                value_sou.text = zar.toString()
                value_aed.text = aed.toString()
                value_eur.text = eur.toString()


            }

        }

    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }

    fun goBack(view:View){
        onBackPressed()
    }
}
