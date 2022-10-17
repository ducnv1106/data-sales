package com.vinatti.datasales.data.api_entities.request

import com.google.gson.annotations.SerializedName

data class MarketingRequest(
    @SerializedName("Channel")
    val Channel: Int = 0,
    @SerializedName("FromDate")
    val fromDate : String = "",
    @SerializedName("ToDate")
    val toDate : String = "",
)