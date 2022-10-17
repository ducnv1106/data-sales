package com.vinatti.datasales.ui.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.vinatti.datasales.R
import com.vinatti.datasales.adapter.ListEmployeesAdapter
import com.vinatti.datasales.adapter.ListRoleAdapter
import com.vinatti.datasales.data.api_entities.response.ResponseListRole
import com.vinatti.datasales.data.api_entities.response.ResponseManagerStaff
import com.vinatti.datasales.databinding.DialogListEmployeesBinding
import com.vinatti.datasales.databinding.DialogListRoleBinding
import com.vinatti.datasales.utils.setSingleClick

class DialogListEmployees(private val listContent:ArrayList<ResponseManagerStaff.ManagerStaffResponse>,
                     val itemClick : ((ResponseManagerStaff.ManagerStaffResponse?)->Unit)) : BaseDialogFragment<DialogListEmployeesBinding>() {

    private var responseEmployee: ResponseManagerStaff.ManagerStaffResponse? = null

    private val adapter by lazy { ListEmployeesAdapter(listContent,requireContext()){

        responseEmployee = it

    } }

    override fun loadControlsAndResize(binding: DialogListEmployeesBinding) {
        binding.apply {
            imgBack.setSingleClick {
                dismiss()
            }
            tvOption.setSingleClick {
                responseEmployee?.let {
                    if (it.selected){
                        itemClick(it)
                        dismiss()
                        return@setSingleClick
                    }

                }
                toast(resources.getString(R.string.str_please_choose_employee))

            }
        }

    }

    override fun initView() {
        initAdapter()
    }

    private fun initAdapter(){
        binding.apply {
            recyclerView.adapter = this@DialogListEmployees.adapter
        }
    }



    override fun initStyle(): Int  = R.style.FullScreenDialog

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogListEmployeesBinding = DialogListEmployeesBinding.inflate(inflater,container,false)
}