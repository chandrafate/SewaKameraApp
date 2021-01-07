package com.candra.sewakameraapp.adminpromo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Promo (
    var key: String ?="",
    var id: String ?="",
    var harga: Int ?=0,
    var expired: String ?=""
): Parcelable