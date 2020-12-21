package com.candra.sewakameraapp.checkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.candra.sewakameraapp.HomeActivity
import com.candra.sewakameraapp.R
import kotlinx.android.synthetic.main.activity_success_checkout.*

class SuccessCheckoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_checkout)

        btn_home.setOnClickListener {
            finishAffinity()
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }
}