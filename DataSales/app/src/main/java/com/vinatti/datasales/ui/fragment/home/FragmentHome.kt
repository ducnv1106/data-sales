package com.vinatti.datasales.ui.fragment.home

import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.vinatti.datasales.R
import com.vinatti.datasales.constants.ApiConst
import com.vinatti.datasales.constants.PrefConst
import com.vinatti.datasales.data.api_entities.request.DashboardRequest
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.api_entities.response.ResponseDashboard
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.databinding.FragmentHomeBinding
import com.vinatti.datasales.ui.fragment.BaseFragment
import com.vinatti.datasales.utils.setSingleClick
import com.vinatti.datasales.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.log

@AndroidEntryPoint
class FragmentHome : BaseFragment<FragmentHomeBinding>() {

    private val homeViewModel by viewModels<HomeViewModel> ()
    private val authViewModel by activityViewModels<AuthViewModel>()

    @Inject
    lateinit var gson: Gson

    override fun loadControlsAndResize(binding: FragmentHomeBinding?) {
        binding?.apply {
            navigation.apply {
                btnHome.isChecked = true
                tabHome.setSingleClick {

                }
                tabCustom.setSingleClick {
                    mActivity?.showCustom()
                }
                tabMarketing.setSingleClick {
                    mActivity?.showMarketing()
                }
                tabPersonal.setSingleClick {
                    mActivity?.showPersonal()
                }
            }
            tvDay.setSingleClick {
                requestDashBoard(HomeViewModel.TypeDashboard.DAY)
            }
            tvWeek.setSingleClick {
                requestDashBoard(HomeViewModel.TypeDashboard.WEEK)
            }

        }

    }



    override fun initView(savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this
        binding.viewModel = homeViewModel
        requestDashBoard()

    }

    override fun observerState() {
        observerDashboard()
        observerTabCurrent()
    }
    private fun requestDashBoard(type:HomeViewModel.TypeDashboard = HomeViewModel.TypeDashboard.DAY){
        try {
            homeViewModel.also { homeViewModel ->
                val responseLogin = mActivity?.getLoginResponse(PrefConst.PREF_LOGIN_RESPONSE)
                responseLogin?.let {

                    if (type == HomeViewModel.TypeDashboard.DAY) homeViewModel.updateFromDateDay()
                    else homeViewModel.updateFromDateWeek()

                    val request = DashboardRequest(it.id, fromDate = homeViewModel._fromDate.value ?: "", toDate = homeViewModel._toDate.value ?: "")
                    val data = gson.toJson(request)
                    homeViewModel.requestDashboard(RequestObject(data=data, code = ApiConst.DASHBOARD_STATISTIC))
                    homeViewModel.setTypeDashBoard(type)
                }
            }
        }catch (e:Exception){

        }
    }
    private fun observerDashboard(){
        try {
            homeViewModel.dashBoardResult.observe(viewLifecycleOwner){
                loadingViewModel.showOrHideLoading(it)
                when(it.status){
                    ApiResult.Status.SUCCESS -> {
                        if (it?.data?.response is ArrayList<*>){
                            (it.data.response as ArrayList<ResponseDashboard.DashboardResponse>).also { result ->
                                if (result.size > 0 ) homeViewModel.updateResponseDashboard(result[0])
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
    private fun observerTabCurrent(){
        try {
            authViewModel.tabCurrent.observe(viewLifecycleOwner){
                if (it==AuthViewModel.TabHomeCurrent.HOME){
                    binding.navigation.apply {
                        btnHome.isChecked = true
                        btnCustom.isChecked = false
                        btnMarketing.isChecked = false
                        btnPersonal.isChecked = false
                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun finish() {

    }
}