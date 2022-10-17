package com.vinatti.datasales.viewmodel

import androidx.lifecycle.*
import com.google.gson.Gson
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.ResponseChannel
import com.vinatti.datasales.data.remote.ApiController
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.utils.performGetOperation
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SearchMarketingViewModel @Inject constructor(private val gson: Gson, private val apiController: ApiController) : ViewModel() {

    private val _fromDate : MutableLiveData<String> = MutableLiveData()
    fun updateFromDateTextView(date:String){
        _fromDate.value=date
    }

    val fromDate :LiveData<String>
        get() =  _fromDate

    private val _toDate : MutableLiveData<String> = MutableLiveData()
    fun updateToDateTextView(date:String){
        _toDate.value=date
    }

    val toDate : LiveData<String>
        get() =  _toDate

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

    private fun initChannel(){
        _responseChannel.value = ResponseChannel.ChannelResponse(id = 0,"Tất cả")
    }

    private fun initDateTextView(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        updateFromDateTextView("15/${month}/$year")
        updateToDateTextView("$day/${month.plus(1)}/$year")

    }
    init {
        initDateTextView()
        initChannel()
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
}