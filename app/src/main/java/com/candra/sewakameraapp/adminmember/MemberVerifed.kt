package com.candra.sewakameraapp.adminmember

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MemberVerifed (
    var username: String ?="",
    var nama: String ?="",
    var nohp: String ?="",
    var alamat: String ?="",
    var ktp: String ?="",
    var sim: String ?="",
): Parcelable