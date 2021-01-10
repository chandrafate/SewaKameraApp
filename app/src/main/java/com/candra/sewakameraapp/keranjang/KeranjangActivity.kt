package com.candra.sewakameraapp.keranjang

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.barang.Barang
import com.candra.sewakameraapp.checkout.Booking
import com.candra.sewakameraapp.checkout.CheckoutActivity
import com.candra.sewakameraapp.utils.Preferences
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_keranjang.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class KeranjangActivity : AppCompatActivity() {

    lateinit var preferences: Preferences

    lateinit var mDatabase: DatabaseReference
    lateinit var mDatabase2: DatabaseReference

    var dataList = ArrayList<Barang>()
    var idProduk = ArrayList<Keranjang>()

    var hari = 0
    var bulan = 0
    var tahun = 0

    var savehari = 0
    var savebulan = 0
    var savetahun = 0

    var tanggalIn = ""
    var tanggalOut = ""

    var totalHargaBarang = 0
    var totalHargaCheckout = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keranjang)

        preferences = Preferences(this)

        mDatabase = FirebaseDatabase.getInstance().getReference("produk")
        mDatabase2 = FirebaseDatabase.getInstance().getReference("member")
            .child(preferences.getValues("username").toString()).child("keranjang")

        iv_back_keranjang.setOnClickListener {
            finish()
        }

        rc_item_keranjang.layoutManager = LinearLayoutManager(this)

        getData()

        getDateCalender()

//        pickDate()

        et_tgl_in_keranjang.setOnClickListener {
            val dpdIn = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, stahun, sbulan, shari ->
                    et_tgl_in_keranjang.setText("$shari-${sbulan+1}-$stahun")

                    tanggalIn = "$shari/${sbulan+1}/$stahun"

                    savehari = shari
                    savebulan = sbulan
                    savetahun = stahun
                },
                tahun,
                bulan,
                hari
            )
            dpdIn.datePicker.minDate = Calendar.getInstance().timeInMillis
            dpdIn.show()
        }

        et_tgl_out_keranjang.setOnClickListener {
            val dpdIn = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, stahun, sbulan, shari ->
                    et_tgl_out_keranjang.setText("$shari-${sbulan+1}-$stahun")
                    tanggalOut = "$shari/${sbulan+1}/$stahun"

                    hitungHari(tanggalIn, tanggalOut)

                    val localeID = Locale("in", "ID")
                    val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
                    val formatHarga = formatRupiah.format(totalHargaCheckout).toString()

                    tv_total_keranjang.text = formatHarga.substring(0, formatHarga.length - 3)

                },
                savetahun,
                savebulan,
                savehari
            )
            dpdIn.datePicker.minDate = Calendar.getInstance().timeInMillis
            dpdIn.show()


        }

        btn_lanjut_keranjang.setOnClickListener {
            var booking = Booking()
            booking.username = preferences.getValues("username").toString()
            booking.status = "pending"
            booking.tgl_in = tanggalIn
            booking.tgl_out = tanggalOut
            booking.total = totalHargaCheckout
            startActivity(Intent(this, CheckoutActivity::class.java).putExtra("keranjang", idProduk).putExtra("booking", booking))
        }
    }

    private fun hitungHari(tanggalIn: String, tanggalOut: String) {
        val format = SimpleDateFormat("dd/MM/yyyy")
        val days = TimeUnit.DAYS.convert(
            format.parse(tanggalOut).getTime() -
                    format.parse(tanggalIn).getTime(),
            TimeUnit.MILLISECONDS
        )
        
        if (days > 0) {
            tv_hari_keranjang.text = "$days Hari"

            totalHargaCheckout = totalHargaBarang * days.toInt()

            btn_lanjut_keranjang.setText("Lanjut Pembayaran(${idProduk.size})")
            btn_lanjut_keranjang.visibility = View.VISIBLE
        } else {
            totalHargaCheckout = 0

            btn_lanjut_keranjang.setText("")
            btn_lanjut_keranjang.visibility = View.INVISIBLE
            Toast.makeText(this, "Minimal 1 Hari", Toast.LENGTH_SHORT).show()
        }

       
    }

    private fun getDateCalender() {
        val cal = Calendar.getInstance()
        hari = cal.get(Calendar.DAY_OF_MONTH)
        bulan = cal.get(Calendar.MONTH)
        tahun = cal.get(Calendar.YEAR)
    }


    private fun getData() {
        mDatabase2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                idProduk.clear()

                for (getdatasnapshot in snapshot.getChildren()) {
                    val keranjang = getdatasnapshot.getValue(Keranjang::class.java)
                    idProduk.add(keranjang!!)
                }

                if (idProduk.isEmpty()) {
                    et_tgl_in_keranjang.visibility = View.INVISIBLE
                    et_tgl_out_keranjang.visibility = View.INVISIBLE
                    textView9.visibility = View.INVISIBLE
                    imageView3.visibility = View.INVISIBLE
                    textView14.visibility = View.INVISIBLE
                    textView15.visibility = View.INVISIBLE
                    dockconstraintkeranjang.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@KeranjangActivity, "" + error.message, Toast.LENGTH_LONG).show()
            }

        })

        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                dataList.clear()

                idProduk.forEach {

                    for (getdatasnapshot in snapshot.getChildren()) {

                        val produk = getdatasnapshot.getValue(Barang::class.java)

                        if (produk!!.id.equals(it.id)) {
                            dataList.add(produk)
                            totalHargaBarang += produk.harga!!
                        }
                    }
                }


                rc_item_keranjang.adapter = KeranjangAdapter(dataList) {
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@KeranjangActivity, "" + error.message, Toast.LENGTH_LONG).show()
            }

        })
    }

}