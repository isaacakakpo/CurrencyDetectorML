package com.example.ceditect.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ceditect.R

class CurrencyValueAdapter(private val mCtx: Context, private val CurrencyList: List<CurrencyModel>) : RecyclerView.Adapter<CurrencyValueAdapter.MyViewHolder>() {

    private val inflater: LayoutInflater


    init {

        inflater = LayoutInflater.from(mCtx)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyValueAdapter.MyViewHolder {

        val view = inflater.inflate(R.layout.curreny_layout, parent, false)

        return MyViewHolder(view)


    }

    override fun onBindViewHolder(holder: CurrencyValueAdapter.MyViewHolder, position: Int) {

        val cur = CurrencyList[position]


        holder.ghsValue.text = cur.ghsValue
        holder.textForeign.text = cur.textForeign
        holder.valueForeign.text = cur.valueForeign




    }

    override fun getItemCount(): Int {
        return CurrencyList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var ghsValue: TextView
        var textForeign: TextView
        var valueForeign: TextView




        init {

            ghsValue = itemView.findViewById(R.id.valGhs) as TextView
            textForeign = itemView.findViewById(R.id.txtForeign) as TextView
            valueForeign = itemView.findViewById(R.id.valForeign) as TextView


        }

    }



}
