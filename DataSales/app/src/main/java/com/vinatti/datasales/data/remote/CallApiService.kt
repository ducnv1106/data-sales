package com.vinatti.datasales.data.remote

import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface CallApiService {

    @POST("Gateway/Execute")
    fun login(@Body requestObject: RequestObject): Call<ResponseLogin.LoginResult>

    @POST("Gateway/Execute")
    fun requestCustom(@Body requestObject: RequestObject): Call<ResponseCustomSearch.CustomSearchResult>

    @POST("Gateway/Execute")
    fun requestMarketing(@Body requestObject: RequestObject): Call<ResponseMarketing.MarketingResult>

    @POST("Gateway/Execute")
    fun requestUpdateMarketing(@Body requestObject: RequestObject): Call<ResponseUpdateMarketing.UpdateMarketingResult>

    @POST("Gateway/Execute")
    fun requestChannel(@Body requestObject: RequestObject): Call<ResponseChannel.ChannelResult>

    @POST("Gateway/Execute")
    fun requestCreateMarketing(@Body requestObject: RequestObject): Call<ResponseCreateMarketing.CreateMarketingResult>

    @POST("Gateway/Execute")
    fun requestSearchMarketing(@Body requestObject: RequestObject): Call<ResponseSearchMarketing.SearchMarketingResult>

    @POST("Gateway/Execute")
    fun requestEmployees(@Body requestObject: RequestObject): Call<ResponseManagerStaff.ManagerStaffResult>

    @POST("Gateway/Execute")
    fun changePassword(@Body requestObject: RequestObject): Call<ResponseChangePassword.ChangePasswordResult>

    @POST("Gateway/Execute")
    fun employeeAdd(@Body requestObject: RequestObject): Call<ResponseEmployeeAdd.EmployeeAddResult>

    @POST("Gateway/Execute")
    fun listRole(@Body requestObject: RequestObject): Call<ResponseListRole.ListRoleResult>

    @POST("Gateway/Execute")
    fun resetPassword(@Body requestObject: RequestObject): Call<ResponseResetPassword.ResetPasswordResult>

    @POST("Gateway/Execute")
    fun employeeEdit(@Body requestObject: RequestObject): Call<ResponseEmployeeEdit.EmployeeEdit>

    @POST("Gateway/Execute")
    fun employeeDelete(@Body requestObject: RequestObject) : Call<ResponseEmployeeDelete.EmployeeDeleteResult>

    @POST("Gateway/Execute")
    fun requestListStatus(@Body requestObject: RequestObject) : Call<ResponseListStatus.ListStatusResult>

    @POST("Gateway/Execute")
    fun addCustom(@Body requestObject: RequestObject) : Call<ResponseAddCustom.AddCustomResult>

    @POST("Gateway/Execute")
    fun editCustom(@Body requestObject: RequestObject) : Call<ResponseEditCustom.EditCustomResult>

    @POST("Gateway/Execute")
    fun deleteCustom(@Body requestObject: RequestObject) : Call<ResponseDeleteCustom.DeleteCustomResult>

    @POST("Gateway/Execute")
    fun dashboard(@Body requestObject: RequestObject) : Call<ResponseDashboard.DashboardResult>


}