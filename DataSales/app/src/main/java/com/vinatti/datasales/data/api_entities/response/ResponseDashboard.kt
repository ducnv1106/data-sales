package com.vinatti.datasales.data.api_entities.response

import com.google.gson.annotations.SerializedName

object ResponseDashboard {
    class DashboardResult : BaseApiResult()
    data class DashboardResponse(
        @SerializedName("TongKhachHang")
        val tongKhachHang :Int,
        @SerializedName("TongKhachPhatSinh")
        val tongKhachPhatSinh :Int,
        @SerializedName("TongKhachCuaBan")
        val tongKhachCuaBan :Int,
        @SerializedName("TongChiPhiDauTu")
        val tongChiPhiDauTu :Int,
        @SerializedName("TongKhachTuChiPhi")
        val tongKhachTuChiPhi :Int,
        @SerializedName("TongKhachHen")
        val tongKhachHen :Int,
        @SerializedName("TongKhachDaMua")
        val tongKhachDaMua :Int,

    )
}