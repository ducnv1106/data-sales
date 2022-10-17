package com.vinatti.datasales.data.api_entities.request

import com.google.gson.annotations.SerializedName

data class EmployeeEditRequest(
    @SerializedName("ID")
    val id:Int,
    @SerializedName("Name")
    val name :String,
    @SerializedName("MobileNumber")
    val mobileNumber :String,
    @SerializedName("IsLock")
    val isLock :Boolean,
    @SerializedName("ModifiedBy")
    val modifiedBy :Int,
    @SerializedName("Role")
    val role :String,
)