package com.example.ceditect.ui.currencyvalue

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import com.example.ceditect.R
import com.example.ceditect.models.AppDatabase
import com.example.ceditect.models.Rates
import com.example.ceditect.network.MyApi
import com.example.ceditect.network.NetworkConnectionInterceptor
import com.example.ceditect.network.NoInternetException
import com.example.ceditect.network.QuotesRepository
import com.example.ceditect.ui.BaseActivity
import com.example.ceditect.ui.MainPageActivity
import com.example.ceditect.utils.Coroutines
import com.example.ceditect.utils.toast
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlinx.android.synthetic.main.activity_update_exchange_rates.*
import kotlinx.coroutines.launch
import java.io.IOException

class UpdateExchangeRates : BaseActivity() {

    var eur:String ?= null
    var can:String ?= null
    var nig:String ?= null
    var fran:String ?= null
    var ghs:String ?= null
    var zar:String ?= null
    var aed:String ?= null
    var cny:String ?= null
    var disc:String ?= null
    var linc:String ?= null
    var timp:String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_exchange_rates)

        var visible: Boolean = false

        Reload.setOnClickListener {
            Intent(this, UpdateExchangeRates::class.java).also {
                startActivity(intent)
            }
        }

        val networkConnectionInterceptor = NetworkConnectionInterceptor(this)
        val api = MyApi(networkConnectionInterceptor)
        val repository = QuotesRepository(api)

        val transitionsContainer =
            findViewById(R.id.transition_group) as ViewGroup

        val ButtonContinue =
            transitionsContainer.findViewById<View>(R.id.Continue) as Button

        val pleasewait =
            transitionsContainer.findViewById<View>(R.id.pleaseWait) as RelativeLayout

        val ImageAlldone =
            transitionsContainer.findViewById<View>(R.id.imgAlldone) as ImageView


        val circularProgressBar = findViewById<CircularProgressBar>(R.id.circularProgressBar)
        circularProgressBar.apply {
            // Set Progress
            progress = 65f
            // or with animation
            setProgressWithAnimation(65f, 1000) // =1s

            // Set Progress Max
            progressMax = 200f

            // Set ProgressBar Color
            progressBarColor = Color.BLACK
            // or with gradient
            progressBarColorStart = Color.GRAY
            progressBarColorEnd = Color.RED
            progressBarColorDirection = CircularProgressBar.GradientDirection.TOP_TO_BOTTOM

            // Set background ProgressBar Color
            backgroundProgressBarColor = Color.GRAY
            // or with gradient
            backgroundProgressBarColorStart = Color.WHITE
            backgroundProgressBarColorEnd = Color.RED
            backgroundProgressBarColorDirection = CircularProgressBar.GradientDirection.TOP_TO_BOTTOM

            // Set Width
            progressBarWidth = 7f // in DP
            backgroundProgressBarWidth = 3f // in DP

            // Other
            roundBorder = true
            startAngle = 180f
            progressDirection = CircularProgressBar.ProgressDirection.TO_RIGHT
        }


        Coroutines.main {

            try {
                val response = repository.fetchQuotes()
                if(response.isSuccessful){
                    val canada:String = response.body()?.rates?.CAD.toString()
                    val nigeria:String = response.body()?.rates?.NGN.toString()
                    val franc:String = response.body()?.rates?.XOF.toString()
                    val ghana:String = response.body()?.rates?.GHS.toString()
                    val euro:String = response.body()?.rates?.EUR.toString()
                    val arab:String = response.body()?.rates?.AED.toString()
                    val south:String = response.body()?.rates?.ZAR.toString()
                    val chi:String = response.body()?.rates?.CNY.toString()
                    val disclaimer:String = response.body()?.disclaimer.toString()
                    val licence:String = response.body()?.license.toString()
                    val timestamp:String = response.body()?.timestamp.toString()

                    eur = (euro.toDouble()/ghana.toDouble()).toString()
                    can = (canada.toDouble()/ghana.toDouble()).toString()
                    nig = (nigeria.toDouble()/ghana.toDouble()).toString()
                    fran = (franc.toDouble()/ghana.toDouble()).toString()
                    zar = (south.toDouble()/ghana.toDouble()).toString()
                    aed = (arab.toDouble()/ghana.toDouble()).toString()
                    cny = (chi.toDouble()/ghana.toDouble()).toString()
                    ghs = (1/ghana.toDouble()).toString()
                    disc = disclaimer
                    linc = licence
                    timp = timestamp




                    Handler(Looper.getMainLooper()).postDelayed({

                        TransitionManager.beginDelayedTransition(transitionsContainer)
                        visible = !visible
                        ButtonContinue.visibility = View.VISIBLE
                        ImageAlldone.visibility = View.VISIBLE
                        txtAllDone.visibility = View.VISIBLE
                        circularProgressBar.visibility = View.GONE
                        pleasewait.visibility = View.GONE
                        toast("Exchange Rates Updated")


                    }, 150)
                }else{
                    toast(response.toString())
                }

            }catch (e: NoInternetException){
                toast(e.message!!)

                Handler(Looper.getMainLooper()).postDelayed({

                    TransitionManager.beginDelayedTransition(transitionsContainer)
                    visible = !visible
                    getting.visibility = View.VISIBLE
                    noData.visibility = View.VISIBLE
                    circularProgressBar.visibility = View.GONE
                    pleasewait.visibility = View.GONE


                }, 150)
            }catch (e: IOException){
                toast("Slow Internet Connection Retry Later")
                TransitionManager.beginDelayedTransition(transitionsContainer)
                visible = !visible
                noData.visibility = View.VISIBLE
                circularProgressBar.visibility = View.GONE
                pleasewait.visibility = View.GONE
                Reload.visibility = View.VISIBLE
            }

        }



        ButtonContinue.setOnClickListener {
            val rates = Rates(ghs!!,can!!,eur!!,nig!!,fran!!,zar!!,aed!!,cny!!)
            val intent = Intent(this, MainPageActivity::class.java)


            launch {
                AppDatabase(this@UpdateExchangeRates).getRatesDao().insertRates(rates)

                runOnUiThread {
                    toast("All Set")
                    startActivity(intent)
                }
            }
        }


    }




}
