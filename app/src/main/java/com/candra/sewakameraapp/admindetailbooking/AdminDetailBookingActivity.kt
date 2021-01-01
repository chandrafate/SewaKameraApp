package com.candra.sewakameraapp.admindetailbooking

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.admin.AdminActivity
import com.candra.sewakameraapp.barang.Barang
import com.candra.sewakameraapp.booking.Booking2
import com.candra.sewakameraapp.booking.ListItemBookingAdapter
import com.candra.sewakameraapp.keranjang.Keranjang
import com.candra.sewakameraapp.transaksi.AdminDetailTransaksiActivity
import com.candra.sewakameraapp.transaksi.Transaksi
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_admin_detail_booking.*
import kotlinx.android.synthetic.main.activity_admin_detail_booking.tv_denda
import kotlinx.android.synthetic.main.activity_admin_detail_booking.tv_hari
import kotlinx.android.synthetic.main.activity_detail_booking.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AdminDetailBookingActivity : AppCompatActivity() {

    lateinit var mDatabase: DatabaseReference

    private var keranjang = ArrayList<Keranjang>()

    private var barang = ArrayList<Barang>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_detail_booking)

        mDatabase = FirebaseDatabase.getInstance().getReference()

        val data = intent.getParcelableExtra<Booking2>("detailBooking")

        keranjang = intent.getSerializableExtra("listBarang") as ArrayList<Keranjang>

        tv_kode.text = data!!.key
        tv_nama.text = intent.getStringExtra("nama")
        tv_username.text = data!!.username
        tv_tgl_in.text = data!!.tgl_in
        tv_tgl_out.text = data!!.tgl_out
        tv_hari.text = hitungHari(data.tgl_in!!, data.tgl_out!!)
        tv_status.text = data!!.status
        tv_denda.text = formatHarga(data.denda!!).substring(0, formatHarga(data.denda!!).length - 3)
        tv_total.text = formatHarga(data.total!!).substring(0, formatHarga(data.total!!).length - 3)

        rc_list_barang.layoutManager = LinearLayoutManager(this)

        iv_back2.setOnClickListener {
            finish()
        }

        btn_detail_tf.setOnClickListener {
            detailTf(data.key!!)
        }

        getData()

        if (data!!.status.equals("success")) {
            btn_tolak.visibility = View.INVISIBLE
            btn_terima.setText("Konfirmasi Pengembalian")
        }

        btn_terima.setOnClickListener {
            if (data!!.status.equals("pending")) {
                terimaBooking(data.key!!)
            } else if (data!!.status.equals("success")) {
                PengembalianBooking(data.key!!)
            }
        }

        btn_tolak.setOnClickListener {
            tolakBooking(data.key)
        }
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

    private fun getData() {
        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                barang.clear()

                keranjang.forEach {

                    for (getdatasnapshot in snapshot.child("produk").getChildren()) {

                        val produk = getdatasnapshot.getValue(Barang::class.java)

                        if (produk!!.id.equals(it.id)) {
                            barang.add(produk!!)
                        }
                    }
                }
                rc_list_barang.adapter = ListItemBookingAdapter(barang) {
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@AdminDetailBookingActivity,
                    "" + error.message,
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }

    private fun detailTf(idbooking: String) {

        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (getdatasnapshot in snapshot.child("transaksi").getChildren()) {

                    val tf = getdatasnapshot.getValue(Transaksi::class.java)

                    if (tf!!.kode_booking.equals(idbooking)) {
                        startActivity(Intent(this@AdminDetailBookingActivity, AdminDetailTransaksiActivity::class.java).putExtra("detailTf", tf))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun terimaBooking(idbooking: String) {
        mDatabase.child("booking/$idbooking/status").setValue("success")

        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                barang.clear()

                keranjang.forEach {

                    for (getdatasnapshot in snapshot.child("produk").getChildren()) {

                        val produk = getdatasnapshot.getValue(Barang::class.java)

                        if (produk!!.id.equals(it.id)) {
                            var stok = produk.stok!! -1
                            var totalSewa = produk.totalSewa!! + 1

                            mDatabase.child("produk/${it.id}/stok").setValue(stok)//set stok
                            mDatabase.child("produk/${it.id}/totalSewa").setValue(totalSewa)//set total sewa
                        }
                    }
                }

                finishAffinity()
                startActivity(Intent(this@AdminDetailBookingActivity, AdminActivity::class.java))

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@AdminDetailBookingActivity,
                    "" + error.message,
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }

    private fun tolakBooking(key: String?) {
        mDatabase.child("booking/$key/status").setValue("ditolak")
        finishAffinity()
        startActivity(Intent(this@AdminDetailBookingActivity, AdminActivity::class.java))
    }

    private fun PengembalianBooking(idbooking: String) {
        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var progressDialog = ProgressDialog(this@AdminDetailBookingActivity)
                progressDialog.setTitle("Proses...")
                progressDialog.show()

                barang.clear()

//                kembalikan stok
                keranjang.forEach {

                    for (getdatasnapshot in snapshot.child("produk").getChildren()) {

                        val produk = getdatasnapshot.getValue(Barang::class.java)

                        if (produk!!.id.equals(it.id)) {
                            var stok = produk.stok!! +1

                            mDatabase.child("produk/${it.id}/stok").setValue(stok)//set stok
                        }
                    }
                }

//                seacrh id tf
                for (getdatasnapshot in snapshot.child("transaksi").getChildren()) {

                    val tf = getdatasnapshot.getValue(Transaksi::class.java)

                    if (tf!!.kode_booking.equals(idbooking)) {
                        mDatabase.child("transaksi/${tf.key}").removeValue()
                        mDatabase.child("booking/$idbooking").removeValue()
                    }
                }

                progressDialog.dismiss()
                finishAffinity()
                startActivity(Intent(this@AdminDetailBookingActivity, AdminActivity::class.java))

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@AdminDetailBookingActivity,
                    "" + error.message,
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }
}