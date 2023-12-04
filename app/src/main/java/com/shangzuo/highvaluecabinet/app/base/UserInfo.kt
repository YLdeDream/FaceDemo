package com.shangzuo.highvaluecabinet.app.base

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserInfo(
    var cardNumber: String,
    var departmentName: String,
    var employeeNumber: String,
    var iccardNo: String,
    var realName: String,
    var rfcardNo: String,
    var rights: String,
    var telephone: String,
    var userid: String,
    var face: String,
) : Parcelable