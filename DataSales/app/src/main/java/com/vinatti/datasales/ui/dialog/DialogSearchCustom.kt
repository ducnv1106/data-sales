package com.vinatti.datasales.ui.dialog

import android.app.DatePickerDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.vinatti.datasales.R
import com.vinatti.datasales.constants.ApiConst
import com.vinatti.datasales.constants.PrefConst
import com.vinatti.datasales.data.api_entities.request.CustomerSearchRequest
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.request.RequestSearchMarketing
import com.vinatti.datasales.data.api_entities.response.ResponseChannel
import com.vinatti.datasales.data.api_entities.response.ResponseListStatus
import com.vinatti.datasales.data.api_entities.response.ResponseManagerStaff
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.databinding.DialogSearchCustomBinding
import com.vinatti.datasales.databinding.DialogSearchMarketingBinding
import com.vinatti.datasales.ui.activities.MainActivity
import com.vinatti.datasales.utils.setSingleClick
import com.vinatti.datasales.viewmodel.LoadingViewModel
import com.vinatti.datasales.viewmodel.SearchCustomViewModel
import com.vinatti.datasales.viewmodel.SearchMarketingViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class DialogSearchCustom(val itemSearchClick : ((CustomerSearchRequest)->Unit)) : BaseDialogFragment<DialogSearchCustomBinding>() {

    private val searchCustomViewModel  by viewModels<SearchCustomViewModel>()
    private val loadingViewModel by activityViewModels<LoadingViewModel>()

    @Inject
    lateinit var gson: Gson


    override fun loadControlsAndResize(binding: DialogSearchCustomBinding) {
        binding.apply {
            tvFromDate.setSingleClick {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = activity?.let { activity ->
                    DatePickerDialog(activity, { _, year, monthOfYear, dayOfMonth ->
                        searchCustomViewModel.updateFromDateTextView("$dayOfMonth/${monthOfYear.plus(1)}/$year")

                    }, year, month, day)
                }
                datePickerDialog?.show()
            }
            tvToDate.setSingleClick {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = activity?.let { activity ->
                    DatePickerDialog(activity, { _, year, monthOfYear, dayOfMonth ->
                        searchCustomViewModel.updateToDateTextView("$dayOfMonth/${monthOfYear.plus(1)}/$year")
                    }, year, month, day)
                }
                datePickerDialog?.show()
            }
            tvChannel.setSingleClick {
                searchCustomViewModel.apply {
                    if (listChannel.size == 0) requestChannel()
                    else{
                        showListChannel(searchCustomViewModel.listChannel)
                    }
                }
            }
            tvStatus.setSingleClick {
                searchCustomViewModel.apply {
                    if (listStatus.size == 0) requestListStatus()
                    else{
                        showListStatus(searchCustomViewModel.listStatus)
                    }
                }
            }
            tvPersonal.setSingleClick {
                searchCustomViewModel.apply {
                    if (requireActivity() is MainActivity){
                        (requireActivity() as MainActivity).also { mainActivity ->
                            val responseLogin = mainActivity.getLoginResponse(PrefConst.PREF_LOGIN_RESPONSE)
                            responseLogin?.let {
                                if (it.role == "ADMIN"){
                                    if (listEmployees.size == 0) requestEmployees()
                                    else{
                                        showListEmployees(searchCustomViewModel.listEmployees)
                                    }
                                }else{
                                    searchCustomViewModel.responseEmployee.value?.let {
                                        val listEmployees = arrayListOf<ResponseManagerStaff.ManagerStaffResponse>().apply { add(it) }
                                        showListEmployees(listEmployees)
                                    }
                                }
                            }

                        }
                    }

                }
            }
            btnSearch.setSingleClick {
                searchCustomViewModel.also {
                    val assignedTo = searchCustomViewModel.responseEmployee.value?.id ?: 0
                    val request = CustomerSearchRequest(mobileNumber = binding.edtPhone.text.toString(), channel = it.responseChannel.value?.id ?: 0,
                                                        status = it.responseStatus.value?.id ?: 0, fromDate = it.fromDate.value.toString(),
                                                        toDate = it.toDate.value.toString(),assignedTo = assignedTo)
                    itemSearchClick(request)
                }
                dismiss()

            }
        }

    }

    override fun initView() {
        binding.lifecycleOwner = this
        binding.viewModel  = searchCustomViewModel
        initViewModel()
        observerRequestChannel()
        observerRequestListStatus()
        observerEmployees()

    }
    private fun initViewModel(){
        searchCustomViewModel.apply {
            if (requireActivity() is MainActivity){
                (requireActivity() as MainActivity).also { mainActivity ->
                    val responseLogin = mainActivity.getLoginResponse(PrefConst.PREF_LOGIN_RESPONSE)
                    responseLogin?.let {
                        if (it.role == "ADMIN"){
                            updateEmployee(ResponseManagerStaff.ManagerStaffResponse(id = it.id, name = "Tất cả", isLock = "N"))
                        }else{
                            updateEmployee(ResponseManagerStaff.ManagerStaffResponse(id = it.id, name = it.name))
                        }
                    }
                }
            }
        }
    }

    private fun requestListStatus(){
        try {
            searchCustomViewModel.also { viewModel ->
                val data = gson.toJson("")
                viewModel.requestListStatus(RequestObject(data=data, code = ApiConst.DICTIONARY_GET_LIST_STATUS))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun observerRequestListStatus(){
        try {
            searchCustomViewModel.requestListStatusResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS ->{
                        if (it?.data?.response is ArrayList<*>){
                            (it.data.response as ArrayList<ResponseListStatus.ListStatusResponse>).also { result ->
                                searchCustomViewModel.responseStatus.value?.let {
                                    result.add(0,it)
                                }
                                searchCustomViewModel.updateListStatus(result)
                                showListStatus(result)
                            }
                        }

                    }
                    else-> {}
                }


            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun requestChannel(){
        try {
            searchCustomViewModel.also { viewModel ->
                val data = gson.toJson("")
                viewModel.requestChannel(RequestObject(data=data, code = ApiConst.DICTIONARY_GET_LIST_CHANNEL))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerRequestChannel(){
        try {
            searchCustomViewModel.requestChannelResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS ->{
                        if (it?.data?.response is ArrayList<*>){
                            (it.data.response as ArrayList<ResponseChannel.ChannelResponse>).also { result ->
                                searchCustomViewModel.responseChannel.value?.let {
                                    result.add(0,it)
                                }
                                searchCustomViewModel.updateListChannel(result)
                                showListChannel(result)
                            }
                        }

                    }
                    else-> {}
                }


            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun requestEmployees(){
        try {
            searchCustomViewModel.also {
                val data = gson.toJson("")
                it.requestEmployees(RequestObject(data=data, code = ApiConst.EMPLOYEE_GETS))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerEmployees(){
        try {
            searchCustomViewModel.employeesResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS -> {
                        if (it?.data?.response is ArrayList<*>){
                            (it.data.response as ArrayList<ResponseManagerStaff.ManagerStaffResponse>).also { result->

                                searchCustomViewModel.responseEmployee.value?.let {
                                    result.add(0,it)
                                }

                                val filter = result.filter { item -> item.isLock=="N"}
                                val filterResult = arrayListOf<ResponseManagerStaff.ManagerStaffResponse>().apply {
                                    addAll(filter)
                                }
                                searchCustomViewModel.updateListEmployees(filterResult)
                                showListEmployees(filterResult)
                            }
                        }

                    }
                    else -> {}
                }
            }

        }catch (e:Exception){

        }
    }

    private fun showListChannel(listChannel: ArrayList<ResponseChannel.ChannelResponse>){
        listChannel.find { it.selected }?.selected = false
        DialogListChannel(listChannel){ channelResponse ->
            channelResponse?.let {
                searchCustomViewModel.updateChannel(it)
            }
        }.show(parentFragmentManager,"DialogMarketingDetail")
    }

    private fun showListStatus(listStatus:ArrayList<ResponseListStatus.ListStatusResponse>){
        listStatus.find { it.selected }?.selected = false
        DialogListStatus(listStatus){ statusResponse ->
            statusResponse?.let {
                searchCustomViewModel.updateStatus(it)
            }
        }.show(parentFragmentManager,"DialogMarketingDetail")
    }

    private fun showListEmployees(listEmployees: ArrayList<ResponseManagerStaff.ManagerStaffResponse>){
        listEmployees.find { it.selected }?.selected = false
        DialogListEmployees(listEmployees){ employee->
            employee?.let {
                searchCustomViewModel.updateEmployee(it)
            }

        }.show(parentFragmentManager,"DialogMarketingDetail")

    }


    override fun initCancelable(): Boolean =  false

    override fun initStyle(): Int = R.style.DialogStyle

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogSearchCustomBinding = DialogSearchCustomBinding.inflate(inflater,container,false)
}