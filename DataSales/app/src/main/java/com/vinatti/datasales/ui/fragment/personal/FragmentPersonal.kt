package com.vinatti.datasales.ui.fragment.personal

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.vinatti.datasales.R
import com.vinatti.datasales.constants.ApiConst
import com.vinatti.datasales.constants.PrefConst
import com.vinatti.datasales.data.api_entities.request.LoginRequest
import com.vinatti.datasales.data.api_entities.request.RequestObject
import com.vinatti.datasales.data.local.pref.PrefManager
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.databinding.FragmentPersonalBinding
import com.vinatti.datasales.ui.activities.SplashActivity
import com.vinatti.datasales.ui.dialog.DialogChangePassword
import com.vinatti.datasales.ui.fragment.BaseFragment
import com.vinatti.datasales.utils.setSingleClick
import com.vinatti.datasales.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FragmentPersonal : BaseFragment<FragmentPersonalBinding>() {

    private val personalViewModel by  viewModels<PersonalViewModel>()
    private val authViewModel by activityViewModels<AuthViewModel>()

    @Inject
    lateinit var gson: Gson


    override fun loadControlsAndResize(binding: FragmentPersonalBinding?) {
        binding?.apply {
            navigation.apply {
                btnPersonal.isChecked = true
                tabHome.setSingleClick {
                    authViewModel.updateTabCurrent(AuthViewModel.TabHomeCurrent.HOME)
                    finish()
                }
                tabCustom.setSingleClick {
                    mActivity?.showCustom()
                }
                tabMarketing.setSingleClick {
                    mActivity?.showMarketing()
                }
                tabPersonal.setSingleClick {

                }
            }

            layoutManagerStaff.setSingleClick {
                mActivity?.showManagerStaff()
            }
            layoutChangePassword.setSingleClick {
                DialogChangePassword().show(childFragmentManager,"FragmentPersonal")
            }
            layoutLogout.setSingleClick {
                logout()
            }

        }

    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this
        val responseLogin = mActivity?.getLoginResponse(PrefConst.PREF_LOGIN_RESPONSE)
        responseLogin?.let {
            binding.item = it
            if (it.role == "ADMIN") binding.layoutManagerStaff.visibility = View.VISIBLE
        }

    }

    override fun observerState() {

    }


    private fun logout(){
        PrefManager.getInstance(requireContext()).releaseMemory()

        val intent = Intent(activity, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }


    override fun getLayoutId(): Int  = R.layout.fragment_personal

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