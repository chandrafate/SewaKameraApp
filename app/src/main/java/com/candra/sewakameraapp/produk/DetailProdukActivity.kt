package com.candra.sewakameraapp.produk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.candra.sewakameraapp.keranjang.Keranjang
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.utils.Preferences
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_detail_produk.*
import java.text.NumberFormat
import java.util.*

class DetailProdukActivity : AppCompatActivity() {

    lateinit var mDatabase: DatabaseReference
    lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_produk)

        mDatabase = FirebaseDatabase.getInstance().getReference("member")
        preferences = Preferences(this)

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

            data?.id?.let { it1 -> insertKeranjang(it1) }
        }

        iv_back.setOnClickListener {
            finish()
        }
    }

    private fun insertKeranjang(data : String) {
        var keranjang = Keranjang()
        keranjang.id = data
        mDatabase.child(preferences.getValues("username").toString()).child("keranjang").push().setValue(keranjang)
    }

}