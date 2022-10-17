package com.vinatti.datasales.data.api_entities.request

import com.google.gson.annotations.SerializedName

data class CustomerAddRequest(
    @SerializedName("Name")
    val name:String,
    @SerializedName("MobileNumber")
    val mobileNumber :String,
    @SerializedName("Address")
    val address :String,
    @SerializedName("Content")
    val content :String,
    @SerializedName("Channel")
    val channel :Int,
    @SerializedName("Status")
    val status :Int,
    @SerializedName("AppointmentDate")
    val appointmentDate :String,
    @SerializedName("AppointmentTime")
    val appointmentTime :String,
    @SerializedName("AppointmentInfo")
    val appointmentInfo :String,
    @SerializedName("CreatedBy")
    val createdBy :Int,
    @SerializedName("AssignedTo")
    val assignedTo :Int,


)