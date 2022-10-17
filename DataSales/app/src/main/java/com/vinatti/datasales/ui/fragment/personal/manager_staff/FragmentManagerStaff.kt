package com.vinatti.datasales.ui.fragment.personal.manager_staff

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemDragListener
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemSwipeListener
import com.google.gson.Gson
import com.vinatti.datasales.R
import com.vinatti.datasales.constants.ApiConst
import com.vinatti.datasales.data.api_entities.request.RequestEmployeeByID
import com.vinatti.datasales.data.api_entities.request.RequestEmployeeDelete
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.ResponseManagerStaff
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.databinding.FragmentManagerStaffBinding
import com.vinatti.datasales.ui.dialog.DialogAddStaff
import com.vinatti.datasales.ui.dialog.DialogConfirm
import com.vinatti.datasales.ui.dialog.DialogEmployeeEdit
import com.vinatti.datasales.ui.dialog.SuccessDialog
import com.vinatti.datasales.ui.fragment.BaseFragment
import com.vinatti.datasales.ui.fragment.marketing.MarketingViewModel
import com.vinatti.datasales.utils.setSingleClick
import com.vinatti.datasales.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FragmentManagerStaff : BaseFragment<FragmentManagerStaffBinding>() {
    private val managerStaffViewModel by viewModels<ManagerStaffViewModel>()
    private val authViewModel by activityViewModels<AuthViewModel>()
    private lateinit var managerStaffAdapter: ManagerStaffAdapter

    @Inject
    lateinit var gson: Gson

    override fun loadControlsAndResize(binding: FragmentManagerStaffBinding?) {
        binding?.apply {
            imgBack.setSingleClick {
                finish()
            }
            imgAdd.setSingleClick {
                DialogAddStaff().show(childFragmentManager,"FragmentManagerStaff")
            }

        }

    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this
        initAdapter()
        requestEmployees()
    }

    override fun observerState() {
        observerEmployees()
        observerAddItem()
        observerStateNotify()
        observerDelete()
    }
    private fun initAdapter(){
        binding.apply {
            managerStaffAdapter = ManagerStaffAdapter(requireContext(),{ item,position ->
                managerStaffViewModel.selectedContent(item,position)
                DialogEmployeeEdit().show(childFragmentManager,"FragmentManagerStaff")

            },{ item,position ->
                DialogConfirm(resources.getString(R.string.str_message_confirm_delete_employee)){
                    managerStaffViewModel.selectedContent(item,position)
                    deleteItem(item)
                }.show(childFragmentManager,"FragmentCustom")

            })
            recycleView.apply {
                adapter = managerStaffAdapter
            }
        }

    }

    private fun requestEmployees(){
        try {
            managerStaffViewModel.also {
                val request = RequestEmployeeByID(id=1)
                val data = gson.toJson("")
                it.requestEmployees(RequestObject(data=data, code = ApiConst.EMPLOYEE_GETS))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerEmployees(){
        try {
            managerStaffViewModel.employeesResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS -> {
                        if (it?.data?.response is ArrayList<*>){
                            (it.data.response as ArrayList<ResponseManagerStaff.ManagerStaffResponse>).also { result->
                                managerStaffAdapter.updateList(result)
                                authViewModel.updateActionSate(AuthViewModel.StateAddItem.NOTHING)
                            }
                        }

                    }
                    else -> {}
                }
            }

        }catch (e:Exception){

        }
    }

    private fun deleteItem(item:ResponseManagerStaff.ManagerStaffResponse){
        try {
            managerStaffViewModel.also {
                val request = RequestEmployeeDelete(id=item.id)
                val data = gson.toJson(request)
                it.requestEmployeeDelete(RequestObject(data=data, code = ApiConst.EMPLOYEE_DELETE))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerDelete(){
        try {
            managerStaffViewModel.employeeDeleteResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS -> {
                        managerStaffViewModel.setActionSate(ManagerStaffViewModel.STATE.REMOVE_ITEM)
                        SuccessDialog(resources.getString(R.string.str_employee_delete_success)){}.show(childFragmentManager,"FragmentManagerStaff")

                    }
                    else -> {}
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun observerAddItem(){
        try {
            authViewModel.actionState.observe(viewLifecycleOwner){
                if (it== AuthViewModel.StateAddItem.NOTIFY_DATA){
                    requestEmployees()
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun observerStateNotify(){
        try {
            managerStaffViewModel.actionState.observe(viewLifecycleOwner){
                try {
                    if (it== ManagerStaffViewModel.STATE.NOTIFY_DATA){
                        managerStaffAdapter.notifyItemChanged(managerStaffViewModel.positionSelected)
                    }else if(it==ManagerStaffViewModel.STATE.REMOVE_ITEM){
                        managerStaffAdapter.removeItem(managerStaffViewModel.positionSelected)
                    }
                }catch (e:Exception){

                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_manager_staff

    override fun finish() {
        mActivity?.closeFuncChildScreen(this)
    }

    override fun backToPrevious() {
        finish()
    }
    override fun isBackPreviousEnable(): Boolean {
        return true
    }
}