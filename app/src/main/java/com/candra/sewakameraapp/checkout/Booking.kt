package com.candra.sewakameraapp.checkout

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Booking (
  var key: String ?="",
  var denda: Int ?=0,
  var status: String ?="",
  var tgl_in: String ?="",
  var tgl_out: String ?="",
  var total: Int ?=0,
  var username: String ?="",
): Parcelable