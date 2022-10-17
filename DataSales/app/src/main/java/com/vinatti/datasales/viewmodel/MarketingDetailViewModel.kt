package com.vinatti.datasales.viewmodel

import androidx.lifecycle.*
import com.google.gson.Gson
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.ResponseChannel
import com.vinatti.datasales.data.api_entities.response.ResponseUpdateMarketing
import com.vinatti.datasales.data.remote.ApiController
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.utils.performGetOperation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MarketingDetailViewModel @Inject constructor(private val gson: Gson, private val apiController: ApiController) : ViewModel()  {

    private val _responseChannel = MutableLiveData<ResponseChannel.ChannelResponse>()
    val responseChannel :LiveData<ResponseChannel.ChannelResponse>
        get() = _responseChannel
    val contentChannel :LiveData<String>
        get() = _responseChannel.switchMap {
            liveData { emit(it.name) }
        }
    fun updateChannel(channel:ResponseChannel.ChannelResponse){
        _responseChannel.value = channel
    }


    // update Marketing
    private val _updateMarketing : MutableLiveData<RequestObject> = MutableLiveData()
    fun requestUpdateMarketing(request : RequestObject) {
        _updateMarketing.value = request
    }
    private val _updateMarketingResult : LiveData<ApiResult<ResponseUpdateMarketing.UpdateMarketingResult>> = Transformations.switchMap(_updateMarketing) {
        performGetOperation(gson){
            apiController.requestUpdateMarketing(it)
        }
    }
    val updateMarketingResult : LiveData<ApiResult<ResponseUpdateMarketing.UpdateMarketingResult>> = _updateMarketingResult

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