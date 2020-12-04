package com.candra.sewakameraapp.kategori

import android.media.Image
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Kategori (
    var id: Int ?=0,
    var nama: String ?="",
    var gambar: String ?= "",
    var item: Int ?=0
): Parcelable