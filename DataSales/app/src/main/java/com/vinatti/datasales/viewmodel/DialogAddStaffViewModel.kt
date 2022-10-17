package com.vinatti.datasales.viewmodel

import androidx.lifecycle.*
import com.google.gson.Gson
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.ResponseChannel
import com.vinatti.datasales.data.api_entities.response.ResponseEmployeeAdd
import com.vinatti.datasales.data.api_entities.response.ResponseListRole
import com.vinatti.datasales.data.api_entities.response.ResponseManagerStaff
import com.vinatti.datasales.data.remote.ApiController
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.utils.performGetOperation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DialogAddStaffViewModel  @Inject constructor(private val gson: Gson, private val apiController: ApiController) : ViewModel()  {

    // add employee
    private val _employeeAdd : MutableLiveData<RequestObject> = MutableLiveData()
    fun requestEmployeeAdd(request : RequestObject) {
        _employeeAdd.value = request
    }
    private val _employeeAddResult : LiveData<ApiResult<ResponseEmployeeAdd.EmployeeAddResult>> = Transformations.switchMap(_employeeAdd) {
        performGetOperation(gson){
            apiController.employeeAdd(it)
        }
    }
    val employeeAddResult : LiveData<ApiResult<ResponseEmployeeAdd.EmployeeAddResult>> = _employeeAddResult

    private val _responseRole = MutableLiveData<ResponseListRole.RoleResponse>()
    val responseRole :LiveData<ResponseListRole.RoleResponse>
        get() = _responseRole
    val contentRole :LiveData<String>
        get() = _responseRole.switchMap {
            liveData { emit(it.name) }
        }
    fun updateRole(channel: ResponseListRole.RoleResponse){
        _responseRole.value = channel
    }



    // list role

    val listRole = arrayListOf<ResponseListRole.RoleResponse>()
    fun updateListRole(newList:ArrayList<ResponseListRole.RoleResponse>){
        listRole.clear()
        listRole.addAll(newList)
    }
    private val _role : MutableLiveData<RequestObject> = MutableLiveData()
    fun requestListRole(request : RequestObject) {
        _role.value = request
    }
    private val _listRoleResult : LiveData<ApiResult<ResponseListRole.ListRoleResult>> = Transformations.switchMap(_role) {
        performGetOperation(gson){
            apiController.listRole(it)
        }
    }
    val listRoleResult : LiveData<ApiResult<ResponseListRole.ListRoleResult>> = _listRoleResult
}