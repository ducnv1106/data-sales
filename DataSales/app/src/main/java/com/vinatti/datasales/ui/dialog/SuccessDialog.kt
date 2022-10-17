package com.vinatti.datasales.ui.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.vinatti.datasales.R
import com.vinatti.datasales.databinding.SuccessDialogBinding
import com.vinatti.datasales.utils.setSingleClick


class SuccessDialog(val message:String, var listener: (()->Unit)) : BaseDialogFragment<SuccessDialogBinding>() {

    override fun initView(){
        binding.apply {
            btnClose.setSingleClick {
                dismiss()
                listener.invoke()
            }
            if (message.isNotEmpty()) tvMessage.text = message
        }
    }

    override fun initStyle(): Int  = R.style.DialogStyle

    override fun initCancelable(): Boolean  = true

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) =  SuccessDialogBinding.inflate(inflater,container,false)


}