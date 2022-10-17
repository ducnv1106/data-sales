package com.vinatti.datasales.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import com.vinatti.datasales.ui.activities.MainActivity
import com.vinatti.datasales.utils.autoCleared
import com.vinatti.datasales.viewmodel.LoadingViewModel
import timber.log.Timber

abstract class BaseFragment<T: ViewBinding> : Fragment()  {

    protected val TAG = this.javaClass.simpleName
    val autoCleared = autoCleared<T>()
    protected var binding by autoCleared

    protected abstract fun loadControlsAndResize(binding: T?)
    protected abstract fun initView(savedInstanceState: Bundle?)
    open fun observerState() {}

    //
    protected open fun getCurrentFragment(): Int  = 0
    @LayoutRes
    abstract fun getLayoutId():Int

    protected abstract fun finish()
    open fun isBackPreviousEnable(): Boolean {
        return false
    }

    open fun backToPrevious() {}
    //


    protected val loadingViewModel by activityViewModels<LoadingViewModel>()

    //
    protected var mActivity: MainActivity? = null

    fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    //============================
    //======  Override  ==========
    //============================
    override fun onAttach(activity: Activity) {
        Timber.d("=>> $TAG: [onAttach]")
        super.onAttach(activity)
        this.mActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("=>> $TAG: onCreateView")
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        loadControlsAndResize(binding)
        initView(savedInstanceState)
        return (binding as ViewBinding).root
    }

    override fun onDestroyView() {
        Timber.d("=>> $TAG: onDestroyView")
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("=>> $TAG: onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        if (mActivity == null) mActivity = activity as MainActivity
        observerState()
    }


    override fun onDestroy() {
        Timber.d("=>> $TAG: onDestroy")
        super.onDestroy()
    }

    override fun onResume() {
        Timber.d("=>> $TAG: onResume")
        super.onResume()
    }

    override fun onPause() {
        Timber.d("=>> $TAG: onPause")
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("=>> $TAG: onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        Timber.d("=>> $TAG: onStart")
        super.onStart()
    }

    override fun onStop() {
        Timber.d("=>> $TAG: onStop")
        super.onStop()
    }

    override fun onDetach() {
        Timber.d("=>> $TAG: [onDetach]")
        super.onDetach()
    }


}