package com.vinatti.datasales.viewmodel

import androidx.lifecycle.*
import com.google.gson.Gson
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.ResponseChannel
import com.vinatti.datasales.data.api_entities.response.ResponseEditCustom
import com.vinatti.datasales.data.api_entities.response.ResponseListStatus
import com.vinatti.datasales.data.api_entities.response.ResponseManagerStaff
import com.vinatti.datasales.data.remote.ApiController
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.utils.performGetOperation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailCustomViewModel @Inject constructor(private val gson: Gson, private val apiController: ApiController) : ViewModel()  {

    val timeDate = MutableLiveData<String>().apply { value ="" }

    val timeHours = MutableLiveData<String>().apply { value = "" }

    val contentName = MutableLiveData<String>().apply { value = "" }
    val contentPhone = MutableLiveData<String>().apply { value = "" }

    val contentAddress = MutableLiveData("")
    private val _lengthContentAddress: LiveData<Int> = contentAddress.switchMap {
        liveData {
            emit(it.length)
        }
    }
    val lengthContentAddress: LiveData<Int> = _lengthContentAddress

    val contentAdvisory = MutableLiveData("")
    private val _lengthContentAdvisory: LiveData<Int> = contentAdvisory.switchMap {
        liveData {
            emit(it.length)
        }
    }
    val lengthContentAdvisory: LiveData<Int> = _lengthContentAdvisory


    val contentAppointment  = MutableLiveData("")
    private val _lengthContentAppointment: LiveData<Int> = contentAppointment.switchMap {
        liveData {
            emit(it.length)
        }
    }
    val lengthContentAppointment: LiveData<Int> = _lengthContentAppointment


    private val _responseChannel = MutableLiveData<ResponseChannel.ChannelResponse>()
    val responseChannel :LiveData<ResponseChannel.ChannelResponse>
        get() = _responseChannel
    val contentChannel :LiveData<String>
        get() = _responseChannel.switchMap {
            liveData { emit(it.name) }
        }
    fun updateChannel(channel: ResponseChannel.ChannelResponse){
        _responseChannel.value = channel
    }

    // request channel
    val listChannel = arrayListOf<ResponseChannel.ChannelResponse>()
    fun updateListChannel(newList:ArrayList<ResponseChannel.ChannelResponse>){
        listChannel.clear()
        listChannel.addAll(newList)
    }

    private val _channel : MutableLiveData<RequestObject> = MutableLiveData()
    fun requestChannel(request : RequestObject) {
        _channel.value = request
    }
    private val _requestChannelResult : LiveData<ApiResult<ResponseChannel.ChannelResult>> = Transformations.switchMap(_channel) {
        performGetOperation(gson){
            apiController.requestChannel(it)
        }
    }
    val requestChannelResult : LiveData<ApiResult<ResponseChannel.ChannelResult>> = _requestChannelResult

    private val _responseStatus = MutableLiveData<ResponseListStatus.ListStatusResponse>()
    val responseStatus :LiveData<ResponseListStatus.ListStatusResponse>
        get() = _responseStatus
    val contentStatus :LiveData<String>
        get() = _responseStatus.switchMap {
            liveData { emit(it.name) }
        }
    fun updateStatus(status: ResponseListStatus.ListStatusResponse){
        _responseStatus.value = status
    }

    // request list Status
    val listStatus = arrayListOf<ResponseListStatus.ListStatusResponse>()
    fun updateListStatus(newList:ArrayList<ResponseListStatus.ListStatusResponse>){
        listStatus.clear()
        listStatus.addAll(newList)
    }

    private val _status : MutableLiveData<RequestObject> = MutableLiveData()
    fun requestListStatus(request : RequestObject) {
        _status.value = request
    }
    private val _requestListStatusResult : LiveData<ApiResult<ResponseListStatus.ListStatusResult>> = Transformations.switchMap(_status) {
        performGetOperation(gson){
            apiController.requestListStatus(it)
        }
    }
    val requestListStatusResult : LiveData<ApiResult<ResponseListStatus.ListStatusResult>> = _requestListStatusResult

    // request edit custom

    private val _editCustom : MutableLiveData<RequestObject> = MutableLiveData()
    fun requestEditCustom(request : RequestObject) {
        _editCustom.value = request
    }
    private val _editCustomResult : LiveData<ApiResult<ResponseEditCustom.EditCustomResult>> = Transformations.switchMap(_editCustom) {
        performGetOperation(gson){
            apiController.editCustom(it)
        }
    }
    val editCustomResult : LiveData<ApiResult<ResponseEditCustom.EditCustomResult>> = _editCustomResult


    private val _responseEmployee = MutableLiveData<ResponseManagerStaff.ManagerStaffResponse>()
    val responseEmployee :LiveData<ResponseManagerStaff.ManagerStaffResponse>
        get() = _responseEmployee
    val contentCaringStaff :LiveData<String>
        get() = _responseEmployee.switchMap {
            liveData { emit(it.name) }
        }
    fun updateEmployee(employee: ResponseManagerStaff.ManagerStaffResponse){
        _responseEmployee.value = employee
    }

    // manager staff
    val listEmployees = arrayListOf<ResponseManagerStaff.ManagerStaffResponse>()
    fun updateListEmployees(newList:ArrayList<ResponseManagerStaff.ManagerStaffResponse>){
        listEmployees.clear()
        listEmployees.addAll(newList)
    }

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