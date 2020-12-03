package com.candra.sewakameraapp.home

import android.media.Image
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Kategori (
    var id: Int ?=0,
    var nama: String ?="",
    var gambar: String ?= ""
): Parcelable