package com.example.ceditect.ui

import android.content.Intent
import android.os.*
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.ceditect.R
import com.example.ceditect.adapters.CurrencyModel
import com.example.ceditect.adapters.CurrencyValueAdapter
import com.example.ceditect.models.AppDatabase
import com.example.ceditect.models.Rates
import com.example.ceditect.utils.TTS
import com.yarolegovich.discretescrollview.DSVOrientation
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter
import com.yarolegovich.discretescrollview.transform.Pivot
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.activity_currency_detected.*
import kotlinx.coroutines.launch
import java.util.*


class CurrencyDetectedActivity : BaseActivity() {
    private var recyclerView: RecyclerView? = null
    private var currencyArrayList: ArrayList<CurrencyModel>? = null
    private var adapter: CurrencyValueAdapter? = null

    private var ghValues:Array<String>?= null
    private var foreignTexts:Array<String>? = null

    private var foreignValues:Array<String>? = null
    private var size:Int? = null

    private val vibrator: Vibrator? = null


    private val infiniteAdapter: InfiniteScrollAdapter<*>? = null

    var eur:Double?= null
    var can:Double?= null
    var nig:Double?= null
    var fran:Double ?= null
    var ghs:Double?= null
    var zar:Double?= null
    var aed:Double?= null
    var cny:Double?= null


    var eurv:Double?= null
    var canv:Double?= null
    var nigv:Double?= null
    var franv:Double ?= null
    var ghsv:Double?= null
    var zarv:Double?= null
    var aedv:Double?= null
    var cnyv:Double?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency_detected)
        var currencyValue = intent.getStringExtra("value")

        vibratePhone()


        when (currencyValue){
            "1_cedi" -> {currencyValue = "1"}
            "2_cedis" -> {currencyValue = "2"}
            "5_cedis" -> {currencyValue = "5"}
            "10_cedis" -> {currencyValue = "10"}
            "20_cedis" -> {currencyValue = "20"}
            "50_cedis" -> {currencyValue = "50"}
            "100_cedis" -> {currencyValue = "100"}
            "200_cedis" -> {currencyValue = "200"}
        }

        Repeat.setOnClickListener {
            if (currencyValue =="1"){
                TTS(this@CurrencyDetectedActivity, "$currencyValue Ghana  Ceedi", false)
            }
            else{
                TTS(this@CurrencyDetectedActivity, "$currencyValue Ghana   Cedis", false)

            }
        }

        say.setOnClickListener {
            val intent = Intent(this, CediTectActivity::class.java)
            startActivity(intent)
        }

        launch{

            val storedRates:List<Rates> = AppDatabase(this@CurrencyDetectedActivity).getRatesDao().loadAllRates()

            storedRates.forEach {
                ghs = it.GHS.toDouble().round(2)
                can = it.CAD.toDouble().round(2)
                nig = it.NGN.toDouble().round(2)
                fran = it.XOF.toDouble().round(2)
                eur = it.EUR.toDouble().round(2)
                zar = it.ZAR.toDouble().round(2)
                aed  = it.AED.toDouble().round(2)
                cny  = it.CNY.toDouble().round(2)



                ghsv = (currencyValue!!.toDouble().times(ghs!!)).round(2)
                canv = (currencyValue!!.toDouble().times(can!!)).round(2)
                nigv = (currencyValue!!.toDouble().times(nig!!)).round(2)
                franv = (currencyValue.toDouble().times(fran!!)).round(2)
                eurv = (currencyValue.toDouble().times(eur!!)).round(2)
                zarv = (currencyValue.toDouble().times(zar!!)).round(2)
                aedv  = (currencyValue.toDouble().times(aed!!)).round(2)
                cnyv  = (currencyValue.toDouble().times(cny!!)).round(2)

                Handler(Looper.getMainLooper()).postDelayed({
                    if (currencyValue =="1"){
                        TTS(this@CurrencyDetectedActivity, "$currencyValue Ghana   Ceedi", false)
                    }
                    else{
                        TTS(this@CurrencyDetectedActivity, "$currencyValue  Ghana Cedis", false)

                    }
                },1000)



            }


            ghValues = arrayOf("GHC  $currencyValue","GHC  $currencyValue","GHC  $currencyValue","GHC  $currencyValue","GHC  $currencyValue","GHC  $currencyValue","GHC  $currencyValue")
            foreignTexts = arrayOf("Value in Dollars","Value in CFA Franc","Value in UAE Dirham","Value in Naira","Value in Euros","Value in Rand","Value in Chinese Yuan")
            foreignValues = arrayOf("$  ${ghsv.toString()}","CFA  ${franv.toString()}"," د.إ ${aed.toString()}","₦ ${nigv.toString()}","€ ${eurv.toString()}","R ${zarv.toString()}","¥ ${cnyv.toString()}")

            size = foreignTexts!!.size
            currencyArrayList = populateList()
            adapter = CurrencyValueAdapter(this@CurrencyDetectedActivity, currencyArrayList!!)

            val scrollView = findViewById<DiscreteScrollView>(R.id.picker)
            scrollView.setOrientation(DSVOrientation.HORIZONTAL)
            scrollView.setItemTransformer(
                ScaleTransformer.Builder()
                    .setMaxScale(0.97f)
                    .setMinScale(0.8f)
                    .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                    .setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one
                    .build()
            )
            scrollView.setSlideOnFling(true);
            scrollView.setSlideOnFlingThreshold(2000)
            //scrollView.setOffscreenItems(1); //Reserve extra space equal to (childSize * count) on each side of the view
            scrollView.setOverScrollEnabled(true);
            scrollView.adapter = adapter


            val wrapper: InfiniteScrollAdapter<*> =
                InfiniteScrollAdapter(adapter!!)

            scrollView.adapter = wrapper


        }








        val textView = findViewById(R.id.support) as TextView
        val content = SpannableString("Support Us")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        textView.text = content

        val c: Calendar = Calendar.getInstance()
        val monthName = arrayOf(
            "January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"
        )
        val month = monthName[c.get(Calendar.MONTH)]
        println("Month name:$month")
        val year: Int = c.get(Calendar.YEAR)
        val date: Int = c.get(Calendar.DATE)

        currentdate.text = " "+ date+ " "+month+ "  " + "" + year











    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }


    private fun populateList(): ArrayList<CurrencyModel> {

        val list = ArrayList<CurrencyModel>()

        for (i in 0 until size!!) {
            val currencyModel = CurrencyModel()
            currencyModel.ghsValue = ghValues?.get(i)
            currencyModel.textForeign = foreignTexts?.get(i)
            currencyModel.valueForeign = foreignValues?.get(i)
            list.add(currencyModel)
        }

        return list
    }

    fun vibratePhone() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(600, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator?.vibrate (600)
        }
    }
}
