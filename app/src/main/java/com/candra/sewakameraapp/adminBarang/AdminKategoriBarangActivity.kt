package com.candra.sewakameraapp.adminBarang

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.kategori.Kategori
import kotlinx.android.synthetic.main.activity_admin_barang.*

class AdminKategoriBarangActivity : AppCompatActivity() {

    private var dataList = ArrayList<Kategori>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_barang)

        rv_kategori.layoutManager = LinearLayoutManager(this)
        dummydata()
        rv_kategori.adapter = AdminKategoriAdapter(dataList) {
            startActivity(Intent(this, AdminKelolaProdukActivity::class.java).putExtra("kategori", it))
        }

        iv_back_5.setOnClickListener {
            finish()
        }
    }

    private fun dummydata() {
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
    }
}