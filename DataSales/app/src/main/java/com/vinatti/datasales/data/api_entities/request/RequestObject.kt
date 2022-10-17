package com.vinatti.datasales.data.api_entities.request

import com.google.gson.annotations.SerializedName

data class RequestObject(
    @SerializedName("Code")
    private val code: String? = null,
    @SerializedName("Data")
    private val data: String? = null,
)