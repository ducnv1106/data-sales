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
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.databinding.DialogChangePasswordBinding
import com.vinatti.datasales.ui.activities.MainActivity
import com.vinatti.datasales.utils.AppUtils
import com.vinatti.datasales.utils.passwordToggle
import com.vinatti.datasales.utils.setSingleClick
import com.vinatti.datasales.viewmodel.ChangePasswordViewModel
import com.vinatti.datasales.viewmodel.LoadingViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DialogChangePassword : BaseDialogFragment<DialogChangePasswordBinding>() {

    private val changePasswordViewModel by viewModels<ChangePasswordViewModel>()
    private val loadingViewModel by activityViewModels<LoadingViewModel>()

    @Inject
    lateinit var gson:Gson

    override fun loadControlsAndResize(binding: DialogChangePasswordBinding) {
        binding.apply {

            rootView.setSingleClick {
                AppUtils.hideKeyboard(it)
            }

            btnUpdate.setSingleClick {
                if (confirmInput()) requestChangePassword()
            }

            imgBack.setSingleClick {
                dismiss()
            }

            buttonPasswordToggleOld.setSingleClick {
                edtPasswordOld.passwordToggle(requireContext(),buttonPasswordToggleOld)
            }
            buttonPasswordToggleNew.setSingleClick {
                edtPasswordNew.passwordToggle(requireContext(),buttonPasswordToggleNew)
            }
            buttonConfirmPasswordToggleNew.setSingleClick {
                edtConfirmPasswordNew.passwordToggle(requireContext(),buttonConfirmPasswordToggleNew)
            }
        }
    }

    override fun initView() {
        observerChangePassword()

    }

    private fun requestChangePassword(){
        try {
            changePasswordViewModel.also { viewModel ->
                (requireActivity() as MainActivity).also {  mainActivity ->
                    val responseLogin = mainActivity.getLoginResponse(PrefConst.PREF_LOGIN_RESPONSE)
                    responseLogin?.let {
                        val passwordOld = binding.edtPasswordOld.text.toString()
                        val passwordNew = binding.edtPasswordNew.text.toString()
                        val request = ChangePasswordRequest(id = it.id, mobileNumber = it.mobileNumber, oldPassword = passwordOld, newPassword = passwordNew)
                        val data = gson.toJson(request)
                        viewModel.requestChangePassword(RequestObject(data=data, code = ApiConst.USER_CHANGE_PASSWORD))
                    }
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerChangePassword(){
        try {
            changePasswordViewModel.changePasswordResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS -> {
                        SuccessDialog(resources.getString(R.string.str_change_password_success)){
                            dismiss()

                        }.show(childFragmentManager,"DialogChangePassword")

                    }
                    else ->{}
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun validateCurrentPassword():Boolean{
        val currentPassword = binding.edtPasswordOld.text.toString()
        if (currentPassword.isBlank()){
            toast(resources.getString(R.string.str_not_entered_current_password))
            return false
        }
        return true
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
        if (!validateCurrentPassword() || !validatePassword()  || !validateConfirmPassword() || !validateNotMatchPassword()) {
            return false
        }
        return true
    }

    override fun initStyle(): Int = R.style.FullScreenDialog

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogChangePasswordBinding = DialogChangePasswordBinding.inflate(inflater,container,false)
}