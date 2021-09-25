package com.example.ceditect.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ceditect.R
import com.example.ceditect.network.MyApi
import com.example.ceditect.network.QuotesRepository
import com.example.ceditect.utils.Coroutines
import com.example.ceditect.utils.toast
import kotlinx.android.synthetic.main.activity_print_dummy.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class printDummy : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()

    private val repository:QuotesRepository by instance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_print_dummy)


        test.setOnClickListener {
            Coroutines.main {
                val response = repository.fetchQuotes()
                if(response.isSuccessful){
                    val canada:String = response.body()?.rates?.GHS.toString()
                    toast(canada)
                }else{
                    toast(response.toString())
                }
            }
        }


    }
}
