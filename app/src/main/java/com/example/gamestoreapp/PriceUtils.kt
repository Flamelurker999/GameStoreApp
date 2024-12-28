package com.example.gamestoreapp


import android.content.Context
import android.graphics.Paint
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.gamestoreapp.R

object PriceUtils {

    fun setPrice(
        context: Context,
        priceTextView: TextView,
        oldPriceTextView: TextView?,
        price: Double,
        oldPrice: Double?
    ) {
        priceTextView.text = "${price.toInt()} ₽"

        if (oldPrice != null && oldPrice > 0) {
            oldPriceTextView?.let {
                it.visibility = TextView.VISIBLE
                it.text = "${oldPrice.toInt()} ₽"
                // Устанавливаем зачёркивание для старой цены
                it.paintFlags = it.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                it.setTextColor(
                    ContextCompat.getColor(context, android.R.color.darker_gray)
                )
            }
            // Меняем цвет текущей цены на красный
            priceTextView.setTextColor(
                ContextCompat.getColor(context, android.R.color.holo_red_dark)
            )
        } else {
            oldPriceTextView?.visibility = TextView.GONE
            // Возвращаем стандартный цвет для цены
            priceTextView.setTextColor(
                ContextCompat.getColor(context, android.R.color.black)
            )
        }
    }
}
