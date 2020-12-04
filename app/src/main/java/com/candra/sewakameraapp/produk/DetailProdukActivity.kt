package com.candra.sewakameraapp.produk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.kategori.Kategori
import kotlinx.android.synthetic.main.activity_detail_produk.*
import java.text.NumberFormat
import java.util.*

class DetailProdukActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_produk)

        val data = intent.getParcelableExtra<Produk>("detailitem")

        tv_nama.text = data?.nama
        tv_stok.text = data?.stok.toString()
        tv_total_sewa.text = data?.totalSewa.toString()
        tv_jenis.text = data?.jenis
        tv_keterangan.text = data?.keterangan

        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        tv_harga.text = formatRupiah.format(data?.harga).toString()

        Glide.with(this)
            .load(data?.gambar)
            .into(iv_gambar)

        btn_tambah_keranjang.setOnClickListener {

        }

        iv_back.setOnClickListener {
            finish()
        }
    }
}