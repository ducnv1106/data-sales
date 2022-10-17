package com.vinatti.datasales.data.api_entities.request

import com.google.gson.annotations.SerializedName

data class EmployeeAddRequest(
    @SerializedName("Name")
    val name: String,
    @SerializedName("MobileNumber")
    val mobileNumber :String,
    @SerializedName("CreatedBy")
    val createdBy :Int,
    @SerializedName("Role")
    val role :String,
)