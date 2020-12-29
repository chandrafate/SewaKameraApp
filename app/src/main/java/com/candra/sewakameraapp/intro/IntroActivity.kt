package com.candra.sewakameraapp.intro

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.sign.SignInActivity
import com.example.introact.Intro.ScreenItem
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.android.synthetic.main.activity_intro.*
import java.util.*

class IntroActivity : AppCompatActivity() {

    private var screenPager: ViewPager? = null
    var introViewPagerAdapter: IntroViewPagerAdapter? = null
    var tabIndicator: TabLayout? = null
    var btnNext: TextView? = null
    var position = 0
    var btnGetStarted: Button? = null
    var btnAnim: Animation? = null
    var tvSkip: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        // ini views
        btnNext = findViewById(R.id.tv_next)
        btnGetStarted = findViewById(R.id.btn_get_started)
        tabIndicator = findViewById(R.id.tab_indicator)
        btnAnim = AnimationUtils.loadAnimation(applicationContext, R.anim.button_animation)
        tvSkip = findViewById(R.id.tv_skip)


        // when this activity is about to be launch we need to check if its openened before or not
        if (restorePrefData()) {
            val mainActivity = Intent(applicationContext, IntroActivity::class.java)
            startActivity(mainActivity)
            finish()
        }


        // fill list screen
        val mList: MutableList<ScreenItem> = ArrayList<ScreenItem>()

        val fresh_food = mList.add(
            ScreenItem(
                "Cek Barang Tersedia",
                "Lebih mudah mengetahui\n" +
                        "produk kamera yang tersedia",
                R.drawable.fruitsintro
            )
        )
        mList.add(
            ScreenItem(
                "Harga Menarik",
                "Dapatkan harga khusus\n" +
                        "untuk member",
                R.drawable.vegintro
            )
        )
        mList.add(ScreenItem("Booking", "langsung booking di aplikasi\nke toko tinggal ambil", R.drawable.img2))


        // setup viewpager
        screenPager = findViewById(R.id.screen_viewpager)
        introViewPagerAdapter = IntroViewPagerAdapter(this, mList)
        screenPager!!.setAdapter(introViewPagerAdapter)

        // setup tablayout with viewpager
        tabIndicator!!.setupWithViewPager(screenPager)

        // Get Started button click listener
        btnGetStarted!!.setOnClickListener(View.OnClickListener {
            val mainActivity = Intent(applicationContext, SignInActivity::class.java)
            startActivity(mainActivity)
            savePrefsData()
            finish()
        })


        // next button click Listner
        btnNext!!.setOnClickListener(View.OnClickListener {
            position = screenPager!!.getCurrentItem()

            if (position < mList.size) {
                unloadLastScreen()
                position++
                screenPager!!.setCurrentItem(position)
            }else if (position == mList.size - 1) { // when we rech to the last screen
                loaddLastScreen()
            }

            if (position > 0) {
                tv_pref.visibility = View.VISIBLE
            }else if (position == 0) {
                tv_pref.visibility = View.INVISIBLE
            }
        })

        tv_pref.setOnClickListener {
            unloadLastScreen()
            if (position > 0) {
                tv_pref.visibility = View.VISIBLE
            }else if (position == 0) {
                tv_pref.visibility = View.INVISIBLE
            }

            position = screenPager!!.getCurrentItem()
            position--
            screenPager!!.setCurrentItem(position)

        }

        btn_login.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }


        // skip button click listener
        tvSkip!!.setOnClickListener(View.OnClickListener { screenPager!!.setCurrentItem(mList.size) })


        tabIndicator!!.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == mList.size - 1) {
                    loaddLastScreen()
                } else {
                    unloadLastScreen()
                }

                if (tab.position > 0) {
                    tv_pref.visibility = View.VISIBLE
                }else if (position == 0) {
                    tv_pref.visibility = View.INVISIBLE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun savePrefsData() {
        val pref = applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("isIntroOpnend", true)
        editor.commit()
    }

    private fun restorePrefData(): Boolean {
        val pref = applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        return pref.getBoolean("isIntroOpnend", false)
    }

    // show the GETSTARTED Button and hide the indicator and the next button
    private fun loaddLastScreen() {
        btnNext!!.visibility = View.INVISIBLE
        btn_login.visibility = View.VISIBLE
//        btnGetStarted!!.visibility = View.VISIBLE
        tvSkip!!.visibility = View.INVISIBLE
        // setup animation
//        btnGetStarted!!.animation = btnAnim
    }

    private fun unloadLastScreen() {
        tvSkip!!.visibility = View.VISIBLE
        btnNext!!.visibility = View.VISIBLE
        btn_login.visibility = View.INVISIBLE

    }
}