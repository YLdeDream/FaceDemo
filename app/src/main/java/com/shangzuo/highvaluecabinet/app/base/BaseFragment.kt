package com.shangzuo.highvaluecabinet.app.base

import androidx.viewbinding.ViewBinding
import com.shangzuo.mvvm.base.BaseVbFragment
import com.shangzuo.mvvm.base.BaseViewModel



abstract class BaseFragment<VM : BaseViewModel,VB: ViewBinding> : BaseVbFragment<VM, VB>(){

}