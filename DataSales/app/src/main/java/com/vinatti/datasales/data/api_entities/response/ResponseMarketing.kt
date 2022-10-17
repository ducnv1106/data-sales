package com.vinatti.datasales.data.api_entities.response

import com.google.gson.annotations.SerializedName
import java.net.IDN

object ResponseMarketing {
    class MarketingResult : BaseApiResult()
    data class MarketingResponse(
        @SerializedName("ID")
        val iD:Int,
        @SerializedName("Channel")
        var channel:Int,
        @SerializedName("ChannelName")
        var channelName:String,
        @SerializedName("Cost")
        var cost:Int,
        @SerializedName("TotalAmount")
        var totalAmount:Int,
        @SerializedName("Quantity")
        var quantity:Int,
        @SerializedName("CreatedDate")
        val createdDate:String,
        @SerializedName("CreatedBy")
        val createdBy:Int,
        @SerializedName("CreatedName")
        val createdName:String,
        @SerializedName("ModifiedDate")
        val modifiedDate:String,
        @SerializedName("ModifiedBy")
        val modifiedBy:Int,
        @SerializedName("ModifiedName")
        val modifiedName:String,
        @SerializedName("FromDate")
        var fromDate:String,
        @SerializedName("ToDate")
        var toDate:String,
        @SerializedName("ModifiedTime")
        val modifiedTime:String,

    )
}