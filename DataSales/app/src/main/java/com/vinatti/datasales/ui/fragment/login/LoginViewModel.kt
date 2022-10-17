package com.vinatti.datasales.ui.fragment.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.ResponseLogin
import com.vinatti.datasales.data.remote.ApiController
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.utils.performGetOperation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val gson: Gson, apiController: ApiController) : ViewModel(){


    // login
    private val _login : MutableLiveData<RequestObject> = MutableLiveData()
    fun requestLogin(requestLogin : RequestObject) {
        _login.value = requestLogin
    }
    private val _loginResult : LiveData<ApiResult<ResponseLogin.LoginResult>> = Transformations.switchMap(_login) {
        performGetOperation(gson){
            apiController.login(it)
        }
    }
    val loginResult : LiveData<ApiResult<ResponseLogin.LoginResult>> = _loginResult
}