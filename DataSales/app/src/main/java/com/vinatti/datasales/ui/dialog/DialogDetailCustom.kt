package com.vinatti.datasales.ui.dialog

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.vinatti.datasales.R
import com.vinatti.datasales.constants.ApiConst
import com.vinatti.datasales.constants.PrefConst
import com.vinatti.datasales.data.api_entities.request.CustomerAddRequest
import com.vinatti.datasales.data.api_entities.request.CustomerEditRequest
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.ResponseChannel
import com.vinatti.datasales.data.api_entities.response.ResponseListStatus
import com.vinatti.datasales.data.api_entities.response.ResponseLogin
import com.vinatti.datasales.data.api_entities.response.ResponseManagerStaff
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.databinding.DialogDetailCustomBinding
import com.vinatti.datasales.ui.activities.MainActivity
import com.vinatti.datasales.ui.fragment.custom.CustomViewModel
import com.vinatti.datasales.ui.fragment.marketing.MarketingViewModel
import com.vinatti.datasales.utils.setSingleClick
import com.vinatti.datasales.viewmodel.DetailCustomViewModel
import com.vinatti.datasales.viewmodel.LoadingViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DialogDetailCustom :BaseDialogFragment<DialogDetailCustomBinding>() {

    private val detailCustomViewModel by viewModels<DetailCustomViewModel>()
    private val customViewModel by viewModels<CustomViewModel> ({requireParentFragment()})
    private val loadingViewModel by activityViewModels<LoadingViewModel>()

    @Inject
    lateinit var gson:Gson


    override fun loadControlsAndResize(binding: DialogDetailCustomBinding) {
        binding.apply {
            imgBack.setSingleClick {
                dismiss()
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
                detailCustomViewModel.apply {
                    if (listChannel.size == 0) requestChannel()
                    else{
                        showListChannel(detailCustomViewModel.listChannel)
                    }
                }
            }
            tvStatus.setSingleClick {
                detailCustomViewModel.apply {
                    if (listStatus.size == 0) requestListStatus()
                    else{
                        showListStatus(detailCustomViewModel.listStatus)
                    }
                }
            }
            tvPersonal.setSingleClick {
                detailCustomViewModel.apply {
                    if (requireActivity() is MainActivity){
                        (requireActivity() as MainActivity).also { mainActivity ->
                            val responseLogin = mainActivity.getLoginResponse(PrefConst.PREF_LOGIN_RESPONSE)
                            responseLogin?.let {
                                if (it.role == "ADMIN"){
                                    if (listEmployees.size == 0) requestEmployees()
                                    else{
                                        showListEmployees(detailCustomViewModel.listEmployees)
                                    }
                                }else{
                                    detailCustomViewModel.responseEmployee.value?.let {
                                        val listEmployees = arrayListOf<ResponseManagerStaff.ManagerStaffResponse>().apply { add(it) }
                                        showListEmployees(listEmployees)
                                    }
                                }
                            }

                        }
                    }

                }
            }
            btnAdd.setSingleClick {
                if (confirmInput()) requestEditCustom()
            }
        }
    }

    override fun initView() {

        binding.lifecycleOwner = this
        binding.viewModel = detailCustomViewModel
        customViewModel.contentSelected?.let {

            binding.item = it
        }
        initViewModel()
        observerRequestChannel()
        observerRequestListStatus()
        observerEditCustom()
        observerEmployees()

    }
    private fun initViewModel(){
        customViewModel.contentSelected?.let {
            detailCustomViewModel.apply {
                contentName.value = it.name
                contentPhone.value = it.mobileNumber
                contentAddress.value = it.address
                contentAdvisory.value = it.content
                contentAppointment.value = it.appointmentInfo
                timeDate.value = it.appointmentDate
                timeHours.value = it.appointmentTime

                updateChannel(ResponseChannel.ChannelResponse(it.channel,it.channelName))
                updateStatus(ResponseListStatus.ListStatusResponse(it.status,it.statusName))
                updateEmployee(ResponseManagerStaff.ManagerStaffResponse(id=it.assignedTo, name = it.assignedName))
//                if (requireActivity() is MainActivity){
//                    (requireActivity() as MainActivity).also { mainActivity->
//                        val responseLogin = mainActivity.getLoginResponse(PrefConst.PREF_LOGIN_RESPONSE)
//                        responseLogin?.let {
////                            contentCaringStaff.value = it.name
//                        }
//
//                    }
//                }

            }

        }

    }

    private fun requestEmployees(){
        try {
            detailCustomViewModel.also {
                val data = gson.toJson("")
                it.requestEmployees(RequestObject(data=data, code = ApiConst.EMPLOYEE_GETS))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerEmployees(){
        try {
            detailCustomViewModel.employeesResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS -> {
                        if (it?.data?.response is ArrayList<*>){
                            (it.data.response as ArrayList<ResponseManagerStaff.ManagerStaffResponse>).also { result->
                                val filter = result.filter { item -> item.isLock=="N" }
                                val filterResult = arrayListOf<ResponseManagerStaff.ManagerStaffResponse>().apply {

                                    addAll(filter)
                                }
                                detailCustomViewModel.updateListEmployees(filterResult)
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
            detailCustomViewModel.also { viewModel ->
                val data = gson.toJson("")
                viewModel.requestChannel(RequestObject(data=data, code = ApiConst.DICTIONARY_GET_LIST_CHANNEL))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerRequestChannel(){
        try {
            detailCustomViewModel.requestChannelResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS ->{
                        if (it?.data?.response is ArrayList<*>){
                            (it.data.response as ArrayList<ResponseChannel.ChannelResponse>).also { result ->
                                detailCustomViewModel.updateListChannel(result)
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
            detailCustomViewModel.also { viewModel ->
                val data = gson.toJson("")
                viewModel.requestListStatus(RequestObject(data=data, code = ApiConst.DICTIONARY_GET_LIST_STATUS))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun observerRequestListStatus(){
        try {
            detailCustomViewModel.requestListStatusResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS ->{
                        if (it?.data?.response is ArrayList<*>){
                            (it.data.response as ArrayList<ResponseListStatus.ListStatusResponse>).also { result ->
                                detailCustomViewModel.updateListStatus(result)
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

    private fun requestEditCustom(){
        try {
            if (requireActivity() is MainActivity){
                (requireActivity() as MainActivity).also { mainActivity ->
                    val responseLogin = mainActivity.getLoginResponse(PrefConst.PREF_LOGIN_RESPONSE)
                    responseLogin?.let {
                        detailCustomViewModel.also { viewModel ->
                            val name = detailCustomViewModel.contentName.value ?: ""
                            val phone = detailCustomViewModel.contentPhone.value ?: ""
                            val address = binding.edtAddress.text.toString()
                            val content = binding.edtContent.text.toString()
                            val channel = detailCustomViewModel.responseChannel.value?.id ?: 0
                            val status = detailCustomViewModel.responseStatus.value?.id ?: 0
                            val appointmentDate = binding.tvTimeDate.text.toString()
                            val appointmentTime = binding.tvHours.text.toString()
                            val appointmentInfo = binding.edtAppointmentContent.text.toString()
                            val request = CustomerEditRequest(id = customViewModel.contentSelected?.id ?: 0 ,name = name, mobileNumber = phone, address = address,content = content,
                                channel = channel, status = status,appointmentTime = appointmentTime ,
                                appointmentDate = appointmentDate, appointmentInfo = appointmentInfo,
                                modifiedBy = it.id, assignedTo = detailCustomViewModel.responseEmployee.value?.id ?: 0)

                            val data = gson.toJson(request)
                            viewModel.requestEditCustom(RequestObject(data=data, code = ApiConst.CUSTOMER_EDIT))
                        }
                    }
                }
            }


        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerEditCustom(){
        try {
            detailCustomViewModel.editCustomResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS->{
                        updateEditCustom()
                        SuccessDialog(resources.getString(R.string.str_add_success)){
                            dismiss()
                        }.show(childFragmentManager,"DialogDetailCustom")

                    }
                    else -> {}
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }
    private fun updateEditCustom(){
        try {
            binding.apply {
                this@DialogDetailCustom.customViewModel.contentSelected?.let {
                    with(it){
                        name = binding.edtName.text.toString()
                        mobileNumber = binding.edtPhone.text.toString()
                        address = binding.edtAddress.text.toString()
                        content = binding.edtContent.text.toString()
                        channel = detailCustomViewModel.responseChannel.value?.id ?: 0
                        channelName = detailCustomViewModel.responseChannel.value?.name ?: ""
                        status = detailCustomViewModel.responseStatus.value?.id ?: 0
                        statusName = detailCustomViewModel.responseStatus.value?.name ?: ""
                        appointmentDate = binding.tvTimeDate.text.toString()
                        appointmentTime = binding.tvHours.text.toString()
                        appointmentInfo = binding.edtAppointmentContent.text.toString()
                        assignedName = binding.tvPersonal.text.toString()

                    }
                }
            }

            customViewModel.setActionSate(CustomViewModel.STATE.NOTIFY_DATA)
        }catch (e:Exception){
            e.printStackTrace()
        }


    }

    private fun showListChannel(listChannel: java.util.ArrayList<ResponseChannel.ChannelResponse>){
        listChannel.find { it.selected }?.selected = false
        DialogListChannel(listChannel){ channelResponse ->
            channelResponse?.let {
                detailCustomViewModel.updateChannel(it)
            }
        }.show(parentFragmentManager,"DialogMarketingDetail")
    }
    private fun showListStatus(listStatus: java.util.ArrayList<ResponseListStatus.ListStatusResponse>){
        listStatus.find { it.selected }?.selected = false
        DialogListStatus(listStatus){ statusResponse ->
            statusResponse?.let {
                detailCustomViewModel.updateStatus(it)
            }
        }.show(parentFragmentManager,"DialogMarketingDetail")
    }

    private fun showListEmployees(listEmployees: ArrayList<ResponseManagerStaff.ManagerStaffResponse>){
        listEmployees.find { it.selected }?.selected = false
        DialogListEmployees(listEmployees){ employee->
            employee?.let {
                detailCustomViewModel.updateEmployee(it)
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

    override fun initStyle(): Int  = R.style.FullScreenDialog

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogDetailCustomBinding  = DialogDetailCustomBinding.inflate(inflater,container,false)
}