package com.vinatti.datasales.ui.fragment.marketing.create

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.vinatti.datasales.R
import com.vinatti.datasales.constants.ApiConst
import com.vinatti.datasales.constants.PrefConst
import com.vinatti.datasales.data.api_entities.request.LoginRequest
import com.vinatti.datasales.data.api_entities.request.RequestAddMarketing
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.ResponseChannel
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.databinding.FragmentCreateMarketingBinding
import com.vinatti.datasales.ui.dialog.DialogListChannel
import com.vinatti.datasales.ui.dialog.SuccessDialog
import com.vinatti.datasales.ui.fragment.BaseFragment
import com.vinatti.datasales.ui.fragment.marketing.MarketingViewModel
import com.vinatti.datasales.utils.AppUtils
import com.vinatti.datasales.utils.CurrencyTextWatcher
import com.vinatti.datasales.utils.InputFilterCharacterNumber
import com.vinatti.datasales.utils.setSingleClick
import com.vinatti.datasales.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class FragmentCreateMarketing(val actionState: (MarketingViewModel.STATE)->Unit) : BaseFragment<FragmentCreateMarketingBinding>(){

    private val createMarketingViewModel by viewModels<CreateMarketingViewModel>()
    private val authViewModel by activityViewModels<AuthViewModel>()

    @Inject
    lateinit var gson:Gson

    override fun loadControlsAndResize(binding: FragmentCreateMarketingBinding?) {
        binding?.apply {
            imgBack.setSingleClick {
                finish()
            }
            rootView.setSingleClick {
                AppUtils.hideKeyboard(it)
            }
            tvFromDate.setSingleClick {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = activity?.let { activity ->
                    DatePickerDialog(activity, { _, year, monthOfYear, dayOfMonth ->
                        tvFromDate.text = "$dayOfMonth/${monthOfYear.plus(1)}/$year"

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
                        tvToDate.text = "$dayOfMonth/${monthOfYear.plus(1)}/$year"

                    }, year, month, day)
                }
                datePickerDialog?.show()
            }
            btnAdd.setSingleClick {
                requestCreate()
            }
            tvChannel.setSingleClick {
                createMarketingViewModel.apply {
                    if (listChannel.size == 0) requestChannel()
                    else{
                        showListChannel(createMarketingViewModel.listChannel)
                    }
                }
            }
        }


    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this
        binding.viewModel = createMarketingViewModel

        initEdt()

    }

    override fun observerState() {
        observerCreate()
        observerRequestChannel()
    }

    private fun initEdt(){
        binding.apply {

            val filtersPrice = arrayOfNulls<InputFilter>(2)
            filtersPrice[0] = InputFilter.LengthFilter(19)
            filtersPrice[1] = InputFilterCharacterNumber()
            edtPrice.filters = filtersPrice
            edtPrice.addTextChangedListener(CurrencyTextWatcher(edtPrice))

            val filtersAmount = arrayOfNulls<InputFilter>(2)
            filtersAmount[0] = InputFilter.LengthFilter(19)
            filtersAmount[1] = InputFilterCharacterNumber()
            edtSumAmount.filters = filtersAmount
            edtSumAmount.addTextChangedListener(CurrencyTextWatcher(edtSumAmount))

            val filtersCountCustom = arrayOfNulls<InputFilter>(2)
            filtersCountCustom[0] = InputFilter.LengthFilter(19)
            filtersCountCustom[1] = InputFilterCharacterNumber()
            edtCountCustom.filters = filtersCountCustom
            edtCountCustom.addTextChangedListener(CurrencyTextWatcher(edtCountCustom))


        }
    }

    private fun requestCreate(){
        if (confirmInput()){
            val responseLogin = mActivity?.getLoginResponse(PrefConst.PREF_LOGIN_RESPONSE)
            responseLogin?.let { response ->
                createMarketingViewModel.also {
                    val loginRequest = RequestAddMarketing(channel = it.responseChannel.value?.id ?: 0,
                        cost = it.price.value?.replace(",","")?.toInt() ?: 0,
                        totalAmount = it.sumAmount.value?.replace(",","")?.toInt() ?: 0,
                        quantity = it.countCustom.value?.toInt() ?: 0,
                        createdBy = response.id, fromDate = it.fromDate.value.toString(), toDate = it.toDate.value.toString())
                    val data = gson.toJson(loginRequest)
                    it.requestCreate(RequestObject(data=data, code = ApiConst.MARKETING_CREATE))
                }
            }

        }
    }
    private fun observerCreate(){
        try {
            createMarketingViewModel.createResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS->{
                        SuccessDialog(resources.getString(R.string.str_add_success)){
                            finish()
                            actionState(MarketingViewModel.STATE.ADD_ITEM)
                        }.show(childFragmentManager,"FragmentCreateMarketing")
                    }
                    else ->{}
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun requestChannel(){
        try {
            createMarketingViewModel.also { viewModel ->
                val data = gson.toJson("")
                viewModel.requestChannel(RequestObject(data=data, code = ApiConst.DICTIONARY_GET_LIST_CHANNEL))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerRequestChannel(){
        try {
            createMarketingViewModel.requestChannelResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS ->{
                        if (it?.data?.response is ArrayList<*>){
                            (it.data.response as ArrayList<ResponseChannel.ChannelResponse>).also { result ->
                                createMarketingViewModel.updateListChannel(result)
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

    private fun showListChannel(listChannel:ArrayList<ResponseChannel.ChannelResponse>){
        listChannel.find { it.selected }?.selected = false
        DialogListChannel(listChannel){ channelResponse ->
            channelResponse?.let {
                createMarketingViewModel.updateChannel(it)
            }
        }.show(parentFragmentManager,"DialogMarketingDetail")
    }

    private fun validateFormDate():Boolean{
        try {
            val formDate = binding.tvFromDate.text.toString()
            if (formDate.isBlank()){
                toast(resources.getString(R.string.str_error_form_date_empty))
                return false
            }

        }catch (e:Exception){
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun validateToDate():Boolean{
        try {
            val toDate = binding.tvToDate.text.toString()
            if (toDate.isBlank()){
                toast(resources.getString(R.string.str_error_to_date_empty))
                return false
            }

        }catch (e:Exception){
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun validateChannel():Boolean{
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

    private fun validatePrice():Boolean{
        try {
            val price = binding.edtPrice.text.toString()
            if (price.isBlank()){
                toast(resources.getString(R.string.str_error_price_empty))
                return false
            }

        }catch (e:Exception){
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun validateAmount():Boolean{
        try {
            val amount = binding.edtSumAmount.text.toString()
            if (amount.isBlank()){
                toast(resources.getString(R.string.str_error_sum_amount_empty))
                return false
            }

        }catch (e:Exception){
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun validateCountCustom():Boolean{
        try {
            val amount = binding.edtCountCustom.text.toString()
            if (amount.isBlank()){
                toast(resources.getString(R.string.str_error_count_custom_empty))
                return false
            }

        }catch (e:Exception){
            e.printStackTrace()
            return false
        }
        return true
    }
    private fun confirmInput():Boolean{
        if (!validateFormDate() || !validateToDate() || !validateChannel() || !validatePrice() || !validateAmount() || !validateCountCustom()) {
            return false
        }
        return true
    }

    override fun getLayoutId(): Int  = R.layout.fragment_create_marketing

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