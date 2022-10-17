package com.vinatti.datasales.data.api_entities.request

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(
    @SerializedName("ID")
    val id:Int,
    @SerializedName("MobileNumber")
    val mobileNumber :String,
    @SerializedName("Password")
    val password :String,
)