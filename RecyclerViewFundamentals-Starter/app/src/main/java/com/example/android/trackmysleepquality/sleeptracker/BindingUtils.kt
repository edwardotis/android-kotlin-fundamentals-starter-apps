package com.example.android.trackmysleepquality.sleeptracker

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight

/**
 * Extension functions applied to list_item_sleep_night
 */
@BindingAdapter("sleepDurationFormatted")
fun TextView.setSleepDurationFormatted(item:SleepNight){
    text = convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, context.resources)
}

@BindingAdapter("sleepQuality")
fun TextView.setSleepQuality(item:SleepNight){
    val res = context.resources//purely for helper function
    text = convertNumericQualityToString(item.sleepQuality, res)
    if (item.sleepQuality <= 1) {
        setTextColor(Color.RED) // red
    } else {
        setTextColor(Color.BLACK)
    }
}

@BindingAdapter("sleepQualityImage")
fun ImageView.setSleepQualityImage(item:SleepNight){
    val res = context.resources//purely for helper function
    setImageResource(when (item.sleepQuality) {
        0 -> R.drawable.ic_sleep_0
        1 -> R.drawable.ic_sleep_1
        2 -> R.drawable.ic_sleep_2
        3 -> R.drawable.ic_sleep_3
        4 -> R.drawable.ic_sleep_4
        5 -> R.drawable.ic_sleep_5
        else -> R.drawable.ic_sleep_active
    })
}