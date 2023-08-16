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

class ListRecycleAdapter(private val mList : List<WeatherData>?,val context: Context?) : RecyclerView.Adapter<ListRecycleAdapter.ViewHolder>() {

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){

        val txt = view.findViewById<TextView>(R.id.text1)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount() : Int{
        return mList?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.txt.text = mList?.get(position)?.name

    }


}