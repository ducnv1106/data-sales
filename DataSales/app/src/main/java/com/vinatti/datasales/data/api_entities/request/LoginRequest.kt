package com.vinatti.datasales.data.api_entities.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("MobileNumber")
    val mobileNumber:String,
    @SerializedName("Password")
    val password:String,
)