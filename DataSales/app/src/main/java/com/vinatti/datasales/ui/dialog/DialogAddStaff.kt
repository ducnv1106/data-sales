package com.vinatti.datasales.ui.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.vinatti.datasales.R
import com.vinatti.datasales.constants.ApiConst
import com.vinatti.datasales.constants.PrefConst
import com.vinatti.datasales.data.api_entities.request.ChangePasswordRequest
import com.vinatti.datasales.data.api_entities.request.EmployeeAddRequest
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.ResponseListRole
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.databinding.DialogAddStaffBinding
import com.vinatti.datasales.ui.activities.MainActivity
import com.vinatti.datasales.utils.AppUtils
import com.vinatti.datasales.utils.setSingleClick
import com.vinatti.datasales.viewmodel.AuthViewModel
import com.vinatti.datasales.viewmodel.DialogAddStaffViewModel
import com.vinatti.datasales.viewmodel.LoadingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_marketing_detail.*
import javax.inject.Inject

@AndroidEntryPoint
class DialogAddStaff : BaseDialogFragment<DialogAddStaffBinding>() {

    private val dialogAddStaffViewModel by viewModels<DialogAddStaffViewModel>()
    private val loadingViewModel by activityViewModels<LoadingViewModel>()
    private val authViewModel by activityViewModels<AuthViewModel>()

    @Inject
    lateinit var gson:Gson

    override fun loadControlsAndResize(binding: DialogAddStaffBinding) {
        binding.apply {
            imgBack.setSingleClick {
                dismiss()
            }
            rootView.setSingleClick {
                AppUtils.hideKeyboard(it)
            }

            tvRole.setSingleClick {
                dialogAddStaffViewModel.apply {
                    if (listRole.size == 0) requestListRole()
                    else{
                        showListRole(dialogAddStaffViewModel.listRole)
                    }
                }

            }
            btnUpdate.setSingleClick {
                if (confirmInput()) requestEmployeeAdd()
            }
        }
    }

    override fun initView() {
        binding.lifecycleOwner = this
        binding.viewModel = dialogAddStaffViewModel
        observerRequestRole()
        observerEmployeeAdd()

    }

    private fun requestListRole(){
        try {
            dialogAddStaffViewModel.also { viewModel ->
                val data = gson.toJson("")
                viewModel.requestListRole(RequestObject(data=data, code = ApiConst.DICTIONARY_GET_LIST_ROLE))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerRequestRole(){
        try {
            dialogAddStaffViewModel.listRoleResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS ->{
                        if (it?.data?.response is ArrayList<*>){
                            (it.data.response as ArrayList<ResponseListRole.RoleResponse>).also { result ->
                                dialogAddStaffViewModel.updateListRole(result)
                                showListRole(result)
                            }
                        }

                    }
                    else-> {}
                }


            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun requestEmployeeAdd(){
        try {
            dialogAddStaffViewModel.also { viewModel ->
                (requireActivity() as MainActivity).also { mainActivity ->
                    val responseLogin = mainActivity.getLoginResponse(PrefConst.PREF_LOGIN_RESPONSE)
                    responseLogin?.let {
                        val name = binding.edtName.text.toString()
                        val phone = binding.edtPhone.text.toString()
                        val role = viewModel.responseRole.value?.code ?: ""
                        val request = EmployeeAddRequest(name = name, mobileNumber = phone, createdBy = it.id, role = role)
                        val data = gson.toJson(request)
                        viewModel.requestEmployeeAdd(RequestObject(data=data, code = ApiConst.EMPLOYEE_CREATE))
                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun observerEmployeeAdd(){
        try {
            dialogAddStaffViewModel.employeeAddResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS->{
                        SuccessDialog(resources.getString(R.string.str_add_success)){
                            dismiss()
                            authViewModel.updateActionSate(AuthViewModel.StateAddItem.NOTIFY_DATA)
                        }.show(childFragmentManager,"FragmentCreateMarketing")
                    }
                    else -> {}
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun showListRole(listRole:ArrayList<ResponseListRole.RoleResponse>){
        listRole.find { it.selected }?.selected = false
        DialogListRole(listRole){ channelResponse ->
            channelResponse?.let {
                dialogAddStaffViewModel.updateRole(it)
            }
        }.show(parentFragmentManager,"DialogMarketingDetail")
    }

    private fun validateFullName():Boolean{
        val fullName: String = binding.edtName.text.toString()
        if (fullName.isBlank()) {
            toast(resources.getString(R.string.str_error_full_name_empty))
            return false
        }
        return true
    }
    private fun validatePhoneNumber(): Boolean {
        val phone: String = binding.edtPhone.text.toString()
        if (phone.isBlank()) {
            toast(resources.getString(R.string.error_empty_phone_number))
            return false
        }
        if (phone[0] != '0' || phone.length != 10) {
            toast(resources.getString(R.string.error_warning_phone))
            return false
        }
        return true
    }

    private fun validateRole():Boolean{
        val role: String = binding.tvRole.text.toString()
        if (role.isBlank()) {
            toast(resources.getString(R.string.str_error_role_empty))
            return false
        }
        return true
    }

    private fun confirmInput():Boolean{
        if (!validateFullName() || !validatePhoneNumber() || !validateRole()) return false
        return true
    }

    override fun initStyle(): Int  = R.style.FullScreenDialog

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogAddStaffBinding  = DialogAddStaffBinding.inflate(inflater,container,false)
}