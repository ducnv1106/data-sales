package com.vinatti.datasales.utils

import android.content.Context
import android.net.Uri
import android.os.SystemClock
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.vinatti.datasales.R
import com.vinatti.datasales.constants.AppConst


@BindingAdapter("onSingleClick")
fun View.setSingleClick(execution: (View) -> Unit) {
    setOnClickListener(object : View.OnClickListener {
        var lastClickTime: Long = 0
        override fun onClick(p0: View?) {
            if (SystemClock.elapsedRealtime() - lastClickTime < AppConst.THRESHOLD_CLICK_TIME) {
                return
            }
            lastClickTime = SystemClock.elapsedRealtime()
            execution.invoke(this@setSingleClick)
        }
    })
}

fun AppCompatEditText.passwordToggle(context: Context, imageView: AppCompatImageView){
    if (this.transformationMethod.equals(PasswordTransformationMethod.getInstance())) {
        this.transformationMethod = HideReturnsTransformationMethod.getInstance()
        imageView.setImageDrawable(
            ContextCompat.getDrawable(context,
                R.drawable.ic_show_password))
    } else {
        this.transformationMethod = PasswordTransformationMethod.getInstance()
        imageView.setImageDrawable(
            ContextCompat.getDrawable(context,
                R.drawable.ic_hide_password))
    }
    this.setSelection(this.length())
}



fun View.resizeLayout(w: Int, h: Int) {
    try {
        layoutParams.apply {
            width = w
            height = h
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.resizeWidth(w: Int) {
    try {
        layoutParams.apply {
            width = w
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.resizeWidthWithHeightMatchParent(w: Int) {
    try {
        layoutParams.apply {
            width = w
            height = ViewGroup.LayoutParams.MATCH_PARENT
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.resizeHeight(h: Int) {
    try {
        layoutParams.apply {
            height = h
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.resizeLayout(size: ViewSize) {
    try {
        layoutParams.apply {
            width = size.width.toInt()
            height = size.height.toInt()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
