package com.shangzuo.highvaluecabinet.app

import android.app.Application
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.style.IOSStyle
import com.shangzuo.mvvm.base.MvvmHelper

import com.tencent.mmkv.MMKV


object FaceApp {
    //柜1对应的锁号

    lateinit var instance: Application
    fun getInstanceApp(): Application {
        return instance
    }
    lateinit var app: Application

    fun init(application: Application) {
        app = application
        instance = application
        //mmKv初始化
        MvvmHelper.init(app, true)
        MMKV.initialize(app)
        //dialog 初始化
        DialogX.init(app)

        DialogX.globalStyle = IOSStyle()
    }

}