package com.vinatti.datasales.ui.dialog

import android.app.DatePickerDialog
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.vinatti.datasales.R
import com.vinatti.datasales.constants.ApiConst
import com.vinatti.datasales.data.api_entities.request.MarketingRequest
import com.vinatti.datasales.data.api_entities.request.RequestEitMarketing
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.ResponseChannel
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.databinding.DialogMarketingDetailBinding
import com.vinatti.datasales.ui.fragment.BaseFragment
import com.vinatti.datasales.ui.fragment.marketing.MarketingViewModel
import com.vinatti.datasales.utils.AppUtils
import com.vinatti.datasales.utils.CurrencyTextWatcher
import com.vinatti.datasales.utils.InputFilterCharacterNumber
import com.vinatti.datasales.utils.setSingleClick
import com.vinatti.datasales.viewmodel.LoadingViewModel
import com.vinatti.datasales.viewmodel.MarketingDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class DialogMarketingDetail : BaseDialogFragment<DialogMarketingDetailBinding>() {

    private val marketingViewModel by viewModels<MarketingViewModel> ({requireParentFragment()})
    private val detailMarketingViewModel by viewModels<MarketingDetailViewModel>()
    private val loadingViewModel by activityViewModels<LoadingViewModel>()

    @Inject
    lateinit var gson:Gson


    override fun loadControlsAndResize(binding: DialogMarketingDetailBinding) {
        binding.apply {
            imgBack.setSingleClick {
                dismiss()
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
            tvChannel.setSingleClick {
                detailMarketingViewModel.apply {
                    if (listChannel.size == 0) requestChannel()
                    else{
                       showListChannel(detailMarketingViewModel.listChannel)
                    }
                }

            }
            btnUpdate.setSingleClick {
                if (confirmInput()){
                    requestUpdateMarketing()
                }

            }
        }
    }

    override fun initView() {
        binding.item = marketingViewModel.contentSelected
        binding.viewModel = detailMarketingViewModel
        binding.lifecycleOwner = this

        initEdt()
        initViewModel()
        observerRequestChannel()
        observerUpdate()


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


        }
    }
    private fun initViewModel(){
        detailMarketingViewModel.apply {
            val responseChannel = ResponseChannel.ChannelResponse(id = marketingViewModel.contentSelected?.channel?: 0,
                                                                  name = marketingViewModel.contentSelected?.channelName ?: "")
            updateChannel(responseChannel)

        }
    }

    private fun requestUpdateMarketing(){
        try {
            binding.apply {
                detailMarketingViewModel.also { viewModel ->
                    val fromDate = tvFromDate.text.toString()
                    val toDate = tvToDate.text.toString()
                    val price = edtPrice.text.toString().replace(",","").toInt()
                    val amount = edtSumAmount.text.toString().replace(",","").toInt()
                    val countCustom = edtCountCustom.text.toString().toInt()
                    val request = RequestEitMarketing(id = marketingViewModel.contentSelected?.iD ?: 0,
                                                      channel = detailMarketingViewModel.responseChannel.value?.id ?: 0,
                                                      cost = price, totalAmount = amount, quantity = countCustom, fromDate = fromDate, toDate = toDate )
                    val data = gson.toJson(request)
                    viewModel.requestUpdateMarketing(RequestObject(data=data, code = ApiConst.MARKETING_EDIT))
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerUpdate(){
        try {
            detailMarketingViewModel.updateMarketingResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS->{
                        updateEditMarketing()
                        SuccessDialog(resources.getString(R.string.str_update_custom_success)){
                            dismiss()
                        }.show(childFragmentManager,"DialogMarketingDetail")

                    }
                    else -> {}
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun requestChannel(){
        try {
            detailMarketingViewModel.also { viewModel ->
                val data = gson.toJson("")
                viewModel.requestChannel(RequestObject(data=data, code = ApiConst.DICTIONARY_GET_LIST_CHANNEL))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerRequestChannel(){
        try {
            detailMarketingViewModel.requestChannelResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS ->{
                        if (it?.data?.response is ArrayList<*>){
                            (it.data.response as ArrayList<ResponseChannel.ChannelResponse>).also { result ->
                                detailMarketingViewModel.updateListChannel(result)
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
                detailMarketingViewModel.updateChannel(it)
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

    private fun updateEditMarketing(){
        try {
            binding.apply {
                marketingViewModel.contentSelected?.let {
                    with(it){
                        fromDate = tvFromDate.text.toString()
                        toDate = tvToDate.text.toString()
                        channel = detailMarketingViewModel.responseChannel.value?.id ?: 0
                        channelName = tvChannel.text.toString()
                        quantity = edtCountCustom.text.toString().toInt()
                        cost = edtCountCustom.text.toString().replace(",","").toInt()
                        totalAmount = edtSumAmount.text.toString().replace(",","").toInt()
                    }
                }
            }

            marketingViewModel.setActionSate(MarketingViewModel.STATE.NOTIFY_DATA)
        }catch (e:Exception){
            e.printStackTrace()
        }


    }

    override fun initStyle(): Int  = R.style.FullScreenDialog

    override fun getViewBinding(inflater: LayoutInflater,
                                container: ViewGroup?
    ): DialogMarketingDetailBinding = DialogMarketingDetailBinding.inflate(inflater,container,false)
}