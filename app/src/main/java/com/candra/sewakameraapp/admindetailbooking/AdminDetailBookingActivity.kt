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
import com.candra.sewakameraapp.adminBarang.ListBarangAdminAdapter
import com.candra.sewakameraapp.adminmember.DetailDataMemberVerifedActivity
import com.candra.sewakameraapp.barang.Barang
import com.candra.sewakameraapp.booking.Booking2
import com.candra.sewakameraapp.keranjang.Keranjang
import com.candra.sewakameraapp.transaksi.AdminDetailTransaksiActivity
import com.candra.sewakameraapp.transaksi.Transaksi
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_admin_detail_booking.*
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

        tv_kode_detail_booking_admin.text = data!!.key
        tv_nama_detail_booking_admin.text = intent.getStringExtra("nama")
        tv_username_detail_booking_admin.text = data.username
        tv_tgl_in_detail_booking_admin.text = data.tgl_in
        tv_tgl_out_detail_booking_admin.text = data.tgl_out
        tv_hari_detail_booking_admin.text = hitungHari(data.tgl_in!!, data.tgl_out!!)
        tv_status_detail_booking_admin.text = data.status
        tv_denda_detail_booking_admin.text = hitungDenda(data.tgl_out!!, data.total!!)
        tv_total_detail_booking_admin.text = formatHarga(data.total!!).substring(0, formatHarga(data.total!!).length - 3)

        rc_list_detail_booking_admin.layoutManager = LinearLayoutManager(this)

        iv_back_detail_booking_admin.setOnClickListener {
            finish()
        }

        btn_detail_tf_detail_booking_admin.setOnClickListener {
            detailTf(data.key!!)
        }

        btn_detail_verifikasi_detail_booking_admin.setOnClickListener {
            startActivity(Intent(this, DetailDataMemberVerifedActivity::class.java).putExtra("username", data.username))
        }

        getData()

        if (data!!.status.equals("success")) {
            btn_tolak_detail_booking_admin.visibility = View.INVISIBLE
            btn_terima_detail_booking_admin.setText("Konfirmasi Pengembalian")
        }

        btn_terima_detail_booking_admin.setOnClickListener {
            if (data!!.status.equals("pending")) {
                terimaBooking(data.key!!)
            } else if (data!!.status.equals("success")) {
                PengembalianBooking(data.key!!)
            }
        }

        btn_tolak_detail_booking_admin.setOnClickListener {
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
                rc_list_detail_booking_admin.adapter = ListBarangAdminAdapter(barang) {
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

    private fun hitungDenda(tglout: String, total: Int): String {
        val skrang = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        val format = SimpleDateFormat("dd/MM/yyyy")
        val days = TimeUnit.DAYS.convert(
            format.parse(skrang).getTime() -
                    format.parse(tglout).getTime(),
            TimeUnit.MILLISECONDS
        )

        val totall: Int

        if (days > 0) {
            totall = total * days.toInt()
        } else {
            totall = 0
        }


        return formatHarga(totall).substring(0, formatHarga(totall).length - 3)
    }
}