package com.vinatti.datasales.data.api_entities.request

import com.google.gson.annotations.SerializedName

data class DashboardRequest(
    @SerializedName("ID")
    val id: Int,
    @SerializedName("FromDate")
    val fromDate: String,
    @SerializedName("ToDate")
    val toDate : String,

    )