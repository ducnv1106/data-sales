package com.vinatti.finpost.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.TextView
import com.vinatti.datasales.R


class DialogProgress(context: Context) : Dialog(context, R.style.dialogProgress) {
    private var isCancelAble = false
    fun show(cancelAble: Boolean) {
        try {
            isCancelAble = cancelAble
            setCancelable(cancelAble)
            show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    init {
        setContentView(R.layout.progress_dialog_loading)

    }
}