package com.vinatti.datasales.data.api_entities.response

import com.google.gson.annotations.SerializedName

object ResponseListStatus {
    class ListStatusResult : BaseApiResult()
    data class ListStatusResponse(
        @SerializedName("ID")
        val id:Int,
        @SerializedName("Name")
        val name:String,
        var selected : Boolean = false
    )
}