package com.vinatti.datasales.data.api_entities.request

import com.google.gson.annotations.SerializedName

data class RequestEitMarketing (
    @SerializedName("ID")
    val id:Int,
    @SerializedName("Channel")
    val channel :Int,
    @SerializedName("Cost")
    val cost :Int,
    @SerializedName("TotalAmount")
    val totalAmount :Int,
    @SerializedName("Quantity")
    val quantity :Int,
    @SerializedName("ModifiedBy")
    val modifiedBy :Int = 0,
    @SerializedName("FromDate")
    val fromDate  :String,
    @SerializedName("ToDate")
    val toDate  :String,


)