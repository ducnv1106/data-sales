package com.vinatti.datasales.ui.dialog

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.vinatti.datasales.R
import com.vinatti.datasales.constants.ApiConst
import com.vinatti.datasales.constants.PrefConst
import com.vinatti.datasales.data.api_entities.request.EmployeeEditRequest
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.ResponseListRole
import com.vinatti.datasales.data.api_entities.response.ResponseLogin
import com.vinatti.datasales.data.api_entities.response.ResponseManagerStaff
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.databinding.DialogEmployeeEditBinding
import com.vinatti.datasales.ui.activities.MainActivity
import com.vinatti.datasales.ui.fragment.marketing.MarketingViewModel
import com.vinatti.datasales.ui.fragment.personal.manager_staff.ManagerStaffViewModel
import com.vinatti.datasales.utils.AppUtils
import com.vinatti.datasales.utils.setSingleClick
import com.vinatti.datasales.viewmodel.EmployeeEditViewModel
import com.vinatti.datasales.viewmodel.LoadingViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DialogEmployeeEdit : BaseDialogFragment<DialogEmployeeEditBinding>() {

    private val employeeEditViewModel by viewModels<EmployeeEditViewModel>()
    private val loadingViewModel by activityViewModels<LoadingViewModel>()
    private val managerStaffViewModel by viewModels<ManagerStaffViewModel> ({requireParentFragment()})

    @Inject
    lateinit var gson: Gson

    override fun loadControlsAndResize(binding: DialogEmployeeEditBinding) {
        binding.apply {
            imgBack.setSingleClick {
                dismiss()
            }
            rootView.setSingleClick {
                AppUtils.hideKeyboard(it)
            }
            layoutLock.setSingleClick {
                employeeEditViewModel.isLock.value = !(employeeEditViewModel.isLock.value ?: false)
            }

            tvRole.setSingleClick {
                employeeEditViewModel.apply {
                    if (listRole.size == 0) requestListRole()
                    else{
                        showListRole(employeeEditViewModel.listRole)
                    }
                }

            }
            btnGetPassword.setSingleClick {
                showDialogResetPassword()
            }
            btnUpdate.setSingleClick {
                if (confirmInput()) requestEditEmployee()
            }
        }
    }



    override fun initView() {
        binding.lifecycleOwner = this
        binding.viewModel = employeeEditViewModel
        binding.managerStaffViewModel = this.managerStaffViewModel
        initViewModel()
        observerRequestRole()
        observerEmployeeEdit()

    }
    private fun initViewModel(){
        employeeEditViewModel.apply {
            contentFullName.value = managerStaffViewModel.contentSelected?.name
            contentPhone.value = managerStaffViewModel.contentSelected?.mobileNumber
            updateRole(ResponseListRole.RoleResponse(managerStaffViewModel.contentSelected?.role ?: "",
                                                     managerStaffViewModel.contentSelected?.roleName ?: ""))
            Log.e(TAG, "initViewModel: ${managerStaffViewModel.contentSelected?.role}", )
            isLock.value = managerStaffViewModel.contentSelected?.isLock != "N"
        }
    }



    private fun requestListRole(){
        try {
            employeeEditViewModel.also { viewModel ->
                val data = gson.toJson("")
                viewModel.requestListRole(RequestObject(data=data, code = ApiConst.DICTIONARY_GET_LIST_ROLE))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerRequestRole(){
        try {
            employeeEditViewModel.listRoleResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS ->{
                        if (it?.data?.response is ArrayList<*>){
                            (it.data.response as ArrayList<ResponseListRole.RoleResponse>).also { result ->
                                employeeEditViewModel.updateListRole(result)
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

    private fun requestEditEmployee(){
        try {
            managerStaffViewModel.contentSelected?.let {
                employeeEditViewModel.also { viewModel ->
                    var responseLogin : ResponseLogin.LoginResponse?
                    (requireActivity() as MainActivity).also {  mainActivity ->
                        responseLogin = mainActivity.getLoginResponse(PrefConst.PREF_LOGIN_RESPONSE)
                    }
                    val name = viewModel.contentFullName.value ?: ""
                    val phone = viewModel.contentPhone.value ?: ""
                    val role = viewModel.responseRole.value?.code ?: ""
                    val isLock = viewModel.isLock.value ?: false

                    val request = EmployeeEditRequest(id = it.id, name = name, mobileNumber = phone,
                        isLock = isLock, role = role,
                        modifiedBy = responseLogin?.id ?: 0 )

                    val data = gson.toJson(request)
                    viewModel.requestEmployeeEdit(RequestObject(data=data, code = ApiConst.EMPLOYEE_EDIT))
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerEmployeeEdit(){
        try {
            employeeEditViewModel.employeeEditResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS ->{
                        updateEditMarketing()
                        SuccessDialog(resources.getString(R.string.str_edit_employee_success)){
                            dismiss()
                        }.show(childFragmentManager,"DialogEmployeeEdit")

                    }
                    else -> {}
                }
            }
        }catch (e:Exception){

        }
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
        if (!validateFullName()  || !validateRole()) return false
        return true
    }

    private fun showListRole(listRole:ArrayList<ResponseListRole.RoleResponse>){
        listRole.find { it.selected }?.selected = false
        DialogListRole(listRole){ channelResponse ->
            channelResponse?.let {
                employeeEditViewModel.updateRole(it)
            }
        }.show(parentFragmentManager,"DialogMarketingDetail")
    }
    private fun showDialogResetPassword(){
        managerStaffViewModel.contentSelected?.let {
            DialogEmployeeResetPassword(it.id,it.mobileNumber).show(childFragmentManager,"DialogEmployeeEdit")
        }
    }


    private fun updateEditMarketing(){
        try {
            binding.apply {
                this@DialogEmployeeEdit.managerStaffViewModel.contentSelected?.let {
                    with(it){
                        name = binding.edtName.text.toString()
                        isLock = if (employeeEditViewModel.isLock.value==true) "Y" else "N"
                        role = employeeEditViewModel.responseRole.value?.code ?: ""
                        roleName = employeeEditViewModel.responseRole.value?.name ?: ""
                    }
                }
            }

            managerStaffViewModel.setActionSate(ManagerStaffViewModel.STATE.NOTIFY_DATA)
        }catch (e:Exception){
            e.printStackTrace()
        }


    }

    override fun initStyle(): Int = R.style.FullScreenDialog

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogEmployeeEditBinding  = DialogEmployeeEditBinding.inflate(inflater,container,false)
}