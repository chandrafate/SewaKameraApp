package com.candra.sewakameraapp.adminhome

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.adminBarang.AdminKategoriBarangActivity
import com.candra.sewakameraapp.adminlistbooking.AdminListBookingActivity
import kotlinx.android.synthetic.main.fragment_admin_home.*

class AdminHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_barang_home_fragment.setOnClickListener {
            startActivity(Intent(context, AdminKategoriBarangActivity::class.java))
        }

        btn_list_booking_home_fragment.setOnClickListener {
            startActivity(Intent(context, AdminListBookingActivity::class.java))
        }
    }

}