package com.example.ceditect.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.ceditect.R
import com.example.ceditect.models.AppDatabase
import com.example.ceditect.models.Rates
import com.example.ceditect.network.MyApi
import com.example.ceditect.network.NetworkConnectionInterceptor
import com.example.ceditect.network.NoInternetException
import com.example.ceditect.network.QuotesRepository
import com.example.ceditect.ui.currencyvalue.GetExchangeRates
import com.example.ceditect.utils.Coroutines
import com.example.ceditect.utils.toast
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.automl.FirebaseAutoMLRemoteModel
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlinx.android.synthetic.main.activity_download_model.*
import kotlinx.coroutines.launch
import java.io.IOException


class DownloadModelActivity : BaseActivity() {

    private var modelDownloaded = false
    private lateinit var labeler: FirebaseVisionImageLabeler

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
        setContentView(R.layout.activity_download_model)

        val isFirstRun =
            getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                .getBoolean("isModelDownload", false)

        if (isFirstRun) { //show start activity
            startActivity(Intent(this, MainPageActivity::class.java))
            finish()

        }



        Reload.setOnClickListener {
            Intent(this, DownloadModelActivity::class.java).also {
                startActivity(intent)
            }
        }


        var visible: Boolean = false


        val networkConnectionInterceptor = NetworkConnectionInterceptor(this)
        val api = MyApi(networkConnectionInterceptor)
        val repository = QuotesRepository(api)



        var isvisible: Boolean = false


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
                if(response.isSuccessful) {
                    val canada: String = response.body()?.rates?.CAD.toString()
                    val nigeria: String = response.body()?.rates?.NGN.toString()
                    val franc: String = response.body()?.rates?.XOF.toString()
                    val ghana: String = response.body()?.rates?.GHS.toString()
                    val euro: String = response.body()?.rates?.EUR.toString()
                    val arab: String = response.body()?.rates?.AED.toString()
                    val south: String = response.body()?.rates?.ZAR.toString()
                    val chi: String = response.body()?.rates?.CNY.toString()
                    val disclaimer: String = response.body()?.disclaimer.toString()
                    val licence: String = response.body()?.license.toString()
                    val timestamp: String = response.body()?.timestamp.toString()

                    eur = (euro.toDouble() / ghana.toDouble()).toString()
                    can = (canada.toDouble() / ghana.toDouble()).toString()
                    nig = (nigeria.toDouble() / ghana.toDouble()).toString()
                    fran = (franc.toDouble() / ghana.toDouble()).toString()
                    zar = (south.toDouble() / ghana.toDouble()).toString()
                    aed = (arab.toDouble() / ghana.toDouble()).toString()
                    cny = (chi.toDouble() / ghana.toDouble()).toString()
                    ghs = (1 / ghana.toDouble()).toString()
                    disc = disclaimer
                    linc = licence
                    timp = timestamp
                }else{
                    toast(response.toString())
                }
                    val remoteModel = FirebaseAutoMLRemoteModel.Builder("Cedi_Tect_20203793036").build()
                val conditions = FirebaseModelDownloadConditions.Builder()
                    .build()


                FirebaseModelManager.getInstance().isModelDownloaded(remoteModel)
                    .addOnSuccessListener {
                        modelDownloaded = true

                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Failure in downloading Auto ML model.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                if (!modelDownloaded){
                    FirebaseModelManager.getInstance().download(remoteModel, conditions)
                        .addOnCompleteListener {
                            Handler(Looper.getMainLooper()).postDelayed({
                                getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit()
                                    .putBoolean("isModelDownload", true).apply()

                                TransitionManager.beginDelayedTransition(transitionsContainer)
                                visible = !visible
                                ButtonContinue.visibility = View.VISIBLE
                                ImageAlldone.visibility = View.VISIBLE
                                txtAllDone.visibility = View.VISIBLE
                                circularProgressBar.visibility = View.GONE
                                pleasewait.visibility = View.GONE


                            }, 150)
                            Toast.makeText(
                                this,
                                "Model Downloaded.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit()
                                .putBoolean("isModelDownload", true).apply()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                this,
                                "Failure in downloading Auto ML model.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                }else{
                    val rates = Rates(ghs!!,can!!,eur!!,nig!!,fran!!,zar!!,aed!!,cny!!)

                    val intent = Intent(this, MainPageActivity::class.java)

                    launch {
                        AppDatabase(this@DownloadModelActivity).getRatesDao().insertRates(rates)

                        runOnUiThread {
                            toast("All Set")
                            startActivity(intent)
                        }
                    }
                    toast("model ready")

                }




            }catch (e:NoInternetException){
                toast(e.message!!)
                no_data.visibility = View.VISIBLE

                Handler(Looper.getMainLooper()).postDelayed({

                    TransitionManager.beginDelayedTransition(transitionsContainer)
                    isvisible = !isvisible
                    noData.visibility = View.VISIBLE
                    circularProgressBar.visibility = View.GONE
                    pleasewait.visibility = View.GONE


                }, 150)
            }catch (e:IOException){
                toast("Slow Internet Connection Retry Later")
                TransitionManager.beginDelayedTransition(transitionsContainer)
                isvisible = !isvisible
                noData.visibility = View.VISIBLE
                circularProgressBar.visibility = View.GONE
                pleasewait.visibility = View.GONE
                Reload.visibility = View.VISIBLE
            }
        }




        /*  val remoteModel = FirebaseRemoteModel.Builder("Cedi_Tect_20203793036")
              .enableModelUpdates(true)
              .setInitialDownloadConditions(conditions)
              .setUpdatesDownloadConditions(conditions)
              .build()
          FirebaseModelManager.getInstance().registerRemoteModel(remoteModel)

          val labelerOptions = FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder()
              .setRemoteModelName("Cedi_Tect_20203793036")
              .setConfidenceThreshold(0.65f)
              .build()

          labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(labelerOptions)

          FirebaseModelManager.getInstance().downloadRemoteModelIfNeeded(remoteModel)
              .addOnSuccessListener {
                  modelDownloaded = true
                  Handler(Looper.getMainLooper()).postDelayed({

                      TransitionManager.beginDelayedTransition(transitionsContainer)
                      visible = !visible
                      ButtonContinue.visibility = View.VISIBLE
                      ImageAlldone.visibility = View.VISIBLE
                      txtAllDone.visibility = View.VISIBLE
                      circularProgressBar.visibility = View.GONE
                      pleasewait.visibility = View.GONE


                  }, 150)

              }
              .addOnFailureListener {
                  Toast.makeText(
                          this,
                          "Failure in downloading Auto ML model.",
                          Toast.LENGTH_SHORT
                      )
                      .show()
              }
  */

        ButtonContinue.setOnClickListener {
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
        }



    }


}
