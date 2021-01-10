package com.candra.sewakameraapp.checkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.candra.sewakameraapp.HomeActivity
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.booking.Booking2
import com.candra.sewakameraapp.booking.DetailBookingActivity
import kotlinx.android.synthetic.main.activity_success_checkout.*

class SuccessCheckoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_checkout)

        btn_home.setOnClickListener {
            finishAffinity()
            startActivity(Intent(this, HomeActivity::class.java))
        }

        btn_lihat_checkout.setOnClickListener {
            finishAffinity()
            startActivity(Intent(this, DetailBookingActivity::class.java).putExtra("detailBooking",intent.getParcelableExtra<Booking2>("detailBooking")))
        }
    }

    override fun onBackPressed() {
    }
}