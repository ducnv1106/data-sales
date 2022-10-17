package com.vinatti.datasales.data.api_entities.response

import com.google.gson.annotations.SerializedName

object ResponseListRole {
    class ListRoleResult : BaseApiResult()
    data class RoleResponse(
        @SerializedName("Code")
        val code:String,
        @SerializedName("Name")
        val name:String,
        var selected : Boolean = false
    )
}