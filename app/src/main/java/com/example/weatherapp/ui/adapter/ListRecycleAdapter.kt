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
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.model.Item
import com.example.weatherapp.room.WeatherData
import com.example.weatherapp.viewModel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ListRecycleAdapter(private val mList : List<Item>, val viewModel : MainViewModel,val binding : ActivityMainBinding) : RecyclerView.Adapter<ListRecycleAdapter.ViewHolder>() {

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){

        val city = view.findViewById<TextView>(R.id.text1)
//        val updatedat = view.findViewById<TextView>(R.id.text2)
//        val temp = view.findViewById<TextView>(R.id.text3)


    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount() : Int{
        return mList?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.city.text = mList[position].address.label
        holder.city.setOnClickListener{
            viewModel.getCityWeather(mList[position].address.city)
            binding.recycleview.visibility = View.GONE
        }

    }

    private fun dateFormatConverter(date: Long): String {

        return SimpleDateFormat(
            "hh:mm a",
            Locale.ENGLISH
        ).format(Date(date * 1000))
    }



}