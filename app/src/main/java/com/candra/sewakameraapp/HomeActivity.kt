package com.candra.sewakameraapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.candra.sewakameraapp.home.HomeFragment
import com.candra.sewakameraapp.listbooking.ListBookingFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val fragmentHome = HomeFragment()
        val fragmentBooking = ListBookingFragment()

        setFragment(fragmentHome)

        iv_menu_home.setOnClickListener {
            setFragment(fragmentHome)

            changeIcon(iv_menu_home, R.drawable.ic_logo_home_aktif)
            changeIcon(iv_menu_booking, R.drawable.ic_logo_histori)
            changeIcon(iv_menu_user, R.drawable.ic_logo_user)
        }

        iv_menu_booking.setOnClickListener {
            setFragment(fragmentBooking)

            changeIcon(iv_menu_home, R.drawable.ic_logo_home)
            changeIcon(iv_menu_booking, R.drawable.ic_logo_histori_aktif)
            changeIcon(iv_menu_user, R.drawable.ic_logo_user)
        }

        iv_menu_user.setOnClickListener {

        }
    }

    protected fun setFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun changeIcon(imageView: ImageView, int: Int){
        imageView.setImageResource(int)
    }
}