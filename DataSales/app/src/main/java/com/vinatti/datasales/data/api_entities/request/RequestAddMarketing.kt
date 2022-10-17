package com.vinatti.datasales.data.api_entities.request

import com.google.gson.annotations.SerializedName

data class RequestAddMarketing(
    @SerializedName("Channel")
    val channel:Int,
    @SerializedName("Cost")
    val cost :Int,
    @SerializedName("TotalAmount")
    val totalAmount :Int,
    @SerializedName("Quantity")
    val quantity :Int,
    @SerializedName("CreatedBy")
    val createdBy :Int,
    @SerializedName("FromDate")
    val fromDate :String,
    @SerializedName("ToDate")
    val toDate :String,

)