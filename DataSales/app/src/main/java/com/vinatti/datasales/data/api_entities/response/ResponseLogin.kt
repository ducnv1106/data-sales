package com.vinatti.datasales.data.api_entities.response

import com.google.gson.annotations.SerializedName

object ResponseLogin {
    class LoginResult : BaseApiResult()
    data class LoginResponse(
        @SerializedName("ID")
        val id:Int,
        @SerializedName("Name")
        val name:String,
        @SerializedName("MobileNumber")
        val mobileNumber:String,
        @SerializedName("Avatar")
        val avatar:String,
        @SerializedName("Role")
        val role:String,
        @SerializedName("IsLock")
        val isLock:String,
    )
}