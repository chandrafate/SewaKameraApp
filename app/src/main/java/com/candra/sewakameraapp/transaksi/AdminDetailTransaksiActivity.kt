package com.candra.sewakameraapp.transaksi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.candra.sewakameraapp.R
import kotlinx.android.synthetic.main.activity_admin_detail_transaksi.*
import java.text.NumberFormat
import java.util.*

class AdminDetailTransaksiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_detail_transaksi)

        val data = intent.getParcelableExtra<Transaksi>("detailTf")

        tv_username.text = data!!.username
        tv_kode_book.text = data!!.kode_booking
        tv_tgl.text = data!!.tanggal

        if (data.pembayaran.equals("ovo")) {
            iv_jenis_pay.setImageResource(R.drawable.ovo_unselected)
        } else if (data.pembayaran.equals("dana")) {
            iv_jenis_pay.setImageResource(R.drawable.dana_unselected)
        } else if (data.pembayaran.equals("ditempat")) {
            iv_jenis_pay.setImageResource(R.drawable.cod_unselected)
        }

        tv_total_tf.text =
            formatHarga(data.total!!).substring(0, formatHarga(data.total!!).length - 3)

        Glide.with(this).load(data.bukti_transfer).
        placeholder(R.drawable.unnamed_image).fitCenter().into(iv_bukti)

        iv_back.setOnClickListener {
            finish()
        }
    }

    private fun formatHarga(denda: Int): String {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(denda).toString()
    }
}