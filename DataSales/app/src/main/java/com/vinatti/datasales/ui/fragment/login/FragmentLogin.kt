package com.vinatti.datasales.ui.fragment.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.vinatti.datasales.R
import com.vinatti.datasales.constants.ApiConst
import com.vinatti.datasales.constants.PrefConst
import com.vinatti.datasales.data.api_entities.request.LoginRequest
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.ResponseLogin
import com.vinatti.datasales.data.local.pref.PrefManager
import com.vinatti.datasales.data.local.pref.PrefStr
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.databinding.FragmentLoginBinding
import com.vinatti.datasales.utils.AppUtils
import com.vinatti.datasales.utils.setSingleClick
import com.vinatti.datasales.ui.fragment.BaseFragment

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FragmentLogin : BaseFragment<FragmentLoginBinding>() {
    @set: Inject
    lateinit var gson: Gson
    private val loginViewModel by viewModels<LoginViewModel>()

    override fun getLayoutId(): Int = R.layout.fragment_login

    override fun loadControlsAndResize(binding: FragmentLoginBinding?) {
        binding?.apply {
            rootView.setSingleClick {
                AppUtils.hideKeyboard(it)
            }
            btnLogin.setSingleClick {
                requestLogin()
            }

        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this

    }

    override fun observerState() {
        observerLogin()
    }

    private fun requestLogin(){
        if (validateInput()){
            loginViewModel.also {
                val loginRequest = LoginRequest(binding.edtNumberPhone.text.toString(), binding.edtPassword.text.toString())
                val data = gson.toJson(loginRequest)
                it.requestLogin(RequestObject(data=data, code = ApiConst.USER_LOGIN))
            }
        }
    }
    private fun observerLogin(){
        try {
            loginViewModel.loginResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS -> {
                        if (it?.data?.response is ResponseLogin.LoginResponse){
                            (it.data.response as ResponseLogin.LoginResponse).also { result ->
                                writeDataPref(result)
                                mActivity?.showHome()
                            }
                        }

                    }
                    else -> {}
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun validateInput() : Boolean {
        binding.apply {
            textFieldPhoneNumber.error = ""
            textFieldPassword.error = ""
            val phoneNumber = binding.edtNumberPhone.text.toString()
            val passWord = binding.edtPassword.text.toString()
            if (phoneNumber.isBlank()) {
                textFieldPhoneNumber.error = getString(R.string.error_empty_phone_number)
                return false
            }
            if (passWord.isBlank()) {
                textFieldPassword.error = getString(R.string.error_password_empty)
                return false
            }
        }
        return true

    }

    private fun writeDataPref(result:ResponseLogin.LoginResponse) {
        mActivity?.apply {
            try {
                PrefManager.getInstance(this).writeStr(PrefStr(PrefConst.PREF_LOGIN_RESPONSE, gson.toJson(result)))
                PrefManager.getInstance(this).writeBoolean(PrefConst.PREF_IS_LOGIN,true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun finish() {

    }


}