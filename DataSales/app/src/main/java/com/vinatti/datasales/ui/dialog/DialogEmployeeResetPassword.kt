package com.vinatti.datasales.ui.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.vinatti.datasales.R
import com.vinatti.datasales.constants.ApiConst
import com.vinatti.datasales.data.api_entities.request.LoginRequest
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.request.ResetPasswordRequest
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.databinding.DialogEmployeeResetPasswordBinding
import com.vinatti.datasales.utils.AppUtils
import com.vinatti.datasales.utils.passwordToggle
import com.vinatti.datasales.utils.setSingleClick
import com.vinatti.datasales.viewmodel.EmployeeResetPasswordViewModel
import com.vinatti.datasales.viewmodel.LoadingViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DialogEmployeeResetPassword(val ID : Int, private val phone:String) : BaseDialogFragment<DialogEmployeeResetPasswordBinding>() {

    private val employeeResetPasswordViewModel by viewModels<EmployeeResetPasswordViewModel>()
    private val loadingViewModel by  activityViewModels<LoadingViewModel>()

    @Inject
    lateinit var gson:Gson

    override fun loadControlsAndResize(binding: DialogEmployeeResetPasswordBinding) {
        binding.apply {
            imgBack.setSingleClick {
                dismiss()
            }
            rootView.setSingleClick {
                AppUtils.hideKeyboard(it)
            }
            buttonPasswordToggleNew.setSingleClick {
                edtPasswordNew.passwordToggle(requireContext(),buttonPasswordToggleNew)
            }
            buttonConfirmPasswordToggleNew.setSingleClick {
                edtConfirmPasswordNew.passwordToggle(requireContext(),buttonConfirmPasswordToggleNew)
            }
            btnUpdate.setSingleClick {
                if (confirmInput()) requestResetPassword()
            }
        }
    }


    override fun initView() {
        binding.lifecycleOwner = this
        observerResetPassword()

    }

    private fun requestResetPassword(){
        try {
            employeeResetPasswordViewModel.also { viewModel ->
                val password = binding.edtPasswordNew.text.toString()
                val request = ResetPasswordRequest(id = ID, mobileNumber = phone, password =  password )
                val data = gson.toJson(request)
                viewModel.requestResetPassword(RequestObject(data=data, code = ApiConst.EMPLOYEE_RESET_PASSWORD))

            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerResetPassword(){
        try {
            employeeResetPasswordViewModel.resetPasswordResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS -> {
                        SuccessDialog(resources.getString(R.string.str_reset_password_success)){
                            dismiss()
                        }.show(childFragmentManager,"DialogEmployeeResetPassword")
                    }
                    else -> {}
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun validatePassword():Boolean{
        val password = binding.edtPasswordNew.text.toString()
        if (password.isBlank()){
            toast(resources.getString(R.string.str_not_entered_new_password))
            return false
        }
        return true
    }

    private fun validateConfirmPassword():Boolean{
        val password = binding.edtConfirmPasswordNew.text.toString()
        if (password.isBlank()){
            toast(resources.getString(R.string.str_not_entered_confirm_password))
            return false
        }
        return true
    }

    private fun validateNotMatchPassword() : Boolean{
        val password = binding.edtPasswordNew.text.toString()
        val confirmPassword = binding.edtConfirmPasswordNew.text.toString()
        if (password != confirmPassword){
            toast(resources.getString(R.string.str_not_match_password))
            return false
        }
        return true
    }

    private fun confirmInput(): Boolean {
        if (!validatePassword()  || !validateConfirmPassword() || !validateNotMatchPassword()) {
            return false
        }
        return true
    }


    override fun initStyle() = R.style.FullScreenDialog

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogEmployeeResetPasswordBinding = DialogEmployeeResetPasswordBinding.inflate(inflater,container,false)
}