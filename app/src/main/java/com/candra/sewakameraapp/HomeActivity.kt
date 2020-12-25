package com.candra.sewakameraapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.candra.sewakameraapp.home.HomeFragment
import com.candra.sewakameraapp.booking.ListBookingFragment
import com.candra.sewakameraapp.member.MemberFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val fragmentHome = HomeFragment()
        val fragmentBooking = ListBookingFragment()
        val fragmentUser = MemberFragment()

        setFragment(fragmentHome)

        iv_menu_home.setOnClickListener {
            setFragment(fragmentHome)

            changeIcon(iv_menu_home, R.drawable.ic_home_aktif)
            changeIcon(iv_menu_booking, R.drawable.ic_histori_unaktif)
            changeIcon(iv_menu_user, R.drawable.ic_user_unaktif)
        }

        iv_menu_booking.setOnClickListener {
            setFragment(fragmentBooking)

            changeIcon(iv_menu_home, R.drawable.ic_home_unaktif)
            changeIcon(iv_menu_booking, R.drawable.ic_histori_aktif)
            changeIcon(iv_menu_user, R.drawable.ic_user_unaktif)
        }

        iv_menu_user.setOnClickListener {
            setFragment(fragmentUser)

            changeIcon(iv_menu_home, R.drawable.ic_home_unaktif)
            changeIcon(iv_menu_booking, R.drawable.ic_histori_unaktif)
            changeIcon(iv_menu_user, R.drawable.ic_user_aktif)
        }
    }

    protected fun setFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun changeIcon(imageView: ImageView, int: Int) {
        imageView.setImageResource(int)
    }
}