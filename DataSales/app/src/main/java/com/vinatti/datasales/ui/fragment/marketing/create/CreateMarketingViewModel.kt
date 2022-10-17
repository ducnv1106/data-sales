package com.vinatti.datasales.ui.fragment.marketing.create

import androidx.lifecycle.*
import com.google.gson.Gson
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.ResponseChannel
import com.vinatti.datasales.data.api_entities.response.ResponseCreateMarketing
import com.vinatti.datasales.data.api_entities.response.ResponseLogin
import com.vinatti.datasales.data.remote.ApiController
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.utils.performGetOperation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class CreateMarketingViewModel @Inject constructor(private val gson: Gson, private val apiController: ApiController) : ViewModel()  {

    val fromDate = MutableLiveData<String>().apply { value ="" }

    val toDate = MutableLiveData<String>().apply { value ="" }

    val price = MutableLiveData<String>().apply { value ="" }

    val sumAmount = MutableLiveData<String>().apply { value ="" }

    val countCustom = MutableLiveData<String>().apply { value ="" }

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


    // create Marketing
    private val _createMarketing : MutableLiveData<RequestObject> = MutableLiveData()
    fun requestCreate(request : RequestObject) {
        _createMarketing.value = request
    }
    private val _createResult : LiveData<ApiResult<ResponseCreateMarketing.CreateMarketingResult>> = Transformations.switchMap(_createMarketing) {
        performGetOperation(gson){
            apiController.requestCreateMarketing(it)
        }
    }
    val createResult : LiveData<ApiResult<ResponseCreateMarketing.CreateMarketingResult>> = _createResult

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