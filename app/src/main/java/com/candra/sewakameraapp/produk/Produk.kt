package com.candra.sewakameraapp.produk

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Produk (
    var id: String ?="",
    var nama: String ?="",
    var gambar: String ?= "",
    var harga: Int ?=0,
    var kategori: Int ?=0,
    var keterangan: String ?="",
    var jenis: String ?="",
    var stok: Int ?=0,
    var totalSewa: Int ?=0
): Parcelable