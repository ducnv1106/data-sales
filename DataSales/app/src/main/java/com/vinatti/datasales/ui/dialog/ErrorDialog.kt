package com.vinatti.datasales.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.TextView
import com.vinatti.datasales.R
import com.vinatti.datasales.utils.setSingleClick


class ErrorDialog(context: Context) : Dialog(context, R.style.dialogWithoutBox) {
    private var isCancelAble = false

    fun show(message: CharSequence?, cancelAble: Boolean, dismissListener: DialogInterface.OnDismissListener?) {
        try {
            isCancelAble = cancelAble
            setCancelable(cancelAble)
            setOnDismissListener(dismissListener)
            (findViewById<View>(R.id.tv_message) as TextView).text = message
            show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        setContentView(R.layout.error_dialog)
        findViewById<View>(R.id.btn_close).setSingleClick {
            dismiss()
        }

    }
}