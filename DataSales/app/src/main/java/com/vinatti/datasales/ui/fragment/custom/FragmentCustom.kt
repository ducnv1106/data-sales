package com.vinatti.datasales.ui.fragment.custom

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
import com.vinatti.datasales.constants.PrefConst
import com.vinatti.datasales.data.api_entities.request.*
import com.vinatti.datasales.data.api_entities.response.ResponseChannel
import com.vinatti.datasales.data.api_entities.response.ResponseCustomSearch
import com.vinatti.datasales.data.api_entities.response.ResponseListStatus
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.databinding.FragmentCustomBinding
import com.vinatti.datasales.ui.dialog.*
import com.vinatti.datasales.ui.fragment.BaseFragment
import com.vinatti.datasales.ui.fragment.marketing.MarketingAdapter
import com.vinatti.datasales.ui.fragment.marketing.MarketingViewModel
import com.vinatti.datasales.utils.setSingleClick
import com.vinatti.datasales.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FragmentCustom : BaseFragment<FragmentCustomBinding>() {

    private val customViewModel by viewModels<CustomViewModel>()
    private val authViewModel by activityViewModels<AuthViewModel>()
    private lateinit var adapter: CustomAdapter


    @Inject
    lateinit var gson:Gson

    override fun loadControlsAndResize(binding: FragmentCustomBinding?) {
        binding?.apply {
            navigation.apply {
                btnCustom.isChecked = true
                tabHome.setSingleClick {
                    authViewModel.updateTabCurrent(AuthViewModel.TabHomeCurrent.HOME)
                    finish()
                }
                tabCustom.setSingleClick {

                }
                tabMarketing.setSingleClick {
                    mActivity?.showMarketing()
                }
                tabPersonal.setSingleClick {
                    mActivity?.showPersonal()
                }
            }

            imgAdd.setSingleClick {
                mActivity?.showCreateCustom{
                    customViewModel.setActionSate(it)
                    requestCustom()
                }
            }
            imgSearch.setSingleClick {
                DialogSearchCustom{
                    requestSearch(it)
                }.show(childFragmentManager,"FragmentMarketing")
            }


        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this
        binding.viewModel = customViewModel
        initAdapter()
        requestCustom()

    }

    override fun observerState() {
        observerCustom()
        observerStateNotify()
        observerDeleteCustom()
    }

    private fun initAdapter(){
        binding.apply {
            adapter = CustomAdapter(requireContext(),{ item,position ->
                customViewModel.selectedContent(item,position)
                DialogDetailCustom().show(childFragmentManager,"FragmentCustom")

            },{item,position ->
                DialogConfirm(resources.getString(R.string.str_message_confirm_delete_custom)){
                    customViewModel.selectedContent(item,position)
                    requestDeleteCustom(item.id)
                }.show(childFragmentManager,"FragmentCustom")

             })
            recyclerView.adapter = this@FragmentCustom.adapter

        }

    }

    private fun requestSearch(request:CustomerSearchRequest){
        try {
            customViewModel.also { viewModel ->
                val data = gson.toJson(request)
                viewModel.requestCustom(RequestObject(data=data, code = ApiConst.CUSTOMER_SEARCH))
                adapter.clearAll()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun requestCustom(){
        try {
            customViewModel.also { viewModel ->
                val assignedTo = mActivity?.getLoginResponse(PrefConst.PREF_LOGIN_RESPONSE)?.id ?: 0
                val request = CustomerSearchRequest(assignedTo = assignedTo)
                val data = gson.toJson(request)
                viewModel.requestCustom(RequestObject(data=data, code = ApiConst.CUSTOMER_SEARCH))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerCustom(){
        try {
            customViewModel.customResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS->{
                        if (it?.data?.response is ArrayList<*>){
                            (it.data.response as ArrayList<ResponseCustomSearch.CustomSearchResponse>).also { result ->
                                if (result.size == 0) binding.layoutFilter.visibility = View.VISIBLE
                                else binding.layoutFilter.visibility = View.GONE
                                when (customViewModel.actionState.value){
                                    CustomViewModel.STATE.NOTHING ->  adapter.updateListContent(result)
                                    CustomViewModel.STATE.ADD_ITEM -> {
                                        adapter.updateListContent(result)
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            binding.recyclerView.scrollToPosition(result.size-1)
                                        }, 300L)
                                        authViewModel.updateActionSate(AuthViewModel.StateAddItem.NOTHING)

                                    }
                                }
                            }
                        }

                    }
                    else ->{}
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun requestDeleteCustom(id:Int){
        try {
            customViewModel.also { viewModel ->
                val request = DeleteCustomRequest(id)
                val data = gson.toJson(request)
                viewModel.requestDeleteCustom(RequestObject(data=data, code = ApiConst.CUSTOMER_DELETE))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerDeleteCustom(){
        try {
            customViewModel.deleteCustomResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS->{
                        SuccessDialog(resources.getString(R.string.str_add_success)){
                            adapter.removeItem(customViewModel.positionSelected)
                        }.show(childFragmentManager,"FragmentCustom")

                    }
                    else -> {

                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun observerStateNotify(){
        try {
            customViewModel.actionState.observe(viewLifecycleOwner){
                try {
                    if (it== CustomViewModel.STATE.NOTIFY_DATA){
                        adapter.notifyItemChanged(customViewModel.positionSelected)
                    }
                }catch (e:Exception){

                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }



    override fun getLayoutId(): Int = R.layout.fragment_custom

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