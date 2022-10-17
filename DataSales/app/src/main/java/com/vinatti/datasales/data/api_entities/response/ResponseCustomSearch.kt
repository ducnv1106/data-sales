package com.vinatti.datasales.data.api_entities.response

import com.google.gson.annotations.SerializedName

object ResponseCustomSearch {
    class CustomSearchResult :  BaseApiResult()
    data class CustomSearchResponse(
        @SerializedName("ID")
        val id:Int,
        @SerializedName("Name")
        var name:String,
        @SerializedName("MobileNumber")
        var mobileNumber:String,
        @SerializedName("Address")
        var address:String,
        @SerializedName("Content")
        var content:String,
        @SerializedName("Channel")
        var channel:Int,
        @SerializedName("ChannelName")
        var channelName:String,
        @SerializedName("Status")
        var status:Int,
        @SerializedName("StatusName")
        var statusName:String,
        @SerializedName("AppointmentDate")
        var appointmentDate:String,
        @SerializedName("AppointmentTime")
        var appointmentTime:String,
        @SerializedName("AppointmentInfo")
        var appointmentInfo:String,
        @SerializedName("CreatedDate")
        var createdDate:String,
        @SerializedName("CreatedTime")
        var createdTime:String,
        @SerializedName("CreatedBy")
        var createdBy:Int,
        @SerializedName("CreatedName")
        var createdName:String,
        @SerializedName("ModifiedDate")
        var modifiedDate:String,
        @SerializedName("ModifiedTime")
        var modifiedTime:String,
        @SerializedName("ModifiedBy")
        var modifiedBy:String,
        @SerializedName("ModifiedName")
        var modifiedName:String,
        @SerializedName("AssignedTo")
        var assignedTo:Int,
        @SerializedName("AssignedName")
        var assignedName:String,

    )
}