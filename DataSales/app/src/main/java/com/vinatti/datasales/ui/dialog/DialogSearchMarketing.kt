package com.vinatti.datasales.ui.dialog

import android.app.DatePickerDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.vinatti.datasales.R
import com.vinatti.datasales.constants.ApiConst
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.request.RequestSearchMarketing
import com.vinatti.datasales.data.api_entities.response.ResponseChannel
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.databinding.DialogSearchMarketingBinding
import com.vinatti.datasales.ui.fragment.BaseFragment
import com.vinatti.datasales.utils.setSingleClick
import com.vinatti.datasales.viewmodel.LoadingViewModel
import com.vinatti.datasales.viewmodel.SearchMarketingViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DialogSearchMarketing(val itemSearchClick : ((RequestSearchMarketing)->Unit)) : BaseDialogFragment<DialogSearchMarketingBinding>() {

    private val searchMarketingViewModel  by viewModels<SearchMarketingViewModel>()
    private val loadingViewModel by activityViewModels<LoadingViewModel>()

    @Inject
    lateinit var gson:Gson


    override fun loadControlsAndResize(binding: DialogSearchMarketingBinding) {
        binding.apply {
            tvFromDate.setSingleClick {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = activity?.let { activity ->
                    DatePickerDialog(activity, { _, year, monthOfYear, dayOfMonth ->
                        searchMarketingViewModel.updateFromDateTextView("$dayOfMonth/${monthOfYear.plus(1)}/$year")

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
                        searchMarketingViewModel.updateToDateTextView("$dayOfMonth/${monthOfYear.plus(1)}/$year")
                    }, year, month, day)
                }
                datePickerDialog?.show()
            }
            tvChannel.setSingleClick {
                searchMarketingViewModel.apply {
                    if (listChannel.size == 0) requestChannel()
                    else{
                        showListChannel(searchMarketingViewModel.listChannel)
                    }
                }
            }
            btnSearch.setSingleClick {
                searchMarketingViewModel.also {
                    val request = RequestSearchMarketing(channel = it.responseChannel.value?.id ?: 0,
                                                         fromDate = it.fromDate.value.toString(),
                                                         toDate = it.toDate.value.toString())
                    itemSearchClick(request)
                }
                dismiss()

            }
        }

    }

    override fun initView() {
        binding.lifecycleOwner = this
        binding.viewModel  = searchMarketingViewModel
        observerRequestChannel()

    }

    private fun requestChannel(){
        try {
            searchMarketingViewModel.also { viewModel ->
                val data = gson.toJson("")
                viewModel.requestChannel(RequestObject(data=data, code = ApiConst.DICTIONARY_GET_LIST_CHANNEL))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerRequestChannel(){
        try {
            searchMarketingViewModel.requestChannelResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS ->{
                        if (it?.data?.response is ArrayList<*>){
                            (it.data.response as ArrayList<ResponseChannel.ChannelResponse>).also { result ->
                                searchMarketingViewModel.responseChannel.value?.let {
                                    result.add(0,it)
                                }
                                searchMarketingViewModel.updateListChannel(result)
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
                searchMarketingViewModel.updateChannel(it)
            }
        }.show(parentFragmentManager,"DialogMarketingDetail")
    }


    override fun initCancelable(): Boolean =  false

    override fun initStyle(): Int = R.style.DialogStyle

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogSearchMarketingBinding  = DialogSearchMarketingBinding.inflate(inflater,container,false)
}