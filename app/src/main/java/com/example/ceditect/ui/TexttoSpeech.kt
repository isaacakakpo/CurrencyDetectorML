package com.example.ceditect.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.example.ceditect.R
import kotlinx.android.synthetic.main.activity_textto_speech.*
import java.util.*

class TexttoSpeech : AppCompatActivity() {
    lateinit var mTTS: TextToSpeech


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_textto_speech)
        mTTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR){
                //if there is no error then set language
                mTTS.language = Locale.UK
            }
        })

        //speak button click
        speakBtn.setOnClickListener {
            //get text from edit text
            val toSpeak = textEt.text.toString()
            if (toSpeak == ""){
                //if there is no text in edit text
                Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show()
            }
            else{
                //if there is text in edit text
                Toast.makeText(this, toSpeak, Toast.LENGTH_SHORT).show()
                mTTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null)
            }
        }

        //stop speaking button click
        stopBtn.setOnClickListener {
            if (mTTS.isSpeaking){
                //if speaking then stop
                mTTS.stop()
                //mTTS.shutdown()
            }
            else{
                //if not speaking
                Toast.makeText(this, "Not speaking", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        if (mTTS.isSpeaking){
            //if speaking then stop
            mTTS.stop()
            //mTTS.shutdown()
        }
        super.onPause()
    }
}