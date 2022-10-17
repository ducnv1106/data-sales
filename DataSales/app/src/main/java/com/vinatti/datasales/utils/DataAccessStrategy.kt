package com.vinatti.datasales.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vinatti.datasales.constants.ApiConst
import com.vinatti.datasales.data.api_entities.response.*
import com.vinatti.datasales.data.remote.ApiResult
import kotlinx.coroutines.Dispatchers

fun <T> performGetOperation(gson: Gson, networkCall: suspend () -> ApiResult<T>): LiveData<ApiResult<T>> =
    liveData(Dispatchers.IO) {
        emit(ApiResult.loading())
        val responseStatus = networkCall.invoke()
        if (responseStatus.data is BaseApiResult) {
            if (responseStatus.status == ApiResult.Status.SUCCESS) {
                if (responseStatus.data.status == ApiConst.HTTP_OK) { //OK
                    val dataResponse = if (responseStatus.data.response is String){
                        (responseStatus.data.response as String)
                    }else gson.toJson(responseStatus.data.response)
                    if (dataResponse.isNotEmpty()) {
                        try {
                            var responseConvert: Any? = null
                            when (responseStatus.data) {
                                is ResponseLogin.LoginResult ->{
                                    responseConvert=gson.fromJson(dataResponse,object : TypeToken<ResponseLogin.LoginResponse>(){}.type)
                                }
                                is ResponseMarketing.MarketingResult ->{
                                    responseConvert = gson.fromJson(dataResponse,object :TypeToken<ArrayList<ResponseMarketing.MarketingResponse>>(){}.type)
                                }
                                is ResponseChannel.ChannelResult -> {
                                    responseConvert = gson.fromJson(dataResponse,object : TypeToken<ArrayList<ResponseChannel.ChannelResponse>>(){}.type)
                                }
                                is ResponseSearchMarketing.SearchMarketingResult ->{
                                    responseConvert = gson.fromJson(dataResponse,object :TypeToken<ArrayList<ResponseMarketing.MarketingResponse>>(){}.type)
                                }
                                is ResponseManagerStaff.ManagerStaffResult ->{
                                    responseConvert = gson.fromJson(dataResponse,object : TypeToken<ArrayList<ResponseManagerStaff.ManagerStaffResponse>>(){}.type)
                                }
                                is ResponseListRole.ListRoleResult -> {
                                    responseConvert = gson.fromJson(dataResponse,object : TypeToken<ArrayList<ResponseListRole.RoleResponse>>(){}.type)
                                }
                                is ResponseCustomSearch.CustomSearchResult -> {
                                    responseConvert = gson.fromJson(dataResponse,object : TypeToken<ArrayList<ResponseCustomSearch.CustomSearchResponse>>(){}.type)
                                }
                                is ResponseListStatus.ListStatusResult ->{
                                    responseConvert = gson.fromJson(dataResponse,object : TypeToken<ArrayList<ResponseListStatus.ListStatusResponse>>(){}.type)
                                }
                                is ResponseDashboard.DashboardResult -> {
                                    responseConvert = gson.fromJson(dataResponse,object : TypeToken<ArrayList<ResponseDashboard.DashboardResponse>>(){}.type)
                                }

                            }
                            responseConvert?.let {
                                responseStatus.data.response = it
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    emit(ApiResult.success(responseStatus.data))
                    return@liveData
                }else{
                    emit(ApiResult.error(responseStatus.data.message, null))
                    return@liveData
                }
            }
            emit(ApiResult.error(responseStatus.data.message, null))
            return@liveData
        }
        if (responseStatus.status== ApiResult.Status.ERROR_NETWORK){
            emit(ApiResult.errorNetwork(null))
            return@liveData
        }else{
            emit(ApiResult.error(responseStatus.message, null))
            return@liveData
        }

    }