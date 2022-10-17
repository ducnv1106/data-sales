package com.vinatti.datasales.ui.fragment.custom.create

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.vinatti.datasales.R
import com.vinatti.datasales.constants.ApiConst
import com.vinatti.datasales.constants.PrefConst
import com.vinatti.datasales.data.api_entities.request.CustomerAddRequest
import com.vinatti.datasales.data.api_entities.request.RequestEmployeeByID
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.ResponseChannel
import com.vinatti.datasales.data.api_entities.response.ResponseListStatus
import com.vinatti.datasales.data.api_entities.response.ResponseManagerStaff
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.databinding.FragmentCreateCustomBinding
import com.vinatti.datasales.ui.activities.MainActivity
import com.vinatti.datasales.ui.dialog.DialogListChannel
import com.vinatti.datasales.ui.dialog.DialogListEmployees
import com.vinatti.datasales.ui.dialog.DialogListStatus
import com.vinatti.datasales.ui.dialog.SuccessDialog
import com.vinatti.datasales.ui.fragment.BaseFragment
import com.vinatti.datasales.ui.fragment.custom.CustomViewModel
import com.vinatti.datasales.ui.fragment.marketing.MarketingViewModel
import com.vinatti.datasales.utils.setSingleClick
import com.vinatti.datasales.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class FragmentCreateCustom(val actionState: (CustomViewModel.STATE)->Unit) : BaseFragment<FragmentCreateCustomBinding>() {
    private val createCustomViewModel by viewModels<CreateCustomViewModel>()
    private val authViewModel by activityViewModels<AuthViewModel>()

    @Inject
    lateinit var gson:Gson

    override fun loadControlsAndResize(binding: FragmentCreateCustomBinding?) {
        binding?.apply {
            imgBack.setSingleClick {
                finish()
            }
            tvTimeDate.setSingleClick {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = activity?.let { activity ->
                    DatePickerDialog(activity, { _, year, monthOfYear, dayOfMonth ->
                        tvTimeDate.text = "$dayOfMonth/${monthOfYear.plus(1)}/$year"

                    }, year, month, day)
                }
                datePickerDialog?.show()
            }
            tvHours.setSingleClick {
                val c = Calendar.getInstance()
                val hour = c.get(Calendar.HOUR_OF_DAY)
                val minute = c.get(Calendar.MINUTE)
                val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                    c.set(Calendar.HOUR_OF_DAY, hour)
                    c.set(Calendar.MINUTE, minute)
                    val mSelectedTime = SimpleDateFormat("HH:mm:ss").format(c.time)
                    tvHours.text = mSelectedTime
                }
                val timePickerDialog = TimePickerDialog(requireContext(), AlertDialog.THEME_HOLO_LIGHT, timeSetListener, hour, minute, DateFormat.is24HourFormat(requireContext()))
                timePickerDialog.show()
            }
            tvChannel.setSingleClick {
                createCustomViewModel.apply {
                    if (listChannel.size == 0) requestChannel()
                    else{
                        showListChannel(createCustomViewModel.listChannel)
                    }
                }
            }
            tvStatus.setSingleClick {
                createCustomViewModel.apply {
                    if (listStatus.size == 0) requestListStatus()
                    else{
                        showListStatus(createCustomViewModel.listStatus)
                    }
                }
            }
            tvPersonal.setSingleClick {
                createCustomViewModel.apply {
                    val responseLogin = mActivity?.getLoginResponse(PrefConst.PREF_LOGIN_RESPONSE)
                    responseLogin?.let {
                        if (it.role == "ADMIN"){
                            if (listEmployees.size == 0) requestEmployees()
                            else{
                                showListEmployees(createCustomViewModel.listEmployees)
                            }
                        }else{
                            createCustomViewModel.responseEmployee.value?.let {
                                val listEmployees = arrayListOf<ResponseManagerStaff.ManagerStaffResponse>().apply { add(it) }
                                showListEmployees(listEmployees)
                            }
                        }
                    }
                }
            }
            btnAdd.setSingleClick {
                if (confirmInput()) requestAddCustom()
            }
        }

    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this
        initViewModel()
        binding.viewModel = createCustomViewModel

    }

    override fun observerState() {
        observerRequestChannel()
        observerRequestListStatus()
        observerAddCustom()
        observerEmployees()
    }

    private fun initViewModel(){
        val responseLogin = mActivity?.getLoginResponse(PrefConst.PREF_LOGIN_RESPONSE)
        responseLogin?.let {
            createCustomViewModel.apply {
                updateEmployee(ResponseManagerStaff.ManagerStaffResponse(id = it.id, name = it.name))
            }
        }


    }

    private fun requestEmployees(){
        try {
            createCustomViewModel.also {
                val data = gson.toJson("")
                it.requestEmployees(RequestObject(data=data, code = ApiConst.EMPLOYEE_GETS))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerEmployees(){
        try {
            createCustomViewModel.employeesResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS -> {
                        if (it?.data?.response is ArrayList<*>){
                            (it.data.response as ArrayList<ResponseManagerStaff.ManagerStaffResponse>).also { result->
                                val filter = result.filter { item -> item.isLock=="N"}
                                val filterResult = arrayListOf<ResponseManagerStaff.ManagerStaffResponse>().apply {
                                    addAll(filter)
                                }
                                createCustomViewModel.updateListEmployees(filterResult)
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

    private fun requestChannel(){
        try {
            createCustomViewModel.also { viewModel ->
                val data = gson.toJson("")
                viewModel.requestChannel(RequestObject(data=data, code = ApiConst.DICTIONARY_GET_LIST_CHANNEL))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerRequestChannel(){
        try {
            createCustomViewModel.requestChannelResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS ->{
                        if (it?.data?.response is ArrayList<*>){
                            (it.data.response as ArrayList<ResponseChannel.ChannelResponse>).also { result ->
                                createCustomViewModel.updateListChannel(result)
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

    private fun requestListStatus(){
        try {
            createCustomViewModel.also { viewModel ->
                val data = gson.toJson("")
                viewModel.requestListStatus(RequestObject(data=data, code = ApiConst.DICTIONARY_GET_LIST_STATUS))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun observerRequestListStatus(){
        try {
            createCustomViewModel.requestListStatusResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS ->{
                        if (it?.data?.response is ArrayList<*>){
                            (it.data.response as ArrayList<ResponseListStatus.ListStatusResponse>).also { result ->
                                createCustomViewModel.updateListStatus(result)
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

    private fun requestAddCustom(){
        try {
            val responseLogin = mActivity?.getLoginResponse(PrefConst.PREF_LOGIN_RESPONSE)
            responseLogin?.let {
                createCustomViewModel.also { viewModel ->
                    val name = binding.edtName.text.toString()
                    val phone = binding.edtPhone.text.toString()
                    val address = binding.edtAddress.text.toString()
                    val content = binding.edtContent.text.toString()
                    val channel = createCustomViewModel.responseChannel.value?.id ?: 0
                    val status = createCustomViewModel.responseStatus.value?.id ?: 0
                    val appointmentDate = binding.tvTimeDate.text.toString()
                    val appointmentTime = binding.tvHours.text.toString()
                    val appointmentInfo = binding.edtAppointmentContent.text.toString()


                    val request = CustomerAddRequest(name = name, mobileNumber = phone, address = address,content = content,
                                                    channel = channel, status = status,appointmentTime = appointmentTime ,
                                                    appointmentDate = appointmentDate, appointmentInfo = appointmentInfo,
                                                    createdBy = it.id, assignedTo = createCustomViewModel.responseEmployee.value?.id ?: 0)

                    val data = gson.toJson(request)
                    viewModel.requestAddCustom(RequestObject(data=data, code = ApiConst.CUSTOMER_CREATE))
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerAddCustom(){
        try {
            createCustomViewModel.requestAddCustomResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS ->{
                        actionState(CustomViewModel.STATE.ADD_ITEM)
                        SuccessDialog(resources.getString(R.string.str_create_custom_success)){
                            finish()
                        }.show(childFragmentManager,"FragmentCreateCustom")

                    }
                    else -> {}
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun showListChannel(listChannel:ArrayList<ResponseChannel.ChannelResponse>){
        listChannel.find { it.selected }?.selected = false
        DialogListChannel(listChannel){ channelResponse ->
            channelResponse?.let {
                createCustomViewModel.updateChannel(it)
            }
        }.show(parentFragmentManager,"DialogMarketingDetail")
    }
    private fun showListStatus(listStatus:ArrayList<ResponseListStatus.ListStatusResponse>){
        listStatus.find { it.selected }?.selected = false
        DialogListStatus(listStatus){ statusResponse ->
            statusResponse?.let {
                createCustomViewModel.updateStatus(it)
            }
        }.show(parentFragmentManager,"DialogMarketingDetail")
    }

    private fun showListEmployees(listEmployees:ArrayList<ResponseManagerStaff.ManagerStaffResponse>){
        listEmployees.find { it.selected }?.selected = false
        DialogListEmployees(listEmployees){ employee->
            employee?.let {
                createCustomViewModel.updateEmployee(it)
            }

        }.show(parentFragmentManager,"DialogMarketingDetail")

    }

    private fun validateFullName():Boolean{
        try {
            val fullName = binding.edtName.text.toString()
            if (fullName.isBlank()){
                toast(resources.getString(R.string.str_error_full_name_empty))
                return false
            }

        }catch (e:Exception){
            e.printStackTrace()
            return false
        }
        return true
    }
    private fun validatePhoneNumber():Boolean{
        val phone: String = binding.edtPhone.text.toString()
        if (phone.isBlank()) {
            toast(resources.getString(R.string.error_empty_phone_number))
            return false
        }
        if (phone[0] != '0' || phone.length != 10) {
            toast(resources.getString(R.string.error_warning_phone))
            return false
        }
        return true
    }
    private fun validateAddress():Boolean{
        try {
            val address = binding.edtAddress.text.toString()
            if (address.isBlank()){
                toast(resources.getString(R.string.str_error_address_empty))
                return false
            }

        }catch (e:Exception){
            e.printStackTrace()
            return false
        }
        return true
    }
    private fun validateContentAdvisory():Boolean{
        try {
            val contentAdvisory = binding.edtContent.text.toString()
            if (contentAdvisory.isBlank()){
                toast(resources.getString(R.string.str_error_content_advisory_empty))
                return false
            }

        }catch (e:Exception){
            e.printStackTrace()
            return false
        }
        return true
    }
    private fun validateChanel():Boolean{
        try {
            val channel = binding.tvChannel.text.toString()
            if (channel.isBlank()){
                toast(resources.getString(R.string.str_error_channel_empty))
                return false
            }

        }catch (e:Exception){
            e.printStackTrace()
            return false
        }
        return true
    }
    private fun validateStatus():Boolean{
        try {
            val status = binding.tvStatus.text.toString()
            if (status.isBlank()){
                toast(resources.getString(R.string.str_error_status_empty))
                return false
            }

        }catch (e:Exception){
            e.printStackTrace()
            return false
        }
        return true
    }
    private fun validateTimeDate():Boolean{
        try {
            val timeDate = binding.tvTimeDate.text.toString()
            if (timeDate.isBlank()){
                toast(resources.getString(R.string.str_error_time_date_empty))
                return false
            }

        }catch (e:Exception){
            e.printStackTrace()
            return false
        }
        return true
    }
    private fun validateHours():Boolean{
        try {
            val timeDate = binding.tvHours.text.toString()
            if (timeDate.isBlank()){
                toast(resources.getString(R.string.str_error_hours_empty))
                return false
            }

        }catch (e:Exception){
            e.printStackTrace()
            return false
        }
        return true
    }
    private fun validateContentAppointment():Boolean{
        try {
            val content = binding.edtAppointmentContent.text.toString()
            if (content.isBlank()){
                toast(resources.getString(R.string.str_error_content_appointment_empty))
                return false
            }

        }catch (e:Exception){
            e.printStackTrace()
            return false
        }
        return true
    }
    private fun validateContentPersonal():Boolean{
        try {
            val contentPersonal = binding.tvPersonal.text.toString()
            if (contentPersonal.isBlank()){
                toast(resources.getString(R.string.str_error_content_personal_empty))
                return false
            }

        }catch (e:Exception){
            e.printStackTrace()
            return false
        }
        return true
    }
    private fun confirmInput():Boolean{
        if (!validateFullName() || !validatePhoneNumber() || !validateAddress() ||
            !validateContentAdvisory() || !validateChanel() || !validateStatus() ||
            !validateTimeDate() || !validateHours() || !validateContentAppointment() || !validateContentPersonal()){
            return false
        }
        return true
    }

    override fun getLayoutId(): Int = R.layout.fragment_create_custom

    override fun finish() {
        mActivity?.closeFuncChildScreen(this)
    }

    override fun backToPrevious() {
        finish()
    }

    override fun isBackPreviousEnable(): Boolean {
        return true
    }
}