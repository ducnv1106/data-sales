package com.vinatti.datasales.data.api_entities.request

import com.google.gson.annotations.SerializedName

data class CustomerSearchRequest(
    @SerializedName("MobileNumber")
    val mobileNumber:String = "",
    @SerializedName("Channel")
    val channel :Int = 0,
    @SerializedName("Status")
    val status :Int = 0,
    @SerializedName("FromDate")
    val fromDate :String = "",
    @SerializedName("ToDate")
    val toDate :String = "",
    @SerializedName("AssignedTo")
    val assignedTo:Int = 0 ,
)