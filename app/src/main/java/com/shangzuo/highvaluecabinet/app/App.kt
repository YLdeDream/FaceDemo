package com.shangzuo.highvaluecabinet.app

import android.app.Application
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.style.IOSStyle
import com.shangzuo.mvvm.base.MvvmHelper
import com.tencent.mmkv.MMKV


class App : Application() {
    //柜1对应的锁号

    companion object {
        lateinit var instance: App
        fun getInstanceApp(): App {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        //mmKv初始化
        MvvmHelper.init(this, true)
        MMKV.initialize(this)
        //dialog 初始化
        DialogX.init(this);
        DialogX.globalStyle = IOSStyle()


    }
}