package com.example.pp.ui.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.room.WeatherData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ListRecycleAdapter(private val mList : List<WeatherData>?,val context: Context?) : RecyclerView.Adapter<ListRecycleAdapter.ViewHolder>() {

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){

        val city = view.findViewById<TextView>(R.id.text1)
        val updatedat = view.findViewById<TextView>(R.id.text2)
        val temp = view.findViewById<TextView>(R.id.text3)


    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount() : Int{
        return mList?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.city.text = mList?.get(position)?.name
        holder.updatedat.text = mList?.get(position)?.UpdateAt?.let {
            dateFormatConverter(
                it.toLong()
            )
        }
        holder.temp.text = mList?.get(position)?.Temperature.toString()

    }

    private fun dateFormatConverter(date: Long): String {

        return SimpleDateFormat(
            "hh:mm a",
            Locale.ENGLISH
        ).format(Date(date * 1000))
    }



}