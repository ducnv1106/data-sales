package com.vinatti.datasales.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vinatti.datasales.data.api_entities.ApiNoticeEntity
import com.vinatti.datasales.data.remote.ApiResult

class LoadingViewModel : ViewModel() {

    val isNeedShowErr: MutableLiveData<ApiNoticeEntity> = MutableLiveData()
    //Show-Hide Loading
    val isShowLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    fun <T> showOrHideLoading(result: ApiResult<T>, isNeedShowLoading: Boolean = true) {
        try {
            when (result.status) {
                ApiResult.Status.LOADING -> {
                    if (isNeedShowLoading)
                        isShowLoading.value = true
                }
                ApiResult.Status.SUCCESS -> {
                    if (isNeedShowLoading)
                        isShowLoading.value = false
                }
                ApiResult.Status.ERROR_NETWORK ->{
                    if (isNeedShowLoading)
                        isShowLoading.value = false
                    isNeedShowErr.value= ApiNoticeEntity(result.status,null)

                }
                ApiResult.Status.ERROR -> {
                    if (isNeedShowLoading)
                        isShowLoading.value = false
                    isNeedShowErr.value = ApiNoticeEntity(result.status, result.message)
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}