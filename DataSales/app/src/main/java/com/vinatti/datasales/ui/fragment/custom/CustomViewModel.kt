package com.vinatti.datasales.ui.fragment.custom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.*
import com.vinatti.datasales.data.remote.ApiController
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.utils.performGetOperation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CustomViewModel @Inject constructor(private val gson: Gson, apiController: ApiController) : ViewModel(){

    var contentSelected : ResponseCustomSearch.CustomSearchResponse? = null
    var positionSelected = 0
    fun selectedContent(content: ResponseCustomSearch.CustomSearchResponse, position:Int){
        positionSelected = position
        contentSelected = content
    }

    // request Custom
    private val _custom : MutableLiveData<RequestObject> = MutableLiveData()
    fun requestCustom(request : RequestObject) {
        _custom.value = request
    }
    private val _customResult : LiveData<ApiResult<ResponseCustomSearch.CustomSearchResult>> = Transformations.switchMap(_custom) {
        performGetOperation(gson){
            apiController.requestCustom(it)
        }
    }
    val customResult : LiveData<ApiResult<ResponseCustomSearch.CustomSearchResult>> = _customResult

    // request delete Custom
    private val _deleteCustom: MutableLiveData<RequestObject> = MutableLiveData()
    fun requestDeleteCustom(request : RequestObject) {
        _deleteCustom.value = request
    }
    private val _deleteCustomResult : LiveData<ApiResult<ResponseDeleteCustom.DeleteCustomResult>> = Transformations.switchMap(_deleteCustom) {
        performGetOperation(gson){
            apiController.deleteCustom(it)
        }
    }
    val deleteCustomResult : LiveData<ApiResult<ResponseDeleteCustom.DeleteCustomResult>> = _deleteCustomResult

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