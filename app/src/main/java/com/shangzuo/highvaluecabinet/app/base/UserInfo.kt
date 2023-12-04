package com.shangzuo.highvaluecabinet.app.base

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserInfo(
    var cardNumber: String? = null,
    var departmentName: String? = null,
    var employeeNumber: String? = null,
    var iccardNo: String? = null,
    var realName: String? = null,
    var rfcardNo: String? = null,
    var rights: String? = null,
    var telephone: String? = null,
    var userid: String? = null,
    var face: String? = null,
) : Parcelable