package com.vinatti.datasales.data.remote

import com.vinatti.datasales.data.api_entities.request.RequestObject
import javax.inject.Inject

class ApiController @Inject constructor(private val apiService: CallApiService) : BaseHandleResult() {

    suspend fun login(requestObject: RequestObject) = getResult { apiService.login(requestObject) }

    suspend fun requestCustom(requestObject: RequestObject) = getResult { apiService.requestCustom(requestObject) }

    suspend fun requestMarketing(requestObject: RequestObject) = getResult { apiService.requestMarketing(requestObject) }

    suspend fun requestUpdateMarketing(requestObject: RequestObject) = getResult { apiService.requestUpdateMarketing(requestObject) }

    suspend fun requestChannel(requestObject: RequestObject) =  getResult { apiService.requestChannel(requestObject) }

    suspend fun requestCreateMarketing(requestObject: RequestObject) =  getResult { apiService.requestCreateMarketing(requestObject) }

    suspend fun requestSearchMarketing(requestObject: RequestObject) =  getResult { apiService.requestSearchMarketing(requestObject) }

    suspend fun requestEmployees(requestObject: RequestObject) =  getResult { apiService.requestEmployees(requestObject) }

    suspend fun changePassword(requestObject: RequestObject) =  getResult { apiService.changePassword(requestObject) }

    suspend fun employeeAdd(requestObject: RequestObject) =  getResult { apiService.employeeAdd(requestObject) }

    suspend fun listRole(requestObject: RequestObject) =  getResult { apiService.listRole(requestObject) }

    suspend fun resetPassword(requestObject: RequestObject) =  getResult { apiService.resetPassword(requestObject) }

    suspend fun employeeEdit(requestObject: RequestObject) =  getResult { apiService.employeeEdit(requestObject) }

    suspend fun employeeDelete(requestObject: RequestObject) =  getResult { apiService.employeeDelete(requestObject) }

    suspend fun requestListStatus(requestObject: RequestObject) =  getResult { apiService.requestListStatus(requestObject) }

    suspend fun addCustom(requestObject: RequestObject) =  getResult { apiService.addCustom(requestObject) }

    suspend fun editCustom(requestObject: RequestObject) =  getResult { apiService.editCustom(requestObject) }

    suspend fun deleteCustom(requestObject: RequestObject) =  getResult { apiService.deleteCustom(requestObject) }

    suspend fun dashboard(requestObject: RequestObject) =  getResult { apiService.dashboard(requestObject) }


}