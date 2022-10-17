package com.vinatti.datasales.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vinatti.datasales.R
import com.vinatti.datasales.constants.PrefConst
import com.vinatti.datasales.data.api_entities.ApiNoticeEntity
import com.vinatti.datasales.data.api_entities.response.ResponseLogin
import com.vinatti.datasales.data.remote.ApiResult
import com.vinatti.datasales.databinding.ActMainBinding
import com.vinatti.datasales.viewmodel.AuthViewModel
import com.vinatti.datasales.viewmodel.LoadingViewModel
import com.vinatti.datasales.data.local.pref.PrefManager
import com.vinatti.finpost.ui.dialog.DialogProgress
import com.vinatti.datasales.ui.dialog.ErrorDialog
import com.vinatti.datasales.ui.fragment.BaseFragment
import com.vinatti.datasales.ui.fragment.custom.CustomViewModel
import com.vinatti.datasales.ui.fragment.custom.FragmentCustom
import com.vinatti.datasales.ui.fragment.custom.create.FragmentCreateCustom
import com.vinatti.datasales.ui.fragment.home.FragmentHome
import com.vinatti.datasales.ui.fragment.login.FragmentLogin
import com.vinatti.datasales.ui.fragment.marketing.FragmentMarketing
import com.vinatti.datasales.ui.fragment.marketing.MarketingViewModel
import com.vinatti.datasales.ui.fragment.marketing.create.FragmentCreateMarketing
import com.vinatti.datasales.ui.fragment.personal.FragmentPersonal
import com.vinatti.datasales.ui.fragment.personal.manager_staff.FragmentManagerStaff
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActMainBinding>() {

    override fun setBinding(): ActMainBinding {
        return ActMainBinding.inflate(layoutInflater)
    }
    private val loadingViewModel by viewModels<LoadingViewModel>() //Need add @AndroidEntryPoint to class use
    private val authViewModel by viewModels<AuthViewModel>()

    @Inject
    lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onNewIntent(intent)
        setupObserverLoadingAndError()
        navigationApp()
    }

    private fun navigationApp() {
        if (isLogin()) {
            showHome()
        } else {
            showLogin()
        }
    }

    private fun setupObserverLoadingAndError() {
        loadingViewModel.isShowLoading.observe(this) {
            if (it == true) {
                showProgress(false)
            } else {
                hideProgress()
            }
        }
        loadingViewModel.isNeedShowErr.observe(this) {
            if (it.status == ApiResult.Status.ERROR_NETWORK) showErrDialog(
                ApiNoticeEntity(
                    it.status,
                    this.getString(R.string.no_internet_connection)
                )
            )
            else showErrDialog(it)
        }
    }


    //Start Dialog
    private var dialogProgress: DialogProgress? = null
    private var dialogError: ErrorDialog? = null

    private fun showErrDialog(apiNoticeEntity: ApiNoticeEntity) {
        if (dialogError == null) {
            dialogError = ErrorDialog(this)
        }
        if (dialogError!!.isShowing) return

        if (apiNoticeEntity.message?.isEmpty() ==true || apiNoticeEntity.message==null){
            dialogError!!.show(resources.getString(R.string.error_fail_default), false) {}
        }else{
            dialogError!!.show(apiNoticeEntity.message , false) {}
        }


    }

    fun showProgress(cancelAble: Boolean) {
        try {
            if (dialogProgress==null) dialogProgress = DialogProgress(this)
            if (dialogProgress!!.isShowing) return
            dialogProgress?.show(cancelAble)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun hideProgress() {
        try {
            if (dialogProgress != null){
                dialogProgress?.dismiss()

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // End dialog

    //Start Utils
    private fun isLogin() = PrefManager.getInstance(this).getBoolean(PrefConst.PREF_IS_LOGIN,false)

    /**
     * Dismiss all DialogFragments added to given FragmentManager and child fragments
     */
    private fun dismissAllDialogs(manager: FragmentManager?) {
        val fragments: List<Fragment> = manager?.fragments ?: return
        for (fragment in fragments) {
            if (fragment is DialogFragment) {
                val dialogFragment = fragment as DialogFragment
                dialogFragment.dismissAllowingStateLoss()
            }
            val childFragmentManager: FragmentManager = fragment.childFragmentManager
            dismissAllDialogs(childFragmentManager)
        }
    }
    fun releaseMemory() {
        PrefManager.getInstance(this).releaseUserDataWhenLogout()
    }

    /**
     * Dismiss all DialogFragments:
     */
    fun dismissDialogs(){
        this.supportFragmentManager.fragments.takeIf {
            it.isNotEmpty()
        }?.map { (it as? DialogFragment)?.dismiss() }
    }

    /**
     * If your want to dismiss all DialogFragments except one:
     */
    fun dismissDialogsExceptOne(f: DialogFragment){
        this.supportFragmentManager.fragments.takeIf { it.isNotEmpty() }
            ?.map { if (it != f) (it as? DialogFragment)?.dismiss() }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }


    fun showLogin(){
        addFragmentToMain(FragmentLogin())
    }

    fun showHome() {
        addFragmentToMain(FragmentHome())
    }
//
    fun showCustom(){
        addFragmentToMainFunc(FragmentCustom())
    }
    fun showMarketing(){
        addFragmentToMainFunc(FragmentMarketing())
    }
    fun showPersonal(){
        addFragmentToMainFunc(FragmentPersonal())
    }

    fun showCreateMarketing(actionState:(MarketingViewModel.STATE) -> Unit){
        addFragmentToFuncChild(FragmentCreateMarketing(actionState))
    }

    fun showManagerStaff(){
        addFragmentToFuncChild(FragmentManagerStaff())
    }
    fun showCreateCustom(actionState:(CustomViewModel.STATE) -> Unit){
        addFragmentToFuncChild(FragmentCreateCustom(actionState))
    }
//    fun showManagerBrief(){
//        addFragmentToMainFunc(FragmentManagerBrief())
//    }
//    fun showFragmentPersonal(){
//        addFragmentToMainFunc(FragmentPersonal())
//    }
//    fun showChangePassword(){
//        addFragmentToFuncChild(ChangePasswordFragment())
//    }


    fun closeMainFuncScreen(f : Fragment) {
        var currentFragment = supportFragmentManager.findFragmentById(R.id.flContainerMainFunc)
        if (currentFragment == null) currentFragment = f
        closeScreen(currentFragment)
    }

    fun closeFuncChildScreen(f : Fragment) {
        var currentFragment = supportFragmentManager.findFragmentById(R.id.flContainerFuncChild)
        if (currentFragment == null ) currentFragment = f
        closeScreen(currentFragment)
    }


    private fun closeScreen(currentFragment : Fragment) {
        supportFragmentManager.beginTransaction()
            .remove(currentFragment)
            .commitAllowingStateLoss()
    }


    //
    private var currentFragment: Int = 0

    fun setCurrentScreen(currentFragment: Int) {
        this.currentFragment = currentFragment
    }

    private fun addFragmentToMain(f: Fragment) {
        removeAllTopScreenIfNeed(R.id.flContainerMain)
        addOrReplaceFragment(f,R.id.flContainerMain, f.javaClass.simpleName)
    }
    private fun addFragmentToMainFunc(f: Fragment) {
        removeAllTopScreenIfNeed(R.id.flContainerMainFunc)
        addOrReplaceFragment(f,R.id.flContainerMainFunc, f.javaClass.simpleName)
    }
    private fun addFragmentToFuncChild(f: Fragment) {
        removeAllTopScreenIfNeed(R.id.flContainerFuncChild)
        addOrReplaceFragment(f,R.id.flContainerFuncChild, f.javaClass.simpleName)
    }
    private fun addFragmentToDetail(f: Fragment) {
        addFragment(f,R.id.flContainerFuncChild,f.javaClass.simpleName)
    }
    private fun addFragmentToListPeopleInteractive(f: Fragment) {
        addFragment(f,R.id.flContainerFuncChild,f.javaClass.simpleName)
    }
    private fun addFragmentToPersonalPage(f: Fragment) {
        addFragment(f,R.id.flContainerFuncChild,f.javaClass.simpleName)
    }

    private fun addFragmentQuestion(f: Fragment){
        addFragment(f,R.id.flContainerFuncChild,f.javaClass.simpleName)
    }

    private val listFrame = arrayListOf(R.id.flContainerFuncChild,R.id.flContainerMainFunc,R.id.flContainerMain)
    fun removeAllTopScreenIfNeed(resId : Int) {
        try {
            var maxPosition = when(resId) {
                R.id.flContainerMain -> {
                    listFrame.size-2
                } R.id.flContainerMainFunc -> {
                    listFrame.size-3
                } R.id.flContainerFuncChild -> {
                    listFrame.size-4
                }
                else -> {
                    -1
                }
            }
            if (maxPosition < 0) return
            for (i in 0..maxPosition) {
                val currentFragment = supportFragmentManager.findFragmentById(listFrame[i])
                if (currentFragment != null) {
                    supportFragmentManager.beginTransaction()
                        .remove(currentFragment)
                        .commitAllowingStateLoss()
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    // share pref
    fun getLoginResponse(key: String): ResponseLogin.LoginResponse?{
        try {
            return gson.fromJson(PrefManager.getInstance(this).getString(key), object : TypeToken<ResponseLogin.LoginResponse>(){}.type)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
    }


    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                if (handleBackScreen())
                    return true
                onBackPressed()
                return true
            }
            return super.dispatchKeyEvent(event)
        }
        return false
    }

    private fun handleBackScreen() : Boolean {
        try {
            for (frame in listFrame) {
                val currentFragment = supportFragmentManager.findFragmentById(frame)
                if (currentFragment != null && currentFragment is BaseFragment<*>) {
                    if (currentFragment.isBackPreviousEnable()) {
                        currentFragment.backToPrevious()
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun onResume() {
        super.onResume()

    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent?) {
//        navigationApp()
        super.onNewIntent(intent)
    }


}