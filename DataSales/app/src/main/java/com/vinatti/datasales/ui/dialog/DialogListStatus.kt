package com.vinatti.datasales.ui.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.vinatti.datasales.R
import com.vinatti.datasales.adapter.ListChannelAdapter
import com.vinatti.datasales.adapter.ListStatusAdapter
import com.vinatti.datasales.data.api_entities.response.ResponseChannel
import com.vinatti.datasales.data.api_entities.response.ResponseListStatus
import com.vinatti.datasales.databinding.DialogListChannelBinding
import com.vinatti.datasales.databinding.DialogListStatusBinding
import com.vinatti.datasales.utils.setSingleClick

class DialogListStatus(private val listContent:ArrayList<ResponseListStatus.ListStatusResponse>,
                        val itemClick : ((ResponseListStatus.ListStatusResponse?)->Unit)) : BaseDialogFragment<DialogListStatusBinding>() {

    private var responseStatus: ResponseListStatus.ListStatusResponse? = null

    private val adapter by lazy { ListStatusAdapter(listContent,requireContext()){
        responseStatus = it

    } }

    override fun loadControlsAndResize(binding: DialogListStatusBinding) {
        binding.apply {
            imgBack.setSingleClick {
                dismiss()
            }
            tvOption.setSingleClick {
                responseStatus?.let {
                    if (it.selected){
                        itemClick(it)
                        dismiss()
                        return@setSingleClick
                    }

                }
                toast(resources.getString(R.string.str_please_choose_status))

            }
        }

    }

    override fun initView() {
        initAdapter()
    }

    private fun initAdapter(){
        binding.apply {
            recyclerView.adapter = this@DialogListStatus.adapter
        }
    }



    override fun initStyle(): Int  = R.style.FullScreenDialog

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogListStatusBinding = DialogListStatusBinding.inflate(inflater,container,false)
}