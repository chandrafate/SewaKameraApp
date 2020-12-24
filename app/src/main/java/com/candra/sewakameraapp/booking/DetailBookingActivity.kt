package com.candra.sewakameraapp.booking

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.sewakameraapp.Barang.Barang
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.keranjang.Keranjang
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_detail_booking.*
import kotlinx.android.synthetic.main.activity_detail_booking.iv_back
import kotlinx.android.synthetic.main.activity_detail_booking.rc_booking_item
import kotlinx.android.synthetic.main.activity_detail_booking.tv_hari
import kotlinx.android.synthetic.main.activity_detail_booking.tv_jumlah_item
import kotlinx.android.synthetic.main.activity_detail_booking.tv_total_checkout
import kotlinx.android.synthetic.main.activity_keranjang.*
import kotlinx.android.synthetic.main.activity_list_item.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DetailBookingActivity : AppCompatActivity() {

    lateinit var mDatabase: DatabaseReference
    lateinit var mDatabase2: DatabaseReference
    lateinit var idBooking: String

    private var dataList = ArrayList<Barang>()
    private var idProduk = ArrayList<Keranjang>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_booking)

        val data = intent.getParcelableExtra<Booking2>("detailBooking")

        mDatabase = FirebaseDatabase.getInstance().getReference("produk")
        mDatabase2 = FirebaseDatabase.getInstance().getReference("booking")

        idBooking = data!!.key.toString()

        tv_jumlah_item.text = data?.jumlah_item.toString() + " Items"
        val tglin = data?.tgl_in?.substring(0, 2) ?: String()
        tv_tgl.text = tglin + " - " + data?.tgl_out
        tv_hari.text = data!!.tgl_in?.let { data!!.tgl_out?.let { it1 -> hitungHari(it, it1) } }
        tv_denda.text =
            "+" + formatHarga(data.denda!!).substring(0, formatHarga(data.denda!!).length - 3)
        tv_total_checkout.text =
            formatHarga(data.total!!).substring(0, formatHarga(data.total!!).length - 3)

        iv_back.setOnClickListener {
            finish()
        }

        rc_booking_item.layoutManager = LinearLayoutManager(this)

        getData()

        if (data.status.equals("pending")) {
            btn_konfirm_qr.setText("Konfirmasi Pembayaran")
            btn_konfirm_qr.setBackgroundResource(R.drawable.btn_primary)
        } else if (data.status.equals("success")){
            btn_konfirm_qr.setText("Tampilkan Kode Booking")
            btn_konfirm_qr.setBackgroundResource(R.drawable.btn_secondary)
        }

        btn_konfirm_qr.setOnClickListener {

        }
    }

    private fun getData() {
        mDatabase2.child(idBooking).child("barang").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                idProduk.clear()

                for (getdatasnapshot in snapshot.getChildren()) {
                    val keranjang = getdatasnapshot.getValue(Keranjang::class.java)
                    idProduk.add(keranjang!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailBookingActivity, "" + error.message, Toast.LENGTH_LONG).show()
            }

        })

        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                dataList.clear()

                for (getdatasnapshot in snapshot.getChildren()) {

                    idProduk.forEach {

                        for (getdatasnapshot in snapshot.getChildren()) {

                            val produk = getdatasnapshot.getValue(Barang::class.java)

                            if (produk!!.id.equals(it.id)) {
                                dataList.add(produk!!)
                            }
                        }
                    }

                }

                rc_booking_item.adapter = ListItemBookingAdapter(dataList) {
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailBookingActivity, "" + error.message, Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun formatHarga(denda: Int): String {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(denda).toString()
    }

    private fun hitungHari(tanggalIn: String, tanggalOut: String): String {
        val format = SimpleDateFormat("dd/MM/yyyy")
        val days = TimeUnit.DAYS.convert(
            format.parse(tanggalOut).getTime() -
                    format.parse(tanggalIn).getTime(),
            TimeUnit.MILLISECONDS
        )

        return days.toString() + " Hari"
    }


}