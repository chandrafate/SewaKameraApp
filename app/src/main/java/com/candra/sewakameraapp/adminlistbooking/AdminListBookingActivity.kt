package com.candra.sewakameraapp.adminlistbooking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.admindetailbooking.AdminDetailBookingActivity
import com.candra.sewakameraapp.booking.Booking2
import com.candra.sewakameraapp.keranjang.Keranjang
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_admin_list_booking.*

class AdminListBookingActivity : AppCompatActivity() {

    private var dataList = ArrayList<Booking2>()

    private var idProduk = java.util.ArrayList<Keranjang>()

    lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_list_booking)

        iv_back_list_booking_admin.setOnClickListener {
            finish()
        }

        mDatabase = FirebaseDatabase.getInstance().getReference()

        rc_list_booking_admin.layoutManager = LinearLayoutManager(this)

        getData()

    }

    private fun getData() {
        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                dataList.clear()

                for (getdatasnapshot in snapshot.child("booking").getChildren()) {

                    val booking = getdatasnapshot.getValue(Booking2::class.java)
                    dataList.add(booking!!)

                }

                rc_list_booking_admin.adapter = AdminListBookingAdapter(dataList) {

                    idProduk.clear()

                    val nama = snapshot.child("member/${it.username}/nama").getValue()

                    for (getdatasnapshot in snapshot.child("booking/${it.key}/barang").getChildren()) {
                        val keranjang = getdatasnapshot.getValue(Keranjang::class.java)
                        idProduk.add(keranjang!!)
                    }

                    val intent = Intent(this@AdminListBookingActivity, AdminDetailBookingActivity::class.java)
                        .putExtra("detailBooking", it)
                        .putExtra("nama", nama.toString())
                        .putExtra("listBarang", idProduk)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminListBookingActivity, "" + error.message, Toast.LENGTH_LONG).show()
            }

        })
    }
}