package com.shangzuo.highvaluecabinet.app.base

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding
import com.shangzuo.mvvm.base.BaseVBActivity
import com.shangzuo.mvvm.base.BaseViewModel


abstract class BaseActivity<VM : BaseViewModel, VB : ViewBinding> : BaseVBActivity<VM, VB>() {

    abstract override fun initView(savedInstanceState: Bundle?)


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {

                //如果现在取得焦点的View为EditText则进入判断
                currentFocus?.let { view ->
                    if (view is EditText) {
                        if (!isInSide(view, ev) && isSoftInPutDisplayed()) {
                            hideSoftInPut(view)
                        }
                    }
                }
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    //判断点击坐标是否在EditText内部
    private fun isInSide(currentFocus: View, ev: MotionEvent): Boolean {
        val location = intArrayOf(0, 0)
        //获取当前EditText坐标
        currentFocus.getLocationInWindow(location)
        //上下左右
        val left = location[0]
        val top = location[1]
        val right = left + currentFocus.width
        val bottom = top + currentFocus.height
        //点击坐标是否在其内部
        return (ev.x >= left && ev.x <= right && ev.y > top && ev.y < bottom)
    }

    private fun isSoftInPutDisplayed(): Boolean {
        return ViewCompat.getRootWindowInsets(window.decorView)
            ?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
    }

    private fun hideSoftInPut(currentFocus: View) {
        currentFocus.let {
            //清除焦点
            it.clearFocus()
            //关闭软键盘
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }


}