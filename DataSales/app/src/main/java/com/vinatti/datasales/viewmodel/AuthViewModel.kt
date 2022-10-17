package com.vinatti.datasales.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.vinatti.datasales.data.remote.ApiController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val gson: Gson, private val apiController: ApiController) : ViewModel()  {

    val actionState = MutableLiveData<StateAddItem>().apply { value = StateAddItem.NOTHING  }
    fun updateActionSate(sate:StateAddItem){
        actionState.value = sate
    }

    enum class StateAddItem{
        NOTHING,
        NOTIFY_DATA
    }

    enum class TabHomeCurrent{
        NOTHING,
        HOME
    }

    val tabCurrent = MutableLiveData<TabHomeCurrent>().apply { value = TabHomeCurrent.NOTHING  }
    fun updateTabCurrent(tab:TabHomeCurrent){
        tabCurrent.value = tab
    }
}