package com.candra.sewakameraapp.booking

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Booking2 (
    var key: String ?="",
    var denda: Int ?=0,
    var status: String ?="",
    var tgl_in: String ?="",
    var tgl_out: String ?="",
    var total: Int ?=0,
    var username: String ?="",
    var jumlah_item: Long?=0
): Parcelable