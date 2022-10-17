package com.vinatti.datasales.ui.fragment.marketing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.ResponseCustom
import com.vinatti.datasales.data.api_entities.response.ResponseLogin
import com.vinatti.datasales.data.api_entities.response.ResponseMarketing
import com.vinatti.datasales.data.api_entities.response.ResponseSearchMarketing
import com.vinatti.datasales.data.remote.ApiController
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.utils.performGetOperation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MarketingViewModel @Inject constructor(private val gson: Gson, apiController: ApiController) : ViewModel(){

    var contentSelected : ResponseMarketing.MarketingResponse? = null
    var positionSelected = 0
    fun selectedContent(content:ResponseMarketing.MarketingResponse,position:Int){
        positionSelected = position
        contentSelected = content
    }

    // request Marketing
    private val _marketing : MutableLiveData<RequestObject> = MutableLiveData()
    fun requestMarketing(request : RequestObject) {
        _marketing.value = request
    }
    private val _marketingResult : LiveData<ApiResult<ResponseMarketing.MarketingResult>> = Transformations.switchMap(_marketing) {
        performGetOperation(gson){
            apiController.requestMarketing(it)
        }
    }

    val marketingResult : LiveData<ApiResult<ResponseMarketing.MarketingResult>> = _marketingResult

    enum class STATE{
        NOTHING,
        NOTIFY_DATA,
        ADD_ITEM
    }
    val actionState : MutableLiveData<STATE> = MutableLiveData(STATE.NOTHING)

    fun setActionSate(state:STATE){
        actionState.value=state
    }


}