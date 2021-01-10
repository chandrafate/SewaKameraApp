package com.candra.sewakameraapp.barang

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.candra.sewakameraapp.keranjang.Keranjang
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.keranjang.KeranjangActivity
import com.candra.sewakameraapp.utils.Preferences
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_detail_produk.*
import java.text.NumberFormat
import java.util.*

class DetailBarangActivity : AppCompatActivity() {

    lateinit var mDatabase: DatabaseReference
    lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_produk)

        mDatabase = FirebaseDatabase.getInstance().getReference("member")
        preferences = Preferences(this)

        val data = intent.getParcelableExtra<Barang>("detailitem")

        tv_nama_detail_barang.text = data?.nama
        tv_stok_detail_barang.text = data?.stok.toString()
        tv_total_sewa_detail_barang.text = data?.totalSewa.toString()
        tv_jenis_detail_barang.text = data?.jenis
        tv_keterangan_detail_barang.text = data?.keterangan

        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        val formatHarga = formatRupiah.format(data?.harga).toString()
        tv_harga_detail_barang.text = formatHarga.substring(0, formatHarga.length - 3)

        Glide.with(this)
            .load(data?.gambar)
            .into(iv_gambar_detail_barang)

        btn_tambah_keranjang_detail_barang.setOnClickListener {

            if (preferences.getValues("status").equals("ya")) {
                data?.id?.let { it1 -> insertKeranjang(it1) }
            } else {
                showMember()
            }
        }

        iv_back_detail_barang.setOnClickListener {
            finish()
        }

        iv_keranjang_detail_barang.setOnClickListener {
            startActivity(Intent(this, KeranjangActivity::class.java))
        }
    }

    private fun insertKeranjang(data : String) {
        var keranjang = Keranjang()
        keranjang.id = data
        mDatabase.child(preferences.getValues("username").toString()).child("keranjang").push().setValue(keranjang).addOnSuccessListener {
            showSuccess()
        }
    }
    private fun showSuccess() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.tambah_keranjang_success)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val btnClose = dialog.findViewById(R.id.btn_close) as Button

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showMember() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.member_non_konfirm_dialog)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val btnClose = dialog.findViewById(R.id.btn_close) as Button

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}