package com.vinatti.datasales.data.api_entities.response

import com.google.gson.annotations.SerializedName

open class BaseApiResult {
    @SerializedName("Message")
    var message: String? = null
    @SerializedName("Code")
    var status: String? = null
    @SerializedName("Data")
    var response: Any? = null

}