package com.vinatti.datasales.utils

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vinatti.datasales.R


class BindingAdapter {
    companion object {
        @BindingAdapter("textPrice")
        @JvmStatic
        fun AppCompatEditText.textPrice(price:Int){
            try {
                this.setText(AppUtils.formatPriceNumber(price.toLong()))
            }catch (e:Exception){
                e.printStackTrace()
            }


        }
        @BindingAdapter("textPrice")
        @JvmStatic
        fun AppCompatTextView.textPrice(price:Int){
            try {
                this.text = AppUtils.formatPriceNumber(price.toLong())
            }catch (e:Exception){
                e.printStackTrace()
            }


        }

        @BindingAdapter("setImageLock")
        @JvmStatic
        fun AppCompatImageView.setImageLock(isLock: MutableLiveData<Boolean>){
            try {
                if (isLock.value==true){
                    this.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_tick))
                }else{
                    this.background = ContextCompat.getDrawable(context,R.drawable.bg_circle_tick)
                    this.setImageDrawable(null)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }


        }

        @SuppressLint("SetTextI18n")
        @BindingAdapter("android:lengthText")
        @JvmStatic
        fun TextView.setLiveDataLength(liveData: LiveData<Int>){
            val length = liveData.value ?: 0
            this.text =  "${liveData.value.toString()}/500"
            if (length<500) {
                this.setTextColor(resources.getColor(R.color.grey_600))
            }else{
                this.setTextColor(resources.getColor(R.color.colorRed))
            }

        }
        @SuppressLint("SetTextI18n")
        @BindingAdapter("setBackgroundView")
        @JvmStatic
        fun View.setBackgroundView(isSelected: Boolean){
            if (isSelected){
                this.setBackgroundColor(resources.getColor(R.color.colorPrimary,null))
            }else{
                this.setBackgroundColor(resources.getColor(R.color.grey_400,null))
            }

        }
    }
}