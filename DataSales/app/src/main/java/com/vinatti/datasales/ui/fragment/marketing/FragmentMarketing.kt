package com.vinatti.datasales.ui.fragment.marketing

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.vinatti.datasales.R
import com.vinatti.datasales.constants.ApiConst
import com.vinatti.datasales.data.api_entities.request.*
import com.vinatti.datasales.data.api_entities.response.ResponseMarketing
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.databinding.FragmentMarketingBinding
import com.vinatti.datasales.ui.activities.MainActivity
import com.vinatti.datasales.ui.dialog.DialogMarketingDetail
import com.vinatti.datasales.ui.dialog.DialogSearchMarketing
import com.vinatti.datasales.ui.fragment.BaseFragment
import com.vinatti.datasales.utils.setSingleClick
import com.vinatti.datasales.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FragmentMarketing : BaseFragment<FragmentMarketingBinding>() {

    private val marketingViewModel by viewModels<MarketingViewModel>()
    private val authViewModel by activityViewModels<AuthViewModel>()
    private lateinit var adapter: MarketingAdapter

    @Inject
    lateinit var gson: Gson

    override fun loadControlsAndResize(binding: FragmentMarketingBinding?) {
        binding?.apply {
            navigation.apply {
                btnMarketing.isChecked = true
                tabHome.setSingleClick {
                    authViewModel.updateTabCurrent(AuthViewModel.TabHomeCurrent.HOME)
                    finish()
                }
                tabCustom.setSingleClick {
                    mActivity?.showCustom()
                }
                tabPersonal.setSingleClick {

                }
                tabPersonal.setSingleClick {
                    mActivity?.showPersonal()
                }
            }

            imgAdd.setSingleClick {
                mActivity?.showCreateMarketing{
                    if (it==MarketingViewModel.STATE.ADD_ITEM){
                        marketingViewModel.setActionSate(it)
                        requestMarketing()
                    }
                }
            }
            imgSearch.setSingleClick {
                DialogSearchMarketing{
                    requestSearch(it)
                }.show(childFragmentManager,"FragmentMarketing")
            }
        }

    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this
        binding.viewModel = marketingViewModel
        initAdapter()
        requestMarketing()

    }

    override fun observerState() {
        observerMarketing()
        observerStateNotify()
    }

    private fun initAdapter(){
        binding.apply {
            adapter = MarketingAdapter(requireContext()){ item,position ->
                marketingViewModel.selectedContent(item,position)
                DialogMarketingDetail().show(childFragmentManager,"FragmentMarketing")

            }
            recyclerView.adapter = this@FragmentMarketing.adapter

        }

    }

    private fun requestMarketing(){
        try {
            marketingViewModel.also { viewModel ->
                val request = MarketingRequest()
                val data = gson.toJson(request)
                viewModel.requestMarketing(RequestObject(data=data, code = ApiConst.MARKETING_SEARCH))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }
    private fun observerMarketing(){
        try {
            marketingViewModel.marketingResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS->{
                        if (it?.data?.response is ArrayList<*>){
                            (it.data.response as ArrayList<ResponseMarketing.MarketingResponse>).also { result ->
                                if (result.size == 0) binding.layoutFilter.visibility = View.VISIBLE
                                else binding.layoutFilter.visibility = View.GONE
                                when (marketingViewModel.actionState.value){
                                    MarketingViewModel.STATE.NOTHING ->  adapter.updateListContent(result)
                                    MarketingViewModel.STATE.ADD_ITEM -> {
                                        adapter.updateListContent(result)
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            binding.recyclerView.scrollToPosition(result.size-1)
                                        }, 300L)
                                        authViewModel.updateActionSate(AuthViewModel.StateAddItem.NOTHING)

                                    }
                                    else -> {}
                                }

                            }
                        }

                    }
                    else->{}
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun requestSearch(request:RequestSearchMarketing){
        try {
            marketingViewModel.also { viewModel ->
                val data = gson.toJson(request)
                viewModel.requestMarketing(RequestObject(data=data, code = ApiConst.MARKETING_SEARCH))
                adapter.clearAll()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    private fun observerStateNotify(){
        try {
            marketingViewModel.actionState.observe(viewLifecycleOwner){
                try {
                    if (it==MarketingViewModel.STATE.NOTIFY_DATA){
                        adapter.notifyItemChanged(marketingViewModel.positionSelected)
                    }
                }catch (e:Exception){

                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    override fun getLayoutId(): Int  = R.layout.fragment_marketing

    override fun finish() {
        mActivity?.closeMainFuncScreen(this)
    }

    override fun backToPrevious() {
        finish()
    }
    override fun isBackPreviousEnable(): Boolean {
        return true
    }
}