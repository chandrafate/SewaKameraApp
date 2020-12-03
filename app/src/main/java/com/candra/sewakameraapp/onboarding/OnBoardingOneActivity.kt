package com.candra.sewakameraapp.onboarding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.candra.sewakameraapp.R
import kotlinx.android.synthetic.main.activity_on_boarding_one.*
import me.relex.circleindicator.CircleIndicator3

class OnBoardingOneActivity : AppCompatActivity() {
    private var titlesList = mutableListOf<String>()
    private var descList = mutableListOf<String>()
    private var imageList = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding_one)

        postToList()

        view_pager2.adapter = ViewPagerAdapter(titlesList, descList, imageList)
        view_pager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val indicator = findViewById<CircleIndicator3>(R.id.indicator)
        indicator.setViewPager(view_pager2)

        btn_fake_swipe.setOnClickListener {
            view_pager2.apply {
                beginFakeDrag()
                fakeDragBy(-10f)
                endFakeDrag()
            }

        }


    }
    private fun addToList(title: String, description: String, image: Int) {
        titlesList.add(title)
        descList.add(description)
        imageList.add(image)
    }

    private fun postToList() {
        for (i in 1..5) {
            addToList("title $i", "Description $i", R.mipmap.ic_launcher_round)
        }
    }

    }
