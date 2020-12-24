package com.candra.sewakameraapp.transaksi

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Transaksi (
    var key: String ?="",
    var pembayaran: String ?="",
    var bukti_transfer: String ?= "",
    var kode_booking: String ?="",
    var total: Int ?=0,
    var username: String ?="",
    var verifikasi: Boolean ?=false,
    var tanggal: String ?=""
): Parcelable