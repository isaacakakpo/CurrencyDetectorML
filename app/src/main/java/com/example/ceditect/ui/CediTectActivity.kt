package com.example.ceditect.ui

import android.content.Intent
import android.os.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.ceditect.BuildConfig
import com.example.ceditect.R
import com.example.ceditect.databinding.ActivityCediTectBinding
import com.example.ceditect.listeners.DetectListener
import com.example.ceditect.utils.toast
import de.crysxd.cameraXTracker.CameraFragment
import de.crysxd.cameraXTracker.ar.BoundingBoxArOverlay
import de.crysxd.cameraXTracker.ar.PathInterpolator
import de.crysxd.cameraXTracker.ar.PositionTranslator
import kotlinx.android.synthetic.main.activity_cedi_tect.*
import timber.log.Timber


class CediTectActivity : AppCompatActivity() ,
    DetectListener {

    private lateinit var imageAnalyzer: ClassifyCediImageAnalyzer

    private var labeler:TextView? = null

    private var isRun:Boolean = false


    private val vibrator: Vibrator? = null


    private var binding:ActivityCediTectBinding? = null



    private val camera
        get() = supportFragmentManager.findFragmentById(R.id.cameraFragment) as CameraFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)





        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_cedi_tect
        )
        imageAnalyzer = ViewModelProviders.of(this).get(ClassifyCediImageAnalyzer::class.java)
        binding!!.viewmodel = imageAnalyzer
        // Setup logging

        imageAnalyzer.detectListener = this

        imageAnalyzer.downloadCediModel()
        imageAnalyzer.isModelDownloaded()

        home.setOnClickListener {
            Intent(this@CediTectActivity,MainPageActivity::class.java).also {
                startActivity(it)
            }
        }


        if (Timber.treeCount() == 0) {
            Timber.plant(Timber.DebugTree())
        }

        val boundingBoxArOverlay = BoundingBoxArOverlay(this,
            BuildConfig.DEBUG
        )



        camera.imageAnalyzer = imageAnalyzer





        Handler(Looper.getMainLooper()).postDelayed({
            camera.arOverlayView.observe(camera, Observer {
                it.doOnLayout { view ->
                    imageAnalyzer.arObjectTracker
                        .pipe(PositionTranslator(view.width, view.height))
                        .pipe(PathInterpolator())
                        .addTrackingListener(boundingBoxArOverlay)
                }

                it.add(boundingBoxArOverlay)
            })

        },3000)

    }

    override fun onStarted(value:String) {
        labeled.text = value
    }

    override fun onSuccess(labelValue: String,conf:String) {
        if (!isRun){
            
            isRun = true
            labeled.text = labelValue
            vibratePhone()
                Intent(this@CediTectActivity,CurrencyDetectedActivity::class.java).also {
                    it.putExtra("value",labelValue)
                    startActivity(it)
                }
                toast(conf)

        }




    }

    override fun onFailure(error: String) {
        Toast.makeText(
                this,
                error,
                Toast.LENGTH_SHORT
            )
            .show()

        Intent(this,DownloadModelActivity::class.java).also {
            startActivity(intent)
        }

    }

    override fun onNoInternet(error: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onModelDownloadComplete(successMessage: String) {
        Toast.makeText(
                this,
                successMessage,
                Toast.LENGTH_SHORT
            )
            .show()
    }

    override fun onBackPressed() {
        Toast.makeText(
            this,
            "Please use in app buttons",
            Toast.LENGTH_SHORT
        )
            .show()
    }

    fun vibratePhone() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(600, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator?.vibrate (600)
        }
    }

}
