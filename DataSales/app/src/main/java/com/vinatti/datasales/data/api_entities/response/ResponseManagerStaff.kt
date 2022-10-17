package com.vinatti.datasales.data.api_entities.response

import com.google.gson.annotations.SerializedName

object ResponseManagerStaff {
    class ManagerStaffResult : BaseApiResult()
    data class ManagerStaffResponse(
        @SerializedName("ID")
        val id:Int = 0,
        @SerializedName("Name")
        var name:String = "",
        @SerializedName("MobileNumber")
        var mobileNumber:String = "",
        @SerializedName("IsLock")
        var isLock:String ="",
        @SerializedName("Avatar")
        val avatar:String ="",
        @SerializedName("Role")
        var role:String = "",
        @SerializedName("RoleName")
        var roleName:String = "",
        var selected : Boolean = false

    )
}