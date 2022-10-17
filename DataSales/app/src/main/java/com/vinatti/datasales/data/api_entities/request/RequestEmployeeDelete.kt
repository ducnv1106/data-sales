package com.vinatti.datasales.data.api_entities.request

import com.google.gson.annotations.SerializedName

data class RequestEmployeeDelete(
    @SerializedName("ID")
    val id:Int,
)