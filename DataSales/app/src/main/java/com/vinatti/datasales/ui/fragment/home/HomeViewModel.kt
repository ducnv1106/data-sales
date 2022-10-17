package com.vinatti.datasales.ui.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.ResponseDashboard
import com.vinatti.datasales.data.remote.ApiController
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.utils.performGetOperation
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val gson: Gson, apiController: ApiController) : ViewModel(){

    private val _timeDate = MutableLiveData<String>().apply { value = "" }
    val timeDate :LiveData<String>
        get() = _timeDate
    private fun initTimeDate(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val toDay = android.text.format.DateFormat.format("EEEE", c).toString()
        val time = "${toDay}, $day/${month.plus(1)}/$year"
        _timeDate.value = time
    }

    val isSelectedTabDay = MutableLiveData<Boolean>().apply { value = true }

    private val _responseDashboard = MutableLiveData<ResponseDashboard.DashboardResponse>()
    val responseDashboard : LiveData<ResponseDashboard.DashboardResponse>
        get() = _responseDashboard
    fun updateResponseDashboard(response:ResponseDashboard.DashboardResponse){
        _responseDashboard.value = response
    }

    val _fromDate : MutableLiveData<String> = MutableLiveData()
    fun updateFromDateDay(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val time = "$day/${month.plus(1)}/$year"
        _fromDate.value=time
    }
    fun updateFromDateWeek(){
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_MONTH,-7)
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val time = "$day/${month.plus(1)}/$year"
        _fromDate.value=time
    }

    val fromDate :LiveData<String>
        get() =  _fromDate

    val _toDate : MutableLiveData<String> = MutableLiveData()
    fun updateToDate(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val time = "$day/${month.plus(1)}/$year"
        _toDate.value=time
    }

    val toDate : LiveData<String>
        get() =  _toDate

    // dashboard
    private val _dashboard : MutableLiveData<RequestObject> = MutableLiveData()
    fun requestDashboard(request : RequestObject) {
        _dashboard.value = request
    }
    private val _dashBoardResult : LiveData<ApiResult<ResponseDashboard.DashboardResult>> = Transformations.switchMap(_dashboard) {
        performGetOperation(gson){
            apiController.dashboard(it)
        }
    }
    val dashBoardResult : LiveData<ApiResult<ResponseDashboard.DashboardResult>> = _dashBoardResult

    enum class TypeDashboard{
        DAY,
        WEEK
    }
    val typeDashBoard : MutableLiveData<TypeDashboard> = MutableLiveData(TypeDashboard.DAY)

    fun setTypeDashBoard(type:TypeDashboard){
        typeDashBoard.value=type

        isSelectedTabDay.value = type == TypeDashboard.DAY
    }
    init {
        updateToDate()
        initTimeDate()
    }


}