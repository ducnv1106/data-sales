package com.vinatti.datasales.data.api_entities.response

import com.google.gson.annotations.SerializedName

object ResponseChannel {

    class ChannelResult : BaseApiResult()
    data class ChannelResponse(
        @SerializedName("ID")
        val id:Int,
        @SerializedName("Name")
        val name:String,
        var selected : Boolean = false
    )
}