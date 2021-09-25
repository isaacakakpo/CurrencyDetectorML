package com.example.ceditect.ui

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.ceditect.R
import com.example.ceditect.ui.currencyvalue.ExchangeRatesActivity
import com.example.ceditect.ui.currencyvalue.GetExchangeRates
import com.example.ceditect.ui.currencyvalue.UpdateExchangeRates
import kotlinx.android.synthetic.main.activity_main_page.*


class MainPageActivity : AppCompatActivity() {

    var mMediaPlayer: MediaPlayer? = null
    var mCurrentVideoPosition = 0
    var videoBG:VideoView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        var visible: Boolean = false

        var videoBG = findViewById<VideoView>(R.id.mVideoView)
        // Build your video Uri


        val transitionsContainer =
            findViewById(R.id.container) as ViewGroup

        Handler(Looper.getMainLooper()).postDelayed({

            TransitionManager.beginDelayedTransition(transitionsContainer)
            visible = !visible
            videoBG.visibility =  View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                imageView.visibility =  View.GONE

            },1500)


        }, 1000)

        // Build your video Uri
        val uri: Uri = Uri.parse(
            "android.resource://" // First start with this,
                    + packageName // then retrieve your package name,
                    + "/" // add a slash,
                    + R.raw.nkrumah
        ) // and then finally add your video resource. Make sure it is stored

        // in the raw folder.


        detectCedi.setOnClickListener {
            val intent = Intent(this, CediTectActivity::class.java)
            startActivity(intent)
        }

        exchangeRate.setOnClickListener {
            val intent = Intent(this, ExchangeRatesActivity::class.java)
            startActivity(intent)
        }

        info.setOnClickListener {
            val intent = Intent(this, UpdateExchangeRates::class.java)
            startActivity(intent)
        }
        // Set the new Uri to our VideoView
        videoBG.setVideoURI(uri)
        // Start the VideoView
        videoBG.start()

        videoBG.setOnPreparedListener { mediaPlayer ->
            mMediaPlayer = mediaPlayer
            // We want our video to play over and over so we set looping to true.
            mMediaPlayer!!.isLooping = true
            // We then seek to the current posistion if it has been set and play the video.
            if (mCurrentVideoPosition != 0) {
                mMediaPlayer?.seekTo(mCurrentVideoPosition)
                mMediaPlayer?.start()
            }
        }
    }


    override fun onPause() {
        super.onPause()
        if(mMediaPlayer != null) {
            mCurrentVideoPosition = mMediaPlayer!!.getCurrentPosition()
            mMediaPlayer?.pause()
        }
    }



    override fun onResume() {
        super.onResume()
        if(videoBG != null) {
            mCurrentVideoPosition = mMediaPlayer!!.currentPosition
            mMediaPlayer?.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer?.release()
        mMediaPlayer = null
    }

}
