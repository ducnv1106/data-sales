package com.vinatti.datasales.ui.fragment.personal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.ResponseLogin
import com.vinatti.datasales.data.api_entities.response.ResponseManagerStaff
import com.vinatti.datasales.data.api_entities.response.ResponseMarketing
import com.vinatti.datasales.data.remote.ApiController
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.utils.performGetOperation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PersonalViewModel @Inject constructor(private val gson: Gson, apiController: ApiController) : ViewModel(){

    // manager staff
    private val _employees : MutableLiveData<RequestObject> = MutableLiveData()
    fun requestEmployees(request : RequestObject) {
        _employees.value = request
    }
    private val _employeesResult : LiveData<ApiResult<ResponseManagerStaff.ManagerStaffResult>> = Transformations.switchMap(_employees) {
        performGetOperation(gson){
            apiController.requestEmployees(it)
        }
    }
    val employeesResult : LiveData<ApiResult<ResponseManagerStaff.ManagerStaffResult>> = _employeesResult

}