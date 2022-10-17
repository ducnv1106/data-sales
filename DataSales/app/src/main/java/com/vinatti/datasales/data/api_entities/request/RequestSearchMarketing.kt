package com.vinatti.datasales.data.api_entities.request

import com.google.gson.annotations.SerializedName

data class RequestSearchMarketing (
    @SerializedName("Channel")
    val channel:Int,
    @SerializedName("FromDate")
    val fromDate :String,
    @SerializedName("ToDate")
    val toDate :String,
)