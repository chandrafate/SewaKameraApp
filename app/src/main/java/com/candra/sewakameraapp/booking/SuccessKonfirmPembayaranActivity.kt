package com.candra.sewakameraapp.booking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.candra.sewakameraapp.HomeActivity
import com.candra.sewakameraapp.R
import kotlinx.android.synthetic.main.activity_success_konfirm_pembayaran.*

class SuccessKonfirmPembayaranActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_konfirm_pembayaran)

        val data = intent.getParcelableExtra<Booking2>("detailBooking")

        btn_list_pesanan.setOnClickListener {
            startActivity(Intent(this, DetailBookingActivity::class.java).putExtra("detailBooking", data))
        }

        btn_home.setOnClickListener {
            finishAffinity()
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }
}