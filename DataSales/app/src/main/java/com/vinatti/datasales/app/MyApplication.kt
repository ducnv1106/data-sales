package com.vinatti.datasales.app

import android.support.multidex.MultiDexApplication
import com.vinatti.datasales.utils.ViewSize
import com.vinatti.datasales.constants.AppConst
import dagger.hilt.android.HiltAndroidApp
import org.conscrypt.BuildConfig
import timber.log.Timber

@HiltAndroidApp
class MyApplication : MultiDexApplication() {
    companion object {
        private lateinit var mInstance: MyApplication
        fun getInstance(): MyApplication = synchronized(this) {
            mInstance
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }


    //Start Resize
    private var scaleValue = 0F
    private fun getScaleValue(): Float {
        if (scaleValue == 0F) {
            scaleValue = resources.displayMetrics.widthPixels * 1f / AppConst.SCREEN_WIDTH_DESIGN
        }
        return scaleValue
    }

    fun getSizeWithScale(sizeDesign: Double): Int {
        return (sizeDesign * getScaleValue()).toInt()
    }

    fun getSizeWithScaleFloat(sizeDesign: Double): Float {
        return (sizeDesign * getScaleValue()).toFloat()
    }

    fun getRealSize(sizeDesign: ViewSize): ViewSize {
        return ViewSize(sizeDesign.width * getScaleValue(), sizeDesign.height * getScaleValue())
    }
    //End Resize


}