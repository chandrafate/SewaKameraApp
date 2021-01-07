package com.candra.sewakameraapp.adminpromo

import android.app.DatePickerDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.adminBarang.ListBarangAdminAdapter
import com.candra.sewakameraapp.barang.Barang
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_tambah_promo.*
import java.util.*
import kotlin.collections.ArrayList

class TambahPromoActivity : AppCompatActivity() {

    lateinit var mDatabase: DatabaseReference
    private var dataList = ArrayList<Barang>()

    var hari = 0
    var bulan = 0
    var tahun = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_promo)

        mDatabase = FirebaseDatabase.getInstance().getReference()

        iv_back_form_tambah_promo.setOnClickListener {
            finish()
        }

        rc_item_list_barang_tambah_promo.layoutManager = LinearLayoutManager(this)

        getData()
    }

    private fun getData() {
        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()

                for (getdatasnapsot in snapshot.child("produk").children) {
                    val produk = getdatasnapsot.getValue(Barang::class.java)
                    dataList.add(produk!!)
                }

                rc_item_list_barang_tambah_promo.adapter = ListBarangAdminAdapter(dataList) {
                    showDialog(it)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TambahPromoActivity, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun showDialog(barang: Barang) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.input_promo_admin_dialog)

        val btnClose = dialog.findViewById(R.id.iv_back_dialog_promo) as ImageView
        val etharga = dialog.findViewById(R.id.et_input_harga_dialog_promo) as EditText
        val ettgl = dialog.findViewById(R.id.et_input_tgl_input_dialog) as EditText
        val btntambah = dialog.findViewById(R.id.btn_submit_input_dialog) as Button

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        ettgl.setOnClickListener {
            val dpdIn = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, stahun, sbulan, shari ->
                    ettgl.setText("$shari-${sbulan+1}-$stahun")
                },
                tahun,
                bulan,
                hari
            )
            dpdIn.datePicker.minDate = Calendar.getInstance().timeInMillis
            dpdIn.show()
        }

        btntambah.setOnClickListener {
            insertPromo(barang.id!!, etharga.text.toString().toInt(), barang.harga!!, ettgl.text.toString())
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun insertPromo(id: String, hargaPromo: Int, hargaAwal: Int, tanggal: String) {
        val promo = Promo()
        promo.key = mDatabase.child("diskon").push().key
        promo.id = id
        promo.harga = hargaAwal
        promo.expired = tanggal

//        ubah harga barang
        mDatabase.child("produk/$id/harga").setValue(hargaPromo)

//        insert
        mDatabase.child("diskon/${promo.key}").setValue(promo).addOnSuccessListener {
            Toast.makeText(this, "Berhasil Ditambah", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

}