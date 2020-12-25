package com.candra.sewakameraapp.keranjang

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.checkout.Booking
import com.candra.sewakameraapp.checkout.CheckoutActivity
import com.candra.sewakameraapp.barang.Barang
import com.candra.sewakameraapp.utils.Preferences
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_keranjang.*
import kotlinx.android.synthetic.main.activity_keranjang.iv_back
import kotlinx.android.synthetic.main.activity_list_item.*
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

        iv_back.setOnClickListener {
            finish()
        }

        rc_booking_item.layoutManager = LinearLayoutManager(this)

        getData()

        getDateCalender()

//        pickDate()

        et_tgl_in.setOnClickListener {
            val dpdIn = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, stahun, sbulan, shari ->
                    et_tgl_in.setText("$shari-$sbulan-$stahun")
                    tanggalIn = "$shari/$sbulan/$stahun"

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

        et_tgl_out.setOnClickListener {
            val dpdIn = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, stahun, sbulan, shari ->
                    et_tgl_out.setText("$shari-$sbulan-$stahun")
                    tanggalOut = "$shari/$sbulan/$stahun"

                    hitungHari(tanggalIn, tanggalOut)

                    val localeID = Locale("in", "ID")
                    val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

                    tv_total_checkout.text = formatRupiah.format(totalHargaCheckout).toString()

                },
                savetahun,
                savebulan,
                savehari
            )
            dpdIn.datePicker.minDate = Calendar.getInstance().timeInMillis
            dpdIn.show()


        }

        btn_lanjut.setOnClickListener {
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

        tv_hari.text = "$days Hari"

        totalHargaCheckout = totalHargaBarang * days.toInt()

        btn_lanjut.setText("Lanjut Pembayaran(${idProduk.size})")
        btn_lanjut.visibility = View.VISIBLE
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
                    et_tgl_in.visibility = View.INVISIBLE
                    et_tgl_out.visibility = View.INVISIBLE
                    textView9.visibility = View.INVISIBLE
                    imageView3.visibility = View.INVISIBLE
                    textView14.visibility = View.INVISIBLE
                    textView15.visibility = View.INVISIBLE
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
                            dataList.add(produk!!)
                            totalHargaBarang += produk.harga!!
                        }
                    }
                }


                rc_booking_item.adapter = KeranjangAdapter(dataList) {
//                    val intent = Intent(this@KeranjangActivity, DetailProdukActivity::class.java)
//                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@KeranjangActivity, "" + error.message, Toast.LENGTH_LONG).show()
            }

        })
    }

}