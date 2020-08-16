package com.example.gallery.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


fun Fragment.getWidthHeightDevice(): IntArray {
    val screen = intArrayOf(0,0)
    return try {
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        screen[1] = displayMetrics.heightPixels
        screen[0] = displayMetrics.widthPixels
        screen
    }catch (e : Exception){
        screen
    }
}

fun convertDpToPixel(dp: Int, context: Context?) : Int{
    return if (context != null) {
         (dp * (context.resources
            .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    } else {
        return dp
    }
}

@SuppressLint("SimpleDateFormat")
fun Long.getDate(): String {
    val currentTime = this * 1000L
    return SimpleDateFormat("dd/MM/yyyy").format(Date(currentTime))
}