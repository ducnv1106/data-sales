package com.vinatti.datasales.ui.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.vinatti.datasales.R
import com.vinatti.datasales.adapter.ListChannelAdapter
import com.vinatti.datasales.adapter.ListRoleAdapter
import com.vinatti.datasales.data.api_entities.response.ResponseChannel
import com.vinatti.datasales.data.api_entities.response.ResponseListRole
import com.vinatti.datasales.databinding.DialogListChannelBinding
import com.vinatti.datasales.databinding.DialogListRoleBinding
import com.vinatti.datasales.utils.setSingleClick

class DialogListRole(private val listContent:ArrayList<ResponseListRole.RoleResponse>,
                        val itemClick : ((ResponseListRole.RoleResponse?)->Unit)) : BaseDialogFragment<DialogListRoleBinding>() {

    private var responseRole: ResponseListRole.RoleResponse? = null

    private val adapter by lazy { ListRoleAdapter(listContent,requireContext()){

        responseRole = it

    } }

    override fun loadControlsAndResize(binding: DialogListRoleBinding) {
        binding.apply {
            imgBack.setSingleClick {
                dismiss()
            }
            tvOption.setSingleClick {
                responseRole?.let {
                    if (it.selected){
                        itemClick(it)
                        dismiss()
                        return@setSingleClick
                    }

                }
                toast(resources.getString(R.string.str_please_choose_role))

            }
        }

    }

    override fun initView() {
        initAdapter()
    }

    private fun initAdapter(){
        binding.apply {
            recyclerView.adapter = this@DialogListRole.adapter
        }
    }



    override fun initStyle(): Int  = R.style.FullScreenDialog

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogListRoleBinding = DialogListRoleBinding.inflate(inflater,container,false)
}