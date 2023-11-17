package com.shangzuo.highvaluecabinet.app.base

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserInfo(
    var userid: String? = null,
    var realName: String? = null,
    var departmentName:String? = null,
    var cardNumber:String? = null,
    var finger1:String? = null,
    var finger2:String? = null,
    var face:String? = null,
) : Parcelable