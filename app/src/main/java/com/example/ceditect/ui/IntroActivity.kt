package com.example.ceditect.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.example.ceditect.adapters.IntroSliderAdapter
import com.example.ceditect.R
import kotlinx.android.synthetic.main.activity_intro.*
import org.flepper.ceditect.IntroSlide

private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
class IntroActivity : AppCompatActivity() {

    private val introSliderAdapter =
        IntroSliderAdapter(
            listOf(
                IntroSlide(
                    "CediTect",
                    "Detect your Cedi Notes with your android device",
                    R.drawable.busy

                ),
                IntroSlide(
                    "Live Updates",
                    "Get live updates on the cedi Exchange rate when you sign In",
                    R.drawable.live_updates
                ),
                IntroSlide(
                    "Cedi Info",
                    "Know about the More cedi Notes",
                    R.drawable.infor
                )

            )
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        var visible: Boolean = false


        val transitionsContainer =
            findViewById(R.id.transition_group) as ViewGroup

        val ButtonNext =
            transitionsContainer.findViewById<View>(R.id.button_next) as TextView

        val SkipIntro =
            transitionsContainer.findViewById<View>(R.id.skipIntro) as TextView

        introSliderViewPager.adapter = introSliderAdapter
        setupIndicators()
        setCurrentIndicator(0)
        introSliderViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)


                if(introSliderViewPager.currentItem + 1 == introSliderAdapter.itemCount){


                    Handler(Looper.getMainLooper()).postDelayed({

                        TransitionManager.beginDelayedTransition(transitionsContainer)
                        visible = !visible
                        ButtonNext.visibility = View.GONE
                        SkipIntro.visibility = View.GONE
                        allDone.visibility = View.VISIBLE
                        ready.visibility = View.VISIBLE



                    }, 150)




                }

                else if(introSliderViewPager.currentItem + 1 < introSliderAdapter.itemCount){


                    TransitionManager.beginDelayedTransition(transitionsContainer)
                    visible = !visible
                    ButtonNext.visibility = View.VISIBLE
                    SkipIntro.visibility = View.VISIBLE
                    allDone.visibility = View.GONE
                    ready.visibility = View.GONE




                }


            }

        })



        ButtonNext.setOnClickListener {
            if(introSliderViewPager.currentItem + 1 < introSliderAdapter.itemCount){
                introSliderViewPager.currentItem += 1
            }
        }


        skipIntro.setOnClickListener {
            Intent(applicationContext, DownloadModelActivity::class.java).also {
                startActivity(it)
            }
        }

        allDone.setOnClickListener {
            Intent(applicationContext, DownloadModelActivity::class.java).also {
                startActivity(it)
            }
        }


        if (allPermissionsGranted()) {
            Log.d("ok","okay")
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

    }

    private fun setupIndicators(){

        val indicators = arrayOfNulls<ImageView>(introSliderAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        layoutParams.setMargins(8,0,0,8)
        for(i in indicators.indices){
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )

                )
                this?.layoutParams = layoutParams
            }
            indicator_container.addView(indicators[i])
        }

    }

    private fun setCurrentIndicator(index:Int){
        val childCount =  indicator_container.childCount
        for(i in 0 until childCount){
            val imageView = indicator_container[i] as ImageView
            if(i == index){
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            } else{
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )

            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "All Set",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
}
