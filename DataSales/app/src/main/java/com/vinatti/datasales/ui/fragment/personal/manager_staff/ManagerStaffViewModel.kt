package com.vinatti.datasales.ui.fragment.personal.manager_staff

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.ResponseEmployeeDelete
import com.vinatti.datasales.data.api_entities.response.ResponseManagerStaff
import com.vinatti.datasales.data.api_entities.response.ResponseMarketing
import com.vinatti.datasales.data.remote.ApiController
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.utils.performGetOperation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManagerStaffViewModel @Inject constructor(private val gson: Gson, apiController: ApiController) : ViewModel(){

    var contentSelected : ResponseManagerStaff.ManagerStaffResponse? = null
    var positionSelected = 0
    fun selectedContent(content: ResponseManagerStaff.ManagerStaffResponse, position:Int){
        positionSelected = position
        contentSelected = content
    }

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

    // delete staff
    private val _employeeDelete : MutableLiveData<RequestObject> = MutableLiveData()
    fun requestEmployeeDelete(request : RequestObject) {
        _employeeDelete.value = request
    }
    private val _employeeDeleteResult : LiveData<ApiResult<ResponseEmployeeDelete.EmployeeDeleteResult>> = Transformations.switchMap(_employeeDelete) {
        performGetOperation(gson){
            apiController.employeeDelete(it)
        }
    }
    val employeeDeleteResult : LiveData<ApiResult<ResponseEmployeeDelete.EmployeeDeleteResult>> = _employeeDeleteResult

    enum class STATE{
        NOTHING,
        NOTIFY_DATA,
        REMOVE_ITEM
    }
    val actionState : MutableLiveData<STATE> = MutableLiveData(STATE.NOTHING)

    fun setActionSate(state:STATE){
        actionState.value=state
    }

}