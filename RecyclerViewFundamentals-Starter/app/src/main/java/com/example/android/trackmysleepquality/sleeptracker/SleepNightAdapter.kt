package com.example.android.trackmysleepquality.sleeptracker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight

class SleepNightAdapter : RecyclerView.Adapter<SleepNightAdapter.SleepViewHolder>() {

    var data = listOf<SleepNight>()
        set(value){
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: SleepViewHolder, position: Int) {
        val item = data[position]
//        holder.quality.text = item.sleepQuality.toString()
        val res = holder.itemView.context.resources//purely for helper function
        holder.quality.text = convertNumericQualityToString(item.sleepQuality, res)
        if (item.sleepQuality <= 1) {
            holder.quality.setTextColor(Color.RED) // red
        }else{
            holder.quality.setTextColor(Color.BLACK)
        }
        holder.sleepLength.text = convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)

        //set image quality icon
//        when(item.sleepQuality){
//            0 -> holder.qualityImage.setImageResource(R.drawable.ic_sleep_0)
//            1 -> holder.qualityImage.setImageResource(R.drawable.ic_sleep_1)
//            2 -> holder.qualityImage.setImageResource(R.drawable.ic_sleep_2)
//            3 -> holder.qualityImage.setImageResource(R.drawable.ic_sleep_3)
//            4 -> holder.qualityImage.setImageResource(R.drawable.ic_sleep_4)
//            4 -> holder.qualityImage.setImageResource(R.drawable.ic_sleep_5)
//            else ->{
//                holder.qualityImage.setImageResource(R.drawable.ic_sleep_active)
//            }
//        }
        holder.qualityImage.setImageResource(when (item.sleepQuality) {
            0 -> R.drawable.ic_sleep_0
            1 -> R.drawable.ic_sleep_1
            2 -> R.drawable.ic_sleep_2
            3 -> R.drawable.ic_sleep_3
            4 -> R.drawable.ic_sleep_4
            5 -> R.drawable.ic_sleep_5
            else -> R.drawable.ic_sleep_active
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item_sleep_night, parent, false) as View
        return SleepViewHolder(view)
    }

    class SleepViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        val sleepLength: TextView = itemView.findViewById(R.id.sleep_length)
        val quality: TextView = itemView.findViewById(R.id.quality_string)
        val qualityImage: ImageView = itemView.findViewById(R.id.quality_image)
    }

}