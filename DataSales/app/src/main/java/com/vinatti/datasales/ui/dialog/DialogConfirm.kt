package com.vinatti.datasales.ui.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import com.vinatti.datasales.R
import com.vinatti.datasales.databinding.DialogConfirmBinding
import com.vinatti.datasales.utils.setSingleClick


class DialogConfirm(val message:String, var listener: (()->Unit)) : BaseDialogFragment<DialogConfirmBinding>() {

    override fun initView(){
        binding.apply {
            tvMessage.text = HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY)
            btnAgree.setSingleClick {
                listener()
                dismiss()
            }
            btnCancel.setSingleClick {
                dismiss()
            }

        }
    }


    override fun initCancelable(): Boolean = true

    override fun initStyle(): Int = R.style.DialogStyle

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = DialogConfirmBinding.inflate(inflater,container,false)


}