package com.vinatti.datasales.data.api_entities.request

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
    @SerializedName("ID")
    val id:Int,
    @SerializedName("MobileNumber")
    val mobileNumber :String,
    @SerializedName("OldPassword")
    val oldPassword :String,
    @SerializedName("NewPassword")
    val newPassword:String,
)