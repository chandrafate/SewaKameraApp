package com.candra.sewakameraapp.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.sewakameraapp.keranjang.KeranjangActivity
import com.candra.sewakameraapp.kategori.MenuKategoriActivity
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.kategori.Kategori
import com.candra.sewakameraapp.utils.Preferences
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    lateinit var preferences: Preferences

    private var dataList = ArrayList<Kategori>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        preferences = Preferences(activity!!.applicationContext)

        tv_nama_home.setText(preferences.getValues("nama"))

        btn_kategori_home.setOnClickListener {
            startActivity(Intent(context, MenuKategoriActivity::class.java))
        }

        iv_keranjang_home.setOnClickListener {
            startActivity(Intent(context, KeranjangActivity::class.java))
        }


        loadDummyData()
    }


    private fun initListener() {
        rc_item_kategori_home.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rc_item_kategori_home.adapter = KategoriAdapter(dataList) {

        }
    }


    private fun loadDummyData() {
        dataList.add(
            Kategori(
                1,
                "Kamera",
                "https://d2pa5gi5n2e1an.cloudfront.net/webp/global/images/product/digitalcameras/Canon_EOS_600D_kit/Canon_EOS_600D_kit_L_1.jpg",

                )
        )
        dataList.add(
            Kategori(
                2,
                "Lensa",
                "https://cdn.vox-cdn.com/thumbor/4-QplCl-YU_IkEbgwR6b3N72WQM=/1400x1050/filters:format(jpeg)/cdn.vox-cdn.com/uploads/chorus_asset/file/21848910/fuji50.jpg"
            )
        )
        dataList.add(
            Kategori(
                3,
                "Action Cam",
                "https://www.static-src.com/wcsstore/Indraprastha/images/catalog/full//96/MTA-4851744/gopro_gopro_hero_8_action_cam_-_black_-garansi_resmi_tam-_full05_0dwje83.jpg"
            )
        )
        dataList.add(
            Kategori(
                4,
                "Flash Cam",
                "https://www.tokocamzone.com/image/cache/Aksesoris%20lain/1506623419000_IMG_876946-600x666.jpg"
            )
        )

        initListener()
    }


}