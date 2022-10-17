package com.vinatti.datasales.ui.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.vinatti.datasales.R
import com.vinatti.datasales.adapter.ListChannelAdapter
import com.vinatti.datasales.data.api_entities.response.ResponseChannel
import com.vinatti.datasales.databinding.DialogListChannelBinding
import com.vinatti.datasales.utils.setSingleClick

class DialogListChannel(private val listContent:ArrayList<ResponseChannel.ChannelResponse>,
                        val itemClick : ((ResponseChannel.ChannelResponse?)->Unit)) : BaseDialogFragment<DialogListChannelBinding>() {

    private var responseChannel: ResponseChannel.ChannelResponse? = null

    private val adapter by lazy { ListChannelAdapter(listContent,requireContext()){

        responseChannel = it

    } }

    override fun loadControlsAndResize(binding: DialogListChannelBinding) {
        binding.apply {
            imgBack.setSingleClick {
                dismiss()
            }
            tvOption.setSingleClick {
                responseChannel?.let {
                    if (it.selected){
                        itemClick(it)
                        dismiss()
                        return@setSingleClick
                    }

                }
                toast(resources.getString(R.string.str_please_choose_channel))

            }
        }

    }

    override fun initView() {
        initAdapter()
    }

    private fun initAdapter(){
        binding.apply {
            recyclerView.adapter = this@DialogListChannel.adapter
        }
    }



    override fun initStyle(): Int  = R.style.FullScreenDialog

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogListChannelBinding = DialogListChannelBinding.inflate(inflater,container,false)
}