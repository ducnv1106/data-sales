package com.vinatti.datasales.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.ResponseListRole
import com.vinatti.datasales.data.api_entities.response.ResponseResetPassword
import com.vinatti.datasales.data.remote.ApiController
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.utils.performGetOperation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EmployeeResetPasswordViewModel  @Inject constructor(private val gson: Gson, private val apiController: ApiController) : ViewModel() {

    // reset password
    private val _resetPassword : MutableLiveData<RequestObject> = MutableLiveData()
    fun requestResetPassword(request : RequestObject) {
        _resetPassword.value = request
    }
    private val _resetPasswordResult : LiveData<ApiResult<ResponseResetPassword.ResetPasswordResult>> = Transformations.switchMap(_resetPassword) {
        performGetOperation(gson){
            apiController.resetPassword(it)
        }
    }
    val resetPasswordResult : LiveData<ApiResult<ResponseResetPassword.ResetPasswordResult>> = _resetPasswordResult
}