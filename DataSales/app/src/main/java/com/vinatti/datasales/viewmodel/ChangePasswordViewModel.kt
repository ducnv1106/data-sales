package com.vinatti.datasales.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.ResponseChangePassword
import com.vinatti.datasales.data.api_entities.response.ResponseUpdateMarketing
import com.vinatti.datasales.data.remote.ApiController
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.utils.performGetOperation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(private val gson: Gson, private val apiController: ApiController) : ViewModel() {

    // change password
    private val _changePassword : MutableLiveData<RequestObject> = MutableLiveData()
    fun requestChangePassword(request : RequestObject) {
        _changePassword.value = request
    }
    private val _changePasswordResult : LiveData<ApiResult<ResponseChangePassword.ChangePasswordResult>> = Transformations.switchMap(_changePassword) {
        performGetOperation(gson){
            apiController.changePassword(it)
        }
    }
    val changePasswordResult : LiveData<ApiResult<ResponseChangePassword.ChangePasswordResult>> = _changePasswordResult
}