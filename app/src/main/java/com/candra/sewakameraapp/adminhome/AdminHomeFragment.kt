package com.candra.sewakameraapp.adminhome

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.adminBarang.AdminKategoriBarangActivity
import com.candra.sewakameraapp.adminlistbooking.AdminListBookingActivity
import com.candra.sewakameraapp.adminpromo.AdminListPromoActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_admin_home.*

class AdminHomeFragment : Fragment() {

    lateinit var mDatabase: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance().getReference()

        getDataDahsboard()

        btn_barang_home_fragment.setOnClickListener {
            startActivity(Intent(context, AdminKategoriBarangActivity::class.java))
        }

        btn_list_booking_home_fragment.setOnClickListener {
            startActivity(Intent(context, AdminListBookingActivity::class.java))
        }

        btn_promo_fragment_home.setOnClickListener {
            startActivity(Intent(context, AdminListPromoActivity::class.java))
        }
    }

    private fun getDataDahsboard() {
        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tv_total_barang_home_fragment.text = snapshot.child("produk").childrenCount.toString()
                tv_total_member_home_fragment.text = snapshot.child("member").childrenCount.toString()
                tv_total_booking_home_fragment.text = snapshot.child("booking").childrenCount.toString()
                tv_total_promo_home_fragment.text = snapshot.child("diskon").childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

}