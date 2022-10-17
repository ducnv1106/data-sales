package com.vinatti.datasales.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.vinatti.datasales.R
import com.vinatti.datasales.utils.autoCleared

abstract class BaseDialogFragment<VB : ViewBinding> : DialogFragment() {
    protected val TAG = this.javaClass.simpleName
    private var _binding: VB by autoCleared()
    val binding: VB get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding(inflater, container)
        loadControlsAndResize(binding)
        return binding.root
    }


    override fun onCreateDialog( savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                this@BaseDialogFragment.onBackPressed()
            }
        }
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    open fun onBackPressed() {
        dismiss()
    }

    abstract fun initView()

    open fun initStyle(): Int {
        return R.style.FullScreenDialog
    }
    open fun initCancelable() : Boolean{
        return false
    }
    open fun loadControlsAndResize(binding:VB){

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setStyle(STYLE_NO_FRAME, initStyle())

    }

    override fun onStart() {
        super.onStart()
        dialog?.setCancelable(initCancelable())
    }


    fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    protected abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB


}